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
package org.apache.calcite.rel.convert;

import org.apache.calcite.plan.RelTraitDef;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;

/**
 * A relational expression implements the interface <code>Converter</code> to
 * indicate that it converts a physical attribute, or
 * {@link org.apache.calcite.plan.RelTrait trait}, of a relational expression
 * from one value to another.
 *
 * <p>Sometimes this conversion is expensive; for example, to convert a
 * non-distinct to a distinct object stream, we have to clone every object in
 * the input.</p>
 *
 * <p>A converter does not change the logical expression being evaluated; after
 * conversion, the number of rows and the values of those rows will still be the
 * same. By declaring itself to be a converter, a relational expression is
 * telling the planner about this equivalence, and the planner groups
 * expressions which are logically equivalent but have different physical traits
 * into groups called <code>RelSet</code>s.
 *
 * <p>In principle one could devise converters which change multiple traits
 * simultaneously (say change the sort-order and the physical location of a
 * relational expression). In which case, the method {@link #getInputTraits()}
 * would return a {@link org.apache.calcite.plan.RelTraitSet}. But for
 * simplicity, this class only allows one trait to be converted at a
 * time; all other traits are assumed to be preserved.</p>
 *
 * 关系表达式实现接口转换器，以指示它将关系表达式的物理属性或特征从一个值转换为另一个值。
 * 有时这种转换是昂贵的;例如，要将非惟一的对象流转换为惟一的对象流，我们必须克隆输入中的每个对象
 * 转换器不改变被求值的逻辑表达式;在转换之后，行数和这些行的值仍然是相同的。通过声明自己是一个转换器，关系表达式就告诉规划器这个等价性，规划器将逻辑上等价但具有不同物理特性的表达式分组为称为relset的组。
 * 原则上，可以设计同时改变多个特征的转换器(比如改变关系表达式的排序顺序和物理位置)。在这种情况下，方法getInputTraits()将返回一个RelTraitSet。但是为了简单起见，这个类一次只允许转换一个特征;所有其他的特征都被认为是保留下来的。
 *
 */
public interface Converter extends RelNode {
  //~ Methods ----------------------------------------------------------------

  /**
   * Returns the trait of the input relational expression.
   *
   * @return input trait
   * 返回输入关系表达式的特征。
   */
  RelTraitSet getInputTraits();

  /**
   * Returns the definition of trait which this converter works on.
   *
   * <p>The input relational expression (matched by the rule) must possess
   * this trait and have the value given by {@link #getInputTraits()}, and the
   * traits of the output of this converter given by {@link #getTraitSet()} will
   * have one trait altered and the other orthogonal traits will be the same.
   *
   * @return trait which this converter modifies
   *
   * 返回此转换器所处理的特征的定义。
   * 输入关系表达式(由规则匹配)必须具有这个特征，并且具有getInputTraits()给出的值，而RelOptNode.getTraitSet()给出的这个转换器的输出的特征将改变一个特征，而其他正交特征将相同。
   */
  RelTraitDef getTraitDef();

  /**
   * Returns the sole input relational expression
   *
   * @return child relational expression 孩子的关系表达式
   * 返回唯一的输入关系表达式
   */
  RelNode getInput();
}

// End Converter.java
