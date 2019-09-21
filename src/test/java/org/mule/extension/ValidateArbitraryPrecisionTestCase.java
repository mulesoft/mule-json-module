/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static java.util.Arrays.asList;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.rules.ExpectedException.none;

import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;

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
        {false, getExpectedExceptionForDuplicateKeys()}
    });
  }

  @Override
  protected String getConfigFile() {
    return "validate-schema-arbitrary-precision-config.xml";
  }

  @Test
  public void allowArbitraryPrecision() throws Exception {
    flowRunner("validate")
        .withPayload(ARBITRARY_PRECISION_JSON_NUMBER)
        .withVariable("schema", SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS)
        .withVariable("allowArbitraryPrecision", allowArbitraryPrecision).run();
  }

  private static ExpectedException getExpectedExceptionForDuplicateKeys() {
    ExpectedException expectedException = none();
    expectedException.expectCause(new ThrowableMessageMatcher<>(containsString("not compliant with schema")));
    return expectedException;
  }

}
