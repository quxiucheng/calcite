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

import org.apache.calcite.plan.CommonRelSubExprRule;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * HepProgramBuilder creates instances of {@link HepProgram}.
 */
public class HepProgramBuilder {
  //~ Instance fields --------------------------------------------------------

  private final List<HepInstruction> instructions = new ArrayList<>();

  private HepInstruction.BeginGroup group;

  //~ Constructors -----------------------------------------------------------

  /**
   * Creates a new HepProgramBuilder with an initially empty program. The
   * program under construction has an initial match order of
   * {@link HepMatchOrder#ARBITRARY}, and an initial match limit of
   * {@link HepProgram#MATCH_UNTIL_FIXPOINT}.
   *
   * 使用初始空程序创建一个新的HepProgramBuilder。正在构建的程序的初始匹配顺序为HepMatchOrder。任意的，并且是HepProgram.MATCH_UNTIL_FIXPOINT的初始匹配限制。
   */
  public HepProgramBuilder() {
  }

  //~ Methods ----------------------------------------------------------------

  private void clear() {
    instructions.clear();
    group = null;
  }

  /**
   * Adds an instruction to attempt to match any rules of a given class. The
   * order in which the rules within a class will be attempted is arbitrary,
   * so if more control is needed, use addRuleInstance instead.
   *
   * <p>Note that when this method is used, it is also necessary to add the
   * actual rule objects of interest to the planner via
   * {@link RelOptPlanner#addRule}. If the planner does not have any
   * rules of the given class, this instruction is a nop.
   *
   * <p>TODO: support classification via rule annotations.
   *
   * @param ruleClass class of rules to fire, e.g. ConverterRule.class
   *
  添加一条指令，以尝试匹配给定类的任何规则。尝试类中的规则的顺序是任意的，因此如果需要更多的控制，请使用addRuleInstance。

  注意，在使用此方法时，还需要通过RelOptPlanner.addRule(org.apache.calcite.plan.RelOptRule)向planner添加实际感兴趣的规则对象。如果计划器没有给定类的任何规则，则此指令为nop。


  TODO:通过规则注释支持分类。
   */
  public <R extends RelOptRule> HepProgramBuilder addRuleClass(
      Class<R> ruleClass) {
    HepInstruction.RuleClass instruction =
        new HepInstruction.RuleClass<R>();
    instruction.ruleClass = ruleClass;
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to attempt to match any rules in a given collection.
   * The order in which the rules within a collection will be attempted is
   * arbitrary, so if more control is needed, use addRuleInstance instead. The
   * collection can be "live" in the sense that not all rule instances need to
   * have been added to it at the time this method is called. The collection
   * contents are reevaluated for each execution of the program.
   *
   * <p>Note that when this method is used, it is NOT necessary to add the
   * rules to the planner via {@link RelOptPlanner#addRule}; the instances
   * supplied here will be used. However, adding the rules to the planner
   * redundantly is good form since other planners may require it.
   *
   * @param rules collection of rules to fire
   *
  添加一条指令，以尝试匹配给定集合中的任何规则。在集合中尝试规则的顺序是任意的，因此如果需要更多的控制，则使用addRuleInstance。集合可以是“活动的”，因为在调用此方法时，并不需要将所有规则实例添加到集合中。对程序的每次执行重新计算集合内容。

  注意，在使用此方法时，没有必要通过RelOptPlanner.addRule(org.apache.calcite.plan.RelOptRule)将规则添加到计划器中;这里提供的实例将被使用。然而，将规则冗余地添加到计划器中是一种很好的形式，因为其他计划器可能需要它。
   */
  public HepProgramBuilder addRuleCollection(Collection<RelOptRule> rules) {
    HepInstruction.RuleCollection instruction =
        new HepInstruction.RuleCollection();
    instruction.rules = rules;
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to attempt to match a specific rule object.
   *
   * <p>Note that when this method is used, it is NOT necessary to add the
   * rule to the planner via {@link RelOptPlanner#addRule}; the instance
   * supplied here will be used. However, adding the rule to the planner
   * redundantly is good form since other planners may require it.
   *
   * @param rule rule to fire
   *
  添加一条指令，以尝试匹配特定的规则对象。

  注意，在使用此方法时，没有必要通过RelOptPlanner.addRule(org.apache.calcite.plan.RelOptRule)将规则添加到计划器中;将使用这里提供的实例。然而，将规则冗余地添加到计划器中是一种很好的形式，因为其他计划器可能需要它。
   */
  public HepProgramBuilder addRuleInstance(RelOptRule rule) {
    HepInstruction.RuleInstance instruction =
        new HepInstruction.RuleInstance();
    instruction.rule = rule;
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to attempt to match a specific rule identified by its
   * unique description.
   *
   * <p>Note that when this method is used, it is necessary to also add the
   * rule object of interest to the planner via {@link RelOptPlanner#addRule}.
   * This allows for some decoupling between optimizers and plugins: the
   * optimizer only knows about rule descriptions, while the plugins supply
   * the actual instances. If the planner does not have a rule matching the
   * description, this instruction is a nop.
   *
   *
   添加一条指令，以尝试匹配由其唯一描述标识的特定规则。

   请注意，在使用此方法时，还需要通过RelOptPlanner.addRule(org.apache.calcite.plan.RelOptRule)向planner添加感兴趣的规则对象。这允许在优化器和插件之间进行一些解耦:优化器只知道规则描述，而插件提供实际的实例。如果计划器没有与描述匹配的规则，则此指令为nop。
   * @param ruleDescription description of rule to fire
   */
  public HepProgramBuilder addRuleByDescription(String ruleDescription) {
    HepInstruction.RuleInstance instruction =
        new HepInstruction.RuleInstance();
    instruction.ruleDescription = ruleDescription;
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to begin a group of rules. All subsequent rules added
   * (until the next endRuleGroup) will be collected into the group rather
   * than firing individually. After addGroupBegin has been called, only
   * addRuleXXX methods may be called until the next addGroupEnd.
   *
   * 添加开始一组规则的指令。所有随后添加的规则(直到下一个endRuleGroup)将被收集到组中，而不是单独触发。在调用addGroupBegin之后，在下一个addGroupEnd之前，只能调用addRuleXXX方法。
   */
  public HepProgramBuilder addGroupBegin() {
    assert group == null;
    HepInstruction.BeginGroup instruction = new HepInstruction.BeginGroup();
    instructions.add(instruction);
    group = instruction;
    return this;
  }

  /**
   * Adds an instruction to end a group of rules, firing the group
   * collectively. The order in which the rules within a group will be
   * attempted is arbitrary. Match order and limit applies to the group as a
   * whole.
   * 添加结束一组规则的指令，集体触发该组。尝试组内规则的顺序是任意的。匹配顺序和限制适用于整个组。
   */
  public HepProgramBuilder addGroupEnd() {
    assert group != null;
    HepInstruction.EndGroup instruction = new HepInstruction.EndGroup();
    instructions.add(instruction);
    group.endGroup = instruction;
    group = null;
    return this;
  }

  /**
   * Adds an instruction to attempt to match instances of
   * {@link org.apache.calcite.rel.convert.ConverterRule},
   * but only where a conversion is actually required.
   *
   * @param guaranteed if true, use only guaranteed converters; if false, use
   *                   only non-guaranteed converters
   * 添加一条指令，以尝试匹配ConverterRule的实例，但仅在实际需要转换时才匹配。
   */
  public HepProgramBuilder addConverters(boolean guaranteed) {
    assert group == null;
    HepInstruction.ConverterRules instruction =
        new HepInstruction.ConverterRules();
    instruction.guaranteed = guaranteed;
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to attempt to match instances of
   * {@link CommonRelSubExprRule}, but only in cases where vertices have more
   * than one parent.
   *
   * 添加一条指令，以尝试匹配CommonRelSubExprRule的实例，但仅在顶点有多个父节点的情况下。
   */
  public HepProgramBuilder addCommonRelSubExprInstruction() {
    assert group == null;
    HepInstruction.CommonRelSubExprRules instruction =
        new HepInstruction.CommonRelSubExprRules();
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to change the order of pattern matching for
   * subsequent instructions. The new order will take effect for the rest of
   * the program (not counting subprograms) or until another match order
   * instruction is encountered.
   *
   * @param order new match direction to set
   *
   * 添加一条指令以更改后续指令的模式匹配顺序。新顺序将对程序的其余部分(不包括子程序)或直到遇到另一条匹配顺序指令为止生效。
   */
  public HepProgramBuilder addMatchOrder(HepMatchOrder order) {
    assert group == null;
    HepInstruction.MatchOrder instruction = new HepInstruction.MatchOrder();
    instruction.order = order;
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to limit the number of pattern matches for subsequent
   * instructions. The limit will take effect for the rest of the program (not
   * counting subprograms) or until another limit instruction is encountered.
   *
   * @param limit limit to set; use {@link HepProgram#MATCH_UNTIL_FIXPOINT} to
   *              remove limit
   * 添加一条指令，以限制后续指令的模式匹配数量。该限制将对程序的其余部分(不包括子程序)或直到遇到另一条限制指令时才生效。
   */
  public HepProgramBuilder addMatchLimit(int limit) {
    assert group == null;
    HepInstruction.MatchLimit instruction = new HepInstruction.MatchLimit();
    instruction.limit = limit;
    instructions.add(instruction);
    return this;
  }

  /**
   * Adds an instruction to execute a subprogram. Note that this is different
   * from adding the instructions from the subprogram individually. When added
   * as a subprogram, the sequence will execute repeatedly until a fixpoint is
   * reached, whereas when the instructions are added individually, the
   * sequence will only execute once (with a separate fixpoint for each
   * instruction).
   *
   * <p>The subprogram has its own state for match order and limit
   * (initialized to the defaults every time the subprogram is executed) and
   * any changes it makes to those settings do not affect the parent program.
   *
   * @param program subprogram to execute
   *
  添加执行子程序的指令。注意，这与单独添加子程序中的指令不同。当作为子程序添加时，序列将重复执行，直到到达一个固定点，而当单独添加指令时，序列只执行一次(每个指令有一个单独的固定点)。

  子程序有自己的匹配顺序和限制状态(每次执行子程序时初始化为默认值)，它对这些设置的任何更改都不会影响父程序。
   */
  public HepProgramBuilder addSubprogram(HepProgram program) {
    assert group == null;
    HepInstruction.Subprogram instruction = new HepInstruction.Subprogram();
    instruction.subprogram = program;
    instructions.add(instruction);
    return this;
  }

  /**
   * Returns the constructed program, clearing the state of this program
   * builder as a side-effect.
   *
   * @return immutable program
   */
  public HepProgram build() {
    assert group == null;
    HepProgram program = new HepProgram(instructions);
    clear();
    return program;
  }
}

// End HepProgramBuilder.java
