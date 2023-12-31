/*
 * Copyright 2023 Salesforce, Inc. All rights reserved.
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.json.internal.cleanup;

import com.github.fge.jackson.JsonNodeReader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundleLoader;
import com.github.fge.msgsimple.load.MessageBundles;
import com.github.fge.msgsimple.provider.LoadingMessageSourceProvider;
import com.github.fge.msgsimple.provider.MessageSourceProvider;
import org.mule.runtime.api.scheduler.SchedulerConfig;
import org.mule.runtime.api.scheduler.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * This class provides convenience methods for managing the internal
 * executor services that the underlying library uses.
 **/
public class JsonModuleResourceReleaser {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonModuleResourceReleaser.class);
  SchedulerService schedulerService;

  /**
   * Creates a JsonModuleResourceReleaserInstance
   * @param schedulerService the scheduler service.
   */
  public JsonModuleResourceReleaser(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  /**
   *  Shutdowns the json validation library executors.
   *  The Json Schema Validator Library Leaks threads
   *  in every application un-deployment.
   *  The leak is produced by the LoadingMessageSourceProvider. This class
   *  is not designed to work in a containerized application server, since it
   *  does not provide any method for stopping the ExecutorService.
   */
  public synchronized void releaseExecutors() {
    LOGGER.debug("Stopping the known executors services");
    MessageBundle messageBundle = JsonNodeReader.getBundle();
    try {
      cleanMessageBundle(messageBundle);
    } catch (InterruptedException ex) {
      LOGGER.error(ex.getMessage(), ex);
      Thread.currentThread().interrupt();
    }

    Map<Class<? extends MessageBundleLoader>, MessageBundle> bundles =
        MessageBundles.getBundles();
    for (MessageBundle bundle : bundles.values()) {
      try {
        cleanMessageBundle(bundle);
      } catch (InterruptedException ex) {
        LOGGER.error(ex.getMessage(), ex);
        Thread.currentThread().interrupt();
      }
    }

    messageBundle = ProcessingMessage.getBundle();
    try {
      cleanMessageBundle(messageBundle);
    } catch (InterruptedException ex) {
      LOGGER.error(ex.getMessage(), ex);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Stops the Executor Service instances of the MessageBundle class
   * @param bundle a MessageBundle Instance
   * @throws InterruptedException the executor shutdown was interrupted due timeout
   */
  private void cleanMessageBundle(MessageBundle bundle)
      throws InterruptedException {

    List<MessageSourceProvider> messageSourceProviders = bundle.getProviders();
    for (MessageSourceProvider provider : messageSourceProviders) {
      if (provider instanceof LoadingMessageSourceProvider) {
        ExecutorService service = ((LoadingMessageSourceProvider) provider).getService();
        service.shutdown();
        service.awaitTermination(10, SECONDS);
      }
    }
  }


  /**
   * Restores de ExecutorServices for the underlying library.
   */
  public synchronized void restoreExecutorServices() {

    MessageBundle messageBundle = JsonNodeReader.getBundle();
    restoreMessageBundle(messageBundle);
    for (MessageBundle bundle : MessageBundles.getBundles().values()) {
      restoreMessageBundle(bundle);
    }

    messageBundle = ProcessingMessage.getBundle();
    restoreMessageBundle(messageBundle);

  }

  /**
   * Restores the executor service for a given MessageBundle Instance
   * @param bundle MessageBundle to restore the Executor service
   *               When restored the SchedulerService is used.
   */
  private void restoreMessageBundle(MessageBundle bundle) {

    List<MessageSourceProvider> messageSourceProviders = bundle.getProviders();

    for (MessageSourceProvider provider : messageSourceProviders) {
      if (provider instanceof LoadingMessageSourceProvider) {
        ExecutorService service = ((LoadingMessageSourceProvider) provider).getService();
        if (service.isShutdown()) {
          service = schedulerService.customScheduler(SchedulerConfig.config().withMaxConcurrentTasks(3).withPrefix("pool"));
          ((LoadingMessageSourceProvider) provider).setService(service);
        }
      }
    }
  }
}
