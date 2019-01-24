package calcite.flink.v1_7.table.calcite;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.prepare.PlannerImpl;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlValidatorImpl;
import org.apache.calcite.sql2rel.RelDecorrelator;
import org.apache.calcite.sql2rel.SqlRexConvertletTable;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.FrameworkConfig;

import java.util.List;
import java.util.Properties;

/**
 * full
 * @author quxiucheng
 * @date 2019-01-22 10:05:00
 */
@Data
public class FlinkPlannerImpl {

    private FrameworkConfig config;
    private RelOptPlanner planner;
    private RelDataTypeFactory typeFactory;
    private RelRoot root;
    private SqlValidatorImpl sqlValidator;

    private SqlOperatorTable operatorTable;
    private ImmutableList<RelTraitDef> traitDefs;
    private SqlParser.Config parserConfig;

    private SqlRexConvertletTable convertletTable;
    private SchemaPlus defaultSchema;

    SqlToRelConverter.Config sqlToRelConverterConfig;
    FlinkCalciteSqlValidator validator;

    private void ready() {
        if (this.traitDefs != null) {
            planner.clearRelTraitDefs();
            for (RelTraitDef traitDef : traitDefs) {
                planner.addRelTraitDef(traitDef);
            }
        }
    }

    public FlinkPlannerImpl(FrameworkConfig config, RelOptPlanner planner, RelDataTypeFactory typeFactory) {
        this.config = config;
        this.planner = planner;
        this.typeFactory = typeFactory;
        operatorTable = config.getOperatorTable();
        convertletTable = config.getConvertletTable();
        defaultSchema = config.getDefaultSchema();
        traitDefs = config.getTraitDefs();
        parserConfig = config.getParserConfig();
        sqlToRelConverterConfig = config.getSqlToRelConverterConfig();
    }

    public SqlNode parse(String sql) throws SqlParseException {
        ready();
        SqlParser sqlParser = SqlParser.create(sql, config.getParserConfig());
        SqlNode sqlNode = sqlParser.parseStmt();
        return sqlNode;
    }

    /**
     * @see PlannerImpl#validate(org.apache.calcite.sql.SqlNode)
     * @param sqlNode
     * @return
     */
    public SqlNode validate(SqlNode sqlNode) {
        sqlValidator = new FlinkCalciteSqlValidator(
                operatorTable, createCatalogReader(false), (JavaTypeFactory) typeFactory);
        sqlValidator.setIdentifierExpansion(true);

        SqlNode validate = sqlValidator.validate(sqlNode);
        return validate;
    }


    /**
     * @see PlannerImpl#rel(org.apache.calcite.sql.SqlNode)
     * @see org.apache.calcite.prepare.PlannerImpl#expandView(org.apache.calcite.rel.type.RelDataType, java.lang.String, java.util.List, java.util.List)
     * @param validatedSqlNode
     * @return
     */
    public RelRoot rel(SqlNode validatedSqlNode) {

        RexBuilder rexBuilder = createRexBuilder();

        RelOptCluster cluster = FlinkRelOptClusterFactory.create(planner, rexBuilder);

        SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(
                new ViewExpanderImpl(),
                sqlValidator,
                createCatalogReader(false),
                cluster,
                convertletTable,
                sqlToRelConverterConfig);

        RelRoot root = sqlToRelConverter.convertQuery(validatedSqlNode, false, true);
        return root;
    }


    /**
     * @param lenientCaseSensitivity
     * @return
     * @see org.apache.calcite.prepare.PlannerImpl#createCatalogReader()
     */
    private CalciteCatalogReader createCatalogReader(Boolean lenientCaseSensitivity) {
        SchemaPlus rootSchema = this.rootSchema(config.getDefaultSchema());

        boolean caseSensitive;
        if (lenientCaseSensitivity) {
            caseSensitive = false;
        } else {
            caseSensitive = this.parserConfig.caseSensitive();
        }


        SqlParser.Config parserConfig = SqlParser.configBuilder(this.parserConfig)
                .setCaseSensitive(caseSensitive)
                .build();

        return new CalciteCatalogReader(
                CalciteSchema.from(rootSchema),
                CalciteSchema.from(config.getDefaultSchema()).path(null),
                typeFactory,
                connectionConfig(parserConfig)
        );
    }


    /**
     * @see PlannerImpl#rootSchema(org.apache.calcite.schema.SchemaPlus)
     * @param schema
     * @return
     */
    private SchemaPlus rootSchema(SchemaPlus schema) {
        if (schema.getParentSchema() == null) {
            return schema;
        } else {
            return rootSchema(schema.getParentSchema());
        }
    }

    private CalciteConnectionConfig connectionConfig(SqlParser.Config parserConfig) {
        Properties prop = new Properties();
        prop.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(),
                String.valueOf(parserConfig.caseSensitive()));
        return new CalciteConnectionConfigImpl(prop);
    }

    /**
     * @see PlannerImpl#createRexBuilder()
     * @return
     */
    private RexBuilder createRexBuilder() {
        return new RexBuilder(typeFactory);
    }



    class ViewExpanderImpl implements RelOptTable.ViewExpander {

        @Override
        public RelRoot expandView(RelDataType rowType,
                                  String queryString,
                                  List<String> schemaPath,
                                  List<String> viewPath) {

            SqlParser parser = SqlParser.create(queryString, parserConfig);
            SqlNode sqlNode;
            try {
                sqlNode = parser.parseQuery();
            } catch (SqlParseException e) {
                throw new RuntimeException(e);
            }
            CalciteCatalogReader catalogReader = createCatalogReader(false)
                    .withSchemaPath(schemaPath);
            SqlValidatorImpl validator =
                    new FlinkCalciteSqlValidator(operatorTable, catalogReader, (JavaTypeFactory) typeFactory);
            validator.setIdentifierExpansion(true);
            SqlNode validatedSqlNode = validator.validate(sqlNode);
            RexBuilder rexBuilder = createRexBuilder();
            RelOptCluster cluster = FlinkRelOptClusterFactory.create(planner, rexBuilder);
            // SqlToRelConverter.Config conversConfig = SqlToRelConverter.configBuilder().withTrimUnusedFields(false).withConvertTableAccess(false).build();
            SqlToRelConverter sqlToRelConverter = new SqlToRelConverter(
                    new ViewExpanderImpl(),
                    validator,
                    catalogReader,
                    cluster,
                    convertletTable,
                    sqlToRelConverterConfig);

            root = sqlToRelConverter.convertQuery(validatedSqlNode, true, false);
            root = root.withRel(sqlToRelConverter.flattenTypes(root.rel, true));
            root = root.withRel(RelDecorrelator.decorrelateQuery(root.rel));

            return FlinkPlannerImpl.this.root;
        }
    }

}
