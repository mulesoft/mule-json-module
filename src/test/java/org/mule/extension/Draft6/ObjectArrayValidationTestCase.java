/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft6;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.api.exception.ErrorMessageAwareException;
import org.mule.runtime.api.metadata.TypedValue;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mule.runtime.api.metadata.DataType.JSON_STRING;

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
  public void validate() throws Exception {
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
        .withVariable("schema", SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT6)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(json).run();
  }
}
