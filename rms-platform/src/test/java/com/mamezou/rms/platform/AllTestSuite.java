package com.mamezou.rms.platform;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages({
    "com.mamezou.rms.platform"})
public class AllTestSuite {

}
