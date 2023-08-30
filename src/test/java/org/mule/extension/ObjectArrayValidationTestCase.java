/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT202012;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT4;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT6;
import static org.mule.extension.TestVariables.SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT7;
import static org.mule.runtime.api.metadata.DataType.JSON_STRING;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.api.exception.ErrorMessageAwareException;
import org.mule.runtime.api.metadata.TypedValue;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ObjectArrayValidationTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  @Rule
  public ExpectedException expectedException = none();

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/object-array.json");
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
  public void Draft202012validate() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT202012);
  }

  private void runTestWithSchemaAndValidate(String schema) throws Exception {
    expectedException.expectCause(new BaseMatcher<Throwable>() {

      @Override
      public boolean matches(Object item) {
        ErrorMessageAwareException e = (ErrorMessageAwareException) item;
        TypedValue<String> report = e.getErrorMessage().getPayload();
        assertThat(report.getDataType(), equalTo(JSON_STRING));

        return true;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Error report did not match");
      }
    });

    flowRunner("validate")
        .withVariable("schema", schema)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(json).run();
  }
}
