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
 * Provides a language for representing row-expressions.
 *
 * <h2>Life-cycle</h2>
 *
 * <p>A {@link org.apache.calcite.sql2rel.SqlToRelConverter} converts a SQL
 * parse tree consisting of {@link org.apache.calcite.sql.SqlNode} objects into
 * a relational expression ({@link org.apache.calcite.rel.RelNode}). Several
 * kinds of nodes in this tree have row expressions
 * ({@link org.apache.calcite.rex.RexNode}).</p>
 *
 * <p>After the relational expression has been optimized, a
 * {@link org.apache.calcite.adapter.enumerable.JavaRelImplementor} converts it
 * into to a plan. If the plan is a Java parse tree, row-expressions are
 * translated into equivalent Java expressions.</p>
 *
 * <h2>Expressions</h2>
 *
 *
 * <p>Every row-expression has a type. (Compare with
 * {@link org.apache.calcite.sql.SqlNode}, which is created before validation,
 * and therefore types may not be available.)</p>
 *
 * <p>Every node in the parse tree is a {@link org.apache.calcite.rex.RexNode}.
 *     Sub-types are:</p>
 * <ul>
 *     <li>{@link org.apache.calcite.rex.RexLiteral} represents a boolean,
 *         numeric, string, or date constant, or the value <code>NULL</code>.
 *     </li>
 *     <li>{@link org.apache.calcite.rex.RexVariable} represents a leaf of the
 *         tree. It has sub-types:
 *         <ul>
 *             <li>{@link org.apache.calcite.rex.RexCorrelVariable} is a
 *                 correlating variable for nested-loop joins
 *             </li>
 *             <li>{@link org.apache.calcite.rex.RexInputRef} refers to a field
 *                 of an input relational expression
 *             </li>
 *             <li>{@link org.apache.calcite.rex.RexCall} is a call to an
 *                 operator or function.  By means of special operators, we can
 *                 use this construct to represent virtually every non-leaf node
 *                 in the tree.
 *             </li>
 *             <li>{@link org.apache.calcite.rex.RexRangeRef} refers to a
 *                 collection of contiguous fields from an input relational
 *                 expression. It usually exists only during translation.
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <p>Expressions are generally
 *     created using a {@link org.apache.calcite.rex.RexBuilder} factory.</p>
 *
 * <h2>Related packages</h2>
 * <ul>
 *     <li>{@link org.apache.calcite.sql} SQL object model</li>
 *
 *     <li>{@link org.apache.calcite.plan} Core classes, including
 * {@link org.apache.calcite.rel.type.RelDataType} and
 * {@link org.apache.calcite.rel.type.RelDataTypeFactory}.</li>
 *
 * </ul>
 * 提供一种表示行表达式的语言。
 *
 * 生命周期
 * SqlToRelConverter将SqlNode对象组成的SQL解析树转换为关系表达式(RelNode)。此树中的几种节点具有行表达式(RexNode)。


 优化关系表达式之后，JavaRelImplementor将其转换为计划。如果计划是Java解析树，则行表达式将转换为等效的Java表达式。

 *
 * 表达式
 * 每个行表达式都有一个类型。(与SqlNode相比，SqlNode是在验证之前创建的，因此类型可能不可用。)
 *
 * 解析树中的每个节点都是RexNode。子类型是:


 RexLiteral表示布尔、数字、字符串、日期常量或值NULL。

        RexVariable表示树的叶子。它有子类型:

            RexCorrelVariable是用于嵌套循环连接的相关变量

            RexInputRef是一个输入关系表达式的字段

            RexCall是对运算符或函数的调用。通过特殊的运算符，我们可以使用这个构造来表示树中的每个非叶节点。

            RexRangeRef引用来自输入关系表达式的连续字段集合。它通常只存在于翻译过程中。

 表达式通常使用RexBuilder工厂创建。
 */
@PackageMarker
package org.apache.calcite.rex;

import org.apache.calcite.avatica.util.PackageMarker;

// End package-info.java
