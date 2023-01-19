/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.extension.TestVariables.JSON_NAMESPACE;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_DRAFTV3;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_INLINE_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_INLINE_DRAFT202012;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_INLINE_DRAFT4;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_INLINE_DRAFT6;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_INLINE_DRAFT7;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT202012;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT4;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT6;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_JSON_DRAFT7;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_REFERRING_DRAFT201909;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_REFERRING_DRAFT202012;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_REFERRING_DRAFT4;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_REFERRING_DRAFT6;
import static org.mule.extension.TestVariables.SCHEMA_FSTAB_REFERRING_DRAFT7;
import static org.mule.module.json.api.JsonSchemaDereferencingMode.CANONICAL;
import static org.mule.module.json.api.JsonSchemaDereferencingMode.INLINE;

import org.mule.functional.api.exception.ExpectedError;
import org.mule.module.json.api.JsonError;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.test.runner.RunnerDelegateTo;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.Parameterized;

@RunnerDelegateTo(Parameterized.class)
public class ValidateJsonSchemaTestCase extends AbstractSchemaValidationTestCase {

  @Parameterized.Parameters(name = "{0}")
  public static Iterable<Object[]> data() throws Exception {
    return Arrays.asList(new Object[][] {

        {"Draft4 as String", SCHEMA_FSTAB_JSON_DRAFT4, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft4 as bytes", SCHEMA_FSTAB_JSON_DRAFT4, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft4 as Stream", SCHEMA_FSTAB_JSON_DRAFT4, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft4: Inline schema as String", SCHEMA_FSTAB_INLINE_DRAFT4, INLINE, getGoodFstabInline(), getBadFstab(),
            getBadFstab2()},
        {"Draft4: Inline schema as bytes", SCHEMA_FSTAB_INLINE_DRAFT4, INLINE, getGoodFstabInline().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft4: Inline schema as Stream", SCHEMA_FSTAB_INLINE_DRAFT4, INLINE, toStream(getGoodFstabInline()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft3 as String", SCHEMA_FSTAB_DRAFTV3, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft3 as bytes", SCHEMA_FSTAB_DRAFTV3, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft3 as Stream", SCHEMA_FSTAB_DRAFTV3, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft4: ReferringV4Schema as String", SCHEMA_FSTAB_REFERRING_DRAFT4, CANONICAL, getGoodFstab(), getBadFstab(),
            getBadFstab2()},
        {"Draft4: ReferringV4Schema as bytes", SCHEMA_FSTAB_REFERRING_DRAFT4, CANONICAL, getGoodFstab().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft4: ReferringV4Schema as Stream", SCHEMA_FSTAB_REFERRING_DRAFT4, CANONICAL, toStream(getGoodFstab()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft6: as String", SCHEMA_FSTAB_JSON_DRAFT6, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft6: as bytes", SCHEMA_FSTAB_JSON_DRAFT6, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft6: as Stream", SCHEMA_FSTAB_JSON_DRAFT6, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft6: Inline schema as String", SCHEMA_FSTAB_INLINE_DRAFT6, INLINE, getGoodFstabInline(), getBadFstab(),
            getBadFstab2()},
        {"Draft6: Inline schema as bytes", SCHEMA_FSTAB_INLINE_DRAFT6, INLINE, getGoodFstabInline().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft6: Inline schema as Stream", SCHEMA_FSTAB_INLINE_DRAFT6, INLINE, toStream(getGoodFstabInline()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},
        {"Draft6: Referring as String", SCHEMA_FSTAB_REFERRING_DRAFT6, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft6: Referring as bytes", SCHEMA_FSTAB_REFERRING_DRAFT6, CANONICAL, getGoodFstab().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft6: Referring as Stream", SCHEMA_FSTAB_REFERRING_DRAFT6, CANONICAL, toStream(getGoodFstab()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft7: as String", SCHEMA_FSTAB_JSON_DRAFT7, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft7: as bytes", SCHEMA_FSTAB_JSON_DRAFT7, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft7: as Stream", SCHEMA_FSTAB_JSON_DRAFT7, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft7: Inline schema as String", SCHEMA_FSTAB_INLINE_DRAFT7, INLINE, getGoodFstabInline(), getBadFstab(),
            getBadFstab2()},
        {"Draft7: Inline schema as bytes", SCHEMA_FSTAB_INLINE_DRAFT7, INLINE, getGoodFstabInline().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft7: Inline schema as Stream", SCHEMA_FSTAB_INLINE_DRAFT7, INLINE, toStream(getGoodFstabInline()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},
        {"Draft7: Referring as String", SCHEMA_FSTAB_REFERRING_DRAFT7, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft7: Referring as bytes", SCHEMA_FSTAB_REFERRING_DRAFT7, CANONICAL, getGoodFstab().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft7: Referring as Stream", SCHEMA_FSTAB_REFERRING_DRAFT7, CANONICAL, toStream(getGoodFstab()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft201909: as String", SCHEMA_FSTAB_JSON_DRAFT201909, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft201909: as bytes", SCHEMA_FSTAB_JSON_DRAFT201909, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft201909: as Stream", SCHEMA_FSTAB_JSON_DRAFT201909, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft201909: Inline schema as String", SCHEMA_FSTAB_INLINE_DRAFT201909, INLINE, getGoodFstabInline(), getBadFstab(),
            getBadFstab2()},
        {"Draft201909: Inline schema as bytes", SCHEMA_FSTAB_INLINE_DRAFT201909, INLINE, getGoodFstabInline().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft201909: Inline schema as Stream", SCHEMA_FSTAB_INLINE_DRAFT201909, INLINE, toStream(getGoodFstabInline()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},
        {"Draft201909: Referring as String", SCHEMA_FSTAB_REFERRING_DRAFT201909, CANONICAL, getGoodFstab(), getBadFstab(),
            getBadFstab2()},
        {"Draft201909: Referring as bytes", SCHEMA_FSTAB_REFERRING_DRAFT201909, CANONICAL, getGoodFstab().getBytes(),
            getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft201909: Referring as Stream", SCHEMA_FSTAB_REFERRING_DRAFT201909, CANONICAL, toStream(getGoodFstab()),
            toStream(getBadFstab()),
            toStream(getBadFstab2())},

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
    expectedError.expectErrorType(JSON_NAMESPACE, JsonError.SCHEMA_NOT_HONOURED.name());
  }

  private void validate(Object content) throws Exception {
    flowRunner("validate")
        .withPayload(content)
        .withVariable("schema", schemaLocation)
        .withVariable("dereferencing", dereferencing)
        .run();
  }
}
