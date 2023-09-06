/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.JSON_NAMESPACE;
import static org.mule.extension.TestVariables.SCHEMA_MALFORMED;
import static org.hamcrest.core.StringContains.containsString;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.module.json.api.JsonError;
import org.junit.Rule;
import org.junit.Test;

public class TryToLoadAMalformedSchemaTestCase extends AbstractSchemaValidationTestCase {

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
  }

  @Test
  public void loadMalformedSchema() throws Throwable {
    expectedError.expectErrorType(JSON_NAMESPACE, JsonError.INVALID_INPUT_JSON.name());
    expectedError.expectMessage(containsString("Malformed Json Schema"));
    flowRunner("validate").withVariable("schema", SCHEMA_MALFORMED).withPayload(json).run();
  }
}
