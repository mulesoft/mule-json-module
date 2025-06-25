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
package org.mule.module.json.internal;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class JsonSchemaTest {

  private JsonSchema jsonSchema;

  @Before
  public void setUp() {
    jsonSchema = new JsonSchema();
  }

  @Test
  public void testGetSchema_whenSchemaIsSet_shouldReturnSchemaValue() throws Exception {
    String expectedSchema = "schema.json";
    setPrivateField(jsonSchema, "schema", expectedSchema);

    assertEquals(expectedSchema, jsonSchema.getSchema());
  }

  @Test
  public void testGetContents_whenContentsIsSet_shouldReturnContentsValue() throws Exception {
    String expectedContents = "{ \"type\": \"object\" }";
    setPrivateField(jsonSchema, "contents", expectedContents);

    assertEquals(expectedContents, jsonSchema.getContents());
  }

  @Test
  public void testGetSchema_whenSchemaIsNull_shouldReturnNull() {
    assertNull(jsonSchema.getSchema());
  }

  @Test
  public void testGetContents_whenContentsIsNull_shouldReturnNull() {
    assertNull(jsonSchema.getContents());
  }

  private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
    Field field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}
