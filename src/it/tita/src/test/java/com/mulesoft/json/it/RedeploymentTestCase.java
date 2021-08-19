/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.json.it;

import com.mulesoft.anypoint.tests.http.HttpResponse;
import com.mulesoft.anypoint.tita.environment.api.ApplicationSelector;
import com.mulesoft.anypoint.tita.environment.api.artifact.ApplicationBuilder;
import com.mulesoft.anypoint.tita.environment.api.artifact.Identifier;
import com.mulesoft.anypoint.tita.runner.ambar.Ambar;
import com.mulesoft.anypoint.tita.runner.ambar.annotation.Application;
import com.mulesoft.anypoint.tita.runner.ambar.annotation.runtime.Standalone;
import com.mulesoft.anypoint.tita.environment.api.runtime.Runtime;
import org.apache.maven.model.Dependency;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.mulesoft.anypoint.tita.environment.api.artifact.Identifier.identifier;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(Ambar.class)
public class RedeploymentTestCase{

    private static final Identifier api = identifier("api");
    private static final Identifier port = identifier("port");
    private static final Identifier REDEPLOYABLE_APP = identifier("json-module-app");
    
    @Standalone(testing="4.4.1-SNAPSHOT")
    private Runtime runtime;

    @Application
    public static ApplicationBuilder app(ApplicationSelector runtimeBuilder) {
        return runtimeBuilder
            .custom("json-module-app", "json-module-app.xml")
            .withDependency(jsonModuleDependency())
            .withResources("schema.json")
            .withApi(api, port);
    }

    @Test
    public void redeploymentTestCase(){
        HttpResponse response = runtime.api(api).request( "/validate").get();
        assertThat(response.statusCode(), is(equalTo(200)));
        for(int i =0;i<2;i++){
            runtime.redeploy(REDEPLOYABLE_APP);
            response = runtime.api(api).request( "/validate").get();
            assertThat(response.statusCode(), is(equalTo(200)));
        }
    }

    private static Dependency jsonModuleDependency() {
        Dependency jsonModule = new Dependency();
        jsonModule.setGroupId("org.mule.modules");
        jsonModule.setArtifactId("mule-json-module");
        jsonModule.setVersion("2.2.0-SNAPSHOT");
        jsonModule.setClassifier("mule-plugin");
        return jsonModule;
    }
}