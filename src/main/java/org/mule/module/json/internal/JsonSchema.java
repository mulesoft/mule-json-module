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

import static org.mule.runtime.api.meta.model.display.PathModel.Type.FILE;

import org.mule.runtime.extension.api.annotation.param.ExclusiveOptionals;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.runtime.extension.api.annotation.param.display.*;

/**
 * Parameters to load Json Schema against which we will validate the payload.
 */
@ExclusiveOptionals(isOneRequired = true)
public class JsonSchema {

  /**
   * The location in which the schema to validate against is to be found. This attribute supports URI
   * representations such as "http://org.mule/schema.json" or "resource:/schema.json". It also supports a most common
   * classpath reference such as simply "schema.json".
   */
  @Parameter
  @Optional
  @Summary("The schema location")
  @Path(type = FILE, acceptedFileExtensions = "json")
  private String schema;

  /**
   * The content of the schema to validate in text format.
   */
  @Parameter
  @Optional
  @Summary("The schema content to validate")
  @Text
  @DisplayName("SchemaContent")
  private String contents;

  public String getSchema() {
    return schema;
  }

  public String getContents() {
    return contents;
  }
}
