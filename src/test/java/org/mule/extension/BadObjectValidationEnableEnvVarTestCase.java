/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.JSON_NAMESPACE;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT4;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT6;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT7;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT202012;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.module.json.api.JsonError;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.core.api.event.CoreEvent;
import org.junit.Rule;
import org.junit.Test;

public class BadObjectValidationEnableEnvVarTestCase extends AbstractSchemaValidationTestCase {

  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";

  private String json;

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/bad-object.json");
    System.setProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "true");
  }

  @Override
  protected void doTearDown() {
    System.clearProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS);
  }

  @Test
  public void Draft4validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT4);
  }

  @Test
  public void Draft6validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT6);
  }

  @Test
  public void Draft7validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT7);
  }

  @Test
  public void Draft201909validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT201909);
  }

  @Test
  public void Draft2020129validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT202012);
  }

  private void runTestWithSchemaAndValidate(String schema) throws Exception {

    expectedError.expectErrorType(JSON_NAMESPACE, JsonError.INVALID_INPUT_JSON.name());
    expectedError.expectMessage(containsString("Trailing token (of type START_OBJECT) found after value"));

    CoreEvent event = flowRunner("validate")
        .withVariable("schema", schema)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(json).run();
    assertEquals(json, event.getMessage().getPayload().getValue());
  }
}