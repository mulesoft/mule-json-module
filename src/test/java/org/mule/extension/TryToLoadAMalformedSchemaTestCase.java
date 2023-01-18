package org.mule.extension;

import static org.mule.extension.TestVariables.SCHEMA_MALFORMED;
import static org.hamcrest.core.StringContains.containsString;

import org.junit.Rule;
import org.junit.Test;
import org.mule.functional.api.exception.ExpectedError;

public class TryToLoadAMalformedSchemaTestCase extends AbstractSchemaValidationTestCase{

    private String json;

    @Rule
    public ExpectedError expectedError = ExpectedError.none();

    @Override
    protected String getConfigFile() {
        return "config/schema-validation-config.xml";
    }

    @Override
    protected void doSetUp() throws Exception {
        json = doGetResource("inputs/bad-object.json");
    }

    @Test
    public void loadMalformedSchema() throws Throwable {
        expectedError.expectErrorType("JSON", "INVALID_INPUT_JSON");
        expectedError.expectMessage(containsString("Invalid Json Schema"));
        flowRunner("validate").withVariable("schema", SCHEMA_MALFORMED).withPayload(json).run();
    }
}