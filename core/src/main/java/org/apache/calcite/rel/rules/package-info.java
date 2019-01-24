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

/**
 * Provides a core set of planner rules.
 *
 * <p>Consider this package to be the "standard library" of planner rules.
 * Most of the common rewrites that you would want to perform on logical
 * relational expressions, or generically on any data source, are present,
 * and have been well tested.
 *
 * <p>Of course, the library is never complete, and contributions are welcome.
 *
 * <p>Not present are rules specific to a particular data source: look in that
 * data source's adapter.
 *
 * <p>Also out of the scope of this package are rules that support a particular
 * operation, such as decorrelation or recognizing materialized views. Those are
 * defined along with the algorithm.
 *
 * <p>For
 *
 * <h2>Related packages and classes</h2>
 * <ul>
 *    <li>Package<code> <a href="../../sql/package-summary.html">
 *        org.apache.calcite.sql</a></code>
 *        is an object model for SQL expressions</li>
 *    <li>Package<code> <a href="../../rex/package-summary.html">
 *        org.apache.calcite.rex</a></code>
 *        is an object model for relational row expressions</li>
 *    <li>Package<code> <a href="../../plan/package-summary.html">
 *        org.apache.calcite.plan</a></code>
 *        provides an optimizer interface.</li>
 * </ul>
 *
 * 提供计划器规则的核心集。

 把这个包看作计划器规则的“标准库”。您希望在逻辑关系表达式上执行的或在任何数据源上执行的大多数常见重写都已经出现，并且经过了良好的测试。


 当然，图书馆永远不可能是完整的，我们欢迎您的贡献。


 不存在特定于特定数据源的规则:请查看该数据源的适配器。


 这个包的作用域之外还有支持特定操作的规则，例如反关系或识别物化视图。这些是和算法一起定义的。


 相关包和类

 包org.apache.calcite.sql是sql表达式的对象模型

 包org.apache.calcite.rex是关系行表达式的对象模型

 包org.apache.calcite.plan提供了一个优化器接口。
 */
@PackageMarker
package org.apache.calcite.rel.rules;

import org.apache.calcite.avatica.util.PackageMarker;

// End package-info.java
