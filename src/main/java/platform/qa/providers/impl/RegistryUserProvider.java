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

package platform.qa.providers.impl;

import lombok.Getter;
import platform.qa.entities.User;
import platform.qa.keycloak.KeycloakClient;
import platform.qa.providers.api.AtomicOperation;
import platform.qa.services.UserService;
import platform.qa.utils.ConfigurationUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Provide registry users data by username
 */
public class RegistryUserProvider implements AtomicOperation<User> {
    private AtomicReference<User> user = new AtomicReference<>();
    @Getter
    private Map<String, User> users;
    @Getter private UserService userService;

    public RegistryUserProvider(String namespace, KeycloakClient keycloakClient, String usersFilePath) {
        this.users = getRegistryUsersFromJson(usersFilePath, namespace);
        userService = new UserService(users, keycloakClient);
    }

    @Override
    public User get(String name) {
        return user.updateAndGet((user) -> {
            User currentUser = userService.initUser(user, name);
            return userService.refreshUserToken(currentUser);
        });
    }

    public User get(String name, String namespace) {
        return user.updateAndGet((user) -> {
            User currentUser = userService.initUser(user, name, namespace);
            return userService.refreshUserToken(currentUser);
        });
    }

    private Map<String, User> getRegistryUsersFromJson(String path, String namespace) {
        Map<String, User> users = ConfigurationUtils.uploadUserConfiguration(path, User.class);
        users.values().forEach(user -> {
            user.setPassword(user.getLogin());
            if (user.getRealm().startsWith("-"))
                user.setRealm(namespace + user.getRealm());
        });
        return users;
    }
}
