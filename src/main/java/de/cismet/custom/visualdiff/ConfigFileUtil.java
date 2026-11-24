/*
 * Copyright (c) 2008-2019 Emmanuel Dupuy.
 * This project is distributed under the GPLv3 license.
 * This is a Copyleft license that gives the user the right to use,
 * copy and modify the code freely for non-commercial purposes.
 */
package de.cismet.custom.visualdiff;

import java.io.File;

public class ConfigFileUtil {

	private static final String CONFIG_FILENAME = "jd-gui.cfg";

	private ConfigFileUtil() {
	}

	public static File getJDGUIConfigFile() {
        String configFilePath = System.getProperty(CONFIG_FILENAME);

        if (configFilePath != null) {
            File configFile = new File(configFilePath);
            if (configFile.exists()) {
                return configFile;
            }
        }

        File platformConfigFile = getPlatformConfigFile();
        if (platformConfigFile != null) {
            return platformConfigFile;
        }
        return new File(CONFIG_FILENAME);
    }

    private static File getPlatformConfigFile() {
        PlatformService platform = PlatformService.getInstance();
        if (platform.isLinux()) {
            return getLinuxConfigFile();
        }
        if (platform.isWindows()) {
            return getConfigFileFromEnvironment("APPDATA");
        }
        if (platform.isMac()) {
            return getConfigFileFromDirectory(new File(System.getProperty("user.home"), "Library/Preferences"));
        }
        return null;
    }

    private static File getLinuxConfigFile() {
        // See: http://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html
        File configFile = getConfigFileFromEnvironment("XDG_CONFIG_HOME");
        if (configFile != null) {
            return configFile;
        }
        return getConfigFileFromDirectory(new File(System.getProperty("user.home"), ".config"));
    }

    private static File getConfigFileFromEnvironment(String variable) {
        String directory = System.getenv(variable);
        if (directory != null) {
            return getConfigFileFromDirectory(new File(directory));
        }
        return null;
    }

    private static File getConfigFileFromDirectory(File directory) {
        if (directory.exists()) {
            return new File(directory, CONFIG_FILENAME);
        }
        return null;
    }
}
