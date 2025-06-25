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
package org.mule.module.json.internal.error;

import org.junit.Test;
import org.mule.runtime.extension.api.error.ErrorTypeDefinition;

import java.util.Set;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.api.JsonError.INVALID_SCHEMA;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_HONOURED;

public class SchemaValidatorErrorTypeProviderTest {

  @Test
  public void testGetErrorTypes_returnsExpectedErrors() {
    SchemaValidatorErrorTypeProvider provider = new SchemaValidatorErrorTypeProvider();
    Set<ErrorTypeDefinition> errorTypes = provider.getErrorTypes();

    // Validate the set is not null or empty
    assertNotNull("Error types should not be null", errorTypes);
    assertThat("Error types size should be 4", errorTypes.size(), is(4));

    // Validate expected error types are included
    assertTrue(errorTypes.contains(SCHEMA_NOT_HONOURED));
    assertTrue(errorTypes.contains(INVALID_INPUT_JSON));
    assertTrue(errorTypes.contains(INVALID_SCHEMA));
    assertTrue(errorTypes.contains(SCHEMA_NOT_FOUND));
  }
}
