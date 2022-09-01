/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package platform.qa.configuration;

import jodd.util.Base64;
import lombok.Getter;
import lombok.SneakyThrows;
import platform.qa.entities.CentralConfiguration;
import platform.qa.entities.Configuration;
import platform.qa.entities.RegistryConfiguration;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.exceptions.ConfigurationExceptions;
import platform.qa.keycloak.KeycloakClient;
import platform.qa.utils.ConfigurationUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Load initial configuration for Central and Registry services.
 *  Currently, supports only {@link CentralConfiguration} and {@link RegistryConfiguration}.
 *  Example of usage :
 *  <p>
 *      {@code
 *          RegistryConfiguration regConfig = MasterConfig.getInstance().getRegistryConfiguration("example-namespace");
 *          GlobalConfiguration globalConfig = MasterConfig.getInstance().getGlobalConfiguration();
 *      }
 *  </p>
 */
public final class MasterConfig {
    private static MasterConfig instance;

    @Getter private final Configuration configuration;

    private final Service oc;
    private final String defaultNamespace;
    private final KeycloakClient keycloakClient;

    @Getter private final CentralConfig centralConfig;
    @Getter private final String cluster;
    @Getter private final String baseDomain;
    private final Map<String, RegistryConfig> registryConfigs = new HashMap<>();

    private MasterConfig() {
        configuration = ConfigurationUtils.uploadConfiguration("properties/platform.json");
        var properties = ConfigurationUtils.uploadPropertiesConfiguration("properties/platform.properties");

        defaultNamespace = System.getProperty("namespace") != null ? System.getProperty("namespace") : properties.getProperty("namespace");

        var ocUser = new User(
                System.getProperty("username") != null ? System.getProperty("username") : properties.getProperty("username"),
                System.getProperty("password") != null ? System.getProperty("password") : Base64.decodeToString(properties.getProperty("password"))
        );
        cluster = System.getProperty("cluster") != null ? System.getProperty("cluster") : properties.getProperty("cluster");
        baseDomain = System.getProperty("baseDomain") != null ? System.getProperty("baseDomain") : properties.getProperty("baseDomain");
        var ocUrl = String.format(
                System.getProperty("url") != null ? System.getProperty("url") : properties.getProperty("url"),
                cluster, baseDomain
        );

        oc = new Service(ocUrl, ocUser);
        centralConfig = new CentralConfig(configuration, oc);
        keycloakClient = centralConfig.getKeycloakClient();
    }

    public Map<String, RegistryConfig> setNamespaces(List<String> namespaces) {
        namespaces.forEach(namespace ->
                registryConfigs.put(
                        namespace,
                        new RegistryConfig(configuration, namespace, oc, keycloakClient, centralConfig.getCeph()
                        )
                )
        );
        return registryConfigs;
    }

    public RegistryConfig getRegistryConfig() {
        if (!registryConfigs.containsKey(defaultNamespace)) {
            registryConfigs.put(defaultNamespace, new RegistryConfig(configuration, defaultNamespace, oc, keycloakClient, centralConfig.getCeph()));
        }

        return registryConfigs.get(defaultNamespace);
    }

    @SneakyThrows(ConfigurationExceptions.MissingNamespaceInConfiguration.class)
    public RegistryConfig getRegistryConfig(String namespace) {
        if (!registryConfigs.containsKey(namespace)) {
            throw new ConfigurationExceptions.MissingNamespaceInConfiguration("Namespace " + namespace + " is missing for registry configuration!");
        }
        return registryConfigs.get(namespace);
    }

    /**
     * Provides instance of {@link MasterConfig}
     * @return {@link MasterConfig}
     */
    public static MasterConfig getInstance() {
        if (instance != null) {
            return instance;
        }

        instance = new MasterConfig();
        return instance;
    }
}
