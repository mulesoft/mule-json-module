/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.api;

import org.mule.runtime.extension.api.annotation.param.Parameter;

/**
 * A redirection between a "from" and a "to" location
 *
 * @since 1.0
 */
public class SchemaRedirect {

  /**
   * The original location
   */
  @Parameter
  private String from;

  /**
   * The redirection location
   */
  @Parameter
  private String to;

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }
}
