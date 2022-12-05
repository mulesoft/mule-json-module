package org.mule.extension;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

public class ValidateCompliantWithSchemaContentTestCase extends AbstractSchemaValidationTestCase {
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
        json = doGetResource("inputs/objet-array-not-compliant.json");
        System.setProperty(VALIDATOR_FAIL_ON_TRAILING_TOKENS, "true");
    }

    @Test
    public void validateCompliantWithSchemaContent() throws Exception {
        expectedException.expectCause(new BaseMatcher<Throwable>() {
            @Override
            public boolean matches(Object item) {
                Exception e = (Exception) item;
                String report = e.getMessage();
                assertThat(report,containsString("Json content is not compliant with schema"));
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Json content is not compliant with schema");
            }
        });
        flowRunner("validateSchemaWithSchemaContent").withPayload(json).run();
    }
}
