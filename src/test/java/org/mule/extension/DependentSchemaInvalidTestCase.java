/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.JSON_NAMESPACE;
import static org.mule.extension.TestVariables.SCHEMA_DEPENDENT_DRAFT2019009;
import static org.mule.extension.TestVariables.SCHEMA_DEPENDENT_DRAFT202012;
import static org.hamcrest.CoreMatchers.containsString;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.module.json.api.JsonError;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.junit.Rule;
import org.junit.Test;

public class DependentSchemaInvalidTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/drarft-2019-09-orGreater-exclusive-function-dependant-schema-INVALID.json");
  }

  @Test
  public void Draft201909validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_DEPENDENT_DRAFT2019009);
  }

  @Test
  public void Draft202012validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_DEPENDENT_DRAFT202012);
  }

  private void runTestWithSchemaAndValidate(String schema) throws Exception {

    expectedError.expectErrorType(JSON_NAMESPACE, JsonError.SCHEMA_NOT_HONOURED.name());
    expectedError.expectMessage(containsString("$.billing_address: is missing but it is required"));

    flowRunner("validate")
        .withVariable("schema", schema)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(json).run();
  }
}