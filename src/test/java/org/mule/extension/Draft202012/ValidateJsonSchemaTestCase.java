/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft202012;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.mule.functional.api.exception.ExpectedError;
import org.mule.module.json.api.JsonError;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.test.runner.RunnerDelegateTo;

import java.util.Arrays;

import static org.mule.module.json.api.JsonSchemaDereferencingMode.CANONICAL;
import static org.mule.module.json.api.JsonSchemaDereferencingMode.INLINE;

@RunnerDelegateTo(Parameterized.class)
public class ValidateJsonSchemaTestCase extends AbstractSchemaValidationTestCase {

  @Parameterized.Parameters(name = "{0}")
  public static Iterable<Object[]> data() throws Exception {
    return Arrays.asList(new Object[][] {
        {"Draft202012: as String", SCHEMA_FSTAB_JSON_DRAFT202012, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft202012: as bytes", SCHEMA_FSTAB_JSON_DRAFT202012, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft202012: as Stream", SCHEMA_FSTAB_JSON_DRAFT202012, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft202012: Inline schema as String", SCHEMA_FSTAB_INLINE_DRAFT202012, INLINE, getGoodFstabInline(), getBadFstab(),
            getBadFstab2()},
        {"Draft202012: Inline schema as bytes", SCHEMA_FSTAB_INLINE_DRAFT202012, INLINE, getGoodFstabInline().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft202012: Inline schema as Stream", SCHEMA_FSTAB_INLINE_DRAFT202012, INLINE, toStream(getGoodFstabInline()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},
        {"Draft202012: Referring as String", SCHEMA_FSTAB_REFERRING_DRAFT202012, CANONICAL, getGoodFstab(), getBadFstab(),
            getBadFstab2()},
        {"Draft202012: Referring as bytes", SCHEMA_FSTAB_REFERRING_DRAFT202012, CANONICAL, getGoodFstab().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft202012: Referring as Stream", SCHEMA_FSTAB_REFERRING_DRAFT202012, CANONICAL, toStream(getGoodFstab()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},
    });
  }

  @Rule
  public ExpectedError expectedError = ExpectedError.none();

  @Parameterized.Parameter(0)
  public String description;

  @Parameterized.Parameter(1)
  public String schemaLocation;

  @Parameterized.Parameter(2)
  public JsonSchemaDereferencingMode dereferencing;

  @Parameterized.Parameter(3)
  public Object goodJson;

  @Parameterized.Parameter(4)
  public Object badJson;

  @Parameterized.Parameter(5)
  public Object badJson2;

  @Override
  protected String getConfigFile() {
    return "config/schema-validation-config.xml";
  }

  @Test
  public void good() throws Exception {
    validate(goodJson);
  }

  @Test
  public void bad() throws Throwable {
    expectValidationError();
    validate(badJson);
  }

  @Test
  public void bad2() throws Throwable {
    expectValidationError();
    validate(badJson2);
  }

  private void expectValidationError() {
    expectedError.expectErrorType("JSON", JsonError.SCHEMA_NOT_HONOURED.name());
  }

  private void validate(Object content) throws Exception {
    flowRunner("validate")
        .withPayload(content)
        .withVariable("schema", schemaLocation)
        .withVariable("dereferencing", dereferencing)
        .run();
  }
}
