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
package org.apache.calcite.rex;

import java.util.List;

/**
 * Can reduce expressions, writing a literal for each into a list.
 * 计算表达式
 * */
public interface RexExecutor {

  /** Reduces expressions, and writes their results into {@code reducedValues}.
   *
   * <p>If an expression cannot be reduced, writes the original expression.
   * For example, {@code CAST('abc' AS INTEGER)} gives an error when executed, so the executor
   * ignores the error and writes the original expression.
   *
   * @param rexBuilder Rex builder
   * @param constExps Expressions to be reduced
   * @param reducedValues List to which reduced expressions are appended
   *
   * 减少表达式，并将其结果写入reducedValues中。
   * 如果表达式不能被还原，则写入原始表达式。例如，CAST('abc'为INTEGER)在执行时给出一个错误，因此执行程序忽略该错误并写入原始表达式。
   */
  void reduce(RexBuilder rexBuilder, List<RexNode> constExps, List<RexNode> reducedValues);
}

// End RexExecutor.java
