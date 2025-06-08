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
package org.mule.module.json.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mule.runtime.extension.api.exception.ModuleException;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static org.mule.module.json.api.JsonError.INVALID_INPUT_JSON;
import static org.mule.module.json.api.JsonError.SCHEMA_NOT_FOUND;
import static org.mule.module.json.internal.ValidatorCommonUtils.isBlank;
import static org.mule.module.json.internal.ValidatorCommonUtils.resolveLocationIfNecessary;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * The objective is get the Json Schema, from a Path (SchemaLocation) or a String(SchemaContent), like a JsonNode.
 */
public class JsonSchemaParser {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger logger = getLogger(JsonSchemaParser.class);
  private static final Set<String> KNOWN_SELF_HOSTS = getLocalHostnamesAndIps();

  private JsonSchemaParser() {}

  public static JsonNode getSchemaJsonNode(String schemaContent, String schemaLocation) {

    if (!isBlank(schemaContent)) {
      try {
        return objectMapper.readTree(schemaContent);
      } catch (JsonProcessingException e) {
        logger.error(e.getMessage());
        throw new ModuleException(format("Malformed Json Schema: %s", e.getMessage()), INVALID_INPUT_JSON);
      }
    }
    try {
      checkState(schemaLocation != null, "schemaLocation has not been provided");
      if (isSelfReferencingOrInternal(schemaLocation)) {
        throw new ModuleException("Self-referencing or internal URLs are not allowed for schemaLocation", INVALID_INPUT_JSON);
      }
      return objectMapper.readTree(new URL(resolveLocationIfNecessary(schemaLocation)));

    } catch (IllegalArgumentException | MalformedURLException e) {
      throw new ModuleException(format("Could not load JSON schema [%s]. %s", schemaLocation, e.getMessage()),
                                SCHEMA_NOT_FOUND, e);
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new ModuleException(format("Malformed Json Schema: %s", e.getMessage()), INVALID_INPUT_JSON);
    }
  }

  private static boolean isSelfReferencingOrInternal(String schemaUrl) {
    try {
      URL url = new URL(schemaUrl);
      String host = url.getHost();
      // Normalize to lowercase to catch variations like "LOCALHOST"
      host = host.toLowerCase(Locale.ROOT);
      InetAddress address = InetAddress.getByName(host);
      return address.isAnyLocalAddress() // covers 0.0.0.0
          || address.isLoopbackAddress() // 127.x.x.x
          || address.isSiteLocalAddress() // 192.168.x.x, 10.x.x.x, etc.
          || KNOWN_SELF_HOSTS.contains(host)
          || KNOWN_SELF_HOSTS.contains(address.getHostAddress());

    } catch (MalformedURLException | UnknownHostException e) {
      return false;
    }
  }

  private static Set<String> getLocalHostnamesAndIps() {
    Set<String> selfHosts = new HashSet<>();
    try {
      InetAddress localHost = InetAddress.getLocalHost();
      selfHosts.add(localHost.getHostName().toLowerCase(Locale.ROOT));
      selfHosts.add(localHost.getCanonicalHostName().toLowerCase(Locale.ROOT));
      selfHosts.add(localHost.getHostAddress());

      // Also add all IPs from all interfaces
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        NetworkInterface netInterface = interfaces.nextElement();
        Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
        while (addresses.hasMoreElements()) {
          InetAddress addr = addresses.nextElement();
          selfHosts.add(addr.getHostAddress());
        }
      }
    } catch (Exception e) {
      logger.warn("Could not resolve local hostnames and IPs for self-reference check", e);
    }
    return selfHosts;
  }
}
