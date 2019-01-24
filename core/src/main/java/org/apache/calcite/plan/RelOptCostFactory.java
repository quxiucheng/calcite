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
 * Cost model for query planning.
 * 查询计划的成本模型
 */
public interface RelOptCostFactory {
  /**
   * Creates a cost object.
   * 创建成本对象。
   */
  RelOptCost makeCost(double rowCount, double cpu, double io);

  /**
   * Creates a cost object representing an enormous non-infinite cost.
   * 创建表示巨大的非无限成本的成本对象。
   */
  RelOptCost makeHugeCost();

  /**
   * Creates a cost object representing infinite cost.
   * 创建表示无限成本的成本对象。
   */
  RelOptCost makeInfiniteCost();

  /**
   * Creates a cost object representing a small positive cost.
   * 创建表示小正成本的成本对象
   */
  RelOptCost makeTinyCost();

  /**
   * Creates a cost object representing zero cost.
   * 创建表示零成本的成本对象
   */
  RelOptCost makeZeroCost();
}

// End RelOptCostFactory.java
