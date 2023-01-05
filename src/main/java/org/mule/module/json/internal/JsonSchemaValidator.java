/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.module.json.api.JsonSchemaDereferencingMode.CANONICAL;
import static org.mule.module.json.internal.ValidatorCommonUtils.*;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;
import static java.lang.String.format;
import static com.networknt.schema.SpecVersion.VersionFlag.*;
import static com.fasterxml.jackson.core.JsonParser.Feature.STRICT_DUPLICATE_DETECTION;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.module.json.internal.cleanup.JsonModuleResourceReleaser;
import org.mule.module.json.internal.error.SchemaValidationException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.SpecVersionDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates json payloads against json schemas compliant with drafts v3 and v4.
 * <p/>
 * Instances are immutable and thread-safe. Correct way of instantiating this class
 * is by invoking {@link #builder()} to obtain a {@link Builder}
 *
 * @since 1.0.0
 */
public class JsonSchemaValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonModuleResourceReleaser.class);
  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";
  private final ObjectMapper objectMapper;
  private final Validator validator;

  private JsonSchemaValidator(ObjectMapper objectMapper, Validator validator) {
    this.objectMapper = objectMapper;
    this.validator = validator;
  }

  /**
   * Validates the {@code input} json against the given schema.
   * <p/>
   * If the validation fails, a {@link SchemaValidationException} is thrown.
   *
   * @param input the json to be validated
   */
  public void validate(InputStream input) {

    if (Boolean.parseBoolean(System.getProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "false"))) {
      objectMapper.enable(FAIL_ON_TRAILING_TOKENS);
    }

    JsonNode jsonNode = asJsonNode(input, objectMapper);
    validator.validate(jsonNode, objectMapper);
  }

  /**
   * An implementation of the builder design pattern to create
   * instances of {@link JsonSchemaValidator}.
   * This builder can be safely reused, returning a different
   * instance each time {@link #()} is invoked.
   * It is mandatory to invoke with a valid value one of the methods that allow setting a value for the schema to be validated against
   * {@link #setSchemaLocation(String)} or {@link #setSchemaContent(String)}
   * before attempting to {@link #()} an instance
   *
   * @since 1.0
   */
  public static final class Builder {

    private static final String RESOURCE_PREFIX = "resource:/";
    private String schemaLocation;
    private JsonSchemaDereferencingMode dereferencing = CANONICAL;
    private final Map<String, String> schemaRedirects = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaContent;
    private Validator validator;

    public Builder setValidator(Validator validator) {
      this.validator = validator;
      return this;
    }

    /**
     * A location in which the json schema is present. It allows both local and external resources. For example, all of the following are valid:
     * <li>
     * <ul>schemas/schema.json</ul>
     * <ul>/schemas/schema.json</ul>
     * <ul>resource:/schemas/schema.json</ul>
     * <ul>http://mule.org/schemas/schema.json</ul>
     * </li>
     *
     * @param schemaLocation the location of the schema to validate against
     * @return this builder
     */
    public Builder setSchemaLocation(String schemaLocation) {
      this.schemaLocation = schemaLocation;
      return this;
    }

    /**
     * Sets the dereferencing mode to be used. If not invoked, then
     * it defaults to {@link JsonSchemaDereferencingMode#CANONICAL}
     *
     * @param dereferencing a dereferencing mode
     * @return this builder
     * @throws IllegalArgumentException if {@code dereferencing} is {@code null}
     */
    public Builder setDereferencing(JsonSchemaDereferencingMode dereferencing) {
      checkArgument(dereferencing != null, "dereferencing cannot be null");
      this.dereferencing = dereferencing;
      return this;
    }

    /**
     * Determines whether the validator should fail when the document contains duplicate keys.
     *
     * @param allowDuplicateKeys: if true, the validator will allow duplicate keys, otherwise it will fail.
     * @return this builder.
     */
    public Builder allowDuplicateKeys(boolean allowDuplicateKeys) {
      if (!allowDuplicateKeys) {
        objectMapper.enable(STRICT_DUPLICATE_DETECTION);
      }
      return this;
    }

    /**
     * Determines whether the validator use arbitrary precision when reading floating points values.
     *
     * @param allowArbitraryPrecision: if true, the validator will use arbitrary precision for floating point values.
     * @return this builder.
     */
    public Builder allowArbitraryPrecision(boolean allowArbitraryPrecision) {
      if (allowArbitraryPrecision) {
        objectMapper.enable(USE_BIG_DECIMAL_FOR_FLOATS);
      }
      return this;
    }

    /**
     * Allows to redirect any given URI in the Schema (or even the schema location itself)
     * to any other specific URI. The most common use case for this feature is to map external
     * namespace URIs without the need to a local resource
     *
     * @param from the location to redirect. Accepts the same formats as {@link #setSchemaLocation(String)}
     * @param to   the location to redirect to. Accepts the same formats as {@link #setSchemaLocation(String)}
     * @return this builder
     * @throws IllegalArgumentException if {@code from} or {@code to} are blank or {@code null}
     */
    public Builder addSchemaRedirect(String from, String to) {
      checkArgument(!isBlank(from), "from cannot be null or blank");
      checkArgument(!isBlank(to), "to cannot be null or blank");
      schemaRedirects.put(formatUri(from), formatUri(to));

      return this;
    }

    /**
     * Allows adding many redirects following the same rules as {@link #addSchemaRedirect(String, String)}
     *
     * @param redirects a {@link Map} with redirections
     * @return this builder
     * @throws IllegalArgumentException if {@code redirects} is {@code null}
     */
    public Builder addSchemaRedirects(Map<String, String> redirects) {
      for (Map.Entry<String, String> redirect : redirects.entrySet()) {
        addSchemaRedirect(redirect.getKey(), redirect.getValue());
      }

      return this;
    }

    /**
     * @param schemaContent the content of the schema against which it is validated
     * @return this builder
     */
    public Builder setSchemaContent(String schemaContent) {
      this.schemaContent = schemaContent;
      return this;
    }

    /**
     * Builds a new instance per the given configuration. This method can be
     * safely invoked many times, returning a different instance each.
     * <p>
     * There is a mutually exclusive relationship between the attributes {@link #schemaLocation} and {@link #schemaContent}.
     * Only one is allowed at a time, you cannot use both at the same time.
     *
     * @return a {@link JsonSchemaValidator}
     * @throws ModuleException
     * @throws MuleRuntimeException
     */

    public JsonSchemaValidator build() {
      try {
        return new JsonSchemaValidator(objectMapper, validator);
      } catch (ModuleException e) {
        throw e;
      } catch (Exception e) {
        throw new MuleRuntimeException(createStaticMessage("Could not initialise JsonSchemaValidator"), e);
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

  ///// ENDS BUILDER LOGIC

  /**
   * Returns a new {@link Builder}
   *
   * @return a {@link Builder}
   */
  public static Builder builder() {
    return new Builder();
  }
}