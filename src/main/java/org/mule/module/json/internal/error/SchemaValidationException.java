/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
