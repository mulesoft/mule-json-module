/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import com.fasterxml.jackson.databind.JsonNode;

public class JavaJsonToolsLink extends LibraryLink {

  public JavaJsonToolsLink(LibraryLink nextLink) {
    super(nextLink);
  }

  @Override
  public JsonSchemaValidator getWrapper(ValidatorKey key, JsonNode schemaJsonNode) {
    return new JsonSchemaValidatorJavaJsonToolsWrapper(key, schemaJsonNode);
  }
}
