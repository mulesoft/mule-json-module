/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.JSON_NAMESPACE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

import org.mule.module.json.api.JsonError;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.functional.api.exception.ExpectedError;
import org.junit.Rule;
import org.junit.Test;

public class BadObjectValidationWithSchemaContentsTestCase extends AbstractSchemaValidationTestCase {

  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";
  private String json;

  private String draft4schemaContent;
  private String draft6schemaContent;
  private String draft7schemaContent;
  private String draft201909schemaContent;
  private String draft202012schemaContent;

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "config/schema-content-config.xml";
  }

  @Override
  protected void doTearDown() {
    System.clearProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS);
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/bad-object.json");
    System.setProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "true");

    draft4schemaContent = doGetResource("Draft4/schemas/schema-default.json");
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
    expectedError.expectErrorType(JSON_NAMESPACE, JsonError.INVALID_INPUT_JSON.name());
    expectedError.expectMessage(containsString("Trailing token (of type START_OBJECT) found after value"));

    CoreEvent event = flowRunner("validate")
        .withVariable("schema", schemaContent)
        .withPayload(json).run();
    assertEquals(json, event.getMessage().getPayload().getValue());
  }
}
