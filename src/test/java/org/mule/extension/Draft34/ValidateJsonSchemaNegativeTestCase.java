/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft34;

import org.junit.Rule;
import org.junit.Test;
import org.mule.extension.AbstractSchemaValidationTestCase;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

import static org.hamcrest.core.StringContains.containsString;

public class ValidateJsonSchemaNegativeTestCase extends AbstractSchemaValidationTestCase {

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Test
  public void nullSchema() throws Throwable {
    expectedError.expectErrorType("JSON", "SCHEMA_NOT_FOUND");
    expectedError.expectMessage(containsString("Cannot find schema [this-does-not-exist.json]"));
    flowRunner("validate-non-existing-schema").run();
  }
}
