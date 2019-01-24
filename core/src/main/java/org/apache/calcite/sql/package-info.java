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

/**
 * Provides a SQL parser and object model.
 *
 * <p>This package, and the dependent <code>org.apache.calcite.sql.parser</code>
 * package, are independent of the other Calcite packages, so may be used
 * standalone.
 *
 * <h2>Parser</h2>
 *
 * <p>{@link org.apache.calcite.sql.parser.SqlParser} parses a SQL string to a
 *     parse tree. It only performs the most basic syntactic validation.</p>
 *
 * <h2>Object model</h2>
 *
 * <p>Every node in the parse tree is a {@link org.apache.calcite.sql.SqlNode}.
 *     Sub-types are:</p>
 * <ul>
 *
 *     <li>{@link org.apache.calcite.sql.SqlLiteral} represents a boolean,
 *         numeric, string, or date constant, or the value <code>NULL</code>.
 *         </li>
 *
 *     <li>{@link org.apache.calcite.sql.SqlIdentifier} represents an
 *         identifier, such as <code> EMPNO</code> or <code>emp.deptno</code>.
 *         </li>
 *
 *     <li>{@link org.apache.calcite.sql.SqlCall} is a call to an operator or
 *         function.  By means of special operators, we can use this construct
 *         to represent virtually every non-leaf node in the tree. For example,
 *         a <code>select</code> statement is a call to the 'select'
 *         operator.</li>
 *
 *     <li>{@link org.apache.calcite.sql.SqlNodeList} is a list of nodes.</li>
 *
 * </ul>
 *
 * <p>A {@link org.apache.calcite.sql.SqlOperator} describes the behavior of a
 *     node in the tree, such as how to un-parse a
 *     {@link org.apache.calcite.sql.SqlCall} into a SQL string.  It is
 *     important to note that operators are metadata, not data: there is only
 *     one <code>SqlOperator</code> instance representing the '=' operator, even
 *     though there may be many calls to it.</p>
 *
 * <p><code>SqlOperator</code> has several derived classes which make it easy to
 *     define new operators: {@link org.apache.calcite.sql.SqlFunction},
 *     {@link org.apache.calcite.sql.SqlBinaryOperator},
 *     {@link org.apache.calcite.sql.SqlPrefixOperator},
 *     {@link org.apache.calcite.sql.SqlPostfixOperator}.
 * And there are singleton classes for special syntactic constructs
 *     {@link org.apache.calcite.sql.SqlSelectOperator}
 *     and {@link org.apache.calcite.sql.SqlJoin.SqlJoinOperator}. (These
 *     special operators even have their own sub-types of
 *     {@link org.apache.calcite.sql.SqlCall}:
 *     {@link org.apache.calcite.sql.SqlSelect} and
 *     {@link org.apache.calcite.sql.SqlJoin}.)</p>
 *
 * <p>A {@link org.apache.calcite.sql.SqlOperatorTable} is a collection of
 *     operators. By supplying your own operator table, you can customize the
 *     dialect of SQL without modifying the parser.</p>
 *
 * <h2>Validation</h2>
 *
 * <p>{@link org.apache.calcite.sql.validate.SqlValidator} checks that
 *     a tree of {@link org.apache.calcite.sql.SqlNode}s is
 *     semantically valid. You supply a
 *     {@link org.apache.calcite.sql.SqlOperatorTable} to describe the available
 *     functions and operators, and a
 *     {@link org.apache.calcite.sql.validate.SqlValidatorCatalogReader} for
 *     access to the database's catalog.</p>
 *
 * <h2>Generating SQL</h2>
 *
 * <p>A {@link org.apache.calcite.sql.SqlWriter} converts a tree of
 * {@link org.apache.calcite.sql.SqlNode}s into a SQL string. A
 * {@link org.apache.calcite.sql.SqlDialect} defines how this happens.</p>
 *
 * 提供一个SQL解析器和对象模型。
 * 这个包，以及依赖的org.apache.calcite.sql。解析器包独立于其他calcite包，因此可以独立使用。
 * Parser
 * SqlParser将SQL字符串解析为解析树。它只执行最基本的语法验证。
 *
 * Object model
 *  解析树中的每个节点都是SqlNode。子类型是:
 *  SqlLiteral表示布尔、数字、字符串、日期常量或值NULL。
 *  SqlIdentifier表示标识符，如EMPNO或emp.deptno
 *  SqlCall是对操作符或函数的调用。通过特殊的运算符，我们可以使用这个构造来表示树中的每个非叶节点。例如，select语句是对“select”操作符的调用。
 *  SqlNodeList是一个节点列表
 *
 * SqlOperator描述树中节点的行为，例如如何将SqlCall解析为SQL字符串。需要注意的是，操作符是元数据，而不是数据:只有一个SqlOperator实例表示'='操作符，尽管可能有许多对它的调用。
 * SqlOperator有几个派生类，它们使定义新操作符变得很容易:SqlFunction、SqlBinaryOperator、SqlPrefixOperator、SqlPostfixOperator。对于特殊的语法结构SqlSelectOperator和SqlJoin.SqlJoinOperator也有单例类。(这些特殊的操作符甚至有它们自己的SqlCall子类型:SqlSelect和SqlJoin。)
 * SqlOperatorTable是操作符的集合。通过提供自己的运算符表，您可以自定义SQL方言，而无需修改解析器。
 *
 * Validation
 *  SqlValidator检查sqlnode树在语义上是否有效。您提供一个SqlOperatorTable来描述可用的函数和操作符，以及一个sqlvalidatorcataloggreader来访问数据库的目录。
 * Generating SQL
 *  SqlWriter将sqlnode树转换为SQL字符串。sql方言定义了这是如何发生的。
 *
 */
@PackageMarker
package org.apache.calcite.sql;

import org.apache.calcite.avatica.util.PackageMarker;

// End package-info.java
