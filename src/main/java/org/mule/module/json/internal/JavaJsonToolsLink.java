/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Link of the chain, determinate when {@link JsonSchemaValidatorJavaJsonToolsWrapper} have to be returned
 */
public class JavaJsonToolsLink extends LibraryLink {

  public JavaJsonToolsLink(LibraryLink nextLink) {
    super(nextLink);
  }

  @Override
  public JsonSchemaValidator getWrapper(ValidatorKey key, JsonNode schemaJsonNode) {
    return new JsonSchemaValidatorNetworkntWrapper(key, schemaJsonNode);
  }
}
