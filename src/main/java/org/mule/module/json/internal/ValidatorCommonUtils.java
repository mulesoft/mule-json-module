/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

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

  public static JsonNode asJsonNode(InputStream input, ObjectMapper objectMapper) {
    try {
      return objectMapper.readTree(input);
    } catch (Exception e) {
      throw new ModuleException(createStaticMessage("Input content was not a valid Json document"), INVALID_INPUT_JSON, e);
    }
  }
}
