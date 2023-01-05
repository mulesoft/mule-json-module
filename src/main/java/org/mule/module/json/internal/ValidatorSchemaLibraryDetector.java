/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import static com.networknt.schema.SpecVersion.VersionFlag.V6;
import static com.networknt.schema.SpecVersion.VersionFlag.V7;
import static com.networknt.schema.SpecVersion.VersionFlag.V201909;
import static com.networknt.schema.SpecVersion.VersionFlag.V202012;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.SpecVersionDetector;

public class ValidatorSchemaLibraryDetector {

    /**
     * Networknt library support these new versions of Json Schema, else we use java-json-tools library.
     * If a version is not detected the method SpecVersionDetector.detect
     * throws and exeption and is returned JAVA_JSON_TOOLS library - Draft V3 o V4.
     */
    public static ValidationLibraries detectValidator(JsonNode jsonNode){
        try {
            if (SpecVersionDetector.detect(jsonNode).equals(V6) ||
                    SpecVersionDetector.detect(jsonNode).equals(V7) ||
                    SpecVersionDetector.detect(jsonNode).equals(V201909) ||
                    SpecVersionDetector.detect(jsonNode).equals(V202012)){
                return ValidationLibraries.NETWORKNT;
            }else{
                return ValidationLibraries.JAVA_JSON_TOOLS;
            }
        } catch (JsonSchemaException exception) {
            return ValidationLibraries.JAVA_JSON_TOOLS;
        }
    }
}