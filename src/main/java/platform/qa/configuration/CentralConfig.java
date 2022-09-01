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

import io.fabric8.openshift.api.model.operatorhub.v1alpha1.CatalogSource;
import lombok.Getter;
import platform.qa.entities.CentralConfiguration;
import platform.qa.entities.Configuration;
import platform.qa.entities.Service;
import platform.qa.entities.ServiceConfiguration;
import platform.qa.entities.User;
import platform.qa.keycloak.KeycloakClient;
import platform.qa.oc.OkdClient;
import platform.qa.providers.impl.PlatformUserProvider;
import platform.qa.utils.OpenshiftServiceProvider;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Initiate and store Central services.
 * Central services defined in {@link CentralConfiguration}
 */
public final class CentralConfig {
    private final CentralConfiguration configuration;

    private Service ceph;
    private Service kibana;
    private Service kiali;
    private Service jaeger;
    private Service defaultGrafana;
    private Service customGrafana;
    private Service jenkins;
    private Service gerrit;
    private Service keycloak;
    private Service wiremock;
    private Service controlPlane;
    private Service nexus;

    private KeycloakClient keycloakClient;
    @Getter
    private Service ocService;
    @Getter
    private PlatformUserProvider platformUserProvider;

    private Service vaultService;

    private AtomicReference<User> controlPlaneUser = new AtomicReference<>();

    public CentralConfig(Configuration configuration, Service ocService) {
        this.configuration = configuration.getCentralConfiguration();
        this.ocService = ocService;
        platformUserProvider = new PlatformUserProvider(ocService, getKeycloakClient(), "properties/platform-users"
                + ".json");
    }

    public Service getCeph() {
        if (ceph != null) {
            return ceph;
        }

        ceph = getService(configuration.getCeph());
        return ceph;
    }

    public Service getKibana() {
        if (kibana != null) {
            return kibana;
        }

        kibana = getService(configuration.getKibana());
        return kibana;
    }

    public Service getKiali() {
        if (kiali != null) {
            return kiali;
        }

        kiali = getService(configuration.getKiali());
        return kiali;
    }

    public Service getJager() {
        if (jaeger != null) {
            return jaeger;
        }

        jaeger = getService(configuration.getJager());
        return jaeger;
    }

    public Service getDefaultGrafana() {
        if (defaultGrafana != null) {
            return defaultGrafana;
        }

        defaultGrafana = getService(configuration.getDefaultGrafana());
        return defaultGrafana;
    }

    public Service getCustomGrafana() {
        if (customGrafana != null) {
            return customGrafana;
        }

        customGrafana = getService(configuration.getCustomGrafana());
        return customGrafana;
    }

    public Service getJenkins() {
        if (jenkins != null) {
            return jenkins;
        }

        jenkins = getServiceWithUser(configuration.getJenkins());
        return jenkins;
    }

    public Service getGerrit() {
        if (gerrit != null) {
            return gerrit;
        }

        gerrit = getServiceWithUser(configuration.getGerrit());
        return gerrit;
    }

    public Service getKeycloak() {
        if (keycloak != null) {
            return keycloak;
        }

        keycloak = getServiceWithUser(configuration.getKeycloak());
        return keycloak;
    }

    public Service getVaultService() {
        if (vaultService != null) {
            return vaultService;
        }

        vaultService = getServiceVaultWithToken(configuration.getVault());
        return vaultService;
    }

    public Service getWiremock() {
        if (wiremock != null) {
            return wiremock;
        }

        wiremock = getService(configuration.getWiremock());
        String wiremockUrl = wiremock.getUrl();
        wiremock.setUrl(wiremockUrl != null ? wiremockUrl.replaceAll("https://", "").replaceAll("/$", "") : null);
        return wiremock;
    }

    public Service getControlPlane() {
        if (controlPlane != null) {
            return controlPlane;
        }

        controlPlane = getService(configuration.getControlPlane());
        return controlPlane;
    }

    public Service getNexus() {
        if (nexus != null) {
            return nexus;
        }

        nexus = getService(configuration.getNexus());
        return nexus;
    }

    public KeycloakClient getKeycloakClient() {
        if (keycloakClient != null) {
            return keycloakClient;
        }

        keycloakClient = new KeycloakClient(getKeycloak());
        return keycloakClient;
    }

    public List<CatalogSource> getClusterSources() {
        return new OkdClient(ocService).getClusterSources();
    }

    private Service getServiceVaultWithToken(ServiceConfiguration configuration) {
        Service service = getService(configuration);
        return Service.builder()
                .url(service.getUrl())
                .token(getTokenForService(configuration))
                .build();
    }

    private Service getServiceWithUser(ServiceConfiguration configuration) {
        Service service = getService(configuration);
        User user = getUserForService(configuration);

        return new Service(service.getUrl(), user);
    }

    private String getTokenForService(ServiceConfiguration configuration) {
        return new OkdClient(ocService, configuration.getNamespace()).getTokenVault(configuration.getSecret());
    }

    private User getUserForService(ServiceConfiguration configuration) {
        return new OkdClient(ocService, configuration.getNamespace()).getCredentials(configuration.getSecret());
    }

    public Service getService(ServiceConfiguration configuration) {
        return OpenshiftServiceProvider.getService(ocService, configuration);
    }

}
