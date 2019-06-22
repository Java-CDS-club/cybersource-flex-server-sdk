/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.exception.FlexSecurityException;
import com.cybersource.flex.sdk.internal.HttpResponse;
import com.cybersource.flex.sdk.internal.SecurityHelper;
import com.cybersource.flex.sdk.repackaged.Base64;
import java.util.List;

public class DigestHelper {
    public static void verifyResponseDigest(HttpResponse response, boolean required) throws FlexSecurityException {
        List<String> digestHeaders = response.getHeaderValues("Digest");
        if (digestHeaders == null || digestHeaders.size() < 1) {
            if (required) {
                throw new FlexSecurityException("Could not verify response digest: missing digest header");
            }
            return;
        }
        for (String digestHeader : digestHeaders) {
            int digestHeaderLength;
            String calculatedDigest;
            if (digestHeader == null || !digestHeader.startsWith("SHA-256=")) continue;
            int delimiterIndex = digestHeader.indexOf(61);
            if (delimiterIndex == (digestHeaderLength = digestHeader.length()) - 1) {
                throw new FlexSecurityException("Could not verify response digest: invalid digest format");
            }
            String digestValue = digestHeader.substring(delimiterIndex + 1, digestHeaderLength);
            try {
                calculatedDigest = Base64.encodeBytes(SecurityHelper.getSha256Digest(response.getBody()));
            }
            catch (Exception e) {
                throw new FlexSecurityException("Could not verify response digest: error calculating digest", e);
            }
            if (!digestValue.equals(calculatedDigest)) {
                throw new FlexSecurityException("Response digest mismatch");
            }
            return;
        }
        throw new FlexSecurityException("Could not verify response digest: no supported digest");
    }

    public static void verifyResponseDigest(HttpResponse response) throws FlexSecurityException {
        DigestHelper.verifyResponseDigest(response, true);
    }
}

