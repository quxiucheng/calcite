package calcite.flink.v1_7.table.calcite;

import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.tools.RuleSet;

/**
 * @author quxiucheng
 * @date 2019-01-22 16:21:00
 */
public interface CalciteConfig {

    /**
     * Returns whether this configuration replaces the built-in normalization rule set.
     * 返回此配置是否替换内置规范化规则集。
     */
    Boolean replacesNormRuleSet();

    /**
     * Returns a custom normalization rule set.
     * 返回自定义规范化规则集
     */
    RuleSet getNormRuleSet();

    /**
     * Returns whether this configuration replaces the built-in logical optimization rule set.
     * 返回此配置是否替换内置逻辑优化规则集
     */
    Boolean replacesLogicalOptRuleSet();

    /**
     * Returns a custom logical optimization rule set.
     * 返回自定义逻辑优化规则集。
     */
    RuleSet getLogicalOptRuleSet();

    /**
     * Returns whether this configuration replaces the built-in physical optimization rule set.
     * 返回此配置是否替换内置物理优化规则集
     */
    Boolean replacesPhysicalOptRuleSet();

    /**
     * Returns a custom physical optimization rule set.
     * 返回自定义物理优化规则集
     */
    RuleSet getPhysicalOptRuleSet();

    /**
     * Returns whether this configuration replaces the built-in decoration rule set.
     * 返回此配置是否替换内置修饰规则集。
     */
    Boolean replacesDecoRuleSet();

    /**
     * Returns a custom decoration rule set.
     * 返回自定义装饰规则集
     */
    RuleSet getDecoRuleSet();

    /**
     * Returns whether this configuration replaces the built-in SQL operator table.
     * 返回此配置是否替换内置SQL运算符表。
     */
    Boolean replacesSqlOperatorTable();

    /**
     * Returns a custom SQL operator table.
     */
    SqlOperatorTable getSqlOperatorTable();

    /**
     * Returns a custom SQL parser configuration.
     */
    SqlParser.Config getSqlParserConfig();

    /**
     * Returns a custom configuration for SqlToRelConverter.
     */
    SqlToRelConverter.Config getSqlToRelConverterConfig();
}
