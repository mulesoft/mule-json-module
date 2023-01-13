/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft202012;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.rules.ExpectedException.none;

public class DependentSchemaGoodTestCase extends AbstractSchemaValidationTestCase {


  private String json;

  @Rule
  public ExpectedException expectedException = none();

  @Override
  protected String getConfigFile() {
    return "Draft202012/config/dependent-schema-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/drarft-2019-09-orGreater-exclusive-function-dependent-schema-GOOD.json");
  }

  @Test
  public void validate() throws Exception {
    flowRunner("validate").withPayload(json).run();
  }
}
