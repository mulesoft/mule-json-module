/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft7;

import org.junit.Test;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.core.api.event.CoreEvent;

import static org.junit.Assert.assertEquals;

public class BadObjectValidationDefaultTestCase extends AbstractSchemaValidationTestCase {

  private String json;

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Override
  protected void doSetUp() throws Exception {
    json = doGetResource("inputs/bad-object.json");
  }

  @Test
  public void validateDefaultBehaviour() throws Exception {

    CoreEvent flowResult = flowRunner("validate")
        .withVariable("schema", SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT7)
        .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
        .withPayload(json).run();
    assertEquals(json, flowResult.getMessage().getPayload().getValue());
  }
}
