/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT202012;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT4;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT6;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT7;
import static java.util.Arrays.asList;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.hamcrest.core.Is.is;

import org.mule.runtime.core.api.event.CoreEvent;
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
    return "config/validate-schema-duplicate-keys-config.xml";
  }

  @Test
  public void Draft4duplicateKeys() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_JSON_DRAFT4);
  }

  @Test
  public void Draft6duplicateKeys() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_JSON_DRAFT6);
  }

  @Test
  public void Draft7duplicateKeys() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_JSON_DRAFT7);
  }

  @Test
  public void Draft201909duplicateKeys() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_JSON_DRAFT201909);
  }


  @Test
  public void Draft202012duplicateKeys() throws Exception {
    runTestWithSchemaAndValidate(SCHEMA_FSTAB_JSON_DRAFT202012);
  }

  private static ExpectedException getExpectedExceptionForDuplicateKeys() {
    ExpectedException expectedException = none();
    expectedException.expectCause(new ThrowableMessageMatcher<>(containsString("Duplicate field")));
    return expectedException;
  }


  private void runTestWithSchemaAndValidate(String schema) throws Exception {

    CoreEvent result = flowRunner("validate")
        .withPayload(getFstabWithDuplicateKeys())
        .withVariable("schema", schema)
        .withVariable("allowDuplicateKeys", allowDuplicateKeys).run();
    assertThat(result.getMessage().getPayload().getValue(), is(getFstabWithDuplicateKeys()));

  }
}
