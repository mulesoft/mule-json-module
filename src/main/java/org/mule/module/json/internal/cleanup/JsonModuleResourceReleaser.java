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

import java.lang.reflect.Field;
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
   * @param schedulerService
   */
  public JsonModuleResourceReleaser(SchedulerService schedulerService) {
    this.schedulerService = schedulerService;
  }

  /**
   *  Shutdowns the json validation library executors.
   *
   *  The Json Schema Validator Library Leaks threads
   *  in every application un-deployment.
   *  The leak is produced by the LoadingMessageSourceProvider. This class
   *  is not designed to work in a containerized application server, since it
   *  does not provide any method for stopping the ExecutorService.
   */
  public synchronized void releaseExecutors() {
    LOGGER.debug("Stopping the known executors services");
    Field bundleField = null;
    try {
      bundleField = JsonNodeReader.class.getDeclaredField("BUNDLE");
      MessageBundle messageBundle = (MessageBundle) bundleField.get(null);
      cleanMessageBundle(messageBundle);
    } catch (NoSuchFieldException | IllegalAccessException | InterruptedException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service reference of the JsonNodeReader class: {}",
                   ex.getMessage(), ex);
    }

    bundleField = null;
    try {
      bundleField = MessageBundles.class.getDeclaredField("BUNDLES");
      Map<Class<? extends MessageBundleLoader>, MessageBundle> bundles =
          (Map<Class<? extends MessageBundleLoader>, MessageBundle>) bundleField.get(null);
      for (MessageBundle bundle : bundles.values()) {
        cleanMessageBundle(bundle);
      }
    } catch (NoSuchFieldException | IllegalAccessException | InterruptedException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service references of the MessageBundles class: {}",
                   ex.getMessage(), ex);
    }

    bundleField = null;

    try {
      bundleField = ProcessingMessage.class.getDeclaredField("BUNDLE");
      MessageBundle messageBundle = (MessageBundle) bundleField.get(null);
      cleanMessageBundle(messageBundle);
    } catch (NoSuchFieldException | IllegalAccessException | InterruptedException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service references of the ProcessingMessage class: {}",
                   ex.getMessage(), ex);
    }

  }

  /**
   * Stops the Executor Service instances of the MessageBundle class
   * @param bundle a MessageBundle Instance
   * @throws NoSuchFieldException the requested field does not exists
   * @throws InterruptedException the executor shutdown was interrupted due timeout
   */
  private void cleanMessageBundle(MessageBundle bundle)
      throws NoSuchFieldException, IllegalAccessException, InterruptedException {

    Field providersField = null;
    Field serviceField = null;

    providersField = MessageBundle.class.getDeclaredField("providers");

    List<MessageSourceProvider> messageSourceProviders = (List<MessageSourceProvider>) providersField.get(bundle);
    for (MessageSourceProvider provider : messageSourceProviders) {
      if (provider instanceof LoadingMessageSourceProvider) {
        try {
          serviceField = LoadingMessageSourceProvider.class.getDeclaredField("service");
          ExecutorService service = (ExecutorService) serviceField.get(provider);
          service.shutdown();
          service.awaitTermination(10, SECONDS);
        } finally {
          serviceField = null;
        }
      }
    }
  }

  /**
   * Restores de ExecutorServices for the underlying library.
   */
  public synchronized void restoreExecutorServices() {
    Field bundleField = null;
    try {
      bundleField = JsonNodeReader.class.getDeclaredField("BUNDLE");
      MessageBundle messageBundle = (MessageBundle) bundleField.get(null);
      restoreMessageBundle(messageBundle);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service reference of the JsonNodeReader class: {}",
                   ex.getMessage(), ex);
    }

    bundleField = null;

    try {
      bundleField = MessageBundles.class.getDeclaredField("BUNDLES");
      Map<Class<? extends MessageBundleLoader>, MessageBundle> bundles =
          (Map<Class<? extends MessageBundleLoader>, MessageBundle>) bundleField.get(null);
      for (MessageBundle bundle : bundles.values()) {
        restoreMessageBundle(bundle);
      }
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service references of the MessageBundles class: {}",
                   ex.getMessage(), ex);
    }

    bundleField = null;

    try {
      bundleField = ProcessingMessage.class.getDeclaredField("BUNDLE");
      MessageBundle messageBundle = (MessageBundle) bundleField.get(null);
      restoreMessageBundle(messageBundle);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service references of the ProcessingMessage class: {}",
                   ex.getMessage(), ex);
    }

  }

  /**
   * Restores the executor service for a given MessageBundle Instance
   * @param bundle MessageBundle to restore the Executor service
   *               When restored the SchedulerService is used.
   * @throws NoSuchFieldException The target field does not exists
   * @throws IllegalAccessException The target field is not accessible
   */
  private void restoreMessageBundle(MessageBundle bundle)
      throws NoSuchFieldException, IllegalAccessException {

    Field providersField = null;
    Field serviceField = null;

    providersField = MessageBundle.class.getDeclaredField("providers");

    List<MessageSourceProvider> messageSourceProviders = (List<MessageSourceProvider>) providersField.get(bundle);

    for (MessageSourceProvider provider : messageSourceProviders) {
      if (provider instanceof LoadingMessageSourceProvider) {
        try {
          serviceField = LoadingMessageSourceProvider.class.getDeclaredField("service");
          ExecutorService service = (ExecutorService) serviceField.get(provider);
          if (service.isShutdown()) {
            service = schedulerService.customScheduler(SchedulerConfig.config().withMaxConcurrentTasks(3).withPrefix("pool"));
            serviceField.set(provider, service);
          }
        } finally {
          serviceField = null;
        }
      }
    }

  }

}
