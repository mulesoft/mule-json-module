/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
