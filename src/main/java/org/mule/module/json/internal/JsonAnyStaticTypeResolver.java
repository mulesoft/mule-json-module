/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static org.mule.metadata.api.model.MetadataFormat.JSON;

import org.mule.metadata.api.builder.BaseTypeBuilder;
import org.mule.metadata.api.model.AnyType;
import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.metadata.resolving.InputStaticTypeResolver;

/**
 * {@link InputStaticTypeResolver} for the json content parameter of the validate schema operation.
 * This resolver indicates that the json content should be a any kind of JSON structure.
 *
 * @since 1.1
 */
public class JsonAnyStaticTypeResolver extends InputStaticTypeResolver {

  private static final AnyType ANY_JSON_TYPE = BaseTypeBuilder.create(JSON).anyType().build();

  @Override
  public MetadataType getStaticMetadata() {
    return ANY_JSON_TYPE;
  }
}

