/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.internal.ValidatorCommonUtils.*;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;
import static com.fasterxml.jackson.core.JsonParser.Feature.STRICT_DUPLICATE_DETECTION;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.google.common.base.Preconditions.checkArgument;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.extension.api.exception.ModuleException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 1.0
 */
public abstract class JsonSchemaValidator {

  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";
  private final ObjectMapper objectMapper;
  private static final String RESOURCE_PREFIX = "resource:/";
  private String schemaLocation;
  private JsonSchemaDereferencingMode dereferencing;

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

  public void validate(InputStream inputStream) {
    // do nothing
  }

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
      throw new ModuleException(createStaticMessage("Input content was not a valid Json document"), INVALID_INPUT_JSON, e);
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
