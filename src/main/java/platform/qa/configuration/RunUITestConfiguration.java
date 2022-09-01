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

import platform.qa.utils.ConfigurationUtils;

import java.util.Properties;

/**
 * Configuration for UI test run abilities
 */
public final class RunUITestConfiguration {
    private static RunUITestConfiguration instance;
    private final Properties properties;

    private RunUITestConfiguration() {
        properties = ConfigurationUtils.uploadPropertiesConfiguration("properties/platform.properties");
    }

    /**
     * @return isRemoteMoonRun defined if user selected to run UI tests using moon or not
     */
    public boolean isRemoteRunEnabled() {
        String propertyKey = "isRemoteMoonRun";
        return Boolean.parseBoolean(System.getProperty(propertyKey) != null ? System.getProperty(propertyKey) :
                properties.getProperty(propertyKey, "false"));
    }

    /**
     * @return isDarkTheme defined if user selected Dark or Light scheme for UI
     */
    public boolean isDarkThemeEnabled() {
        String propertyKey = "isDarkTheme";
        return Boolean.parseBoolean(System.getProperty(propertyKey) != null ? System.getProperty(propertyKey) :
                properties.getProperty(propertyKey, "false"));
    }

    /**
     * @return isProxyEnabled defined if user selected to run UI tests using proxy or not
     */
    public boolean isProxyEnabled() {
        String propertyKey = "isProxyEnabled";
        return Boolean.parseBoolean(System.getProperty(propertyKey) != null ? System.getProperty(propertyKey) :
                properties.getProperty(propertyKey, "false"));
    }

    public static RunUITestConfiguration getInstance() {
        if (instance == null) {
            instance = new RunUITestConfiguration();
        }
        return instance;
    }
}
