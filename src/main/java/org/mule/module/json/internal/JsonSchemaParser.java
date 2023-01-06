/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static com.google.common.base.Preconditions.checkState;
import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.internal.ValidatorCommonUtils.isBlank;
import static org.mule.module.json.internal.ValidatorCommonUtils.resolveLocationIfNecessary;
import static java.lang.String.format;

import org.mule.runtime.extension.api.exception.ModuleException;
import java.net.URL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSchemaParser {

  private final ObjectMapper objectMapper;

  public JsonSchemaParser() {
    objectMapper = new ObjectMapper();
  }

  /**
   * Load Json node from schema content or schema location, and make validations.
   * Decided to obtain first the JsonNode, to read the json and know what version of
   * schema is needed and then create JsonSchemaValidator with class JsonSchema of
   * java-json-tools (Draft 3 & 4) or com.networknt (Draft 6, 7, 2019-09 & 2020-12)
   */

  public JsonNode getSchemaJsonNode(String schemaContent, String schemaLocation) {

    if (!isBlank(schemaContent)) {
      try {
        return objectMapper.readTree(schemaContent);
      } catch (JsonProcessingException e) {
        throw new ModuleException("Invalid Input Content", INVALID_INPUT_JSON, e);
      }
    }
    try {
      checkState(schemaLocation != null, "schemaLocation has not been provided");
      return objectMapper.readTree(new URL(resolveLocationIfNecessary(schemaLocation)));
    } catch (Exception e) {
      throw new ModuleException(format("Could not load JSON schema [%s]. %s", schemaLocation, e.getMessage()),
                                SCHEMA_NOT_FOUND, e);
    }
  }
}