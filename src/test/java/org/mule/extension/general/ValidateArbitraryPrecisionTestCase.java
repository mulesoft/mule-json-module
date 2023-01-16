/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.general;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;
import org.mule.extension.AbstractSchemaValidationTestCase;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.rules.ExpectedException.none;
import static org.mule.extension.TestVariables.*;

@RunnerDelegateTo(Parameterized.class)
public class ValidateArbitraryPrecisionTestCase extends AbstractSchemaValidationTestCase {

  private final String ARBITRARY_PRECISION_JSON_NUMBER = "9999999999999999.99";

  private boolean allowArbitraryPrecision;

  @Rule
  public ExpectedException expectedException;

  public ValidateArbitraryPrecisionTestCase(boolean allowArbitraryPrecision, ExpectedException expectedException) {
    this.expectedException = expectedException;
    this.allowArbitraryPrecision = allowArbitraryPrecision;
  }

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return asList(new Object[][] {
        {true, none()},
        {false, getExpectedExceptionForArbitraryPrecision()}
    });
  }

  @Override
  protected String getConfigFile() {
    return "config/validate-schema-arbitrary-precision-config.xml";
  }

  @Test
  public void Draft4AllowArbitraryPrecision() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS_DRAFT34);
  }

  @Test
  public void Draft6AllowArbitraryPrecision() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS_DRAFT6);
  }

  @Test
  public void Draft7AllowArbitraryPrecision() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS_DRAFT7);
  }

  @Test
  public void Draft201909AllowArbitraryPrecision() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS_DRAFT201909);
  }

  @Test
  public void Draft202012AllowArbitraryPrecision() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS_DRAFT202012);
  }

  private static ExpectedException getExpectedExceptionForArbitraryPrecision() {
    ExpectedException expectedException = none();
    expectedException.expectCause(new ThrowableMessageMatcher<>(containsString("not compliant with schema")));
    return expectedException;
  }


  private void runTestWithSchemaAndValidate(String schema) throws Exception {

    flowRunner(allowArbitraryPrecision ? "validateWithArbitraryPrecision" : "validateWithoutArbitraryPrecision")
        .withPayload(ARBITRARY_PRECISION_JSON_NUMBER)
        .withVariable("schema", schema)
        .withVariable("allowArbitraryPrecision", allowArbitraryPrecision).run();
  }
}
