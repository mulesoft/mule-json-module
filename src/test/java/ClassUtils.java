/**
 * (c) 2003-2024 MuleSoft, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1 a copy of which has been included with this distribution in the LICENSE.md file.
 */

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
public class ClassUtils {

  private ClassUtils() {}

  public static Field getDeclaredField(String className, String fieldName) {
    try {
      Class<?> type = Class.forName(className);
      return type.getDeclaredField(fieldName);
    } catch (ClassNotFoundException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  public static Field getDeclaredField(ClassLoader classLoader, String className, String fieldName) {
    try {
      Class<?> type = classLoader.loadClass(className);
      return type.getDeclaredField(fieldName);
    } catch (ClassNotFoundException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }
}
