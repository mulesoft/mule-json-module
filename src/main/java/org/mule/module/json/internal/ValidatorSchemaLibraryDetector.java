package org.mule.module.json.internal;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchemaException;
import com.networknt.schema.SpecVersionDetector;

import static com.networknt.schema.SpecVersion.VersionFlag.*;
import static com.networknt.schema.SpecVersion.VersionFlag.V202012;

public class ValidatorSchemaLibraryDetector {

    /**
     * Networknt library support these new versions of Json Schema, else we use com.github.fge library.
     * If a version is not detected the method SpecVersionDetector.detect
     * throws and exeption and is returned JAVA_JSON_TOOLS library - Draft V3 o V4.
     */
    public ValidationLibraries detectValidator(JsonNode jsonNode){
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
