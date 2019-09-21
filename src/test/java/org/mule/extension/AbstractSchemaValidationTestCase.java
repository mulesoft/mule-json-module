/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
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

  protected static final String SCHEMA_FSTAB_GOOD_JSON = "schema/fstab-good.json";
  protected static final String SCHEMA_FSTAB_BAD_JSON = "schema/fstab-bad.json";
  protected static final String SCHEMA_FSTAB_BAD2_JSON = "schema/fstab-bad2.json";
  protected static final String SCHEMA_FSTAB_GOOD_INLINE_JSON = "schema/fstab-good-inline.json";
  protected static final String SCHEMA_FSTAB_DRAFTV3 = "/schema/fstab-draftv3.json";
  protected static final String SCHEMA_FSTAB_INLINE = "/schema/fstab-inline.json";
  protected static final String SCHEMA_FSTAB_REFERRING = "/schema/fstab-referring.json";
  protected static final String SCHEMA_FSTAB_JSON = "/schema/fstab.json";
  protected static final String FAKE_SCHEMA_URI = "http://mule.org/schemas/fstab.json";
  protected static final String SCHEMA_FSTAB_DUPLICATE_KEYS = "schema/fstab-duplicate-keys.json";
  protected static final String SCHEMA_FSTAB_ARBITRARY_PRECISION_KEYS = "schema/fstab-arbitrary-precision-keys.json";


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
