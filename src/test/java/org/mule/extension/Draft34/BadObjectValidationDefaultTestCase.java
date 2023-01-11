/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft34;

import static org.junit.Assert.assertEquals;

import org.mule.runtime.core.api.event.CoreEvent;

import org.junit.Test;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

public class BadObjectValidationDefaultTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  @Override
  protected String getConfigFile() {
    return "Draft34/config/object-array-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/bad-object.json");
  }

  @Test
  public void validateDefaultBehaviour() throws Exception {

    CoreEvent flowResult = flowRunner("validate").withPayload(json).run();
    assertEquals(json, flowResult.getMessage().getPayload().getValue());
  }
}
