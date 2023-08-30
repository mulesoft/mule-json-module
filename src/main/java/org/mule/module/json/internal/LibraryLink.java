/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Defines the default behaviour to implement chain of responsibility design pattern
 */
public abstract class LibraryLink {

  private LibraryLink nextLink;

  protected LibraryLink(LibraryLink nextLink) {
    this.nextLink = nextLink;
  }

  public JsonSchemaValidator getWrapper(ValidatorKey key, JsonNode schemaJsonNode) {
    return this.nextLink.getWrapper(key, schemaJsonNode);
  }
}
