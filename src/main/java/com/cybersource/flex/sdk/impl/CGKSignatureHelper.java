/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.internal.SHA256HMAC;
import com.cybersource.flex.sdk.internal.SecurityHelper;
import com.cybersource.flex.sdk.repackaged.Base64;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public final class CGKSignatureHelper {
    private CGKSignatureHelper() {
        throw new IllegalStateException();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String generateSignature(Map<String, String> headers, String keyId, byte[] sharedSecret) throws FlexException {
        try {
            SHA256HMAC sha256hmac = new SHA256HMAC(sharedSecret);
            StringBuilder signatureString = new StringBuilder();
            StringBuilder headersString = new StringBuilder();
            for (Map.Entry<String, String> e : headers.entrySet()) {
                signatureString.append('\n').append(e.getKey()).append(": ").append(e.getValue());
                headersString.append(' ').append(e.getKey());
            }
            signatureString.delete(0, 1);
            headersString.delete(0, 1);
            StringBuilder signature = new StringBuilder();
            sha256hmac.update(signatureString.toString().getBytes(StandardCharsets.UTF_8));
            byte[] hashBytes = sha256hmac.digest();
            signature.append("keyid=\"").append(keyId).append("\", ").append("algorithm=\"HmacSHA256\", ").append("headers=\"").append(headersString).append("\", ").append("signature=\"").append(Base64.encodeBytes(hashBytes)).append('\"');
            String string = signature.toString();
            return string;
        }
        finally {
            SecurityHelper.randomize(sharedSecret);
        }
    }
}

