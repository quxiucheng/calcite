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

import org.apache.calcite.rel.type.RelDataType;

import java.util.List;

/**
 * Node in a planner.
 * 规划器节点
 */
public interface RelOptNode {
  /**
   * Returns the ID of this relational expression, unique among all relational
   * expressions created since the server was started.
   *
   * 节点唯一ID
   * @return Unique ID
   */
  int getId();

  /**
   * Returns a string which concisely describes the definition of this
   * relational expression. Two relational expressions are equivalent if and
   * only if their digests are the same.
   *
   * <p>The digest does not contain the relational expression's identity --
   * that would prevent similar relational expressions from ever comparing
   * equal -- but does include the identity of children (on the assumption
   * that children have already been normalized).
   *
   * <p>If you want a descriptive string which contains the identity, call
   * {@link Object#toString()}, which always returns "rel#{id}:{digest}".
   *
   * @return Digest of this {@code RelNode}
   *
   * 返回一个字符串，该字符串简明地描述了这个关系表达式的定义。当且仅当两个关系表达式的摘要相同时，它们是等效的。
   * 摘要不包含关系表达式的标识——这将防止类似的关系表达式进行相等比较——但是包含子表达式的标识(假设子表达式已经被规范化)。
   * 如果您想要一个包含标识的描述性字符串，请调用Object.toString()，它总是返回“rel#{id}:{digest}”。
   */
  String getDigest();

  /**
   * Retrieves this RelNode's traits. Note that although the RelTraitSet
   * returned is modifiable, it <b>must not</b> be modified during
   * optimization. It is legal to modify the traits of a RelNode before or
   * after optimization, although doing so could render a tree of RelNodes
   * unimplementable. If a RelNode's traits need to be modified during
   * optimization, clone the RelNode and change the clone's traits.
   *
   * 检索此RelNode的特征。
   * 请注意，虽然返回的RelTraitSet是可修改的，但在优化过程中不能修改它。
   * 在优化之前或之后修改RelNode的特性是合法的，尽管这样做可能会导致RelNode树不可实现。
   * 如果在优化过程中需要修改RelNode的特征，克隆RelNode并更改克隆的特征。
   * @return this RelNode's trait set
   */
  RelTraitSet getTraitSet();

  // TODO: We don't want to require that nodes have very detailed row type. It
  // may not even be known at planning time.
  RelDataType getRowType();

  /**
   * Returns a string which describes the relational expression and, unlike
   * {@link #getDigest()}, also includes the identity. Typically returns
   * "rel#{id}:{digest}".
   *
   * @return String which describes the relational expression and, unlike
   *   {@link #getDigest()}, also includes the identity
   *
   * 返回描述关系表达式的字符串，与getDigest()不同，它还包含标识。
   */
  String getDescription();

  /**
   * Returns an array of this relational expression's inputs. If there are no
   * inputs, returns an empty list, not {@code null}.
   * 返回此关系表达式的输入的数组
   *
   * @return Array of this relational expression's inputs
   */
  List<? extends RelOptNode> getInputs();

  /**
   * Returns the cluster this relational expression belongs to.
   * 返回此关系表达式所属的集群。
   * @return cluster
   */
  RelOptCluster getCluster();
}

// End RelOptNode.java
