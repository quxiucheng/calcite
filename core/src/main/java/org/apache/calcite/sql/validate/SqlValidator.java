/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.sql.validate;

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.runtime.CalciteException;
import org.apache.calcite.runtime.Resources;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDelete;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlMatchRecognize;
import org.apache.calcite.sql.SqlMerge;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.SqlWindow;
import org.apache.calcite.sql.SqlWith;
import org.apache.calcite.sql.SqlWithItem;
import org.apache.calcite.sql.validate.implicit.TypeCoercion;
import org.apache.calcite.util.Util;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Validates the parse tree of a SQL statement, and provides semantic
 * information about the parse tree.
 * 验证SQL语句的解析树，并提供有关解析树的语义信息。
 *
 * <p>To create an instance of the default validator implementation, call
 * {@link SqlValidatorUtil#newValidator}.
 * 要创建默认验证器实现的实例，请调用SqlValidatorUtil.newValidator
 *
 * <h2>Visitor pattern</h2>
 * 访问者模式
 *
 * <p>The validator interface is an instance of the
 * {@link org.apache.calcite.util.Glossary#VISITOR_PATTERN visitor pattern}.
 * Implementations
 * of the {@link SqlNode#validate} method call the <code>validateXxx</code>
 * method appropriate to the kind of node:
 * <ul>
 * <li>{@link SqlLiteral#validate(SqlValidator, SqlValidatorScope)}
 *     calls
 *     {@link #validateLiteral(org.apache.calcite.sql.SqlLiteral)};
 * <li>{@link SqlCall#validate(SqlValidator, SqlValidatorScope)}
 *     calls
 *     {@link #validateCall(SqlCall, SqlValidatorScope)};
 * <li>and so forth.</ul>
 *
 * <p>The {@link SqlNode#validateExpr(SqlValidator, SqlValidatorScope)} method
 * is as {@link SqlNode#validate(SqlValidator, SqlValidatorScope)} but is called
 * when the node is known to be a scalar expression.
 * validator接口是访问者模式的一个实例。validate(org.apache.calcite.sql. sql.validate)的实现。方法调用validateXxx方法，该方法适用于以下节点:
 * SqlNode.validateExpr(SqlValidator, SqlValidatorScope)方法作为SqlNode。validate(SqlValidator, SqlValidatorScope)，但在节点已知为标量表达式时调用。
 *
 * <h2>Scopes and namespaces</h2>
 * 范围和名称空间
 *
 * <p>In order to resolve names to objects, the validator builds a map of the
 * structure of the query. This map consists of two types of objects. A
 * {@link SqlValidatorScope} describes the tables and columns accessible at a
 * particular point in the query; and a {@link SqlValidatorNamespace} is a
 * description of a data source used in a query.
 * 为了将名称解析为对象，验证器构建查询结构的映射。此映射由两种类型的对象组成。SqlValidatorScope描述在查询中的特定点可以访问的表和列;SqlValidatorNamespace是查询中使用的数据源的描述。
 *
 * <p>There are different kinds of namespace for different parts of the query.
 * for example {@link IdentifierNamespace} for table names,
 * {@link SelectNamespace} for SELECT queries,
 * {@link SetopNamespace} for UNION, EXCEPT
 * and INTERSECT. A validator is allowed to wrap namespaces in other objects
 * which implement {@link SqlValidatorNamespace}, so don't try to cast your
 * namespace or use <code>instanceof</code>; use
 * {@link SqlValidatorNamespace#unwrap(Class)} and
 * {@link SqlValidatorNamespace#isWrapperFor(Class)} instead.</p>
 *
 * <p>The validator builds the map by making a quick scan over the query when
 * the root {@link SqlNode} is first provided. Thereafter, it supplies the
 * correct scope or namespace object when it calls validation methods.</p>
 *
 * <p>The methods {@link #getSelectScope}, {@link #getFromScope},
 * {@link #getWhereScope}, {@link #getGroupScope}, {@link #getHavingScope},
 * {@link #getOrderScope} and {@link #getJoinScope} get the correct scope
 * to resolve
 * names in a particular clause of a SQL statement.</p>
 *
 * 查询的不同部分有不同类型的名称空间。
 * 例如，用于表名的IdentifierNamespace，用于选择查询的SelectNamespace，用于UNION的SetopNamespace, EXCEPT和INTERSECT。
 * 验证器允许将名称空间包装在实现SqlValidatorNamespace的其他对象中，
 * 因此不要尝试强制转换名称空间或使用instanceof
 * ;改为使用SqlValidatorNamespace.unwrap(类)和SqlValidatorNamespace.isWrapperFor(类)。
 *
 * 验证器通过在首次提供根SqlNode时对查询进行快速扫描来构建映射。
 * 然后，在调用验证方法时，它提供正确的范围或名称空间对象。
 * 方法getSelectScope(org.apache.calcite.sql.SqlSelect)，
 * getFromScope(org.apache.calcite.sql.SqlSelect)，
 * getWhereScope(org.apache.calcite.sql.SqlSelect)，
 * getGroupScope(org.apache.calcite.sql.SqlSelect)，
 * getHavingScope(org.apache.calcite.sql.SqlSelect)，
 * getOrderScope(org.apache.calcite.sql.SqlSelect)和
 * getJoinScope(org.apache.calcite.sql.SqlNode)获得正确的作用域来解析SQL语句特定子句中的名称
 */
public interface SqlValidator {


  //~ Methods ----------------------------------------------------------------

  /**
   * Returns the dialect of SQL (SQL:2003, etc.) this validator recognizes.
   * Default is {@link SqlConformanceEnum#DEFAULT}.
   *
   * @return dialect of SQL this validator recognizes
   */
  SqlConformance getConformance();

  /**
   * Returns the catalog reader used by this validator.
   * 返回此验证器使用的目录阅读器。
   * @return catalog reader
   */
  SqlValidatorCatalogReader getCatalogReader();

  /**
   * Returns the operator table used by this validator.
   * 返回此验证器使用的运算符表。
   *
   * @return operator table
   */
  SqlOperatorTable getOperatorTable();

  /**
   * Validates an expression tree. You can call this method multiple times,
   * but not reentrantly.
   *
   * @param topNode top of expression tree to be validated
   * @return validated tree (possibly rewritten)
   * 验证表达式树。您可以多次调用此方法，但不能重复调用此方法。
   */
  SqlNode validate(SqlNode topNode);

  /**
   * Validates an expression tree. You can call this method multiple times,
   * but not reentrantly.
   *
   * @param topNode       top of expression tree to be validated
   * @param nameToTypeMap map of simple name to {@link RelDataType}; used to
   *                      resolve {@link SqlIdentifier} references
   * @return validated tree (possibly rewritten)
   * 验证表达式树。
  您可以多次调用此方法，但不能重复调用此方法。
   */
  SqlNode validateParameterizedExpression(
      SqlNode topNode,
      Map<String, RelDataType> nameToTypeMap);

  /**
   * Checks that a query is valid.
   *
   * <p>Valid queries include:
   *
   * <ul>
   * <li><code>SELECT</code> statement,
   * <li>set operation (<code>UNION</code>, <code>INTERSECT</code>, <code>
   * EXCEPT</code>)
   * <li>identifier (e.g. representing use of a table in a FROM clause)
   * <li>query aliased with the <code>AS</code> operator
   * </ul>
   *
   * @param node  Query node
   * @param scope Scope in which the query occurs
   * @param targetRowType Desired row type, must not be null, may be the data
   *                      type 'unknown'.
   * @throws RuntimeException if the query is not valid
   * 检查查询是否有效。
   * SELECT语句,

  集合运算(并集、相交、除)

  标识符(例如在FROM子句中表示表的使用)

  使用AS操作符别名的查询
   */
  void validateQuery(SqlNode node, SqlValidatorScope scope,
      RelDataType targetRowType);

  /**
   * Returns the type assigned to a node by validation.
   * 返回通过验证分配给节点的类型
   *
   * @param node the node of interest
   * @return validated type, never null
   */
  RelDataType getValidatedNodeType(SqlNode node);

  /**
   * Returns the type assigned to a node by validation, or null if unknown.
   * This allows for queries against nodes such as aliases, which have no type
   * of their own. If you want to assert that the node of interest must have a
   * type, use {@link #getValidatedNodeType} instead.
   *
   * @param node the node of interest
   * @return validated type, or null if unknown or not applicable
   * 返回通过验证分配给节点的类型，如果未知则返回null。
  这允许对诸如别名之类的节点进行查询，这些节点没有自己的类型。
  如果要断言感兴趣的节点必须具有类型，请改用getValidatedNodeType（org.apache.calcite.sql.SqlNode）
   */
  RelDataType getValidatedNodeTypeIfKnown(SqlNode node);

  /**
   * Resolves an identifier to a fully-qualified name.
   * 将标识符解析为完全限定名称。
   *
   * @param id    Identifier
   * @param scope Naming scope
   * 将标识符解析为完全限定名称
   */
  void validateIdentifier(SqlIdentifier id, SqlValidatorScope scope);

  /**
   * Validates a literal.
   *验证文字。
   * @param literal Literal
   */
  void validateLiteral(SqlLiteral literal);

  /**
   * Validates a {@link SqlIntervalQualifier}
   *
   * @param qualifier Interval qualifier
   */
  void validateIntervalQualifier(SqlIntervalQualifier qualifier);

  /**
   * Validates an INSERT statement.
   *
   * @param insert INSERT statement
   */
  void validateInsert(SqlInsert insert);

  /**
   * Validates an UPDATE statement.
   *
   * @param update UPDATE statement
   */
  void validateUpdate(SqlUpdate update);

  /**
   * Validates a DELETE statement.
   *
   * @param delete DELETE statement
   */
  void validateDelete(SqlDelete delete);

  /**
   * Validates a MERGE statement.
   *
   * @param merge MERGE statement
   */
  void validateMerge(SqlMerge merge);

  /**
   * Validates a data type expression.
   * 验证数据类型表达式。
   * @param dataType Data type
   */
  void validateDataType(SqlDataTypeSpec dataType);

  /**
   * Validates a dynamic parameter.
   *
   * @param dynamicParam Dynamic parameter
   */
  void validateDynamicParam(SqlDynamicParam dynamicParam);

  /**
   * Validates the right-hand side of an OVER expression. It might be either
   * an {@link SqlIdentifier identifier} referencing a window, or an
   * {@link SqlWindow inline window specification}.
   *
   * @param windowOrId SqlNode that can be either SqlWindow with all the
   *                   components of a window spec or a SqlIdentifier with the
   *                   name of a window spec.
   * @param scope      Naming scope
   * @param call       the SqlNode if a function call if the window is attached
   *                   to one.
   */
  void validateWindow(
      SqlNode windowOrId,
      SqlValidatorScope scope,
      SqlCall call);

  /**
   * Validates a MATCH_RECOGNIZE clause.
   *
   * @param pattern MATCH_RECOGNIZE clause
   */
  void validateMatchRecognize(SqlCall pattern);

  /**
   * Validates a call to an operator.
   *
   * @param call  Operator call
   * @param scope Naming scope
   */
  void validateCall(
      SqlCall call,
      SqlValidatorScope scope);

  /**
   * Validates parameters for aggregate function.
   *
   * @param aggCall     Call to aggregate function
   * @param filter      Filter ({@code FILTER (WHERE)} clause), or null
   * @param orderList   Ordering specification ({@code WITHING GROUP} clause),
   *                    or null
   * @param scope       Syntactic scope
   */
  void validateAggregateParams(SqlCall aggCall, SqlNode filter,
      SqlNodeList orderList, SqlValidatorScope scope);

  /**
   * Validates a COLUMN_LIST parameter
   *
   * @param function function containing COLUMN_LIST parameter
   * @param argTypes function arguments
   * @param operands operands passed into the function call
   */
  void validateColumnListParams(
      SqlFunction function,
      List<RelDataType> argTypes,
      List<SqlNode> operands);

  /**
   * If an identifier is a legitimate call to a function that has no
   * arguments and requires no parentheses (for example "CURRENT_USER"),
   * returns a call to that function, otherwise returns null.
   */
  @Nullable SqlCall makeNullaryCall(SqlIdentifier id);

  /**
   * Derives the type of a node in a given scope. If the type has already been inferred,
   * returns the previous type.
   * 派生给定范围内的节点类型。如果已经推断出类型，返回上一个类型
   *
   * @param scope   Syntactic scope
   * @param operand Parse tree node
   * @return Type of the SqlNode. Should never return <code>NULL</code>
   */
  RelDataType deriveType(
      SqlValidatorScope scope,
      SqlNode operand);

  /**
   * Adds "line x, column y" context to a validator exception.
   *
   * <p>Note that the input exception is checked (it derives from
   * {@link Exception}) and the output exception is unchecked (it derives from
   * {@link RuntimeException}). This is intentional -- it should remind code
   * authors to provide context for their validation errors.</p>
   *
   * @param node The place where the exception occurred, not null
   * @param e    The validation error
   * @return Exception containing positional information, never null
   */
  CalciteContextException newValidationError(
      SqlNode node,
      Resources.ExInst<SqlValidatorException> e);

  /**
   * Returns whether a SELECT statement is an aggregation. Criteria are: (1)
   * contains GROUP BY, or (2) contains HAVING, or (3) SELECT or ORDER BY
   * clause contains aggregate functions. (Windowed aggregate functions, such
   * as <code>SUM(x) OVER w</code>, don't count.)
   *
   * @param select SELECT statement
   * @return whether SELECT statement is an aggregation
   * 返回SELECT语句是否为聚合
   *
   */
  boolean isAggregate(SqlSelect select);

  /**
   * Returns whether a select list expression is an aggregate function.
   *
   * @param selectNode Expression in SELECT clause
   * @return whether expression is an aggregate function
   */
  @Deprecated // to be removed before 2.0
  boolean isAggregate(SqlNode selectNode);

  /**
   * Converts a window specification or window name into a fully-resolved
   * window specification. For example, in <code>SELECT sum(x) OVER (PARTITION
   * BY x ORDER BY y), sum(y) OVER w1, sum(z) OVER (w ORDER BY y) FROM t
   * WINDOW w AS (PARTITION BY x)</code> all aggregations have the same
   * resolved window specification <code>(PARTITION BY x ORDER BY y)</code>.
   *
   * @param windowOrRef    Either the name of a window (a {@link SqlIdentifier})
   *                       or a window specification (a {@link SqlWindow}).
   * @param scope          Scope in which to resolve window names
   * @param populateBounds Whether to populate bounds. Doing so may alter the
   *                       definition of the window. It is recommended that
   *                       populate bounds when translating to physical algebra,
   *                       but not when validating.
   * @return A window
   * @throws RuntimeException Validation exception if window does not exist
   * 将窗口规范或窗口名称转换为完全解析的窗口规范
   */
  SqlWindow resolveWindow(
      SqlNode windowOrRef,
      SqlValidatorScope scope,
      boolean populateBounds);

  /**
   * Finds the namespace corresponding to a given node.
   *
   * <p>For example, in the query <code>SELECT * FROM (SELECT * FROM t), t1 AS
   * alias</code>, the both items in the FROM clause have a corresponding
   * namespace.
   *
   * @param node Parse tree node
   * @return namespace of node
   */
  SqlValidatorNamespace getNamespace(SqlNode node);

  /**
   * Derives an alias for an expression. If no alias can be derived, returns
   * null if <code>ordinal</code> is less than zero, otherwise generates an
   * alias <code>EXPR$<i>ordinal</i></code>.
   *
   * @param node    Expression
   * @param ordinal Ordinal of expression
   * @return derived alias, or null if no alias can be derived and ordinal is
   * less than zero
   *
   * 派生表达式的别名。
  如果不能派生别名，则如果ordinal小于零则返回null，否则生成别名EXPR $ ordinal。
   */
  String deriveAlias(
      SqlNode node,
      int ordinal);

  /**
   * Returns a list of expressions, with every occurrence of "&#42;" or
   * "TABLE.&#42;" expanded.
   * 返回表达式列表，每次出现“*”或“TABLE.*”都会扩展。
   *
   * @param selectList        Select clause to be expanded
   * @param query             Query
   * @param includeSystemVars Whether to include system variables
   * @return expanded select clause
   */
  SqlNodeList expandStar(
      SqlNodeList selectList,
      SqlSelect query,
      boolean includeSystemVars);

  /**
   * Returns the scope that expressions in the WHERE and GROUP BY clause of
   * this query should use. This scope consists of the tables in the FROM
   * clause, and the enclosing scope.
   *
   * @param select Query
   * @return naming scope of WHERE clause
   */
  SqlValidatorScope getWhereScope(SqlSelect select);

  /**
   * Returns the type factory used by this validator.
   *
   * @return type factory
   */
  RelDataTypeFactory getTypeFactory();

  /**
   * Saves the type of a {@link SqlNode}, now that it has been validated.
   *
   * @param node A SQL parse tree node, never null
   * @param type Its type; must not be null
   * @deprecated This method should not be in the {@link SqlValidator}
   * interface. The validator should drive the type-derivation process, and
   * store nodes' types when they have been derived.
   */
  void setValidatedNodeType(
      SqlNode node,
      RelDataType type);

  /**
   * Removes a node from the set of validated nodes
   *
   * @param node node to be removed
   */
  void removeValidatedNodeType(SqlNode node);

  /**
   * Returns an object representing the "unknown" type.
   *  返回表示“未知”类型的对象。
   * @return unknown type
   */
  RelDataType getUnknownType();

  /**
   * Returns the appropriate scope for validating a particular clause of a
   * SELECT statement.
   *
   * <p>Consider</p>
   *
   * <blockquote><pre><code>SELECT *
   * FROM foo
   * WHERE EXISTS (
   *    SELECT deptno AS x
   *    FROM emp
   *       JOIN dept ON emp.deptno = dept.deptno
   *    WHERE emp.deptno = 5
   *    GROUP BY deptno
   *    ORDER BY x)</code></pre></blockquote>
   *
   * <p>What objects can be seen in each part of the sub-query?</p>
   *
   * <ul>
   * <li>In FROM ({@link #getFromScope} , you can only see 'foo'.
   *
   * <li>In WHERE ({@link #getWhereScope}), GROUP BY ({@link #getGroupScope}),
   * SELECT ({@code getSelectScope}), and the ON clause of the JOIN
   * ({@link #getJoinScope}) you can see 'emp', 'dept', and 'foo'.
   *
   * <li>In ORDER BY ({@link #getOrderScope}), you can see the column alias 'x';
   * and tables 'emp', 'dept', and 'foo'.
   *
   * </ul>
   *
   * @param select SELECT statement
   * @return naming scope for SELECT statement
   *
   * 返回验证SELECT语句的特定子句的适当范围。
   */
  SqlValidatorScope getSelectScope(SqlSelect select);

  /**
   * Returns the scope for resolving the SELECT, GROUP BY and HAVING clauses.
   * Always a {@link SelectScope}; if this is an aggregation query, the
   * {@link AggregatingScope} is stripped away.
   *
   * @param select SELECT statement
   * @return naming scope for SELECT statement, sans any aggregating scope
   * 返回解析SELECT，GROUP BY和HAVING子句的范围。
  始终是SelectScope;
  如果这是一个聚合查询，则会剥离AggregatingScope
   */
  SelectScope getRawSelectScope(SqlSelect select);

  /**
   * Returns a scope containing the objects visible from the FROM clause of a
   * query.
   *
   * @param select SELECT statement
   * @return naming scope for FROM clause
   */
  SqlValidatorScope getFromScope(SqlSelect select);

  /**
   * Returns a scope containing the objects visible from the ON and USING
   * sections of a JOIN clause.
   *
   * @param node The item in the FROM clause which contains the ON or USING
   *             expression
   * @return naming scope for JOIN clause
   * @see #getFromScope
   */
  SqlValidatorScope getJoinScope(SqlNode node);

  /**
   * Returns a scope containing the objects visible from the GROUP BY clause
   * of a query.
   *
   * @param select SELECT statement
   * @return naming scope for GROUP BY clause
   */
  SqlValidatorScope getGroupScope(SqlSelect select);

  /**
   * Returns a scope containing the objects visible from the HAVING clause of
   * a query.
   *
   * @param select SELECT statement
   * @return naming scope for HAVING clause
   */
  SqlValidatorScope getHavingScope(SqlSelect select);

  /**
   * Returns the scope that expressions in the SELECT and HAVING clause of
   * this query should use. This scope consists of the FROM clause and the
   * enclosing scope. If the query is aggregating, only columns in the GROUP
   * BY clause may be used.
   *
   * @param select SELECT statement
   * @return naming scope for ORDER BY clause
   */
  SqlValidatorScope getOrderScope(SqlSelect select);

  /**
   * Returns a scope match recognize clause.
   *
   * @param node Match recognize
   * @return naming scope for Match recognize clause
   */
  SqlValidatorScope getMatchRecognizeScope(SqlMatchRecognize node);

  /**
   * Declares a SELECT expression as a cursor.
   *
   * @param select select expression associated with the cursor
   * @param scope  scope of the parent query associated with the cursor
   */
  void declareCursor(SqlSelect select, SqlValidatorScope scope);

  /**
   * Pushes a new instance of a function call on to a function call stack.
   * 将函数调用的新实例推送到函数调用堆栈。
   */
  void pushFunctionCall();

  /**
   * Removes the topmost entry from the function call stack.
   * Retrieves the name of the parent cursor referenced by a column list parameter.
   */
  void popFunctionCall();

  /**
   * Retrieves the name of the parent cursor referenced by a column list
   * parameter.
   * 检索列列表参数引用的父游标的​​名称。
   * @param columnListParamName name of the column list parameter
   * @return name of the parent cursor
   */
  String getParentCursor(String columnListParamName);

  /**
   * Enables or disables expansion of identifiers other than column
   * references.
   * 启用或禁用列引用以外的标识符扩展
   * @param expandIdentifiers new setting
   */
  void setIdentifierExpansion(boolean expandIdentifiers);

  /**
   * Enables or disables expansion of column references. (Currently this does
   * not apply to the ORDER BY clause; may be fixed in the future.)
   * 启用或禁用列引用的扩展。
   （目前这不适用于ORDER BY子句;将来可能会修复。）
   *
   * @param expandColumnReferences new setting
   */
  void setColumnReferenceExpansion(boolean expandColumnReferences);

  /**
   * @return whether column reference expansion is enabled
   */
  boolean getColumnReferenceExpansion();

  /**
   * Sets how NULL values should be collated if an ORDER BY item does not contain NULLS FIRST or NULLS LAST.
   * 设置如果ORDER BY项不包含NULLS FIRST或NULLS LAST，应如何整理NULL值。
   * NULL 如何排序,NULL排在第一位或者NULL排最后,默认HIGH,和Oracle相同
   * */
  void setDefaultNullCollation(NullCollation nullCollation);

  /** Returns how NULL values should be collated if an ORDER BY item does not
   * contain NULLS FIRST or NULLS LAST. */
  NullCollation getDefaultNullCollation();

  /**
   * Returns expansion of identifiers.
   * 返回标识符的扩展。
   *
   * @return whether this validator should expand identifiers
   */
  boolean shouldExpandIdentifiers();

  /**
   * Enables or disables rewrite of "macro-like" calls such as COALESCE.
   * 启用或禁用重写“类似宏”的调用，例如COALESCE。
   * @param rewriteCalls new setting
   */
  void setCallRewrite(boolean rewriteCalls);

  /**
   * Derives the type of a constructor.
   * 派生构造函数的类型。
   * @param scope                 Scope
   * @param call                  Call
   * @param unresolvedConstructor TODO
   * @param resolvedConstructor   TODO
   * @param argTypes              Types of arguments
   * @return Resolved type of constructor
   */
  RelDataType deriveConstructorType(
      SqlValidatorScope scope,
      SqlCall call,
      SqlFunction unresolvedConstructor,
      SqlFunction resolvedConstructor,
      List<RelDataType> argTypes);

  /**
   * Handles a call to a function which cannot be resolved. Returns a an
   * appropriately descriptive error, which caller must throw.
   *
   * @param call               Call
   * @param unresolvedFunction Overloaded function which is the target of the
   *                           call
   * @param argTypes           Types of arguments
   * @param argNames           Names of arguments, or null if call by position
   */
  CalciteException handleUnresolvedFunction(SqlCall call,
      SqlFunction unresolvedFunction, List<RelDataType> argTypes,
      List<String> argNames);

  /**
   * Expands an expression in the ORDER BY clause into an expression with the
   * same semantics as expressions in the SELECT clause.
   *
   * <p>This is made necessary by a couple of dialect 'features':
   *
   * <ul>
   * <li><b>ordinal expressions</b>: In "SELECT x, y FROM t ORDER BY 2", the
   * expression "2" is shorthand for the 2nd item in the select clause, namely
   * "y".
   * <li><b>alias references</b>: In "SELECT x AS a, y FROM t ORDER BY a", the
   * expression "a" is shorthand for the item in the select clause whose alias
   * is "a"
   * </ul>
   *
   * @param select    Select statement which contains ORDER BY
   * @param orderExpr Expression in the ORDER BY clause.
   * @return Expression translated into SELECT clause semantics
   *
   * 将ORDER BY子句中的表达式扩展为与SELECT子句中的表达式具有相同语义的表达式。
   *
   */
  SqlNode expandOrderExpr(SqlSelect select, SqlNode orderExpr);

  /**
   * Expands an expression.
   * 扩展表达式。
   *
   * @param expr  Expression
   * @param scope Scope
   * @return Expanded expression
   */
  SqlNode expand(SqlNode expr, SqlValidatorScope scope);

  /**
   * Returns whether a field is a system field. Such fields may have
   * particular properties such as sortedness and nullability.
   *
   * <p>In the default implementation, always returns {@code false}.
   *
   * @param field Field
   * @return whether field is a system field
   * 返回字段是否为系统字段。
  这些字段可能具有特定属性，例如排序性和可空性
   */
  boolean isSystemField(RelDataTypeField field);

  /**
   * Returns a description of how each field in the row type maps to a
   * catalog, schema, table and column in the schema.
   *
   * <p>The returned list is never null, and has one element for each field
   * in the row type. Each element is a list of four elements (catalog,
   * schema, table, column), or may be null if the column is an expression.
   *
   * @param sqlQuery Query
   * @return Description of how each field in the row type maps to a schema
   * object
   * 返回行类型中每个字段如何映射到架构中的目录，架构，表和列的说明。
  返回的列表永远不为null，并且行类型中的每个字段都有一个元素。
  每个元素都是四个元素（目录，模式，表，列）的列表，如果列是表达式，则可以为null。
   */
  List<List<String>> getFieldOrigins(SqlNode sqlQuery);

  /**
   * Returns a record type that contains the name and type of each parameter.
   * Returns a record type with no fields if there are no parameters.
   *
   * @param sqlQuery Query
   * @return Record type
   * 返回包含每个参数的名称和类型的记录类型。
  如果没有参数，则返回没有字段的记录类型。
   */
  RelDataType getParameterRowType(SqlNode sqlQuery);

  /**
   * Returns the scope of an OVER or VALUES node.
   *
   * @param node Node
   * @return Scope
   */
  SqlValidatorScope getOverScope(SqlNode node);

  /**
   * Validates that a query is capable of producing a return of given modality (relational or streaming).
   * 验证查询是否能够生成给定模态（关系或流）的返回。
   * @param select Query
   * @param modality Modality (streaming or relational)
   * @param fail Whether to throw a user error if does not support required
   *             modality
   * @return whether query supports the given modality
   */
  boolean validateModality(SqlSelect select, SqlModality modality,
      boolean fail);

  void validateWith(SqlWith with, SqlValidatorScope scope);

  void validateWithItem(SqlWithItem withItem);

  void validateSequenceValue(SqlValidatorScope scope, SqlIdentifier id);

  SqlValidatorScope getWithScope(SqlNode withItem);

  /**
   * Set if implicit type coercion is allowed when the validator does validation.
   * See {@link org.apache.calcite.sql.validate.implicit.TypeCoercionImpl} for the details.
   * @param enabled default as true.
   */
  SqlValidator setEnableTypeCoercion(boolean enabled);

  /** Get if this validator supports implicit type coercion. */
  boolean isTypeCoercionEnabled();

  /**
   * Set an instance of type coercion, you can customize the coercion rules to
   * override the default ones
   * in {@link org.apache.calcite.sql.validate.implicit.TypeCoercionImpl}.
   * @param typeCoercion instance of {@link TypeCoercion}.
   */
  void setTypeCoercion(TypeCoercion typeCoercion);

  /** Get the type coercion instance. */
  TypeCoercion getTypeCoercion();
}

// End SqlValidator.java
