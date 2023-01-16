/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.SCHEMA_CONDITIONS_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_CONDITIONS_DRAFT202012;
import static org.mule.extension.TestVariables.SCHEMA_CONDITIONS_DRAFT7;
import static org.junit.rules.ExpectedException.none;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConditionsExclusiveFunctionGoodTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  @Rule
  public ExpectedException expectedException = none();

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/drarft-07-orGreater-exclusive-function-conditions-GOOD.json");
  }

  @Test
  public void Draft7validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_CONDITIONS_DRAFT7);
  }

  @Test
  public void Draft201909validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_CONDITIONS_DRAFT201909);
  }

  @Test
  public void Draft202012validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_CONDITIONS_DRAFT202012);
  }

  private void runTestWithSchemaAndValidate(String schema) throws Exception {
    flowRunner("validate")
        .withVariable("schema", schema)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(json).run();
  }
}