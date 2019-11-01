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
package org.apache.calcite.sql;

import org.apache.calcite.jdbc.CalcitePrepare;
import org.apache.calcite.linq4j.function.Experimental;

/**
 * Mix-in interface for {@link SqlNode} that allows DDL commands to be
 * executed directly.
 *
 * <p>NOTE: Subject to change without notice.
 *
 * 用于SqlNode的混合接口，允许直接执行DDL命令。
 * 注:如有更改，恕不另行通知。
 */
@Experimental
public interface SqlExecutableStatement {
  void execute(CalcitePrepare.Context context);
}

// End SqlExecutableStatement.java
