/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import com.fasterxml.jackson.databind.JsonNode;

public class JsonSchemaValidationFactory {

    private final JsonSchemaParser jsonSchemaParser = new JsonSchemaParser();

    public JsonSchemaValidator create(ValidatorKey key){

        JsonNode schemaJsonNode = jsonSchemaParser.getSchemaJsonNode(key.getSchemaContent(), key.getSchemas());

        if (ValidatorSchemaLibraryDetector.detectValidator(schemaJsonNode).equals(ValidationLibraries.NETWORKNT)) {
            return new JsonSchemaValidatorNetworkntWrapper(key.getSchemas(), key.getDereferencingType(), key.isAllowDuplicateKeys(),
                    key.isAllowArbitraryPrecision(), key.getSchemaRedirects(), schemaJsonNode);
        }
        return new JsonSchemaValidatorJavaJsonToolsWrapper(key.getSchemas(), key.getDereferencingType(), key.isAllowDuplicateKeys(),
                key.isAllowArbitraryPrecision(), key.getSchemaRedirects(), schemaJsonNode);
    }
}
