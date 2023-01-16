/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.general;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.extension.AbstractSchemaValidationTestCase;
import org.mule.module.json.api.JsonSchemaDereferencingMode;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mule.extension.TestVariables.SCHEMA_DEPENDENT_DRAFT2019009;
import static org.mule.extension.TestVariables.SCHEMA_DEPENDENT_DRAFT202012;

public class DependentSchemaInvalidTestCase extends AbstractSchemaValidationTestCase {


  private String json;

  @Rule
  public ExpectedException expectedException = none();

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
    expectedException.expectCause(new BaseMatcher<Throwable>() {

      @Override
      public boolean matches(Object item) {
        Exception e = (Exception) item;
        String report = e.getMessage();
        assertThat(report, containsString("$.billing_address: is missing but it is required"));

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
