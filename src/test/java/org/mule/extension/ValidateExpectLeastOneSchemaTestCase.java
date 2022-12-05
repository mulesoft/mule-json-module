package org.mule.extension;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

public class ValidateExpectLeastOneSchemaTestCase extends AbstractSchemaValidationTestCase {

    private static final String VALIDATOR_FAIL_ON_TRAILING_TOKENS = "jsonSchemaValidator.FailOnTrailingTokens";
    private String json;

    @Rule
    public ExpectedException expectedException = none();
    @Override
    protected String getConfigFile(){ return "validate-schema-with-schemaContent-config.xml";}
    @Override
    protected void doTearDown() {
        System.clearProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS);
    }
    @Override
    protected void doSetUp() throws Exception {
        json = doGetResource("inputs/bad-object.json");
        System.setProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "true");
    }

    @Test
    public void validate_expectedAtLeastOneSchemaTestCase() throws Exception {
        expectedException.expectCause(new BaseMatcher<Throwable>() {
            @Override
            public boolean matches(Object item) {
                Exception e = (Exception) item;
                String report = e.getMessage();
                assertThat(report, containsString("Schema cannot be null or blank"));
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Schema cannot be null or blank");

            }
        });
        flowRunner("validateExpectedAtLeastOneSchemaTestCase").withPayload(json).run();

    }
}
