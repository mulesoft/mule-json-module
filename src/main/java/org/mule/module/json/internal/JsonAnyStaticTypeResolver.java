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

