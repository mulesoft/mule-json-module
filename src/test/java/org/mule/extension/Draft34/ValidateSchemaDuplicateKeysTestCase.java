/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft34;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableMessageMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runners.Parameterized;

@RunnerDelegateTo(Parameterized.class)
public class ValidateSchemaDuplicateKeysTestCase extends AbstractSchemaValidationTestCase {

  private boolean allowDuplicateKeys;

  @Rule
  public ExpectedException expectedException;

  public ValidateSchemaDuplicateKeysTestCase(boolean allowDuplicateKeys, ExpectedException expectedException) {
    this.expectedException = expectedException;
    this.allowDuplicateKeys = allowDuplicateKeys;
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
    return "Draft34/config/validate-schema-duplicate-keys-config.xml";
  }

  @Test
  public void duplicateKeys() throws Exception {
    CoreEvent result = flowRunner("validate")
        .withPayload(getFstabWithDuplicateKeys())
        .withVariable("schema", SCHEMA_FSTAB_JSON)
        .withVariable("allowDuplicateKeys", allowDuplicateKeys).run();
    assertThat(result.getMessage().getPayload().getValue(), is(getFstabWithDuplicateKeys()));
  }

  private static ExpectedException getExpectedExceptionForDuplicateKeys() {
    ExpectedException expectedException = none();
    expectedException.expectCause(new ThrowableMessageMatcher<>(containsString("Duplicate field")));
    return expectedException;
  }

}
