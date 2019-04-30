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
 * Defines the set of standard SQL row-level functions and
 * operators.
 *
 * <p>The standard set of row-level functions and operators are declared in
 * class {@link org.apache.calcite.sql.fun.SqlStdOperatorTable}. Anonymous inner
 * classes within that table are allowed only for specifying an operator's test
 * function; if other custom code is needed for an operator, it should be
 * implemented in a top-level class within this package instead.  Operators
 * which are not row-level (e.g. select and join) should be defined in package
 * {@link org.apache.calcite.sql} instead.</p>
 *
 * 定义一组标准SQL行级函数和操作符。
 行级函数和操作符的标准集在类SqlStdOperatorTable中声明。
 该表中的匿名内部类只允许指定操作符的测试函数;
 如果操作符需要其他自定义代码，则应在此包中的顶级类中实现。
 非行级操作符(例如select和join)应在包org.apache.calcite中定义。
 sql。
 */
@PackageMarker
package org.apache.calcite.sql.fun;

import org.apache.calcite.avatica.util.PackageMarker;

// End package-info.java
