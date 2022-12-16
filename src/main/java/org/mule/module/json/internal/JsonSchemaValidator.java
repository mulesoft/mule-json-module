/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.api.JsonError.INVALID_SCHEMA;
import static org.mule.module.json.api.JsonSchemaDereferencingMode.CANONICAL;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import static com.fasterxml.jackson.core.JsonParser.Feature.STRICT_DUPLICATE_DETECTION;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_TRAILING_TOKENS;
import static com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.module.json.internal.error.SchemaValidationException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfigurationBuilder;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;

/**
 * Validates json payloads against json schemas compliant with drafts v3 and v4.
 * <p/>
 * Instances are immutable and thread-safe. Correct way of instantiating this class
 * is by invoking {@link #builder()} to obtain a {@link Builder}
 *
 * @since 1.0.0
 */
public class JsonSchemaValidator {

  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";

  private static boolean isBlank(String value) {
    return value == null || value.trim().length() == 0;
  }

  /**
   * An implementation of the builder design pattern to create
   * instances of {@link JsonSchemaValidator}.
   * This builder can be safely reused, returning a different
   * instance each time {@link #build()} is invoked.
   * with a valid value before
   * attempting to {@link #build()} an instance
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


    private Builder() {}

    /**
     * A location in which the json schema is present. It allows both local and external resources. For example, all of the following are valid:
     * <li>
     * <ul>schemas/schema.json</ul>
     * <ul>/schemas/schema.json</ul>
     * <ul>resource:/schemas/schema.json</ul>
     * <ul>http://mule.org/schemas/schema.json</ul>
     * </li>
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
     *
     * There is a mutually exclusive relationship between the attributes {@link #schemaLocation} and {@link #schemaContent}.
     * Only one is allowed at a time, you cannot use both at the same time.
     *
     * @return a {@link JsonSchemaValidator}
     * @throws ModuleException
     * @throws MuleRuntimeException
     */
    public JsonSchemaValidator build() {

      final URITranslatorConfigurationBuilder translatorConfigurationBuilder = URITranslatorConfiguration.newBuilder();
      for (Map.Entry<String, String> redirect : schemaRedirects.entrySet()) {
        String key = resolveLocationIfNecessary(redirect.getKey());
        String value = resolveLocationIfNecessary(redirect.getValue());

        translatorConfigurationBuilder.addSchemaRedirect(key, value);
      }

      final LoadingConfigurationBuilder loadingConfigurationBuilder = LoadingConfiguration.newBuilder()
          .dereferencing(dereferencing == CANONICAL
              ? Dereferencing.CANONICAL
              : Dereferencing.INLINE)
          .setURITranslatorConfiguration(translatorConfigurationBuilder.freeze());

      LoadingConfiguration loadingConfiguration = loadingConfigurationBuilder.freeze();
      JsonSchemaFactory factory = JsonSchemaFactory.newBuilder()
          .setLoadingConfiguration(loadingConfiguration)
          .freeze();

      try {
        JsonSchema schemaLoaded = isBlank(schemaLocation) ? loadSchema(schemaContent) : loadSchema(factory);
        return new JsonSchemaValidator(schemaLoaded, objectMapper);
      } catch (ModuleException e) {
        throw e;
      } catch (Exception e) {
        throw new MuleRuntimeException(createStaticMessage("Could not initialise JsonSchemaValidator"), e);
      }
    }

    private JsonSchema loadSchema(JsonSchemaFactory factory) {
      try {
        checkState(schemaLocation != null, "schemaLocation has not been provided");
        String realLocation = resolveLocationIfNecessary(formatUri(schemaLocation));
        return factory.getJsonSchema(realLocation);
      } catch (Exception e) {
        throw new ModuleException(format("Could not load JSON schema [%s]. %s", schemaLocation, e.getMessage()),
                                  SCHEMA_NOT_FOUND, e);
      }
    }

    private JsonSchema loadSchema(String schemaContent) {
      try {
        JsonNode schemaNode = JsonLoader.fromString(schemaContent);
        JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        return factory.getJsonSchema(schemaNode);
      } catch (ProcessingException e) {
        throw new ModuleException("Invalid Schema", INVALID_SCHEMA, e);
      } catch (IOException e) {
        throw new ModuleException("Invalid Input Content", INVALID_INPUT_JSON, e);
      }
    }

    private String resolveLocationIfNecessary(String path) {
      URI uri = URI.create(path);

      String scheme = uri.getScheme();
      if (scheme == null || "resource".equals(scheme)) {
        return openSchema(uri.getPath()).toString();
      }
      return path;
    }

    private URL openSchema(String path) {
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


  /**
   * Returns a new {@link Builder}
   *
   * @return a {@link Builder}
   */
  public static Builder builder() {
    return new Builder();
  }

  private final JsonSchema schema;

  private final ObjectMapper objectMapper;

  private JsonSchemaValidator(JsonSchema schema, ObjectMapper objectMapper) {
    this.schema = schema;
    this.objectMapper = objectMapper;
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

    JsonNode jsonNode = asJsonNode(input);
    ProcessingReport report;
    try {
      report = schema.validate(jsonNode, true);
    } catch (Exception e) {
      throw new MuleRuntimeException(createStaticMessage(
                                                         "Exception was found while trying to validate against json schema. Content was: "
                                                             + jsonNode.toString()),
                                     e);
    }

    if (!report.isSuccess()) {
      throw new SchemaValidationException("Json content is not compliant with schema", reportAsJson(report));
    }
  }

  private String reportAsJson(ProcessingReport report) {
    String jsonReport = "[" + newArrayList(report).stream()
        .map(p -> p.asJson().toString())
        .collect(joining(",")) + "]";

    try {
      jsonReport = objectMapper.writer(INDENT_OUTPUT).writeValueAsString(objectMapper.readTree(jsonReport));
    } catch (Exception e) {
      // if couldn't format, then keep unformatted.
    }
    return jsonReport;
  }

  private JsonNode asJsonNode(InputStream input) {
    try {
      return objectMapper.readTree(input);
    } catch (Exception e) {
      throw new ModuleException(createStaticMessage("Input content was not a valid Json document"), INVALID_INPUT_JSON, e);
    }
  }
}
