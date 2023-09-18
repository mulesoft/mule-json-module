/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.module.json.api.JsonError.INVALID_SCHEMA;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.api.JsonSchemaDereferencingMode.CANONICAL;
import static org.mule.module.json.internal.ValidatorCommonUtils.resolveLocationIfNecessary;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.google.common.collect.Lists.newArrayList;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.module.json.internal.error.SchemaValidationException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.extension.api.exception.ModuleException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfigurationBuilder;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * This Wrapper make validations with library java.json.tools
 * Supports Json schema validations with version Draft V3 and V4
 */
public class JsonSchemaValidatorJavaJsonToolsWrapper extends JsonSchemaValidator {

  private static final Pattern INVALID_REFERENCE_MESSAGE_PATTERN = Pattern.compile("fatal: URI.+is not absolute");

  private final JsonSchema jsonSchema;
  private final ObjectMapper objectMapper;

  public JsonSchemaValidatorJavaJsonToolsWrapper(ValidatorKey key, JsonNode jsonSchemaNode) {
    super(key);
    jsonSchema =
        loadSchemaLibrary(jsonSchemaNode, super.getSchemaLocation(), super.getSchemaRedirects(), super.getDereferencing());
    objectMapper = new ObjectMapper();
  }

  @Override
  public void validate(InputStream inputStream) {
    JsonNode jsonNode = super.asJsonNode(inputStream);
    ProcessingReport report;
    try {
      report = jsonSchema.validate(jsonNode);

    } catch (ProcessingException e) {
      Matcher messageMatcher = INVALID_REFERENCE_MESSAGE_PATTERN.matcher(e.getMessage());
      if (messageMatcher.find()) {
        //TODO We must create a new error: INVALID_REFERENCE, to inform the user that the external references declared in the Schema cannot be accessed(W-12301483)
        throw new MuleRuntimeException(createStaticMessage(INVALID_SCHEMA_REFERENCE), e);
      } else {
        throw new MuleRuntimeException(createStaticMessage(ERROR_TRYING_TO_VALIDATE
            + jsonNode.toString()),
                                       e);
      }
    }

    if (!report.isSuccess()) {
      String jsonReport = reportAsJson(report, objectMapper);
      throw new SchemaValidationException(VALIDATION_FAILED_MESSAGE + jsonReport,
                                          jsonReport);
    }
  }

  /**
   * Load Schema for java-json-tools (Draft 3 & 4)
   * If the schema comes from a TEXT, is loaded with a jsonNode
   * but If the schema comes from a PATH, is loaded with this location
   * because is needed to solve references (example: $ref to other schema file).
   */
  private JsonSchema loadSchemaLibrary(JsonNode jsonNode, String schemaLocation,
                                       Map<String, String> schemaRedirects, JsonSchemaDereferencingMode dereferencing) {
    JsonSchemaFactory factory = getFactoryAndLoadConfiguration(schemaRedirects, dereferencing);

    if (schemaLocation == null) {
      try {
        return factory.getJsonSchema(jsonNode);
      } catch (ProcessingException e) {
        throw new ModuleException("Invalid Schema", INVALID_SCHEMA, e);
      }
    } else {
      try {
        return factory.getJsonSchema(resolveLocationIfNecessary(schemaLocation));
      } catch (ProcessingException e) {
        throw new ModuleException(format(SCHEMA_NOT_FOUND_MSG + " [%s]. %s", schemaLocation, e.getMessage()),
                                  SCHEMA_NOT_FOUND, e);
      }
    }
  }

  /**
   * Get factory to create Schema instances for java-json-tools library
   */
  private JsonSchemaFactory getFactoryAndLoadConfiguration(Map<String, String> schemaRedirects,
                                                           JsonSchemaDereferencingMode dereferencing) {
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

    return JsonSchemaFactory.newBuilder()
        .setLoadingConfiguration(loadingConfiguration)
        .freeze();
  }

  private String reportAsJson(ProcessingReport report, ObjectMapper objectMapper) {
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
}
