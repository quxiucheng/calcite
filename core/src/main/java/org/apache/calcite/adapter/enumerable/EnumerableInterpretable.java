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
package org.apache.calcite.adapter.enumerable;

import org.apache.calcite.DataContext;
import org.apache.calcite.avatica.Helper;
import org.apache.calcite.interpreter.Compiler;
import org.apache.calcite.interpreter.InterpretableConvention;
import org.apache.calcite.interpreter.InterpretableRel;
import org.apache.calcite.interpreter.Node;
import org.apache.calcite.interpreter.Row;
import org.apache.calcite.interpreter.Sink;
import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.linq4j.AbstractEnumerable;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.tree.ClassDeclaration;
import org.apache.calcite.linq4j.tree.Expressions;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.prepare.CalcitePrepareImpl;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.convert.ConverterImpl;
import org.apache.calcite.runtime.ArrayBindable;
import org.apache.calcite.runtime.Bindable;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.runtime.Typed;
import org.apache.calcite.runtime.Utilities;
import org.apache.calcite.util.Util;

import org.codehaus.commons.compiler.CompileException;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IClassBodyEvaluator;
import org.codehaus.commons.compiler.ICompilerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

/**
 * Relational expression that converts an enumerable input to interpretable
 * calling convention.
 *
 * @see EnumerableConvention
 * @see org.apache.calcite.interpreter.BindableConvention
 */
public class EnumerableInterpretable extends ConverterImpl
    implements InterpretableRel {
  protected EnumerableInterpretable(RelOptCluster cluster, RelNode input) {
    super(cluster, ConventionTraitDef.INSTANCE,
        cluster.traitSetOf(InterpretableConvention.INSTANCE), input);
  }

  @Override public EnumerableInterpretable copy(RelTraitSet traitSet,
      List<RelNode> inputs) {
    return new EnumerableInterpretable(getCluster(), sole(inputs));
  }

  public Node implement(final InterpreterImplementor implementor) {
    final Bindable bindable = toBindable(implementor.internalParameters,
            implementor.spark, (EnumerableRel) getInput(),
        EnumerableRel.Prefer.ARRAY);
    final ArrayBindable arrayBindable = box(bindable);
    final Enumerable<Object[]> enumerable =
        arrayBindable.bind(implementor.dataContext);
    return new EnumerableNode(enumerable, implementor.compiler, this);
  }

  /**
   * 生成动态代码
   * @param parameters
   * @param spark
   * @param rel
   * @param prefer
   * @return
   */
  public static Bindable toBindable(Map<String, Object> parameters,
      CalcitePrepare.SparkHandler spark, EnumerableRel rel,
      EnumerableRel.Prefer prefer) {
    EnumerableRelImplementor relImplementor =
        new EnumerableRelImplementor(rel.getCluster().getRexBuilder(),
            parameters);

    final ClassDeclaration expr = relImplementor.implementRoot(rel, prefer);
    String s = Expressions.toString(expr.memberDeclarations, "\n", false);

    if (CalcitePrepareImpl.DEBUG) {
      Util.debugCode(System.out, s);
    }

    Hook.JAVA_PLAN.run(s);

    try {
      if (spark != null && spark.enabled()) {
        return spark.compile(expr, s);
      } else {
        return getBindable(expr, s, rel.getRowType().getFieldCount());
      }
    } catch (Exception e) {
      throw Helper.INSTANCE.wrap("Error while compiling generated Java code:\n"
          + s, e);
    }
  }

  static ArrayBindable getArrayBindable(ClassDeclaration expr, String s,
      int fieldCount) throws CompileException, IOException {
    Bindable bindable = getBindable(expr, s, fieldCount);
    return box(bindable);
  }

  static Bindable getBindable(ClassDeclaration expr, String s, int fieldCount)
      throws CompileException, IOException {
    ICompilerFactory compilerFactory;
    try {
      compilerFactory = CompilerFactoryFactory.getDefaultCompilerFactory();
    } catch (Exception e) {
      throw new IllegalStateException(
          "Unable to instantiate java compiler", e);
    }
    IClassBodyEvaluator cbe = compilerFactory.newClassBodyEvaluator();
    cbe.setClassName(expr.name);
    cbe.setExtendedClass(Utilities.class);
    cbe.setImplementedInterfaces(
        fieldCount == 1
            ? new Class[] {Bindable.class, Typed.class}
            : new Class[] {ArrayBindable.class});
    cbe.setParentClassLoader(EnumerableInterpretable.class.getClassLoader());
    if (CalcitePrepareImpl.DEBUG) {
      // Add line numbers to the generated janino class
      cbe.setDebuggingInformation(true, true, true);
    }
    return (Bindable) cbe.createInstance(new StringReader(s));
  }

  /** Converts a bindable over scalar values into an array bindable, with each
   * row as an array of 1 element. */
  static ArrayBindable box(final Bindable bindable) {
    if (bindable instanceof ArrayBindable) {
      return (ArrayBindable) bindable;
    }
    return new ArrayBindable() {
      public Class<Object[]> getElementType() {
        return Object[].class;
      }

      public Enumerable<Object[]> bind(DataContext dataContext) {
        final Enumerable<?> enumerable = bindable.bind(dataContext);
        return new AbstractEnumerable<Object[]>() {
          public Enumerator<Object[]> enumerator() {
            final Enumerator<?> enumerator = enumerable.enumerator();
            return new Enumerator<Object[]>() {
              public Object[] current() {
                return new Object[] {enumerator.current()};
              }

              public boolean moveNext() {
                return enumerator.moveNext();
              }

              public void reset() {
                enumerator.reset();
              }

              public void close() {
                enumerator.close();
              }
            };
          }
        };
      }
    };
  }

  /** Interpreter node that reads from an {@link Enumerable}.
   *
   * <p>From the interpreter's perspective, it is a leaf node. */
  private static class EnumerableNode implements Node {
    private final Enumerable<Object[]> enumerable;
    private final Sink sink;

    EnumerableNode(Enumerable<Object[]> enumerable, Compiler compiler,
        EnumerableInterpretable rel) {
      this.enumerable = enumerable;
      this.sink = compiler.sink(rel);
    }

    public void run() throws InterruptedException {
      final Enumerator<Object[]> enumerator = enumerable.enumerator();
      while (enumerator.moveNext()) {
        Object[] values = enumerator.current();
        sink.send(Row.of(values));
      }
    }
  }
}

// End EnumerableInterpretable.java
