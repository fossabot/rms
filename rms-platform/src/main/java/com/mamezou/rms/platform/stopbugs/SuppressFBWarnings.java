package com.mamezou.rms.platform.stopbugs;

public @interface SuppressFBWarnings {

    String[] value() default {};

    String justification() default "";
}
