/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mule.extension.TestVariables.SCHEMA_FIELD_INTEGER_REQUIRED_DRAFT202012;
import static org.mule.extension.TestVariables.SCHEMA_FIELD_STRING_REQUIRED_DRAFT202012;

import org.mule.module.json.api.JsonSchemaDereferencingMode;
import org.mule.runtime.core.api.event.CoreEvent;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConfigureTwoFlowsWithSameJsonSchemaVersion extends AbstractSchemaValidationTestCase {

    private String inputFieldInteger;
    private String inputFieldString;

    public static final String FLOW1 = "validate1";
    public static final String FLOW2 = "validate2";

    public static final String ERROR_MSG_EXPECTED_STRING_FOUND_INT_EXP = "string found, integer expected";
    public static final String ERROR_MSG_EXPECTED_INT_FOUND_STRING_EXP = "integer found, string expected";

    @Rule
    public ExpectedException expectedException = none();

    @Override
    protected String getConfigFile() {
        return "config/schema-two-validate-flows.xml";
    }

    @Override
    protected void doSetUp() throws Exception {
        inputFieldInteger = doGetResource("inputs/field-integer.json");
        inputFieldString = doGetResource("inputs/field-string.json");
    }

    @Test
    public void RunFlowsAndMakeValidations() throws Exception {

        runTestWithSchema(SCHEMA_FIELD_INTEGER_REQUIRED_DRAFT202012, inputFieldInteger, FLOW1);
        runTestWithSchema(SCHEMA_FIELD_STRING_REQUIRED_DRAFT202012, inputFieldString, FLOW2);

        runTestWithSchemaAndExpectError(SCHEMA_FIELD_INTEGER_REQUIRED_DRAFT202012, inputFieldString, FLOW1, ERROR_MSG_EXPECTED_STRING_FOUND_INT_EXP);
        runTestWithSchemaAndExpectError(SCHEMA_FIELD_STRING_REQUIRED_DRAFT202012, inputFieldInteger, FLOW2, ERROR_MSG_EXPECTED_INT_FOUND_STRING_EXP);
    }

    private void runTestWithSchema(String schema, String input, String nameFlow) throws Exception {
        CoreEvent flowResult = flowRunner(nameFlow)
                .withVariable("schema", schema)
                .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
                .withPayload(input).run();
        assertEquals(input, flowResult.getMessage().getPayload().getValue());
    }

    private void runTestWithSchemaAndExpectError(String schema, String input, String nameFlow, String errorMsgExpected) throws Exception {

        expectedException.expectCause(new BaseMatcher<Throwable>() {

            @Override
            public boolean matches(Object item) {
                Exception e = (Exception) item;
                String report = e.getMessage();
                assertThat(report, containsString(errorMsgExpected));

                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Error report did not match");
            }
        });

        flowRunner(nameFlow)
                .withVariable("schema", schema)
                .withVariable("dereferencing", JsonSchemaDereferencingMode.CANONICAL)
                .withPayload(input).run();
    }
}
