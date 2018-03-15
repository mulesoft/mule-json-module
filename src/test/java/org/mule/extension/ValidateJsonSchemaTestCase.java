package org.mule.extension;

import static org.mule.module.json.api.JsonSchemaDereferencingMode.CANONICAL;
import static org.mule.module.json.api.JsonSchemaDereferencingMode.INLINE;
import org.mule.module.json.api.JsonError;
import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.functional.api.exception.ExpectedError;
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
        {"SimpleV4Schema as String", SCHEMA_FSTAB_JSON, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"SimpleV4Schema as bytes", SCHEMA_FSTAB_JSON, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"SimpleV4Schema as Stream", SCHEMA_FSTAB_JSON, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Inline schema as String", SCHEMA_FSTAB_INLINE, INLINE, getGoodFstabInline(), getBadFstab(), getBadFstab2()},
        {"Inline schema as bytes", SCHEMA_FSTAB_INLINE, INLINE, getGoodFstabInline().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Inline schema as Stream", SCHEMA_FSTAB_INLINE, INLINE, toStream(getGoodFstabInline()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"Draft3 as String", SCHEMA_FSTAB_DRAFTV3, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"Draft3 as bytes", SCHEMA_FSTAB_DRAFTV3, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"Draft3 as Stream", SCHEMA_FSTAB_DRAFTV3, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
            toStream(getBadFstab2())},

        {"ReferringV4Schema as String", SCHEMA_FSTAB_REFERRING, CANONICAL, getGoodFstab(), getBadFstab(), getBadFstab2()},
        {"ReferringV4Schema as bytes", SCHEMA_FSTAB_REFERRING, CANONICAL, getGoodFstab().getBytes(), getBadFstab().getBytes(),
            getBadFstab2().getBytes()},
        {"ReferringV4Schema as Stream", SCHEMA_FSTAB_REFERRING, CANONICAL, toStream(getGoodFstab()), toStream(getBadFstab()),
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
    return "schema-validation-config.xml";
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
        .withVariable("schemas", schemaLocation)
        .withVariable("dereferencing", dereferencing)
        .run();
  }
}
