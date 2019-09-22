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
package org.apache.calcite.plan.hep;

/**
 * HepMatchOrder specifies the order of graph traversal when looking for rule
 * matches.
 * HepMatchOrder指定在查找规则匹配时图的遍历顺序。
 */
public enum HepMatchOrder {
  /**
   * Match in arbitrary order. This is the default because it is
   * efficient, and most rules don't care about order.
   * 以任意顺序匹配。这是默认值，因为它是有效的，而且大多数规则不关心顺序。
   */
  ARBITRARY,

  /**
   * Match from leaves up. A match attempt at a descendant precedes all match
   * attempts at its ancestors.
   * 从叶子上配对。对后代的匹配尝试先于对其祖先的所有匹配尝试。
   */
  BOTTOM_UP,

  /**
   * Match from root down. A match attempt at an ancestor always precedes all
   * match attempts at its descendants.
   * 从根开始匹配。对祖先的匹配尝试总是先于对其后代的所有匹配尝试。
   */
  TOP_DOWN,

  /**
   * Match in depth-first order.
   *
   * <p>It avoids applying a rule to the previous
   * {@link org.apache.calcite.rel.RelNode} repeatedly after new vertex is
   * generated in one rule application. It can therefore be more efficient than
   * {@link #ARBITRARY} in cases such as
   * {@link org.apache.calcite.rel.core.Union} with large fan-out.
   *
   * 深度优先匹配。

   它避免在一个规则应用程序中生成新顶点后，对上一个RelNode重复应用一个规则。因此，在具有大扇出的Union等情况下，它可能比任意情况下更有效。
   */
  DEPTH_FIRST
}

// End HepMatchOrder.java
