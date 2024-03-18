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

  private static final String BUNDLE = "BUNDLE";
  private static final String BUNDLES = "BUNDLES";
  private static final String PROVIDERS = "providers";
  private static final String SERVICE = "service";
  private static final String POOL = "pool";
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
   *  The Json Schema Validator Library Leaks threads
   *  in every application un-deployment.
   *  The leak is produced by the LoadingMessageSourceProvider. This class
   *  is not designed to work in a containerized application server, since it
   *  does not provide any method for stopping the ExecutorService.
   */
  public synchronized void releaseExecutors() {
    LOGGER.debug("Stopping the known executors services");
    Field bundleField;
    try {
      bundleField = JsonNodeReader.class.getDeclaredField(BUNDLE);
      MessageBundle messageBundle = (MessageBundle) bundleField.get(null);
      cleanMessageBundle(messageBundle);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service reference of the JsonNodeReader class: {}",
                   ex.getMessage(), ex);
    }

    try {
      bundleField = MessageBundles.class.getDeclaredField(BUNDLES);
      Map<Class<? extends MessageBundleLoader>, MessageBundle> bundles =
          (Map<Class<? extends MessageBundleLoader>, MessageBundle>) bundleField.get(null);
      for (MessageBundle bundle : bundles.values()) {
        cleanMessageBundle(bundle);
      }
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service references of the MessageBundles class: {}",
                   ex.getMessage(), ex);
    }

    try {
      bundleField = ProcessingMessage.class.getDeclaredField(BUNDLE);
      MessageBundle messageBundle = (MessageBundle) bundleField.get(null);
      cleanMessageBundle(messageBundle);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service references of the ProcessingMessage class: {}",
                   ex.getMessage(), ex);
    }

  }

  /**
   * Stops the Executor Service instances of the MessageBundle class
   * @param bundle a MessageBundle Instance
   * @throws NoSuchFieldException the requested field does not exists
   */
  private void cleanMessageBundle(MessageBundle bundle)
      throws NoSuchFieldException, IllegalAccessException {

    Field providersField;
    Field serviceField;

    providersField = MessageBundle.class.getDeclaredField(PROVIDERS);

    List<MessageSourceProvider> messageSourceProviders = (List<MessageSourceProvider>) providersField.get(bundle);
    for (MessageSourceProvider provider : messageSourceProviders) {
      if (provider instanceof LoadingMessageSourceProvider) {
        serviceField = LoadingMessageSourceProvider.class.getDeclaredField(SERVICE);
        ExecutorService service = (ExecutorService) serviceField.get(provider);
        service.shutdown();

        try {
          service.awaitTermination(10, SECONDS);
        } catch (InterruptedException exception) {
          service.shutdownNow();
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  /**
   * Restores de ExecutorServices for the underlying library.
   */
  public synchronized void restoreExecutorServices() {
    Field bundleField;
    try {
      bundleField = JsonNodeReader.class.getDeclaredField(BUNDLE);
      MessageBundle messageBundle = (MessageBundle) bundleField.get(null);
      restoreMessageBundle(messageBundle);
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service reference of the JsonNodeReader class: {}",
                   ex.getMessage(), ex);
    }

    try {
      bundleField = MessageBundles.class.getDeclaredField(BUNDLES);
      Map<Class<? extends MessageBundleLoader>, MessageBundle> bundles =
          (Map<Class<? extends MessageBundleLoader>, MessageBundle>) bundleField.get(null);
      for (MessageBundle bundle : bundles.values()) {
        restoreMessageBundle(bundle);
      }
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      LOGGER.error("Caught exception while stopping the Executor Service references of the MessageBundles class: {}",
                   ex.getMessage(), ex);
    }

    try {
      bundleField = ProcessingMessage.class.getDeclaredField(BUNDLE);
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

    Field providersField;
    Field serviceField;

    providersField = MessageBundle.class.getDeclaredField(PROVIDERS);

    List<MessageSourceProvider> messageSourceProviders = (List<MessageSourceProvider>) providersField.get(bundle);

    for (MessageSourceProvider provider : messageSourceProviders) {
      if (provider instanceof LoadingMessageSourceProvider) {
        serviceField = LoadingMessageSourceProvider.class.getDeclaredField(SERVICE);
        ExecutorService service = (ExecutorService) serviceField.get(provider);
        if (service.isShutdown()) {
          service = schedulerService.customScheduler(SchedulerConfig.config().withMaxConcurrentTasks(3).withPrefix(POOL));
          serviceField.set(provider, service);
        }
      }
    }
  }
}
