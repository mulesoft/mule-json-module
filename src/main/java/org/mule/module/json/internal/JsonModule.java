package org.mule.module.json.internal;

import org.mule.module.json.api.JsonError;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;


/**
 * The JSON module contains tools to help you deal with JSON documents
 */
@Xml(prefix = "json")
@Extension(name = "JSON")
@ErrorTypes(JsonError.class)
@Operations(ValidateJsonSchemaOperation.class)
public class JsonModule {

}
