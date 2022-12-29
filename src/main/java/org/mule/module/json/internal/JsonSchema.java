/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
