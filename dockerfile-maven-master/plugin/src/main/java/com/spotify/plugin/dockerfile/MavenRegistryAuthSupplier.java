/*-
 * -\-\-
 * Dockerfile Maven Plugin
 * --
 * Copyright (C) 2017 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

package com.spotify.plugin.dockerfile;

import com.spotify.docker.client.ImageRef;
import com.spotify.docker.client.auth.RegistryAuthSupplier;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.RegistryAuth;
import com.spotify.docker.client.messages.RegistryConfigs;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.SettingsProblem;
import org.apache.maven.settings.crypto.DefaultSettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecrypter;
import org.apache.maven.settings.crypto.SettingsDecryptionRequest;
import org.apache.maven.settings.crypto.SettingsDecryptionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenRegistryAuthSupplier implements RegistryAuthSupplier {

  private static final Logger log = LoggerFactory.getLogger(MavenRegistryAuthSupplier.class);

  private final Settings settings;
  private final SettingsDecrypter settingsDecrypter;

  public MavenRegistryAuthSupplier(final Settings settings,
                                   final SettingsDecrypter settingsDecrypter) {
    this.settings = settings;
    this.settingsDecrypter = settingsDecrypter;
  }

  @Override
  public RegistryAuth authFor(final String imageName) throws DockerException {
    final ImageRef ref = new ImageRef(imageName);
    Server server = settings.getServer(ref.getRegistryName());
    if (server != null) {
      return createRegistryAuth(server);
    }
    log.warn("Did not find maven server configuration for docker server " + ref.getRegistryName());
    return null;
  }

  @Override
  public RegistryAuth authForSwarm() throws DockerException {
    return null;
  }

  @Override
  public RegistryConfigs authForBuild() throws DockerException {
    final Map<String, RegistryAuth> allConfigs = new HashMap<>();
    for (Server server : settings.getServers()) {
      allConfigs.put(server.getId(), createRegistryAuth(server));
    }
    return RegistryConfigs.create(allConfigs);
  }

  private RegistryAuth createRegistryAuth(Server server) throws DockerException {
    SettingsDecryptionRequest decryptionRequest = new DefaultSettingsDecryptionRequest(server);
    SettingsDecryptionResult decryptionResult = settingsDecrypter.decrypt(decryptionRequest);

    if (decryptionResult.getProblems().isEmpty()) {
      log.debug("Successfully decrypted Maven server password");
    } else {
      for (SettingsProblem problem : decryptionResult.getProblems()) {
        log.error("Settings problem for server {}: {}", server.getId(), problem);
      }

      throw new DockerException("Failed to decrypt Maven server password");
    }

    return RegistryAuth.builder()
            .username(server.getUsername())
            .password(decryptionResult.getServer().getPassword())
            .build();
  }
}
