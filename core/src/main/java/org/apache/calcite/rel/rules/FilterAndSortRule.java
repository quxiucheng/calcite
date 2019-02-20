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
package org.apache.calcite.rel.rules;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class FilterAndSortRule extends RelOptRule {

  public static final FilterAndSortRule INSTANCE =
          new FilterAndSortRule(operand(Filter.class, any()), "FilterAndSortRule");

  public FilterAndSortRule(RelOptRuleOperand operand, String description) {
    super(operand, description);
  }

  @Override
  public void onMatch(RelOptRuleCall call) {
    final Filter filterRel = call.rel(0);
    // 返回由AND分解的条件
    final List<RexNode> conditions =
            RelOptUtil.conjunctions(filterRel.getCondition());
    TreeMap<Integer, List<RexNode>> treeMap = Maps.newTreeMap();
    for (RexNode condition : conditions) {
      ImmutableBitSet rCols = RelOptUtil.InputFinder.bits(condition);
      int max = Integer.MIN_VALUE;
      for (Integer integer : rCols.toList()) {
        max = Math.max(max, integer);
      }

      List<RexNode> rexNodes = treeMap.get(max);
      if (rexNodes == null) {
        rexNodes = Lists.newArrayList();
      }
      rexNodes.add(condition);
      treeMap.put(max, rexNodes);
    }
    List<RexNode> result = Lists.newArrayList();
    for (Map.Entry<Integer, List<RexNode>> entry : treeMap.entrySet()) {
      result.addAll(entry.getValue());
    }
    boolean finsh = true;
    for (int i = 0; i < result.size(); i++) {
      RexNode rexNode = conditions.get(i);
      if (rexNode!=result.get(i)) {
        finsh = false;
        break;
      }
    }
    if (!finsh) {
      final RelBuilder builder = call.builder();
      RelNode rel =
              builder.push(filterRel.getInput()).filter(result).build();
      System.out.println("===");
      System.out.println(RelOptUtil.toString(rel));
      System.out.println("===");
      call.transformTo(rel);
    }

  }
}


