/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft202012;

import org.junit.Test;
import org.mule.extension.AbstractSchemaValidationTestCase;
import org.mule.runtime.core.api.event.CoreEvent;

import static org.junit.Assert.assertEquals;

public class ValidateWithSchemaContentDefaultTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  @Override
  protected String getConfigFile() {
    return "Draft202012/config/validate-schema-with-schemaContents-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/object-array.json");
  }

  @Test
  public void validateDefaultBehaviourWithSchemaContent() throws Exception {
    CoreEvent event = flowRunner("validateSchemaWithSchemaContents").withPayload(json).run();
    assertEquals(json, event.getMessage().getPayload().getValue());
  }
}
