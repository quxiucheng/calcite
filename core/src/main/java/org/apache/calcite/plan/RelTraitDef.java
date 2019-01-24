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

import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterRule;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

/**
 * RelTraitDef represents a class of {@link RelTrait}s. Implementations of
 * RelTraitDef may be singletons under the following conditions:
 *
 * <ol>
 * <li>if the set of all possible associated RelTraits is finite and fixed (e.g.
 * all RelTraits for this RelTraitDef are known at compile time). For example,
 * the CallingConvention trait meets this requirement, because CallingConvention
 * is effectively an enumeration.</li>
 * <li>Either
 *
 * <ul>
 * <li> {@link #canConvert(RelOptPlanner, RelTrait, RelTrait)} and
 * {@link #convert(RelOptPlanner, RelNode, RelTrait, boolean)} do not require
 * planner-instance-specific information, <b>or</b></li>
 *
 * <li>the RelTraitDef manages separate sets of conversion data internally. See
 * {@link ConventionTraitDef} for an example of this.</li>
 * </ul>
 * </li>
 * </ol>
 *
 * <p>Otherwise, a new instance of RelTraitDef must be constructed and
 * registered with each new planner instantiated.</p>
 *
 * @param <T> Trait that this trait definition is based upon
 *
 * RelTraitDef表示一组RelTraits,RelTraitDef的实现在以下情况下可能是单例的:
 * 如果所有可能相关的RelTraits的集合是有限且固定的(例如，这个RelTraitDef的所有RelTraits在编译时都是已知的)
 * 例如，
 * CallingConvention特征满足这个要求，因为CallingConvention实际上是一个枚举。
 *  要么
 *  canConvert(RelOptPlanner, RelTrait, RelTrait)和convert(RelOptPlanner, RelNode, RelTrait, boolean)不需要特定于计划器实例的信息
 * RelTraitDef在内部管理独立的转换数据集。有关此示例，请参见约定的traitdef。
  否则，必须构造RelTraitDef的新实例，并在实例化的每个新计划器中注册它。
 *
 */
public abstract class RelTraitDef<T extends RelTrait> {
  //~ Instance fields --------------------------------------------------------

  /**
   * Cache of traits.
   *
   * <p>Uses weak interner to allow GC.
   */
  private final Interner<T> interner = Interners.newWeakInterner();

  //~ Constructors -----------------------------------------------------------

  protected RelTraitDef() {
  }

  //~ Methods ----------------------------------------------------------------

  /**
   * Whether a relational expression may possess more than one instance of
   * this trait simultaneously.
   *
   * <p>A subset has only one instance of a trait.</p>
   * 关系表达式是否可以同时拥有该特征的多个实例。
   * 一个子集只有一个trait的实例。
   */
  public boolean multiple() {
    return false;
  }

  /**
   * @return the specific RelTrait type associated with this RelTraitDef.
   * 与此RelTraitDef关联的特定RelTrait类型。
   */
  public abstract Class<T> getTraitClass();

  /**
   * @return a simple name for this RelTraitDef (for use in
   * {@link org.apache.calcite.rel.RelNode#explain}).
   * 这个RelTraitDef的一个简单名称(用于RelNode.explain(org.apache. calcit.relwriter))。
   */
  public abstract String getSimpleName();

  /**
   * Takes an arbitrary RelTrait and returns the canonical representation of
   * that RelTrait. Canonized RelTrait objects may always be compared using
   * the equality operator (<code>==</code>).
   *
   *
   * <p>If an equal RelTrait has already been canonized and is still in use,
   * it will be returned. Otherwise, the given RelTrait is made canonical and
   * returned.
   *
   * @param trait a possibly non-canonical RelTrait
   * @return a canonical RelTrait.
   *
   * 获取任意关系特征并返回该关系特征的规范表示。可以始终使用相等运算符(==)比较已被圣化的RelTrait对象。

  如果一个相等的关系特征已经被圣化并且仍然在使用，它将被返回。否则，将使给定的RelTrait规范化并返回。
   */
  public final T canonize(T trait) {
    if (!(trait instanceof RelCompositeTrait)) {
      assert getTraitClass().isInstance(trait)
          : getClass().getName()
          + " cannot canonize a "
          + trait.getClass().getName();
    }
    return interner.intern(trait);
  }

  /**
   * Converts the given RelNode to the given RelTrait.
   *
   * @param planner                     the planner requesting the conversion
   * @param rel                         RelNode to convert
   * @param toTrait                     RelTrait to convert to
   * @param allowInfiniteCostConverters flag indicating whether infinite cost
   *                                    converters are allowed
   * @return a converted RelNode or null if conversion is not possible
   *
   * 将给定的RelNode转换为给定的RelTrait。
   */
  public abstract RelNode convert(
      RelOptPlanner planner,
      RelNode rel,
      T toTrait,
      boolean allowInfiniteCostConverters);

  /**
   * Tests whether the given RelTrait can be converted to another RelTrait.
   *
   * @param planner   the planner requesting the conversion test
   * @param fromTrait the RelTrait to convert from
   * @param toTrait   the RelTrait to convert to
   * @return true if fromTrait can be converted to toTrait
   *
   * 测试给定的关系特征是否可以转换为另一个关系特征。
   */
  public abstract boolean canConvert(
      RelOptPlanner planner,
      T fromTrait,
      T toTrait);

  /**
   * Tests whether the given RelTrait can be converted to another RelTrait.
   *
   * @param planner   the planner requesting the conversion test
   * @param fromTrait the RelTrait to convert from
   * @param toTrait   the RelTrait to convert to
   * @param fromRel   the RelNode to convert from (with fromTrait)
   * @return true if fromTrait can be converted to toTrait
   */
  public boolean canConvert(
      RelOptPlanner planner,
      T fromTrait,
      T toTrait,
      RelNode fromRel) {
    return canConvert(planner, fromTrait, toTrait);
  }

  /**
   * Provides notification of the registration of a particular
   * {@link ConverterRule} with a {@link RelOptPlanner}. The default
   * implementation does nothing.
   *
   * @param planner       the planner registering the rule
   * @param converterRule the registered converter rule
   */
  public void registerConverterRule(
      RelOptPlanner planner,
      ConverterRule converterRule) {
  }

  /**
   * Provides notification that a particular {@link ConverterRule} has been
   * de-registered from a {@link RelOptPlanner}. The default implementation
   * does nothing.
   *
   * @param planner       the planner registering the rule
   * @param converterRule the registered converter rule
   */
  public void deregisterConverterRule(
      RelOptPlanner planner,
      ConverterRule converterRule) {
  }

  /**
   * Returns the default member of this trait.
   */
  public abstract T getDefault();
}

// End RelTraitDef.java
