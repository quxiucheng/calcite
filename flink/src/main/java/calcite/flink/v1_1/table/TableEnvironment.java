package calcite.flink.v1_1.table;

import org.apache.calcite.config.Lex;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.Frameworks;

/**
 * @author quxiucheng
 * @date 2019-01-21 16:12:00
 */
public class TableEnvironment {

    private SqlParser.Config parserConfig = SqlParser
            .configBuilder()
            .setLex(Lex.JAVA)
            .build();

    private SchemaPlus tables = Frameworks.createRootSchema(true);



}
