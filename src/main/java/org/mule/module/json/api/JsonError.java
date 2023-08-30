/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.api;

import static java.util.Optional.ofNullable;
import static org.mule.runtime.extension.api.error.MuleErrors.VALIDATION;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Optional;

/**
 * Defines the error types thrown by this module
 *
 * @since 1.0
 */
public enum JsonError implements ErrorTypeDefinition<JsonError> {

  /**
   * The input document is not a valid JSON
   */
  INVALID_INPUT_JSON,

  /**
   * The input JSON document does not honour its schema
   */
  SCHEMA_NOT_HONOURED(VALIDATION),

  /**
   * The schema could not be found
   */
  SCHEMA_NOT_FOUND,

  /**
   * The supplied schema is invalid
   */
  INVALID_SCHEMA;

  private ErrorTypeDefinition<? extends Enum<?>> parentError;

  JsonError(ErrorTypeDefinition<? extends Enum<?>> parentError) {
    this.parentError = parentError;
  }

  JsonError() {}

  @Override
  public Optional<ErrorTypeDefinition<? extends Enum<?>>> getParent() {
    return ofNullable(parentError);
  }

}
