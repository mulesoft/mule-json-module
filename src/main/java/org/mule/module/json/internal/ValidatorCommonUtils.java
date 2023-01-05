/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import java.net.URI;
import java.net.URL;

public class ValidatorCommonUtils {

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
