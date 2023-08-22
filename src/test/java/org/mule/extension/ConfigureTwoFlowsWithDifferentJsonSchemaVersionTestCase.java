/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.JSON_NAMESPACE;
import static org.mule.extension.TestVariables.SCHEMA_FIELD_INTEGER_REQUIRED_DRAFT7;
import static org.mule.extension.TestVariables.SCHEMA_FIELD_STRING_REQUIRED_DRAFT202012;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.module.json.api.JsonError;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.core.api.event.CoreEvent;
import org.junit.Rule;
import org.junit.Test;

public class ConfigureTwoFlowsWithDifferentJsonSchemaVersionTestCase extends AbstractSchemaValidationTestCase {

  private String inputFieldInteger;
  private String inputFieldString;

  public static final String FLOW1 = "validate1";
  public static final String FLOW2 = "validate2";

  public static final String ERROR_MSG_EXPECTED_STRING_FOUND_INT_EXP = "string found, integer expected";
  public static final String ERROR_MSG_EXPECTED_INT_FOUND_STRING_EXP = "integer found, string expected";

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "config/schema-two-validate-flows.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    inputFieldInteger = doGetResource("inputs/field-integer.json");
    inputFieldString = doGetResource("inputs/field-string.json");
  }

  @Test
  public void RunFlowsAndMakeValidations() throws Exception {

    runTestWithSchema(SCHEMA_FIELD_INTEGER_REQUIRED_DRAFT7, inputFieldInteger, FLOW1);
    runTestWithSchema(SCHEMA_FIELD_STRING_REQUIRED_DRAFT202012, inputFieldString, FLOW2);

    runTestWithSchemaAndExpectError(SCHEMA_FIELD_INTEGER_REQUIRED_DRAFT7, inputFieldString, FLOW1,
                                    ERROR_MSG_EXPECTED_STRING_FOUND_INT_EXP);
    runTestWithSchemaAndExpectError(SCHEMA_FIELD_STRING_REQUIRED_DRAFT202012, inputFieldInteger, FLOW2,
                                    ERROR_MSG_EXPECTED_INT_FOUND_STRING_EXP);
  }

  private void runTestWithSchema(String schema, String input, String nameFlow) throws Exception {
    CoreEvent flowResult = flowRunner(nameFlow)
        .withVariable("schema", schema)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(input).run();
    assertEquals(input, flowResult.getMessage().getPayload().getValue());
  }

  private void runTestWithSchemaAndExpectError(String schema, String input, String nameFlow, String errorMsgExpected)
      throws Exception {

    expectedError.expectErrorType(JSON_NAMESPACE, JsonError.SCHEMA_NOT_HONOURED.name());
    expectedError.expectMessage(containsString(errorMsgExpected));

    flowRunner(nameFlow)
        .withVariable("schema", schema)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(input).run();
  }
}
