/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import org.mule.runtime.core.api.event.CoreEvent;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BadReferencesValidateWithSchemaContentTests extends AbstractSchemaValidationTestCase {

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
    json = doGetResource("inputs/fstab-good.json");

    draft4schemaContent = doGetResource("Draft4/schemas/schema-content-references.json");
    draft6schemaContent = doGetResource("Draft6/schemas/schema-content-references.json");
    draft7schemaContent = doGetResource("Draft7/schemas/schema-content-references.json");
    draft201909schemaContent = doGetResource("Draft201909/schemas/schema-content-references.json");
    draft202012schemaContent = doGetResource("Draft202012/schemas/schema-content-references.json");
  }

  @Test
  public void Draft4BadReferencesBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft4schemaContent);
  }

  @Test
  public void Draft6BadReferencesBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft6schemaContent);
  }

  @Test
  public void Draft7BadReferencesBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft7schemaContent);
  }

  @Test
  public void Draft201909BadReferencesBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft202012schemaContent);
  }

  @Test
  public void Draft202012BadReferencesBehaviourWithSchemaContent() throws Exception {
    runTestWithSchemaAndValidate(draft201909schemaContent);
  }

  private void runTestWithSchemaAndValidate(String schemaContent) throws Exception {

    expectedException.expectCause(new BaseMatcher<Throwable>() {

      @Override
      public boolean matches(Object item) {
        Exception exception = (Exception) item;
        String report = exception.getMessage();
        assertThat(report, containsString("Invalid Schema References"));
        return true;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Invalid Schema References");

      }
    });

    CoreEvent event = flowRunner("validate")
        .withVariable("schema", schemaContent)
        .withPayload(json).run();
    assertEquals(json, event.getMessage().getPayload().getValue());
  }
}
