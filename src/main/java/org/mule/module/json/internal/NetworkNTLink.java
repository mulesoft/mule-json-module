/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static com.networknt.schema.SpecVersion.VersionFlag.V7;
import static com.networknt.schema.SpecVersion.VersionFlag.V6;
import static com.networknt.schema.SpecVersion.VersionFlag.V201909;
import static com.networknt.schema.SpecVersion.VersionFlag.V202012;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.SpecVersionDetector;

public class NetworkNTLink extends LibraryLink {

  public NetworkNTLink(LibraryLink nextLink) {
    super(nextLink);
  }

  @Override
  public JsonSchemaValidator getWrapper(ValidatorKey key, JsonNode schemaJsonNode) {
    try {
      if (SpecVersionDetector.detect(schemaJsonNode).equals(V6) ||
          SpecVersionDetector.detect(schemaJsonNode).equals(V7) ||
          SpecVersionDetector.detect(schemaJsonNode).equals(V201909) ||
          SpecVersionDetector.detect(schemaJsonNode).equals(V202012)) {

        return new JsonSchemaValidatorNetworkntWrapper(key, schemaJsonNode);
      } else {
        return super.getWrapper(key, schemaJsonNode);
      }
    } catch (JsonSchemaException ex) {
      return super.getWrapper(key, schemaJsonNode);
    }
  }
}