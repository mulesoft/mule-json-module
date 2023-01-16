/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.general;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mule.extension.AbstractSchemaValidationTestCase;
import org.mule.runtime.core.api.event.CoreEvent;

import javax.management.ObjectName;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class ValidateWithSchemaContentDefaultTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  private String draft4schemaContent;
  private String draft6schemaContent;
  private String draft7schemaContent;
  private String draft201909schemaContent;
  private String draft202012schemaContent;

  @Override
  protected String getConfigFile() {
    return "config/schema-content-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/object-array.json");
    draft4schemaContent = doGetResource("Draft34/schemas/schema-default.json");
    draft6schemaContent = doGetResource("Draft6/schemas/schema-default.json");
    draft7schemaContent = doGetResource("Draft7/schemas/schema-default.json");
    draft201909schemaContent = doGetResource("Draft201909/schemas/schema-default.json");
    draft202012schemaContent = doGetResource("Draft202012/schemas/schema-default.json");
  }

  @Test
  public void Draft4validateDefaultBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft4schemaContent);
  }

  @Test
  public void Draft6validateDefaultBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft6schemaContent);
  }

  @Test
  public void Draft7validateDefaultBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft7schemaContent);
  }

  @Test
  public void Draft201909validateDefaultBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft202012schemaContent);
  }

  @Test
  public void Draft202012validateDefaultBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft201909schemaContent);
  }

  private void runTestWithSchemaAndValidate(String schemaContent) throws Exception {
    CoreEvent event = flowRunner("validate")
        .withVariable("schema", schemaContent)
        .withPayload(json).run();
    assertEquals(json, event.getMessage().getPayload().getValue());
  }

}
