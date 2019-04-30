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

/**
 * Enumeration of types of monotonicity.
 * 枚举类型的单调性。
 */
public enum SqlMonotonicity {
  // 严格增加
  STRICTLY_INCREASING,
  // 增加
  INCREASING,
  // 严格降低
  STRICTLY_DECREASING,
  // 降低
  DECREASING,
  // 不变
  CONSTANT,
  /**
   * Catch-all value for expressions that have some monotonic properties.
   * Maybe it isn't known whether the expression is increasing or decreasing;
   * or maybe the value is neither increasing nor decreasing but the value
   *
   * never repeats.
   *
   * 具有一些单调属性的表达式的Catch-all值。
   * 也许不知道表达式是增加还是减少
   * 或者价值既不是增加也不是减少而是价值
   * 从不重复
   * 单调
   */
  MONOTONIC,
  // 不单调
  NOT_MONOTONIC;

  /**
   * If this is a strict monotonicity (StrictlyIncreasing, StrictlyDecreasing)
   * returns the non-strict equivalent (Increasing, Decreasing).
   *
   * @return non-strict equivalent monotonicity
   * 非严格的等效单调性
   */
  public SqlMonotonicity unstrict() {
    switch (this) {
    case STRICTLY_INCREASING:
      return INCREASING;
    case STRICTLY_DECREASING:
      return DECREASING;
    default:
      return this;
    }
  }

  /**
   * Returns the reverse monotonicity.
   *
   * @return reverse monotonicity
   * 返回反向单调性。
   */
  public SqlMonotonicity reverse() {
    switch (this) {
    case STRICTLY_INCREASING:
      return STRICTLY_DECREASING;
    case INCREASING:
      return DECREASING;
    case STRICTLY_DECREASING:
      return STRICTLY_INCREASING;
    case DECREASING:
      return INCREASING;
    default:
      return this;
    }
  }

  /**
   * Whether values of this monotonicity are decreasing. That is, if a value
   * at a given point in a sequence is X, no point later in the sequence will
   * have a value greater than X.
   *
   * @return whether values are decreasing
   */
  public boolean isDecreasing() {
    switch (this) {
    case STRICTLY_DECREASING:
    case DECREASING:
      return true;
    default:
      return false;
    }
  }

  /**
   * Returns whether values of this monotonicity may ever repeat after moving
   * to another value: true for {@link #NOT_MONOTONIC} and {@link #CONSTANT},
   * false otherwise.
   *
   * <p>If a column is known not to repeat, a sort on that column can make
   * progress before all of the input has been seen.
   *
   * @return whether values repeat
   */
  public boolean mayRepeat() {
    switch (this) {
    case NOT_MONOTONIC:
    case CONSTANT:
      return true;
    default:
      return false;
    }
  }
}

// End SqlMonotonicity.java
