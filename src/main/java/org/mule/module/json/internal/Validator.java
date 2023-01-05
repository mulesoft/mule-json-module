/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;

public abstract class Validator {

  Validator create(JsonNode jsonNode, String schemaLocation, JsonSchemaDereferencingMode dereferencing,
                   Map<String, String> schemaRedirects, String schemaContent)
      throws Exception{
    // do nothing
    return null;
  }

  void validate(JsonNode jsonNode, ObjectMapper objectMapper){
    // do nothing
  }
}
