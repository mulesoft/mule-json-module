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
package org.mule.module.json.internal.error;

import static org.mule.module.json.api.JsonError.SCHEMA_NOT_HONOURED;
import org.mule.module.json.api.JsonError;
import org.mule.runtime.api.exception.ErrorMessageAwareException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.MediaType;
import org.mule.runtime.extension.api.exception.ModuleException;

/**
 * A {@link ModuleException} associated with the {@link JsonError#SCHEMA_NOT_HONOURED} type
 *
 * @since 1.0
 */
public class SchemaValidationException extends ModuleException implements ErrorMessageAwareException {

  private final Message message;

  /**
   * Creates a new instance
   *
   * @param message  the exception description
   */
  public SchemaValidationException(String message, String problemsJson) {
    super(message, SCHEMA_NOT_HONOURED);
    this.message = Message.builder().value(problemsJson).mediaType(MediaType.APPLICATION_JSON).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Message getErrorMessage() {
    return message;
  }
}
