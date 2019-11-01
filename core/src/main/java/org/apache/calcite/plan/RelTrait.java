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
 * RelTrait represents the manifestation of a relational expression trait within
 * a trait definition. For example, a {@code CallingConvention.JAVA} is a trait
 * of the {@link ConventionTraitDef} trait definition.
 *
 * <h3><a id="EqualsHashCodeNote">Note about equals() and hashCode()</a></h3>
 *
 * <p>If all instances of RelTrait for a particular RelTraitDef are defined in
 * an {@code enum} and no new RelTraits can be introduced at runtime, you need
 * not override {@link #hashCode()} and {@link #equals(Object)}. If, however,
 * new RelTrait instances are generated at runtime (e.g. based on state external
 * to the planner), you must implement {@link #hashCode()} and
 * {@link #equals(Object)} for proper {@link RelTraitDef#canonize canonization}
 * of your RelTrait objects.</p>
 *
 * RelTrait表示关系表达式特征在特征定义中的表现。例如，调用约定。JAVA是传统的traitdef特征定义的特征。
 *
 * 如果一个特定RelTraitDef的RelTrait的所有实例都在枚举中定义，并且在运行时不能引入新的RelTraits，那么您不需要覆盖hashCode()和equals(Object)。
 * 但是，如果在运行时生成了新的RelTrait实例(例如，基于计划器外部的状态)，那么您必须实现hashCode()和equals(Object)来正确地对RelTrait对象进行规范化。
 */
public interface RelTrait {
  //~ Methods ----------------------------------------------------------------

  /**
   * Returns the RelTraitDef that defines this RelTrait.
   *
   * @return the RelTraitDef that defines this RelTrait
   */
  RelTraitDef getTraitDef();

  /**
   * See <a href="#EqualsHashCodeNote">note about equals() and hashCode()</a>.
   */
  int hashCode();

  /**
   * See <a href="#EqualsHashCodeNote">note about equals() and hashCode()</a>.
   */
  boolean equals(Object o);

  /**
   * Returns whether this trait satisfies a given trait.
   *
   * <p>A trait satisfies another if it is the same or stricter. For example,
   * {@code ORDER BY x, y} satisfies {@code ORDER BY x}.
   *
   * <p>A trait's {@code satisfies} relation must be a partial order (reflexive,
   * anti-symmetric, transitive). Many traits cannot be "loosened"; their
   * {@code satisfies} is an equivalence relation, where only X satisfies X.
   *
   * <p>If a trait has multiple values
   * (see {@link org.apache.calcite.plan.RelCompositeTrait})
   * a collection (T0, T1, ...) satisfies T if any Ti satisfies T.
   *
   * @param trait Given trait
   * @return Whether this trait subsumes a given trait
   *
   * 返回该特征是否满足给定的特征。

  如果一种特质与另一种特质相同或更严格，它就能满足另一种特质。例如，x的阶数，y满足x的阶数。


  特征的满足关系必须是偏序的(反身的、反对称的、传递的)。许多特征是不能“放松”的;它们的满足是等价关系，其中只有X满足X。


  如果一个特征有多个值(参见RelCompositeTrait)，那么集合(T0, T1，…)满足T(如果有Ti满足T)。
   */
  boolean satisfies(RelTrait trait);

  /**
   * Returns a succinct name for this trait. The planner may use this String
   * to describe the trait.
   */
  String toString();

  /**
   * Registers a trait instance with the planner.
   *
   * <p>This is an opportunity to add rules that relate to that trait. However,
   * typical implementations will do nothing.</p>
   *
   * @param planner Planner
   */
  void register(RelOptPlanner planner);
}

// End RelTrait.java
