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
package org.apache.calcite.util;

/**
 * Object which can be a target for a reflective visitation (see
 * {@link ReflectUtil#invokeVisitor(ReflectiveVisitor, Object, Class, String)}.
 *
 * <p>This is a tagging interface: it has no methods, and is not even required
 * in order to use reflective visitation, but serves to advise users of the
 * class of the intended use of the class and refer them to auxilliary classes.
 *
 * 可作为反射性探视目标的对象(参见ReflectUtil)。invokeVisitor(ReflectiveVisitor, Object, Class, String)。
 * 这是一个标记接口:它没有方法，甚至在使用反射访问时也不是必需的，但是它用于向类的用户建议类的预期用途，并将其引用到辅助类。
 */
public interface ReflectiveVisitor {
}

// End ReflectiveVisitor.java
