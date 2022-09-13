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

import lombok.Getter;
import platform.qa.entities.Ceph;
import platform.qa.entities.Configuration;
import platform.qa.entities.Db;
import platform.qa.entities.Redis;
import platform.qa.entities.RegistryConfiguration;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.enumeration.CitusUsers;
import platform.qa.keycloak.KeycloakClient;
import platform.qa.oc.OkdClient;
import platform.qa.providers.impl.RegistryUserProvider;
import platform.qa.utils.OpenshiftServiceProvider;

/**
 * Initiate and store Central services.
 * Central services defined in {@link RegistryConfig}
 */
public final class RegistryConfig {
    private final String namespace;
    private final RegistryConfiguration configuration;
    @Getter
    private final OkdClient ocClient;
    private final KeycloakClient keycloakClient;
    private final Service ceph;

    private final Service oc;

    @Getter
    private RegistryUserProvider registryUserProvider;

    private User citusAdminRole;
    private User citusApplicationRole;
    private User citusRegistryOwnerRole;
    private User citusSettingsRole;
    private User citusAuditRole;
    private User citusAnalyticsRole;
    private User citusExcerptExportedRole;
    private User citusExcerptRole;
    private User citusExcerptWorkerRole;


    private Db citusMaster;
    private Db citusReplica;

    private Service registryManagement;
    private Service dataFactorySoap;
    private Service dataFactory;
    private Service dataFactoryExternalPlatform;
    private Service dataFactoryExternalSystem;
    private Service digitalSignatureOps;
    private Service userSettings;
    private Service bpms;
    private Service formManagementProvider;
    private Service formManagementModeler;
    private Service excerpt;
    private Service userTaskManagement;
    private Service userProcessManagement;
    private Service digitalDocument;
    private Service officerPortal;
    private Service citizenPortal;
    private Service adminPortal;
    private Service processHistory;
    private Service processWebserviceGateway;
    private Service redashViewer;
    private Service redashAdmin;
    private Service gerrit;
    private Service jenkins;

    private Ceph signatureCeph;
    private Ceph fileDataCeph;
    private Ceph fileLowcodeCeph;
    private Ceph excerptCeph;

    private Redis redis;

    public RegistryConfig(Configuration configuration,
                          String namespace,
                          Service ocService,
                          KeycloakClient keycloakClient,
                          Service ceph) {
        this.configuration = configuration.getRegistryConfiguration();
        this.keycloakClient = keycloakClient;
        this.ceph = ceph;
        this.namespace = namespace;
        oc = ocService;

        ocClient = new OkdClient(ocService, namespace);
        registryUserProvider = new RegistryUserProvider(namespace, keycloakClient, "properties/users.json");
    }

    public Ceph getSignatureCeph() {
        if (signatureCeph != null) {
            return signatureCeph;
        }

        signatureCeph = OpenshiftServiceProvider.getCephService(ocClient,
                configuration.getCeph().getSignatureBucket(), ceph.getUrl());
        return signatureCeph;
    }

    public Ceph getFileDataCeph() {
        if (fileDataCeph != null) {
            return fileDataCeph;
        }

        fileDataCeph = OpenshiftServiceProvider.getCephService(ocClient, configuration.getCeph().getDataFileBucket(),
                ceph.getUrl());
        return fileDataCeph;
    }

    public Ceph getFileLowcodeCeph() {
        if (fileLowcodeCeph != null) {
            return fileLowcodeCeph;
        }

        fileLowcodeCeph = OpenshiftServiceProvider.getCephService(ocClient,
                configuration.getCeph().getLowCodeFileBucket(), ceph.getUrl());
        return fileLowcodeCeph;
    }

    public Ceph getExcerptCeph() {
        if (excerptCeph != null) {
            return excerptCeph;
        }

        excerptCeph = OpenshiftServiceProvider.getCephService(ocClient, configuration.getCeph().getExcerptBucket(),
                ceph.getUrl());
        return excerptCeph;
    }

    public Db getCitusMaster() {
        if (citusMaster != null) {
            return citusMaster;
        }

        citusMaster = OpenshiftServiceProvider.getDbService(ocClient, configuration.getCitusMaster());
        return citusMaster;
    }

    public Db getCitusReplica() {
        if (citusReplica != null) {
            return citusReplica;
        }

        citusReplica = OpenshiftServiceProvider.getDbService(ocClient, configuration.getCitusReplica());
        return citusReplica;
    }

    public User getCitusAdminRole() {
        if (citusAdminRole != null) {
            return citusAdminRole;
        }

        citusAdminRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.ADMIN_ROLE.getRoleName()
        );
        return citusAdminRole;
    }

    public User getCitusApplicationRole() {
        if (citusApplicationRole != null) {
            return citusApplicationRole;
        }

        citusApplicationRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.APPLICATION_ROLE.getRoleName()
        );
        return citusApplicationRole;
    }

    public User getCitusRegistryOwnerRole() {
        if (citusRegistryOwnerRole != null) {
            return citusRegistryOwnerRole;
        }

        citusRegistryOwnerRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.REGISTRY_OWNER_ROLE.getRoleName()
        );
        return citusRegistryOwnerRole;
    }

    public User getCitusSettingsRole() {
        if (citusSettingsRole != null) {
            return citusSettingsRole;
        }

        citusSettingsRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.SETTINGS_ROLE.getRoleName()
        );
        return citusSettingsRole;
    }

    public User getCitusAuditRole() {
        if (citusAuditRole != null) {
            return citusAuditRole;
        }

        citusAuditRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.AUDIT_ROLE.getRoleName()
        );
        return citusAuditRole;
    }

    public User getCitusAnalyticsRoleRole() {
        if (citusAnalyticsRole != null) {
            return citusAnalyticsRole;
        }

        citusAnalyticsRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.ANALYTICS_ROLE.getRoleName()
        );
        return citusAnalyticsRole;
    }

    public User getCitusExcerptExportedRole() {
        if (citusExcerptExportedRole != null) {
            return citusExcerptExportedRole;
        }

        citusExcerptExportedRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.EXCERPT_EXPORTER_ROLE.getRoleName()
        );
        return citusExcerptExportedRole;
    }

    public User getCitusExcerptRole() {
        if (citusExcerptRole != null) {
            return citusExcerptRole;
        }

        citusExcerptRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.EXCERPT_ROLE.getRoleName()
        );
        return citusExcerptRole;
    }

    public User getCitusExcerptWorkerRole() {
        if (citusExcerptWorkerRole != null) {
            return citusExcerptWorkerRole;
        }

        citusExcerptWorkerRole = OpenshiftServiceProvider.getUserSecretsBySecretNameAndKey(ocClient,
                configuration.getCitusRoles().getSecret(),
                CitusUsers.EXCERPT_WORKER_ROLE.getRoleName()
        );
        return citusExcerptWorkerRole;
    }

    public Service getDataFactory(String userName) {
        if (dataFactory != null) {
            dataFactory.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return dataFactory;
        }

        dataFactory = OpenshiftServiceProvider.getService(ocClient, configuration.getDataFactory(),
                registryUserProvider.get(userName));
        return dataFactory;
    }

    public Service getDataFactoryExternalPlatform(String userName) {
        if (dataFactoryExternalPlatform != null) {
            dataFactoryExternalPlatform.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return dataFactoryExternalPlatform;
        }

        dataFactoryExternalPlatform = OpenshiftServiceProvider.getService(ocClient,
                configuration.getDataFactoryExternalPlatform(), registryUserProvider.get(userName));
        return dataFactoryExternalPlatform;
    }

    public Service getRegistryManagement(String userName) {
        if (registryManagement != null) {
            registryManagement.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return registryManagement;
        }

        registryManagement = OpenshiftServiceProvider.getService(
                ocClient,
                configuration.getRegistryManagement(),
                registryUserProvider.get(userName)
        );
        return registryManagement;
    }

    public Service getDataFactoryExternalSystem() {
        if (dataFactoryExternalSystem != null) {
            return dataFactoryExternalSystem;
        }

        dataFactoryExternalSystem = OpenshiftServiceProvider.getService(ocClient,
                configuration.getDataFactoryExternalSystem());
        return dataFactoryExternalSystem;
    }

    public Service getDataFactorySoap(String userName) {
        if (dataFactorySoap != null) {
            dataFactorySoap.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return dataFactorySoap;
        }

        dataFactorySoap = OpenshiftServiceProvider.getService(ocClient, configuration.getDataFactorySoap(),
                registryUserProvider.get(userName));
        dataFactorySoap.setUrl(dataFactorySoap.getUrl() + "/ws?wsdl");
        return dataFactorySoap;
    }

    public Service getDigitalSignatureOps(String userName) {
        if (digitalSignatureOps != null) {
            digitalSignatureOps.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return digitalSignatureOps;
        }

        var dsoConfig = configuration.getDigitalSignature();

        if (oc.getUrl().contains("cicd2")) {
            dsoConfig.setPortForwarding(false);
        }


        digitalSignatureOps = OpenshiftServiceProvider.getService(ocClient, dsoConfig,
                registryUserProvider.get(userName));
        return digitalSignatureOps;
    }

    public Service getUserSettings(String userName) {
        if (userSettings != null) {
            userSettings.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return userSettings;
        }

        userSettings = OpenshiftServiceProvider.getService(ocClient, configuration.getUserSettings(),
                registryUserProvider.get(userName));
        return userSettings;
    }

    public Service getBpms(String userName) {
        if (bpms != null) {
            bpms.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return bpms;
        }

        var bpmsConfig = configuration.getBpms();

        if (oc.getUrl().contains("cicd2")) {
            bpmsConfig.setPortForwarding(false);
        }

        bpms = OpenshiftServiceProvider.getService(ocClient, bpmsConfig, registryUserProvider.get(userName));
        return bpms;
    }

    public Service getFormManagementModeler(String userName) {
        if (formManagementModeler != null) {
            formManagementModeler.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return formManagementModeler;
        }

        formManagementModeler = OpenshiftServiceProvider.getService(ocClient,
                configuration.getFormManagementModeler(), registryUserProvider.get(userName));
        return formManagementModeler;
    }

    public Service getProcessWebserviceGateway(String userName) {
        if (processWebserviceGateway != null) {
            processWebserviceGateway.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return processWebserviceGateway;
        }

        processWebserviceGateway = OpenshiftServiceProvider.getService(ocClient,
                configuration.getProcessWebserviceGateway(), registryUserProvider.get(userName));
        processWebserviceGateway.setUrl(processWebserviceGateway.getUrl().concat(":443/ws"));

        return processWebserviceGateway;
    }

    public Service getFormManagementProvider(String userName) {
        if (formManagementProvider != null) {
            formManagementProvider.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return formManagementProvider;
        }

        formManagementProvider = OpenshiftServiceProvider.getService(ocClient,
                configuration.getFormManagementProvider(), registryUserProvider.get(userName));
        return formManagementProvider;
    }

    public Service getExcerpt(String userName) {
        if (excerpt != null) {
            excerpt.setUser(registryUserProvider.getUserService().refreshUserToken(registryUserProvider.get(userName)));
            return excerpt;
        }

        excerpt = OpenshiftServiceProvider.getService(ocClient, configuration.getExcerpt(),
                registryUserProvider.get(userName));
        return excerpt;
    }

    public Service getUserTaskManagement() {
        if (userTaskManagement != null) {
            return userTaskManagement;
        }

        userTaskManagement = OpenshiftServiceProvider.getService(ocClient, configuration.getUserTaskManagement());
        return userTaskManagement;
    }

    public Service getUserProcessManagement() {
        if (userProcessManagement != null) {
            return userProcessManagement;
        }

        userProcessManagement = OpenshiftServiceProvider.getService(ocClient, configuration.getUserProcessManagement());
        return userProcessManagement;
    }

    public Service getDigitalDocument() {
        if (digitalDocument != null) {
            return digitalDocument;
        }

        digitalDocument = OpenshiftServiceProvider.getService(ocClient, configuration.getDigitalDocument());
        return digitalDocument;
    }

    public Service getOfficerPortal() {
        if (officerPortal != null) {
            return officerPortal;
        }

        officerPortal = OpenshiftServiceProvider.getService(ocClient, configuration.getOfficerPortal());
        return officerPortal;
    }

    public Service getCitizenPortal() {
        if (citizenPortal != null) {
            return citizenPortal;
        }

        citizenPortal = OpenshiftServiceProvider.getService(ocClient, configuration.getCitizenPortal());
        return citizenPortal;
    }

    public Service getAdminPortal() {
        if (adminPortal != null) {
            return adminPortal;
        }

        adminPortal = OpenshiftServiceProvider.getService(ocClient, configuration.getAdminPortal());
        return adminPortal;
    }

    public Service getProcessHistory() {
        if (processHistory != null) {
            return processHistory;
        }

        processHistory = OpenshiftServiceProvider.getService(ocClient, configuration.getProcessHistory());
        return processHistory;
    }

    public Service getRedashViewer() {
        if (redashViewer != null) {
            return redashViewer;
        }

        redashViewer = OpenshiftServiceProvider.getService(ocClient, configuration.getRedashViewer());
        redashViewer.setUrl(redashViewer.getUrl() + "/api");
        redashViewer.setToken(OpenshiftServiceProvider.getPasswordFromSecretByKey(
                ocClient,
                configuration.getRedashViewer().getSecret(),
                "viewer-api-key")
        );
        return redashViewer;
    }

    public Service getRedashAdmin() {
        if (redashAdmin != null) {
            return redashAdmin;
        }

        redashAdmin = OpenshiftServiceProvider.getService(ocClient, configuration.getRedashAdmin());
        redashAdmin.setUrl(redashAdmin.getUrl() + "/api");
        redashAdmin.setToken(OpenshiftServiceProvider.getPasswordFromSecretByKey(
                ocClient,
                configuration.getRedashAdmin().getSecret(),
                "admin-api-key")
        );
        return redashAdmin;
    }

    public Service getGerrit() {
        if (gerrit != null) {
            return gerrit;
        }

        gerrit = OpenshiftServiceProvider.getService(ocClient, configuration.getGerrit(),
                ocClient.getCredentials(configuration.getGerrit().getSecret()));
        return gerrit;
    }

    public Service getJenkins() {
        if (jenkins != null) {
            return jenkins;
        }

        jenkins = OpenshiftServiceProvider.getService(ocClient, configuration.getJenkins(),
                ocClient.getCredentials(configuration.getJenkins().getSecret()));
        return jenkins;
    }

    public Redis getRedis(){
        if (redis != null) {
            return redis;
        }
        redis = OpenshiftServiceProvider.getRedisService(ocClient, configuration.getRedis(),
                ocClient.getCredentials(configuration.getRedis().getSecret()));
        redis.setUrl(String.format(configuration.getRedis().getRoute(), this.namespace));
        return redis;
    }
}
