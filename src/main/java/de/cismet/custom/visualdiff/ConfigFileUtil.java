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

        if (PlatformService.getInstance().isLinux()) {
            // See: http://standards.freedesktop.org/basedir-spec/basedir-spec-latest.html
            String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
            if (xdgConfigHome != null) {
                File xdgConfigHomeFile = new File(xdgConfigHome);
                if (xdgConfigHomeFile.exists()) {
                    return new File(xdgConfigHomeFile, CONFIG_FILENAME);
                }
            }

            File userConfigFile = new File(System.getProperty("user.home"), ".config");
            if (userConfigFile.exists()) {
                return new File(userConfigFile, CONFIG_FILENAME);
            }
        } else if (PlatformService.getInstance().isWindows()) {
            // See: http://blogs.msdn.com/b/patricka/archive/2010/03/18/where-should-i-store-my-data-and-configuration-files-if-i-target-multiple-os-versions.aspx
            String roamingConfigHome = System.getenv("APPDATA");
            if (roamingConfigHome != null) {
                File roamingConfigHomeFile = new File(roamingConfigHome);
                if (roamingConfigHomeFile.exists()) {
                    return new File(roamingConfigHomeFile, CONFIG_FILENAME);
                }
            }
        } else if (PlatformService.getInstance().isMac()) {
            File preferencesDir = new File(System.getProperty("user.home"), "Library/Preferences");
            if (preferencesDir.exists()) {
                return new File(preferencesDir, CONFIG_FILENAME);
            }
        }

        return new File(CONFIG_FILENAME);
    }
}
