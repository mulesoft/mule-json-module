/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal;

import org.mule.module.json.api.JsonError;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;

/**
 * The JSON module contains tools to help you deal with JSON documents
 */
@Xml(prefix = "json")
@Extension(name = "JSON")
@ErrorTypes(JsonError.class)
@Operations(ValidateJsonSchemaOperation.class)
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class JsonModule {

}
