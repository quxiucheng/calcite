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
package org.apache.calcite.config;

import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;

/** Named, built-in lexical policy. A lexical policy describes how
 * identifiers are quoted, whether they are converted to upper- or
 * lower-case when they are read, and whether they are matched
 * case-sensitively.
 * 命名，内置词汇策略。
 * 词汇策略描述如何引用标识符，
 * 在读取标识符时是否将其转换为大写或小写，
 * 以及是否区分大小写进行匹配。
 *
 * */
public enum Lex {
  /** Lexical policy similar to Oracle. The case of identifiers enclosed in
   * double-quotes is preserved; unquoted identifiers are converted to
   * upper-case; after which, identifiers are matched case-sensitively.
   * 类似于Oracle的词汇策略。保留双引号括起的标识符的情况;非引号标识符转换为大写;然后，区分大小写地匹配标识符
   * */
  ORACLE(Quoting.DOUBLE_QUOTE, Casing.TO_UPPER, Casing.UNCHANGED, true),

  /** Lexical policy similar to MySQL. (To be precise: MySQL on Windows;
   * MySQL on Linux uses case-sensitive matching, like the Linux file system.)
   * The case of identifiers is preserved whether or not they quoted;
   * after which, identifiers are matched case-insensitively.
   * Back-ticks allow identifiers to contain non-alphanumeric characters.
   * 类似于MySQL的词汇策略。
   * (准确地说:Windows上的MySQL;MySQL在Linux上使用区分大小写的匹配，就像Linux文件系统一样。标识符无论是否引用，其情况都得到保留;之后，
   * 标识符被不敏感地匹配大小写。反勾号允许标识符包含非字母数字字符。
   * */
  MYSQL(Quoting.BACK_TICK, Casing.UNCHANGED, Casing.UNCHANGED, false),

  /** Lexical policy similar to MySQL with ANSI_QUOTES option enabled. (To be
   * precise: MySQL on Windows; MySQL on Linux uses case-sensitive matching,
   * like the Linux file system.) The case of identifiers is preserved whether
   * or not they quoted; after which, identifiers are matched
   * case-insensitively. Double quotes allow identifiers to contain
   * non-alphanumeric characters.
   * 类似于MySQL的词汇策略，支持ANSI_QUOTES选项。(准确地说:Windows上的MySQL;MySQL在Linux上使用区分大小写的匹配，就像Linux文件系统一样。
   * 标识符无论是否引用，其情况都得到保留;之后，标识符被不敏感地匹配大小写。双引号允许标识符包含非字母数字字符。
   * */
  MYSQL_ANSI(Quoting.DOUBLE_QUOTE, Casing.UNCHANGED, Casing.UNCHANGED, false),

  /** Lexical policy similar to Microsoft SQL Server.
   * The case of identifiers is preserved whether or not they are quoted;
   * after which, identifiers are matched case-insensitively.
   * Brackets allow identifiers to contain non-alphanumeric characters.
   * 类似于Microsoft SQL Server的词汇策略。无论标识符是否被引用，标识符的情况都被保留;之后，标识符被不敏感地匹配大小写。方括号允许标识符包含非字母数字字符
   * */
  SQL_SERVER(Quoting.BRACKET, Casing.UNCHANGED, Casing.UNCHANGED, false),

  /** Lexical policy similar to Java.
   * The case of identifiers is preserved whether or not they are quoted;
   * after which, identifiers are matched case-sensitively.
   * Unlike Java, back-ticks allow identifiers to contain non-alphanumeric
   * characters.
   * 类似于Java的词汇策略。无论标识符是否被引用，标识符的情况都被保留;然后，区分大小写地匹配标识符。与Java不同，反勾号允许标识符包含非字母数字字符
   * */
  JAVA(Quoting.BACK_TICK, Casing.UNCHANGED, Casing.UNCHANGED, true);

  public final Quoting quoting;
  public final Casing unquotedCasing;
  public final Casing quotedCasing;
  public final boolean caseSensitive;

  Lex(Quoting quoting,
      Casing unquotedCasing,
      Casing quotedCasing,
      boolean caseSensitive) {
    this.quoting = quoting;
    this.unquotedCasing = unquotedCasing;
    this.quotedCasing = quotedCasing;
    this.caseSensitive = caseSensitive;
  }
}

// End Lex.java
