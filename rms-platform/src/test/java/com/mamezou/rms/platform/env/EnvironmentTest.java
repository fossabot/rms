package com.mamezou.rms.platform.env;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public class EnvironmentTest {

    private static final String MAIN_MANIFEST_JAR_PROP = "main.manifest.jar";

    @AfterEach
    void teardown( ) {
        System.clearProperty(MAIN_MANIFEST_JAR_PROP);
        Environment.clear();
    }

    @Test
    void tetGetMainJarInfo() {

        System.setProperty(MAIN_MANIFEST_JAR_PROP, "environment-test-normal\\.jar$");

        MainJarInfo mainJarInfo1 = Environment.getMainJarInfo();
        MainJarInfo mainJarInfo2 = Environment.getMainJarInfo();

        assertTrue(mainJarInfo1 == mainJarInfo2);
    }

    @Test
    void testUnknowMainJarInfo() {

        // "main.manifest.jar" prop non.
        MainJarInfo mainJarInfo = Environment.getMainJarInfo();

        assertThat(mainJarInfo.getApplicationName()).isEqualTo("unknown");
        assertThat(mainJarInfo.getJarName()).isEqualTo("unknown");
        assertThat(mainJarInfo.getMainClassName()).isEqualTo("unknown");
        assertThat(mainJarInfo.getVersion()).isEqualTo("unknown");
        assertThat(mainJarInfo.getBuildtimeInfo()).isEqualTo("unknown");
    }
}