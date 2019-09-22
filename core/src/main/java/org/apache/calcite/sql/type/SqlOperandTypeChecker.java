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
package org.apache.calcite.sql.type;

import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlOperandCountRange;
import org.apache.calcite.sql.SqlOperator;

/**
 * Strategy interface to check for allowed operand types of an operator call.
 *
 * <p>This interface is an example of the
 * {@link org.apache.calcite.util.Glossary#STRATEGY_PATTERN strategy pattern}.
 *
 * 策略接口，以检查操作符调用的允许操作数类型。
 */
public interface SqlOperandTypeChecker {
  //~ Methods ----------------------------------------------------------------

  /**
   * Checks the types of all operands to an operator call.
   *
   * @param callBinding    description of the call to be checked
   * @param throwOnFailure whether to throw an exception if check fails
   *                       (otherwise returns false in that case)
   * @return whether check succeeded
   *
   * 检查操作符调用的所有操作数的类型。
   */
  boolean checkOperandTypes(
      SqlCallBinding callBinding,
      boolean throwOnFailure);

  /**
   * @return range of operand counts allowed in a call
   * 调用中允许的操作数计数范围
   */
  SqlOperandCountRange getOperandCountRange();

  /**
   * Returns a string describing the allowed formal signatures of a call, e.g.
   * "SUBSTR(VARCHAR, INTEGER, INTEGER)".
   *
   * @param op     the operator being checked
   * @param opName name to use for the operator in case of aliasing
   * @return generated string
   *
   * 返回描述调用允许的正式签名的字符串
   */
  String getAllowedSignatures(SqlOperator op, String opName);

  /**
   * Returns the strategy for making the arguments have consistency types.
   * 返回使参数具有一致性类型的策略
   * */
  Consistency getConsistency();

  /** Returns whether the {@code i}th operand is optional. */
  boolean isOptional(int i);

  /** Strategy used to make arguments consistent. */
  enum Consistency {
    /**
     *  Do not try to make arguments consistent.
     * 不要试图使论证保持一致。
     * */
    NONE,
    /** Make arguments of consistent type using comparison semantics.
     * Character values are implicitly converted to numeric, date-time, interval
     * or boolean.
     * 使用比较语义生成一致类型的参数。
     * */
    COMPARE,
    /**
     * Convert all arguments to the least restrictive type.
     * 将所有参数转换为限制性最小的类型。
     * */
    LEAST_RESTRICTIVE
  }
}

// End SqlOperandTypeChecker.java
