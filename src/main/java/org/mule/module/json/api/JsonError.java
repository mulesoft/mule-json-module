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
