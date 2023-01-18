/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.internal.ValidatorCommonUtils.resolveLocationIfNecessary;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;
import static java.lang.String.format;

import com.networknt.schema.JsonSchemaException;
import org.mule.module.json.internal.error.SchemaValidationException;
import org.mule.runtime.api.exception.MuleRuntimeException;
import org.mule.runtime.extension.api.exception.ModuleException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.SpecVersionDetector;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.SchemaValidatorsConfig;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This Wrapper make validations with library com.networknt
 * Supports Json schema validations with version Draft V6, V7, 2019-09 and 2020-12
 */
public class JsonSchemaValidatorNetworkntWrapper extends JsonSchemaValidator {

  public static final String VALIDATION_FAILED_MESSAGE = "Json content is not compliant with schema.\n";

  private final JsonSchema jsonSchema;

  public JsonSchemaValidatorNetworkntWrapper(ValidatorKey key, JsonNode jsonSchemaNode) {
    super(key);
    jsonSchema = loadSchemaLibrary(jsonSchemaNode, super.getSchemaLocation(), super.getSchemaRedirects());
  }

  @Override
  public void validate(InputStream inputStream) {
    JsonNode jsonNode = super.asJsonNode(inputStream);
    Set<ValidationMessage> responseValidate;
    try {
      responseValidate = jsonSchema.validate(jsonNode);
    } catch (JsonSchemaException e) {
      if (e.getMessage().contains("Reference") && e.getMessage().contains("cannot be resolved")) {
        //TODO We must create a new error: INVALID_REFERENCE, to inform the user that the external references declared in the Schema cannot be accessed(W-12301483)
        throw new MuleRuntimeException(createStaticMessage("Invalid Schema References"), e);
      } else {
        throw new MuleRuntimeException(createStaticMessage(
                                                           "Exception was found while trying to validate against json schema. Content was: "
                                                               + jsonNode.toString()),
                                       e);
      }
    }

    if (!responseValidate.isEmpty()) {
      throw new SchemaValidationException(VALIDATION_FAILED_MESSAGE + responseValidate,
                                          responseValidate.toString());
    }
  }

  /**
   * Load Schema for com.networknt( Draft 6, 7, 2019-09 & 2020-12)
   * If the schema comes from a TEXT, is loaded with a jsonNode
   * but If the schema comes from a PATH, is loaded with this location
   * because is needed to solve references (example: $ref to other schema file).
   */
  private com.networknt.schema.JsonSchema loadSchemaLibrary(JsonNode jsonNode, String schemaLocation,
                                                            Map<String, String> schemaRedirects)
      throws ModuleException {
    try {
      JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonNode));

      if (schemaLocation == null) {
        return schemaFactory.getSchema(jsonNode, getUriRedirectConfig(schemaRedirects));
      } else {
        return schemaFactory.getSchema(new URI(resolveLocationIfNecessary(schemaLocation)),
                                       getUriRedirectConfig(schemaRedirects));
      }
    } catch (URISyntaxException e) {
      throw new ModuleException(format("Could not load JSON schema [%s]. %s", schemaLocation, e.getMessage()),
                                SCHEMA_NOT_FOUND, e);
    }
  }

  private SchemaValidatorsConfig getUriRedirectConfig(Map<String, String> schemaRedirects) {

    SchemaValidatorsConfig schemaValidatorsConfig = new SchemaValidatorsConfig();
    if (!schemaRedirects.entrySet().isEmpty()) {
      Map<String, String> uriRedirects = new HashMap<>();

      for (Map.Entry<String, String> redirect : schemaRedirects.entrySet()) {
        String key = resolveLocationIfNecessary(redirect.getKey());
        String value = resolveLocationIfNecessary(redirect.getValue());
        uriRedirects.put(key, value);
      }
      schemaValidatorsConfig.setUriMappings(uriRedirects);
    }
    return schemaValidatorsConfig;
  }
}
