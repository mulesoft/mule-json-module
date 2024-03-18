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

import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.internal.ValidatorCommonUtils.isBlank;
import static com.fasterxml.jackson.core.JsonParser.Feature.STRICT_DUPLICATE_DETECTION;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.google.common.base.Preconditions.checkArgument;
import static org.slf4j.LoggerFactory.getLogger;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.extension.api.exception.ModuleException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

/**
 * @since 1.0
 */
public abstract class JsonSchemaValidator {

  private static final Logger logger = getLogger(JsonSchemaValidator.class);
  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";
  private final ObjectMapper objectMapper;
  private static final String RESOURCE_PREFIX = "resource:/";
  private String schemaLocation;
  private JsonSchemaDereferencingMode dereferencing;

  protected static final String VALIDATION_FAILED_MESSAGE = "Json content is not compliant with schema.\n";
  protected static final String INVALID_SCHEMA_REFERENCE = "Invalid Schema References";
  protected static final String ERROR_TRYING_TO_VALIDATE =
      "Exception was found while trying to validate against json schema. Content was: ";
  protected static final String SCHEMA_NOT_FOUND_MSG = "Could not load JSON schema";

  /**
   * Allows to redirect any given URI in the Schema (or even the schema location itself)
   * to any other specific URI. The most common use case for this feature is to map external
   * namespace URIs without the need to a local resource
   */
  private final Map<String, String> schemaRedirects = new HashMap<>();

  protected JsonSchemaValidator(ValidatorKey key) {

    checkArgument(key.getDereferencingType() != null, "dereferencing cannot be null");

    this.schemaLocation = key.getSchemas();
    this.dereferencing = key.getDereferencingType();
    objectMapper = new ObjectMapper();

    if (!key.isAllowDuplicateKeys()) {
      objectMapper.enable(STRICT_DUPLICATE_DETECTION);
    }

    if (key.isAllowArbitraryPrecision()) {
      objectMapper.enable(USE_BIG_DECIMAL_FOR_FLOATS);
    }

    if (Boolean.parseBoolean(System.getProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "false"))) {
      objectMapper.enable(FAIL_ON_TRAILING_TOKENS);
    }

    for (Map.Entry<String, String> redirect : key.getSchemaRedirects().entrySet()) {
      checkArgument(!isBlank(redirect.getKey()), "from cannot be null or blank");
      checkArgument(!isBlank(redirect.getValue()), "to cannot be null or blank");
      schemaRedirects.put(formatUri(redirect.getKey()), formatUri(redirect.getValue()));
    }
  }

  public abstract void validate(InputStream inputStream);

  public String getSchemaLocation() {
    return schemaLocation;
  }

  public Map<String, String> getSchemaRedirects() {
    return schemaRedirects;
  }

  public JsonSchemaDereferencingMode getDereferencing() {
    return dereferencing;
  }

  public JsonNode asJsonNode(InputStream input) {
    try {
      return objectMapper.readTree(input);
    } catch (Exception e) {
      logger.error(e.getMessage());
      throw new ModuleException("Invalid Input Content: " + e.getMessage(), INVALID_INPUT_JSON);
    }
  }

  private String formatUri(String location) {
    URI uri = URI.create(location);

    if (uri.getScheme() == null) {
      if (location.charAt(0) == '/') {
        location = location.substring(1);
      }

      location = RESOURCE_PREFIX + location;
    }

    return location;
  }
}
