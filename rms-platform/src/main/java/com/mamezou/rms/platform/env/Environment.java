package com.mamezou.rms.platform.env;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class Environment {

    private static MainJarInfo mainJarInfo;

    public synchronized static MainJarInfo getMainJarInfo() {
        if (mainJarInfo == null) {
            Config config = ConfigProvider.getConfig();
            mainJarInfo = MainJarInfo.builder().build(config);
        }
        return mainJarInfo == null ? MainJarInfo.UNKNOWN_INFO : mainJarInfo;
    }

    synchronized static void clear() { // for TEST
        mainJarInfo = null;
    }
}
