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
package org.mule.module.json.internal;

import java.net.URI;
import java.net.URL;

/**
 * Common methods that are useful for the other classes
 */
public class ValidatorCommonUtils {

  private ValidatorCommonUtils() {}

  public static String resolveLocationIfNecessary(String path) {
    URI uri = URI.create(path);

    String scheme = uri.getScheme();
    if (scheme == null || "resource".equals(scheme)) {
      return openSchema(uri.getPath()).toString();
    }
    return path;
  }

  public static URL openSchema(String path) {
    URL url = Thread.currentThread().getContextClassLoader().getResource(path);
    if (url == null && path.startsWith("/")) {
      url = openSchema(path.substring(1));
      if (url != null) {
        return url;
      }
    }

    if (url == null) {
      throw new IllegalArgumentException("Cannot find schema [" + path + "]");
    }

    return url;
  }

  public static boolean isBlank(String value) {
    return value == null || value.trim().length() == 0;
  }
}
