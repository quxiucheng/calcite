package calcite.flink.v1_7.table.calcite;

import lombok.Builder;
import lombok.Data;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.RuleSet;

import java.util.Properties;

/**
 * @author quxiucheng
 * @date 2019-01-22 16:11:00
 */
@Data
public class CalciteConfigImpl implements CalciteConfig {

    // CalciteConfigImpl DEFAULT = createBuilder().build();
    //
    // /**
    //  * Creates a new builder for constructing a [[CalciteConfig]].
    //  */
    // CalciteConfigBuilder createBuilder(){
    //     return new CalciteConfigBuilder();
    // }

    CalciteConnectionConfigImpl connectionConfig(SqlParser.Config parserConfig ) {
        Properties prop = new Properties();
        prop.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(),
                String.valueOf(parserConfig.caseSensitive()));
        return new CalciteConnectionConfigImpl(prop);
    }

    @Override
    public Boolean replacesNormRuleSet() {
        return null;
    }

    @Override
    public RuleSet getNormRuleSet() {
        return null;
    }

    @Override
    public Boolean replacesLogicalOptRuleSet() {
        return null;
    }

    @Override
    public RuleSet getLogicalOptRuleSet() {
        return null;
    }

    @Override
    public Boolean replacesPhysicalOptRuleSet() {
        return null;
    }

    @Override
    public RuleSet getPhysicalOptRuleSet() {
        return null;
    }

    @Override
    public Boolean replacesDecoRuleSet() {
        return null;
    }

    @Override
    public RuleSet getDecoRuleSet() {
        return null;
    }

    @Override
    public Boolean replacesSqlOperatorTable() {
        return null;
    }

    @Override
    public SqlOperatorTable getSqlOperatorTable() {
        return null;
    }

    @Override
    public SqlParser.Config getSqlParserConfig() {
        return null;
    }

    @Override
    public SqlToRelConverter.Config getSqlToRelConverterConfig() {
        return null;
    }
}


