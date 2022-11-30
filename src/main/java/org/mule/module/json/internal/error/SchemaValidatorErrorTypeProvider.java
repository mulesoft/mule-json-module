/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal.error;

import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.api.JsonError.INVALID_SCHEMA;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_HONOURED;
import static org.mule.module.json.api.JsonError.SCHEMA_INPUT_ERROR;
import org.mule.runtime.extension.api.annotation.error.ErrorTypeProvider;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides the error types for the validate-schema operation
 *
 * @since 1.0
 */
public class SchemaValidatorErrorTypeProvider implements ErrorTypeProvider {

  @Override
  public Set<ErrorTypeDefinition> getErrorTypes() {
    Set<ErrorTypeDefinition> errors = new HashSet<>();
    errors.add(SCHEMA_NOT_HONOURED);
    errors.add(INVALID_INPUT_JSON);
    errors.add(INVALID_SCHEMA);
    errors.add(SCHEMA_NOT_FOUND);
    errors.add(SCHEMA_INPUT_ERROR);

    return errors;
  }
}
