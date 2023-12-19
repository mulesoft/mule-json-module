/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static java.lang.String.format;
import static org.mule.module.json.api.JsonError.INVALID_SCHEMA;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.internal.ValidatorCommonUtils.resolveLocationIfNecessary;

import org.mule.module.json.internal.error.SchemaValidationException;
import org.mule.runtime.extension.api.exception.ModuleException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;
import net.jimblackler.jsonschemafriend.GenerationException;
import net.jimblackler.jsonschemafriend.Schema;
import net.jimblackler.jsonschemafriend.SchemaStore;
import net.jimblackler.jsonschemafriend.ValidationException;
import net.jimblackler.jsonschemafriend.Validator;

/**
 * This Wrapper make validations with library java.json.tools
 * Supports Json schema validations with version Draft V3 and V4
 */
public class JsonSchemaFriendWrapper extends JsonSchemaValidator {


  private final Schema jsonSchema;
  private final Validator validator;

  public JsonSchemaFriendWrapper(ValidatorKey key, JsonNode jsonSchemaNode) {
    super(key);
    jsonSchema = loadSchemaLibrary(jsonSchemaNode, super.getSchemaLocation());
    validator = new Validator();
  }

  @Override
  public void validate(InputStream inputStream) {
    try {
      validator.validate(jsonSchema, inputStream);
    } catch (ValidationException | IOException e) {
      throw new SchemaValidationException(VALIDATION_FAILED_MESSAGE + e.getMessage(), e.getMessage());
    }
  }

  private Schema loadSchemaLibrary(JsonNode jsonNode, String schemaLocation) {

    SchemaStore schemaStore = new SchemaStore();

    if (schemaLocation == null) {
      try {
        return schemaStore.loadSchema(jsonNode);

      } catch (GenerationException e) {
        throw new ModuleException("Invalid Schema", INVALID_SCHEMA, e);
      }
    } else {
      try {
        String resolvedSchemaLocation = resolveLocationIfNecessary(schemaLocation);
        return schemaStore.loadSchema(URI.create(resolvedSchemaLocation));
      } catch (GenerationException e) {
        throw new ModuleException(format(SCHEMA_NOT_FOUND_MSG + " [%s]. %s", schemaLocation, e.getMessage()),
                                  SCHEMA_NOT_FOUND, e);
      }
    }
  }
}
