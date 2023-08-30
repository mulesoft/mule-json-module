/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension;

import static org.mule.runtime.core.api.util.IOUtils.getResourceAsString;

import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public abstract class AbstractSchemaValidationTestCase extends MuleArtifactFunctionalTestCase {

  protected static final String SCHEMA_FSTAB_GOOD_JSON = "inputs/fstab-good.json";
  protected static final String SCHEMA_FSTAB_BAD_JSON = "inputs/fstab-bad.json";
  protected static final String SCHEMA_FSTAB_BAD2_JSON = "inputs/fstab-bad2.json";
  protected static final String SCHEMA_FSTAB_GOOD_INLINE_JSON = "inputs/fstab-good-inline.json";
  protected static final String SCHEMA_FSTAB_DUPLICATE_KEYS = "inputs/fstab-duplicate-keys.json";

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

  protected static String getFstabWithDuplicateKeys() throws Exception {
    return doGetResource(SCHEMA_FSTAB_DUPLICATE_KEYS);
  }
}
