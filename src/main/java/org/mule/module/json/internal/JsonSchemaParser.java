/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.internal.ValidatorCommonUtils.isBlank;
import static org.mule.module.json.internal.ValidatorCommonUtils.resolveLocationIfNecessary;
import static java.lang.String.format;
import static com.google.common.base.Preconditions.checkState;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.runtime.extension.api.exception.ModuleException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

/**
 * The objective is get the Json Schema, from a Path (SchemaLocation) or a String(SchemaContent), like a JsonNode.
 */
public class JsonSchemaParser {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = getLogger(JsonSchemaParser.class);

  private JsonSchemaParser() {}

  public static JsonNode getSchemaJsonNode(String schemaContent, String schemaLocation) {

    if (!isBlank(schemaContent)) {
      try {
        return objectMapper.readTree(schemaContent);
      } catch (JsonProcessingException e) {
        logger.error(e.getMessage());
        throw new ModuleException(format("Malformed Json Schema: %s", e.getMessage()), INVALID_INPUT_JSON);
      }
    }
    try {
      checkState(schemaLocation != null, "schemaLocation has not been provided");
      return objectMapper.readTree(new URL(resolveLocationIfNecessary(schemaLocation)));

    } catch (IllegalArgumentException | MalformedURLException e) {
      throw new ModuleException(format("Could not load JSON schema [%s]. %s", schemaLocation, e.getMessage()),
                                SCHEMA_NOT_FOUND, e);
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new ModuleException(format("Malformed Json Schema: %s", e.getMessage()), INVALID_INPUT_JSON);
    }
  }
}
