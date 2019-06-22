/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.internal.SHA256HMAC;
import com.cybersource.flex.sdk.internal.SecurityHelper;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class VDPSignatureHelper {
    public static final String HTTP_REQHDR_XPAYTOKEN = "X-Pay-Token";

    private VDPSignatureHelper() {
        throw new IllegalStateException();
    }

    public static String generateXpaytoken(String resource_path, String query_string, String request_body, byte[] shared_secret) throws FlexException {
        long timestamp = System.currentTimeMillis() / 1000L;
        return VDPSignatureHelper.generateXpaytoken(timestamp, resource_path, query_string, request_body, shared_secret);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String generateXpaytoken(long timestamp, String resource_path, String query_string, String request_body, byte[] shared_secret) throws FlexException {
        try {
            SHA256HMAC sha256hmac = new SHA256HMAC(shared_secret);
            sha256hmac.update(Long.toString(timestamp).getBytes(StandardCharsets.UTF_8));
            sha256hmac.update(resource_path.getBytes(StandardCharsets.UTF_8));
            sha256hmac.update(query_string.getBytes(StandardCharsets.UTF_8));
            sha256hmac.update(request_body.getBytes(StandardCharsets.UTF_8));
            byte[] hashByte = sha256hmac.digest();
            String string = "xv2:" + timestamp + ":" + VDPSignatureHelper.toHex(hashByte).toLowerCase();
            return string;
        }
        finally {
            SecurityHelper.randomize(shared_secret);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}

