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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  private static final Pattern INVALID_REFERENCE_MESSAGE_PATTERN = Pattern.compile("Reference.+cannot be resolved");

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
      Matcher messageMatcher = INVALID_REFERENCE_MESSAGE_PATTERN.matcher(e.getMessage());
      if (messageMatcher.find()) {
        //TODO We must create a new error: INVALID_REFERENCE, to inform the user that the external references declared in the Schema cannot be accessed(W-12301483)
        throw new MuleRuntimeException(createStaticMessage(INVALID_SCHEMA_REFERENCE), e);
      } else {
        throw new MuleRuntimeException(createStaticMessage(
                                                           ERROR_TRYING_TO_VALIDATE
                                                               + jsonNode.toString()),
                                       e);
      }
    }

    if (!responseValidate.isEmpty()) {
      throw new SchemaValidationException(responseValidate.toString());
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
      throw new ModuleException(format(SCHEMA_NOT_FOUND_MSG + " [%s]. %s", schemaLocation, e.getMessage()),
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
