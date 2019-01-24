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

/**
 * RelOptCost defines an interface for optimizer cost in terms of number of rows
 * processed, CPU cost, and I/O cost. Optimizer implementations may use all of
 * this information, or selectively ignore portions of it. The specific units
 * for all of these quantities are rather vague; most relational expressions
 * provide a default cost calculation, but optimizers can override this by
 * plugging in their own cost models with well-defined meanings for each unit.
 * Optimizers which supply their own cost models may also extend this interface
 * with additional cost metrics such as memory usage.
 *
 * RelOptCost根据处理的行数、CPU成本和I/O成本为优化器成本定义了一个接口。
 * 优化器实现可能会使用所有这些信息，或者选择性地忽略其中的一部分。
 * 所有这些量的具体单位相当模糊;
 * 大多数关系表达式提供默认的成本计算，但是优化器可以通过插入具有定义良好的每个单元含义的成本模型来覆盖这一点。
 * 提供自己的成本模型的优化器还可以使用额外的成本指标(如内存使用)扩展此接口。
 */
public interface RelOptCost {
  //~ Methods ----------------------------------------------------------------

  /**
   * @return number of rows processed; this should not be confused with the
   * row count produced by a relational expression
   * ({@link org.apache.calcite.rel.RelNode#estimateRowCount})
   * 处理的行数;这与关系表达式(RelNode.estimateRowCount(org.apache.calcite.rel.metadata.RelMetadataQuery)生成的行数不应混淆)
   */
  double getRows();

  /**
   * @return usage of CPU resources
   */
  double getCpu();

  /**
   * @return usage of I/O resources
   */
  double getIo();

  /**
   * @return true iff this cost represents an expression that hasn't actually
   * been implemented (e.g. a pure relational algebra expression) or can't
   * actually be implemented, e.g. a transfer of data between two disconnected
   * sites
   *
   * 这个代价表示一个没有实际实现的表达式(例如一个纯关系代数表达式)，或者不能实际实现的表达式(例如在两个断开连接的站点之间传输数据)
   */
  boolean isInfinite();

  // REVIEW jvs 3-Apr-2006:  we should standardize this
  // to Comparator/equals/hashCode

  /**
   * Compares this to another cost.
   *
   * @param cost another cost
   * @return true iff this is exactly equal to other cost
   */
  boolean equals(RelOptCost cost);

  /**
   * Compares this to another cost, allowing for slight roundoff errors.
   *
   * @param cost another cost
   * @return true iff this is the same as the other cost within a roundoff
   * margin of error
   *
   * 将其与另一种成本进行比较，允许出现轻微的舍入错误。
   */
  boolean isEqWithEpsilon(RelOptCost cost);

  /**
   * Compares this to another cost.
   *
   * @param cost another cost
   * @return true iff this is less than or equal to other cost
   * 与另一种成本相比。
   */
  boolean isLe(RelOptCost cost);

  /**
   * Compares this to another cost.
   *
   * @param cost another cost
   * @return true iff this is strictly less than other cost
   */
  boolean isLt(RelOptCost cost);

  /**
   * Adds another cost to this.
   *
   * @param cost another cost
   * @return sum of this and other cost
   */
  RelOptCost plus(RelOptCost cost);

  /**
   * Subtracts another cost from this.
   *
   * @param cost another cost
   * @return difference between this and other cost
   */
  RelOptCost minus(RelOptCost cost);

  /**
   * Multiplies this cost by a scalar factor.
   *
   * @param factor scalar factor
   * @return scalar product of this and factor
   */
  RelOptCost multiplyBy(double factor);

  /**
   * Computes the ratio between this cost and another cost.
   *
   * <p>divideBy is the inverse of {@link #multiplyBy(double)}. For any
   * finite, non-zero cost and factor f, <code>
   * cost.divideBy(cost.multiplyBy(f))</code> yields <code>1 / f</code>.
   *
   * @param cost Other cost
   * @return Ratio between costs
   */
  double divideBy(RelOptCost cost);

  /**
   * Forces implementations to override {@link Object#toString} and provide a
   * good cost rendering to use during tracing.
   */
  String toString();
}

// End RelOptCost.java
