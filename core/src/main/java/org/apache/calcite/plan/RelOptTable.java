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
package org.apache.calcite.plan;

import org.apache.calcite.linq4j.tree.Expression;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelDistribution;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelReferentialConstraint;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.metadata.RelMetadataQuery;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.schema.ColumnStrategy;
import org.apache.calcite.schema.Wrapper;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.List;

/**
 * Represents a relational dataset in a {@link RelOptSchema}. It has methods to
 * describe and implement itself.
 *
 * 表示RelOptSchema中的关系数据集。它有描述和实现自身的方法。
 */
public interface RelOptTable extends Wrapper {
  //~ Methods ----------------------------------------------------------------

  /**
   * Obtains an identifier for this table. The identifier must be unique with
   * respect to the Connection producing this table.
   *
   * @return qualified name
   * 获取此表的标识符。标识符对于生成此表的连接必须是唯一的。
   */
  List<String> getQualifiedName();

  /**
   * Returns an estimate of the number of rows in the table.
   * 返回表中行数的估计值。
   */
  double getRowCount();

  /**
   * Describes the type of rows returned by this table.
   * 描述此表返回的行类型
   */
  RelDataType getRowType();

  /**
   * Returns the {@link RelOptSchema} this table belongs to.
   * 返回此表所属的RelOptSchema。
   */
  RelOptSchema getRelOptSchema();

  /**
   * Converts this table into a {@link RelNode relational expression}.
   *
   * <p>The {@link org.apache.calcite.plan.RelOptPlanner planner} calls this
   * method to convert a table into an initial relational expression,
   * generally something abstract, such as a
   * {@link org.apache.calcite.rel.logical.LogicalTableScan},
   * then optimizes this expression by
   * applying {@link org.apache.calcite.plan.RelOptRule rules} to transform it
   * into more efficient access methods for this table.</p>
   *
   * 将此表转换为关系表达式。
   * 策划者调用这个方法将一个表转换成一个初始关系表达式，通常是一些抽象的东西，比如LogicalTableScan，然后通过应用规则将这个表达式转换成这个表的更有效的访问方法来优化这个表达式。
   */
  RelNode toRel(ToRelContext context);

  /**
   * Returns a description of the physical ordering (or orderings) of the rows
   * returned from this table.
   *
   * @see RelMetadataQuery#collations(RelNode)
   * 返回从该表返回的行的物理顺序(或顺序)的描述。
   */
  List<RelCollation> getCollationList();

  /**
   * Returns a description of the physical distribution of the rows
   * in this table.
   *
   * @see RelMetadataQuery#distribution(RelNode)
   * 返回此表中行物理分布的描述。
   */
  RelDistribution getDistribution();

  /**
   * Returns whether the given columns are a key or a superset of a unique key
   * of this table.
   *
   * @param columns Ordinals of key columns
   * @return Whether the given columns are a key or a superset of a key
   * 返回给定列是该表的键还是唯一键的超集。
   */
  boolean isKey(ImmutableBitSet columns);

  /**
   * Returns the referential constraints existing for this table. These constraints
   * are represented over other tables using {@link RelReferentialConstraint} nodes.
   * 返回此表的引用约束。这些约束使用RelReferentialConstraint节点在其他表上表示。
   */
  List<RelReferentialConstraint> getReferentialConstraints();

  /**
   * Generates code for this table.
   *
   * @param clazz The desired collection class; for example {@code Queryable}.
   * 为该表生成代码。
   */
  Expression getExpression(Class clazz);

  /** Returns a table with the given extra fields.
   *
   * <p>The extended table includes the fields of this base table plus the
   * extended fields that do not have the same name as a field in the base
   * table.
   *
   * 返回具有给定额外字段的表。
   * 扩展表包括此基表的字段以及与基表中的字段名称不同的扩展字段。
   */
  RelOptTable extend(List<RelDataTypeField> extendedFields);

  /** Returns a list describing how each column is populated. The list has the
   *  same number of entries as there are fields, and is immutable.
   *  返回一个描述如何填充每个列的列表。该列表的条目数与字段数相同，并且是不可变的
   **/
  List<ColumnStrategy> getColumnStrategies();

  /**
   * Can expand a view into relational expressions.
   * 可以将视图展开为关系表达式。
   **/
  interface ViewExpander {
    /**
     * Returns a relational expression that is to be substituted for an access
     * to a SQL view.
     *
     * @param rowType Row type of the view
     * @param queryString Body of the view
     * @param schemaPath Path of a schema wherein to find referenced tables
     * @param viewPath Path of the view, ending with its name; may be null
     * @return Relational expression
     *
     * 返回要替代对SQL视图的访问的关系表达式。
     */
    RelRoot expandView(RelDataType rowType, String queryString,
        List<String> schemaPath, List<String> viewPath);
  }

  /** Contains the context needed to convert a a table into a relational
   * expression. */
  interface ToRelContext extends ViewExpander {
    RelOptCluster getCluster();
  }
}

// End RelOptTable.java
