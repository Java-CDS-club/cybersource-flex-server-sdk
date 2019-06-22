/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Constants {
    public static final String SDK_LANGUAGE = "Java";
    public static final String SDK_VERSION;
    public static final String FLEX_SERVER_SDK_USER_AGENT;
    public static final String ENVIRONMENT_FINGERPRINT_FIXED_STRING_1 = "Z6X4KAGHg4jsSeGhDDj7PdkjbPmZ8ZCa";
    public static final String ENVIRONMENT_FINGERPRINT_FIXED_STRING_2 = "VRUZE8ezHGJYH73A8U4faPLAGDAvJp8C";

    static {
        Properties version = new Properties();
        try {
            version.load(Constants.class.getResourceAsStream("/version.properties"));
            SDK_VERSION = version.getProperty("sdk.version");
            FLEX_SERVER_SDK_USER_AGENT = "Flex Server SDK/Java/" + SDK_VERSION;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

