package com.mamezou.rms.core.persistence;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
    "com.mamezou.rms.application.entity",
    "com.mamezou.rms.application.persistence"
    })
public class PersistenceTestSuite {
}
