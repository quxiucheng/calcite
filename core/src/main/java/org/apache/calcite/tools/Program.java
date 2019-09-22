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
package org.apache.calcite.tools;

import org.apache.calcite.plan.RelOptLattice;
import org.apache.calcite.plan.RelOptMaterialization;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;

import java.util.List;

/**
 * Program that transforms a relational expression into another relational
 * expression.
 *
 * <p>A planner is a sequence of programs, each of which is sometimes called
 * a "phase".
 * The most typical program is an invocation of the volcano planner with a
 * particular {@link org.apache.calcite.tools.RuleSet}.</p>
 *
 * 将关系表达式转换为另一个关系表达式的程序。
 *
 * 计划员是一系列程序，每个程序有时被称为“阶段”。最典型的程序是使用特定规则集调用火山计划程序。
 */
public interface Program {
  RelNode run(RelOptPlanner planner, RelNode rel,
      RelTraitSet requiredOutputTraits,
      List<RelOptMaterialization> materializations,
      List<RelOptLattice> lattices);
}

// End Program.java
