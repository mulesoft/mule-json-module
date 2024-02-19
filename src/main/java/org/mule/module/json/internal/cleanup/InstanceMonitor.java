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
package org.mule.module.json.internal.cleanup;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class to account the number of existing instances of ValidateJsonSchemaOperations
 */
public class InstanceMonitor {

  private final AtomicInteger monitor = new AtomicInteger(0);

  /**
   * Increases the instance count
   * @return the actual number of instances
   */
  public int register() {
    return monitor.incrementAndGet();
  }

  /**
   * decresaes the instance count
   * @return the actual number of instances
   */
  public int unregister() {
    return monitor.decrementAndGet();
  }
}
