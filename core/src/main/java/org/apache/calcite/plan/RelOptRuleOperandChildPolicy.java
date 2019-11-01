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
 * Policy by which operands will be matched by relational expressions with
 * any number of children.
 * 策略，通过该策略，操作数将由具有任意数量子元素的关系表达式匹配。
 */
public enum RelOptRuleOperandChildPolicy {
  /**
   * Signifies that operand can have any number of children.
   * 表示操作数可以有任意数量的子操作数。
   */
  ANY,

  /**
   * Signifies that operand has no children. Therefore it matches a
   * leaf node, such as a table scan or VALUES operator.
   *
   * <p>{@code RelOptRuleOperand(Foo.class, NONE)} is equivalent to
   * {@code RelOptRuleOperand(Foo.class)} but we prefer the former because
   * it is more explicit.</p>
   *
   * 表示操作数没有子操作数。因此，它匹配叶节点，例如表扫描或值操作符
   * RelOptRuleOperand(Foo.class, NONE) 等价于RelOptRuleOperand(Foo.class)，但我们更喜欢前者，因为它更显式。
   */
  LEAF,

  /**
   * Signifies that the operand's children must precisely match its
   * child operands, in order.
   * 表示操作数的子操作数必须按顺序精确匹配其子操作数。
   */
  SOME,

  /**
   * Signifies that the rule matches any one of its parents' children.
   * The parent may have one or more children.
   * 表示该规则匹配其任何父元素的子元素。父母可能有一个或多个子女。
   */
  UNORDERED,
}

// End RelOptRuleOperandChildPolicy.java
