/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.api;

/**
 * The URI dereferencing modes defined in Json Schema draft 4
 *
 * @since 1.0
 */
public enum JsonSchemaDereferencingMode {

  /**
   * Canonical mode
   */
  CANONICAL,

  /**
   * Inline mode
   */
  INLINE
}
