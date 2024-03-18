/*
 * Copyright (c) 2024, Salesforce, Inc.
 * SPDX-License-Identifier: Apache-2
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    private static final String RUNNER_COMPATIBLE_RUNTIME_VERSION = "4.3.0-HF-SNAPSHOT";

    @Standalone(testing = RUNNER_COMPATIBLE_RUNTIME_VERSION)
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