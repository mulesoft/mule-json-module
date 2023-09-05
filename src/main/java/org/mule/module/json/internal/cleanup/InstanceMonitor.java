/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
