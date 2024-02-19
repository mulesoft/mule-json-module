/*
 * Copyright (c) 2024, Salesforce, Inc.
 * SPDX-License-Identifier: Apache-2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.mule.runtime.module.artifact.api.classloader.ClassLoaderRepository;

import java.lang.reflect.Field;

/**
 * Provides utilities related to {@link Class} instances needed for tests due to DataWeave limitations.
 */
public class ClassUtils {

  private ClassUtils() {}

  public static Field getDeclaredField(ClassLoader classLoader, String className, String fieldName)
      throws ClassNotFoundException, NoSuchFieldException {
    return classLoader.loadClass(className).getDeclaredField(fieldName);
  }

  public static ClassLoader getClassLoader(ClassLoaderRepository repository, String classLoaderId) {
    return repository.find(classLoaderId).orElse(null);
  }
}
