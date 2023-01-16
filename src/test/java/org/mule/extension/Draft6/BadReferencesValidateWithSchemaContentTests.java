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
import org.mule.extension.Draft34.AbstractSchemaValidationTestCase;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

public class BadReferencesValidateWithSchemaContentTests extends AbstractSchemaValidationTestCase {

  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";
  private String json;

  @Rule
  public ExpectedException expectedException = none();

  @Override
  protected String getConfigFile() {
    return "Draft6/config/validate-schema-references-with-schemaContents.xml";
  }

  @Override
  protected void doTearDown() {
    System.clearProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS);
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/fstab-good.json");
    System.setProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "true");
  }

  @Test
  public void validateSchemaReferencesWithSchemaContents() throws Exception {
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
    flowRunner("validateSchemaReferencesWithSchemaContents").withPayload(json).run();
  }
}
