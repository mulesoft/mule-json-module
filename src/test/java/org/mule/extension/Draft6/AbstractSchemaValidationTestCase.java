/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.Draft6;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.mule.extension.TestCommonVariablesUtil.SCHEMA_FSTAB_BAD_JSON;
import static org.mule.extension.TestCommonVariablesUtil.SCHEMA_FSTAB_GOOD_JSON;
import static org.mule.extension.TestCommonVariablesUtil.SCHEMA_FSTAB_BAD2_JSON;
import static org.mule.extension.TestCommonVariablesUtil.SCHEMA_FSTAB_GOOD_INLINE_JSON;
import static org.mule.extension.TestCommonVariablesUtil.SCHEMA_FSTAB_DUPLICATE_KEYS;
import static org.mule.runtime.core.api.util.IOUtils.getResourceAsString;

public abstract class AbstractSchemaValidationTestCase extends MuleArtifactFunctionalTestCase {

  //Schemas
  //Draft 6
  protected static final String SCHEMA_FSTAB_JSON_DRAFT6 = "/Draft6/schemas/fstab.json";
  protected static final String SCHEMA_FSTAB_INLINE_DRAFT6 = "/Draft6/schemas/fstab-inline.json";
  protected static final String SCHEMA_FSTAB_REFERRING_DRAFT6 = "/Draft6/schemas/fstab-referring.json";
  protected static final String SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS_DRAFT6 =
      "Draft6/schemas/fstab-arbitrary-precision-keys.json";
  protected static final String SCHEMA_REQUIRED_OBJECT_ARRAY_DRAFT6 = "/Draft6/schemas/object-array-schema.json";

  protected static String getGoodFstab() throws Exception {
    return doGetResource(SCHEMA_FSTAB_GOOD_JSON);
  }

  protected static String getBadFstab() throws Exception {
    return doGetResource(SCHEMA_FSTAB_BAD_JSON);
  }

  protected static String getBadFstab2() throws Exception {
    return doGetResource(SCHEMA_FSTAB_BAD2_JSON);
  }

  protected static String getGoodFstabInline() throws Exception {
    return doGetResource(SCHEMA_FSTAB_GOOD_INLINE_JSON);
  }

  protected static String doGetResource(String path) throws Exception {
    return getResourceAsString(path, ValidateJsonSchemaTestCase.class);
  }

  protected static InputStream toStream(String content) {
    return new ByteArrayInputStream(content.getBytes());
  }

  static String getFstabWithDuplicateKeys() throws Exception {
    return doGetResource(SCHEMA_FSTAB_DUPLICATE_KEYS);
  }
}
