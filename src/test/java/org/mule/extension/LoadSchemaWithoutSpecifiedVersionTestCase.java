/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.JSON_NAMESPACE;
import static org.mule.extension.TestVariables.SCHEMA_WITHOUT_VERSION;

import org.junit.Rule;
import org.junit.Test;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.module.json.api.JsonError;
import org.mule.module.json.api.JsonSchemaDereferencingMode;

public class LoadSchemaWithoutSpecifiedVersionTestCase extends AbstractSchemaValidationTestCase {

  private String inputFieldInteger;
  private String inputFieldString;

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    inputFieldInteger = doGetResource("inputs/field-integer.json");
    inputFieldString = doGetResource("inputs/field-string.json");
  }

  @Test
  public void validateSchemaWithoutVersionGood() throws Exception {
    runTestWithSchemaAndValidate(inputFieldInteger);
  }

  @Test
  public void validateSchemaWithoutVersionFails() throws Exception {
    runTestAndExpectError(inputFieldString);
  }


  private void runTestWithSchemaAndValidate(String inputJson) throws Exception {
    flowRunner("validate")
        .withVariable("schema", SCHEMA_WITHOUT_VERSION)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(inputJson).run();
  }

  private void runTestAndExpectError(String inputJson) throws Exception {
    expectedError.expectErrorType(JSON_NAMESPACE, JsonError.SCHEMA_NOT_HONOURED.name());
    flowRunner("validate")
        .withVariable("schema", SCHEMA_WITHOUT_VERSION)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(inputJson).run();
  }
}
