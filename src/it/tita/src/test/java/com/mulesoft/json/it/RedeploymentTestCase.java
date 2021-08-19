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
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.mulesoft.anypoint.tita.environment.api.artifact.Identifier.identifier;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

@RunWith(Ambar.class)
public class RedeploymentTestCase{

    private static final Identifier api1 = identifier("api1");
    private static final Identifier api2 = identifier("api2");
    private static final Identifier port = identifier("port");
    private static final Identifier REDEPLOYABLE_APP = identifier("json-module-app");
    
    @Standalone(testing="4.3.0")
    private Runtime runtime;

    @Application
    public static ApplicationBuilder app(ApplicationSelector runtimeBuilder) {
        return runtimeBuilder
            .custom("json-module-app", "json-module-app.xml")
            .withResources("schema.json")
            .withTemplatePomFile("json-module-app-pom.xml")
            .withApi(api1, port)
            .withApi(api2,port);
    }

    @Test
    public void redeploymentTestCase(){
        for(int i =0;i<10;i++){
            HttpResponse response = runtime.api(api1).request( "/validate").get();
            assertThat(response.statusCode(), is(equalTo(200)));
            runtime.redeploy(REDEPLOYABLE_APP);
            response = runtime.api(api2).request( "/check").get();
            assertThat(Integer.valueOf(response.asString()),is(lessThan(10)));
            assertThat(response.statusCode(), is(equalTo(200)));
        }
    }

}