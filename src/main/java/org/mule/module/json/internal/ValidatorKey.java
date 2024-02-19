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

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.util.Map;
import java.util.Objects;

/**
 * This class contains raw attributes useful to manage Validators and his creation.
 */
class ValidatorKey {

  private String schemas;
  private JsonSchemaDereferencingMode dereferencingType;
  private Map<String, String> schemaRedirects;
  private final boolean allowDuplicateKeys;
  private final boolean allowArbitraryPrecision;
  private String schemaContent;

  public ValidatorKey(String schemas, JsonSchemaDereferencingMode dereferencingType, Map<String, String> schemaRedirects,
                      boolean allowDuplicateKeys, boolean allowArbitraryPrecision, String schemaContent) {
    this.schemas = schemas;
    this.dereferencingType = dereferencingType;
    this.schemaRedirects = schemaRedirects;
    this.allowDuplicateKeys = allowDuplicateKeys;
    this.allowArbitraryPrecision = allowArbitraryPrecision;
    this.schemaContent = schemaContent;
  }

  public String getSchemas() {
    return schemas;
  }

  public JsonSchemaDereferencingMode getDereferencingType() {
    return dereferencingType;
  }

  public Map<String, String> getSchemaRedirects() {
    return schemaRedirects;
  }

  public boolean isAllowDuplicateKeys() {
    return allowDuplicateKeys;
  }

  public boolean isAllowArbitraryPrecision() {
    return allowArbitraryPrecision;
  }

  public String getSchemaContent() {
    return schemaContent;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ValidatorKey) {
      ValidatorKey key = (ValidatorKey) obj;
      return Objects.equals(schemas, key.schemas)
          && dereferencingType == key.dereferencingType
          && Objects.equals(schemaRedirects, key.schemaRedirects)
          && Objects.equals(this.schemaContent, key.schemaContent);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(schemas).append(dereferencingType).append(schemaRedirects).toHashCode();
  }
}
