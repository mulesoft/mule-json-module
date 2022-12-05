package org.mule.extension;

import org.junit.Test;
import org.mule.runtime.core.api.event.CoreEvent;

import static org.junit.Assert.assertEquals;

public class ValidateWithSchemaContentDefaultTestCase extends AbstractSchemaValidationTestCase {
    private String json;

    @Override
    protected String getConfigFile(){ return "validate-schema-with-schemaContent-config.xml";}

    @Override
    protected void doSetUp() throws Exception {
        json = doGetResource("inputs/object-array.json");
    }

    @Test
    public void validateDefaultBehaviourWithSchemaContent() throws Exception {
        CoreEvent event = flowRunner("validateSchemaWithSchemaContent").withPayload(json).run();
        assertEquals(json,event.getMessage().getPayload().getValue());
    }
}
