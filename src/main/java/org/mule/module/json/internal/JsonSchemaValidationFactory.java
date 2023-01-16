/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.module.json.internal.JsonSchemaParser.getSchemaJsonNode;
import static org.mule.runtime.api.i18n.I18nMessageFactory.createStaticMessage;

import com.fasterxml.jackson.databind.JsonNode;
import org.mule.runtime.api.exception.MuleRuntimeException;

/**
 * Create instances of {@link JsonSchemaValidator},
 * returning their wrappers making validations, can return  {@link JsonSchemaValidatorNetworkntWrapper} or
 * {@link JsonSchemaValidatorJavaJsonToolsWrapper
 */
public class JsonSchemaValidationFactory {

  private final LibraryLink firstLinkInTheChain;

  /**
   * Here is defined the order of the links
   */
  public JsonSchemaValidationFactory() {
    JavaJsonToolsLink javaJsonToolsSecondLink = new JavaJsonToolsLink(null);
    firstLinkInTheChain = new NetworkNTLink(javaJsonToolsSecondLink);
  }

  public JsonSchemaValidator create(ValidatorKey key) {
    JsonNode schemaJsonNode = getSchemaJsonNode(key.getSchemaContent(), key.getSchemas());
    return firstLinkInTheChain.getWrapper(key, schemaJsonNode);
  }
}
