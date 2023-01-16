/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft202012;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mule.extension.AbstractSchemaValidationTestCase;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

public class BadObjectValidationWithSchemaContentsTestCase extends AbstractSchemaValidationTestCase {

  private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";
  private String json;

  @Rule
  public ExpectedException expectedException = none();

  @Override
  protected String getConfigFile() {
    return "Draft202012/config/validate-schema-with-schemaContents-config.xml";
  }

  @Override
  protected void doTearDown() {
    System.clearProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS);
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/bad-object.json");
    System.setProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "true");
  }

  @Test
  public void validate_ErrorDataContent() throws Exception {
    expectedException.expectCause(new BaseMatcher<Throwable>() {

      @Override
      public boolean matches(Object item) {
        Exception e = (Exception) item;
        String report = e.getMessage();
        assertThat(report, containsString("Trailing token (of type START_OBJECT) found after value"));
        return true;
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Error report did not match");
      }
    });
    flowRunner("validateSchemaWithSchemaContents").withPayload(json).run();

  }
}
