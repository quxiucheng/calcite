package org.apache.calcite.sql.parser;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.impl.SqlParserImpl;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlConformanceEnum;
import org.apache.calcite.util.Litmus;

/**
 * @author quxiucheng
 * @date 2019-01-21 14:42:00
 */
public class MySqlParserTest {

    // int identifierMaxLength();
    // Casing quotedCasing();
    // Casing unquotedCasing();
    // Quoting quoting();
    // boolean caseSensitive();
    // SqlConformance conformance();

    /**
     *
     默认配置
     private Casing quotedCasing = Lex.ORACLE.quotedCasing;
     private Casing unquotedCasing = Lex.ORACLE.unquotedCasing;
     private Quoting quoting = Lex.ORACLE.quoting;
     private int identifierMaxLength = DEFAULT_IDENTIFIER_MAX_LENGTH;
     private boolean caseSensitive = Lex.ORACLE.caseSensitive;
     private SqlConformance conformance = SqlConformanceEnum.DEFAULT;
     private SqlParserImplFactory parserFactory = SqlParserImpl.FACTORY;
     * @param args
     * @throws SqlParseException
     */
    public static void main(String[] args) throws SqlParseException {
        // 引用符号 ""
        Quoting quoting = Quoting.BACK_TICK;
        // Quoting.BACK_TICK `
       // Quoting.BRACKET []
        // 大小写转换
        Casing unquotedCasing = Casing.UNCHANGED;
        Casing quotedCasing = Casing.UNCHANGED;
        SqlConformance conformance = SqlConformanceEnum.DEFAULT;

        SqlParser sqlParser = SqlParser.create("select a,b,C,D from table_ttt where a='aaaa'", SqlParser.configBuilder()
                .setParserFactory(SqlParserImpl.FACTORY)
                // 最大的字段长度
                .setIdentifierMaxLength(255)
                // 转义字符符号
                .setQuoting(quoting)
                // 大小写转换
                .setUnquotedCasing(unquotedCasing)
                // 大小写匹配
                .setCaseSensitive(true)
                // 未知
                .setQuotedCasing(quotedCasing)
                // sql模式
                .setConformance(conformance)
                .build());

        SqlNode sqlNode = sqlParser.parseStmt();
        SqlNode sqlNode1 = sqlParser.parseQuery("select a,b,C,D from table_ttt where a='aaaa'");
        // SqlAbstractParserImpl.Metadata metadata = sqlParser.getMetadata();
        System.out.println(sqlNode.equalsDeep(sqlNode1, Litmus.IGNORE));
        System.out.println(sqlNode);

    }
}
