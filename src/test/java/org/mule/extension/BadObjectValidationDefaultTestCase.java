/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT4;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT6;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT7;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT202012;
import static org.junit.Assert.assertEquals;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.core.api.event.CoreEvent;
import org.junit.Test;

public class BadObjectValidationDefaultTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/bad-object.json");
  }

  @Test
  public void Draft4validateDefaultBehaviour() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT4);
  }

  @Test
  public void Draft6validateDefaultBehaviour() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT6);
  }

  @Test
  public void Draft7validateDefaultBehaviour() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT7);
  }

  @Test
  public void Draft201909validateDefaultBehaviour() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT201909);
  }

  @Test
  public void Draft202012validateDefaultBehaviour() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT202012);
  }

  private void runTestWithSchemaAndValidate(String schema) throws Exception {

    CoreEvent flowResult = flowRunner("validate")
        .withVariable("schema", schema)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(json).run();
    assertEquals(json, flowResult.getMessage().getPayload().getValue());
  }
}
