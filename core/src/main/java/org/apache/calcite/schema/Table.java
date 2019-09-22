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
package org.apache.calcite.schema;

import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;

/**
 * Table.
 *
 * <p>The typical way for a table to be created is when Calcite interrogates a
 * user-defined schema in order to validate names appearing in a SQL query.
 * Calcite finds the schema by calling {@link Schema#getSubSchema(String)} on
 * the connection's root schema, then gets a table by calling
 * {@link Schema#getTable(String)}.</p>
 *
 * <p>Note that a table does not know its name. It is in fact possible for
 * a table to be used more than once, perhaps under multiple names or under
 * multiple schemas. (Compare with the
 * <a href="http://en.wikipedia.org/wiki/Inode">i-node</a> concept in the UNIX
 * filesystem.)</p>
 *
 * <p>A particular table instance may also implement {@link Wrapper},
 * to give access to sub-objects.
 *创建表的典型方法是，Calcite询问用户定义的模式，以验证出现在SQL查询中的名称。
 Calcite通过在连接的根模式上调用Schema.getSubSchema（String）来查找模式，然后通过调用Schema.getTable（String）获取表。
 请注意，表格不知道其名称。
 事实上，一个表可能被多次使用，可能在多个名称下或多个模式下使用。
 （与UNIX文件系统中的i-node概念比较。）
 特定的表实例也可以实现Wrapper，以提供对子对象的访问
 * @see TableMacro
 */
public interface Table {
  /** Returns this table's row type.
   *
   * <p>This is a struct type whose
   * fields describe the names and types of the columns in this table.</p>
   *
   * <p>The implementer must use the type factory provided. This ensures that
   * the type is converted into a canonical form; other equal types in the same
   * query will use the same object.</p>
   *
   * @param typeFactory Type factory with which to create the type
   * @return Row type
   */
  RelDataType getRowType(RelDataTypeFactory typeFactory);

  /** Returns a provider of statistics about this table. */
  Statistic getStatistic();

  /** Type of table. */
  Schema.TableType getJdbcTableType();

  /**
   * Determines whether the given {@code column} has been rolled up.
   * */
  boolean isRolledUp(String column);

  /**
   * Determines whether the given rolled up column can be used inside the given aggregate function.
   * You can assume that {@code isRolledUp(column)} is {@code true}.
   *确定给定的汇总列是否可以在给定的聚合函数中使用。
   您可以假设isRolledUp（列）为true。
   * @param column The column name for which {@code isRolledUp} is true
   * @param call The aggregate call
   * @param parent Parent node of {@code call} in the {@link SqlNode} tree
   * @param config Config settings. May be null
   * @return true iff the given aggregate call is valid
   */
  boolean rolledUpColumnValidInsideAgg(String column, SqlCall call,
      SqlNode parent, CalciteConnectionConfig config);
}

// End Table.java
