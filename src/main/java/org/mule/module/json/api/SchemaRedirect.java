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
package org.mule.module.json.api;

import org.mule.module.json.internal.util.ExcludeFromGeneratedCoverage;
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

  @ExcludeFromGeneratedCoverage
  public void setFrom(String from) {
    this.from = from;
  }

  @ExcludeFromGeneratedCoverage
  public void setTo(String to) {
    this.to = to;
  }
}
