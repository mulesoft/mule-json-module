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
import org.mule.runtime.core.api.event.CoreEvent;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

public class ValidateCompliantWithSchemaContentTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  private String draft4schemaContent;
  private String draft6schemaContent;
  private String draft7schemaContent;
  private String draft201909schemaContent;
  private String draft202012schemaContent;

  @Rule
  public ExpectedException expectedException = none();

  @Override
  protected String getConfigFile() {
    return "config/schema-content-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/objet-array-not-compliant.json");
    draft4schemaContent = doGetResource("Draft34/schemas/schema-default.json");
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
    runTestWithSchemaAndValidate(draft201909schemaContent);
  }

  @Test
  public void Draft202012validateDefaultBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft202012schemaContent);
  }

  private void runTestWithSchemaAndValidate(String schemaContent) throws Exception {

    expectedException.expectCause(new BaseMatcher<Throwable>() {

      @Override
      public boolean matches(Object item) {
        Exception e = (Exception) item;
        String report = e.getMessage();
        System.err.println(report);
        assertThat(report, containsString("Json content is not compliant with schema"));
        return true;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Json content is not compliant with schema");
      }
    });

    CoreEvent event = flowRunner("validate")
        .withVariable("schema", schemaContent)
        .withPayload(json).run();
    assertEquals(json, event.getMessage().getPayload().getValue());
  }
}
