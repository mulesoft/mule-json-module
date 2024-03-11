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
package org.mule.module.json.internal;

import static com.networknt.schema.SpecVersion.VersionFlag.V7;
import static com.networknt.schema.SpecVersion.VersionFlag.V6;
import static com.networknt.schema.SpecVersion.VersionFlag.V201909;
import static com.networknt.schema.SpecVersion.VersionFlag.V202012;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.SpecVersionDetector;

/**
 * Link of the chain, determinate when {@link JsonSchemaValidatorNetworkntWrapper} have to be returned
 */
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
