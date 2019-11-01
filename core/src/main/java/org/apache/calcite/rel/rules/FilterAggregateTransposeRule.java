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

import org.apache.calcite.plan.Contexts;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelOptRuleOperand;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Aggregate;
import org.apache.calcite.rel.core.Aggregate.Group;
import org.apache.calcite.rel.core.Filter;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.tools.RelBuilder;
import org.apache.calcite.tools.RelBuilderFactory;
import org.apache.calcite.util.ImmutableBitSet;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Planner rule that pushes a {@link org.apache.calcite.rel.core.Filter}
 * past a {@link org.apache.calcite.rel.core.Aggregate}.
 *
 * @see org.apache.calcite.rel.rules.AggregateFilterTransposeRule
 *
 *
 *  org.apache.calcite.test.RelOptRulesTest#testPushFilterPastAgg()
filter下推
dept{ 0
  deptn o
  name 1
}

select dname, c from (
  select name dname, count(*) as c from dept group by name
) t where dname = 'Charlie'

LogicalProject(DNAME=[$0], C=[$1])
  LogicalFilter(condition=[=($0, 'Charlie')])
    LogicalAggregate(group=[{0}], C=[COUNT()])
      LogicalProject(DNAME=[$1])
        LogicalTableScan(table=[[CATALOG, SALES, DEPT]])


LogicalProject(DNAME=[$0], C=[$1])
  LogicalAggregate(group=[{0}], C=[COUNT()])
    LogicalFilter(condition=[=($0, 'Charlie')])
      LogicalProject(DNAME=[$1])
        LogicalTableScan(table=[[CATALOG, SALES, DEPT]])

sql:select dname, c from (select name dname, count(*) as c from dept where deptno=1 group by name) t where dname = 'Charlie' and c >500
LogicalProject(DNAME=[$0], C=[$1])
  LogicalFilter(condition=[AND(=($0, 'Charlie'), >($1, 500))])
    LogicalAggregate(group=[{0}], C=[COUNT()])
      LogicalProject(DNAME=[$1])
        LogicalFilter(condition=[=($0, 1)])
          LogicalTableScan(table=[[CATALOG, SALES, DEPT]])

LogicalProject(DNAME=[$0], C=[$1])
  LogicalFilter(condition=[>($1, 500)])
    LogicalAggregate(group=[{0}], C=[COUNT()])
      LogicalFilter(condition=[=($0, 'Charlie')])
        LogicalProject(DNAME=[$1])
          LogicalFilter(condition=[=($0, 1)])
            LogicalTableScan(table=[[CATALOG, SALES, DEPT]])
 */
public class FilterAggregateTransposeRule extends RelOptRule {

  /** The default instance of
   * {@link FilterAggregateTransposeRule}.
   *
   * <p>It matches any kind of agg. or filter */
  public static final FilterAggregateTransposeRule INSTANCE =
      new FilterAggregateTransposeRule(Filter.class,
          RelFactories.LOGICAL_BUILDER, Aggregate.class);

  //~ Constructors -----------------------------------------------------------

  /**
   * Creates a FilterAggregateTransposeRule.
   *
   * <p>If {@code filterFactory} is null, creates the same kind of filter as
   * matched in the rule. Similarly {@code aggregateFactory}.</p>
   */
  public FilterAggregateTransposeRule(
      Class<? extends Filter> filterClass,
      RelBuilderFactory builderFactory,
      Class<? extends Aggregate> aggregateClass) {
    this(
        operand(filterClass,
            operand(aggregateClass, any())),
        builderFactory);
  }

  protected FilterAggregateTransposeRule(RelOptRuleOperand operand,
      RelBuilderFactory builderFactory) {
    super(operand, builderFactory, null);
  }

  @Deprecated // to be removed before 2.0
  public FilterAggregateTransposeRule(
      Class<? extends Filter> filterClass,
      RelFactories.FilterFactory filterFactory,
      Class<? extends Aggregate> aggregateClass) {
    this(filterClass, RelBuilder.proto(Contexts.of(filterFactory)),
        aggregateClass);
  }

  //~ Methods ----------------------------------------------------------------

  public void onMatch(RelOptRuleCall call) {
    final Filter filterRel = call.rel(0);
    final Aggregate aggRel = call.rel(1);

    // 返回由AND分解的条件
    final List<RexNode> conditions =
        RelOptUtil.conjunctions(filterRel.getCondition());
    final RexBuilder rexBuilder = filterRel.getCluster().getRexBuilder();
    // 聚合字段类型
    final List<RelDataTypeField> origFields =
        aggRel.getRowType().getFieldList();
    // 调整
    final int[] adjustments = new int[origFields.size()];
    int j = 0;
    for (int i : aggRel.getGroupSet()) {
      adjustments[j] = i - j;
      j++;
    }
    final List<RexNode> pushedConditions = new ArrayList<>();
    final List<RexNode> remainingConditions = new ArrayList<>();

    // 判断哪些条件可以下推
    for (RexNode condition : conditions) {
      // 过滤条件所在的位置
      ImmutableBitSet rCols = RelOptUtil.InputFinder.bits(condition);
      if (canPush(aggRel, rCols)) {
        pushedConditions.add(
            condition.accept(
                new RelOptUtil.RexInputConverter(rexBuilder, origFields,
                    aggRel.getInput(0).getRowType().getFieldList(),
                    adjustments)));
      } else {
        remainingConditions.add(condition);
      }
    }

    final RelBuilder builder = call.builder();
    RelNode rel =
        builder.push(aggRel.getInput()).filter(pushedConditions).build();
    if (rel == aggRel.getInput(0)) {
      return;
    }
    rel = aggRel.copy(aggRel.getTraitSet(), ImmutableList.of(rel));
    rel = builder.push(rel).filter(remainingConditions).build();
    call.transformTo(rel);
  }

  private boolean canPush(Aggregate aggregate, ImmutableBitSet rCols) {
    // If the filter references columns not in the group key, we cannot push
    // 字段不在 group by 的字段中,无法下推
    final ImmutableBitSet groupKeys =
        ImmutableBitSet.range(0, aggregate.getGroupSet().cardinality());
    if (!groupKeys.contains(rCols)) {
      return false;
    }

    if (aggregate.getGroupType() != Group.SIMPLE) {
      // If grouping sets are used, the filter can be pushed if
      // the columns referenced in the predicate are present in
      // all the grouping sets.
      // 如果使用组集，则可以在谓词中引用的列出现在所有组集中时推入筛选器。
      for (ImmutableBitSet groupingSet : aggregate.getGroupSets()) {
        if (!groupingSet.contains(rCols)) {
          return false;
        }
      }
    }
    return true;
  }
}

// End FilterAggregateTransposeRule.java
