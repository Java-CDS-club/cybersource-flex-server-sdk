/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.FlexServiceFactory;
import com.cybersource.flex.sdk.authentication.BaseCGKCredentials;
import com.cybersource.flex.sdk.authentication.CyberSourceFlexCredentials;
import com.cybersource.flex.sdk.authentication.CyberSourceGateKeeperCredentials;
import com.cybersource.flex.sdk.authentication.Environment;
import com.cybersource.flex.sdk.authentication.FlexCredentials;
import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.exception.FlexSDKInternalException;
import com.cybersource.flex.sdk.impl.CGKSignatureHelper;
import com.cybersource.flex.sdk.impl.DigestHelper;
import com.cybersource.flex.sdk.impl.FlexHttpClient;
import com.cybersource.flex.sdk.internal.HttpClient;
import com.cybersource.flex.sdk.internal.HttpResponse;
import com.cybersource.flex.sdk.internal.SecureByteArrayWrapper;
import com.cybersource.flex.sdk.internal.SecurityHelper;
import com.cybersource.flex.sdk.repackaged.Base64;
import java.io.IOException;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.DestroyFailedException;

public class CGKHttpClient
implements FlexHttpClient {
    private final FlexServiceFactory.FlexServiceConfiguration flexServiceConfiguration;
    private final Logger logger = Logger.getLogger(CGKHttpClient.class.getName());
    private static final String CGK_V1_KEYS_CAS = "https://apitest.cybersource.com/flex/v1/keys";
    private static final String CGK_V1_KEYS_PROD = "https://api.cybersource.com/flex/v1/keys";
    private static final String APITEST_CYBERSOURCE_COM = "apitest.cybersource.com";
    private static final String API_CYBERSOURCE_COM = "api.cybersource.com";
    private static final String DIRECT_V1_KEYS_CAS = "https://testflex.cybersource.com/flex/v1/keys";
    private static final String DIRECT_V1_KEYS_PROD = "https://flex.cybersource.com/flex/v1/keys";
    private static final String TESTFLEX_CYBERSOURCE_COM = "testflex.cybersource.com";
    private static final String FLEX_CYBERSOURCE_COM = "flex.cybersource.com";
    static final String HTTP_REQHDR_MIDHEADER = "v-c-merchant-id";
    static final String HTTP_REQHDR_DATE = "date";
    static final String HTTP_REQHDR_HOST = "host";
    static final String HTTP_REQHDR_REQUEST_TARGET = "(request-target)";
    static final String HTTP_REQHDR_DIGEST = "digest";
    static final String HTTP_REQHDR_SIGNATURE = "signature";
    private final String mid;
    private final String keyId;
    private final SecureByteArrayWrapper sharedSecret;
    private final String url;
    private final String host;

    CGKHttpClient(BaseCGKCredentials credentials, FlexServiceFactory.FlexServiceConfiguration flexServiceConfiguration) {
        try {
            this.flexServiceConfiguration = flexServiceConfiguration;
            this.mid = credentials.getMid();
            this.keyId = credentials.getKeyId();
            this.sharedSecret = new SecureByteArrayWrapper(credentials.getSharedSecret());
            this.url = CGKHttpClient.getKeysUrl(credentials);
            this.host = CGKHttpClient.getHost(credentials);
            if (flexServiceConfiguration.isLoggingEnabled()) {
                this.logger.log(Level.INFO, "CGK Http client initialized with following credentials: {0}.", credentials);
            }
        }
        finally {
            SecurityHelper.destroy(credentials);
        }
    }

    @Override
    public HttpResponse postForKey(String body) throws FlexException {
        try {
            String date = CGKHttpClient.getServerTime();
            LinkedHashMap<String, String> signedHeaders = new LinkedHashMap<String, String>();
            signedHeaders.put(HTTP_REQHDR_HOST, this.host);
            signedHeaders.put(HTTP_REQHDR_DATE, date);
            signedHeaders.put(HTTP_REQHDR_REQUEST_TARGET, "post /flex/v1/keys");
            signedHeaders.put(HTTP_REQHDR_DIGEST, CGKHttpClient.getDigest(body));
            signedHeaders.put(HTTP_REQHDR_MIDHEADER, this.mid);
            String signature = CGKSignatureHelper.generateSignature(signedHeaders, this.keyId, this.sharedSecret.getData());
            signedHeaders.put(HTTP_REQHDR_SIGNATURE, signature);
            signedHeaders.remove(HTTP_REQHDR_REQUEST_TARGET);
            if (this.flexServiceConfiguration.isLoggingEnabled()) {
                this.logger.log(Level.INFO, "Signed headers for POST to {0} generated: {1}. Request payload:\n{2}.", new Object[]{this.url, signedHeaders, body});
            }
            HttpResponse httpResponse = HttpClient.post(this.flexServiceConfiguration.getProxy(), this.url, signedHeaders, body);
            DigestHelper.verifyResponseDigest(httpResponse, false);
            return httpResponse;
        }
        catch (IOException ioe) {
            throw new FlexSDKInternalException(String.format("IO error on attempt to generate key (%s: %s).", ioe.getClass(), ioe.getMessage()), ioe);
        }
    }

    @Override
    public HttpResponse getForKey(String flexKeyId) throws FlexException {
        try {
            String date = CGKHttpClient.getServerTime();
            LinkedHashMap<String, String> signedHeaders = new LinkedHashMap<String, String>();
            signedHeaders.put(HTTP_REQHDR_HOST, this.host);
            signedHeaders.put(HTTP_REQHDR_DATE, date);
            signedHeaders.put(HTTP_REQHDR_REQUEST_TARGET, "get /flex/v1/keys/" + flexKeyId);
            signedHeaders.put(HTTP_REQHDR_DIGEST, CGKHttpClient.getDigest(""));
            signedHeaders.put(HTTP_REQHDR_MIDHEADER, this.mid);
            String signature = CGKSignatureHelper.generateSignature(signedHeaders, this.keyId, this.sharedSecret.getData());
            signedHeaders.put(HTTP_REQHDR_SIGNATURE, signature);
            signedHeaders.remove(HTTP_REQHDR_REQUEST_TARGET);
            if (this.flexServiceConfiguration.isLoggingEnabled()) {
                this.logger.log(Level.INFO, "Signed headers for GET from {0} generated: {1}. Flex key id: {2}.", new Object[]{this.url, signedHeaders, flexKeyId});
            }
            HttpResponse httpResponse = HttpClient.get(this.flexServiceConfiguration.getProxy(), this.url + "/" + flexKeyId, signedHeaders);
            DigestHelper.verifyResponseDigest(httpResponse, false);
            return httpResponse;
        }
        catch (IOException ioe) {
            throw new FlexSDKInternalException(String.format("IO error on attempt to retrieve key (%s: %s).", ioe.getClass(), ioe.getMessage()), ioe);
        }
    }

    private static String getServerTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    private static String getHost(FlexCredentials credentials) {
        if (credentials instanceof CyberSourceGateKeeperCredentials && ((CyberSourceGateKeeperCredentials)credentials).getEnvironment() == Environment.TEST) {
            return APITEST_CYBERSOURCE_COM;
        }
        if (credentials instanceof CyberSourceGateKeeperCredentials && ((CyberSourceGateKeeperCredentials)credentials).getEnvironment() == Environment.LIVE) {
            return API_CYBERSOURCE_COM;
        }
        if (credentials instanceof CyberSourceFlexCredentials && ((CyberSourceFlexCredentials)credentials).getEnvironment() == Environment.TEST) {
            return TESTFLEX_CYBERSOURCE_COM;
        }
        if (credentials instanceof CyberSourceFlexCredentials && ((CyberSourceFlexCredentials)credentials).getEnvironment() == Environment.LIVE) {
            return FLEX_CYBERSOURCE_COM;
        }
        throw new IllegalStateException();
    }

    private static String getKeysUrl(FlexCredentials credentials) {
        if (credentials instanceof CyberSourceGateKeeperCredentials && ((CyberSourceGateKeeperCredentials)credentials).getEnvironment() == Environment.TEST) {
            return CGK_V1_KEYS_CAS;
        }
        if (credentials instanceof CyberSourceGateKeeperCredentials && ((CyberSourceGateKeeperCredentials)credentials).getEnvironment() == Environment.LIVE) {
            return CGK_V1_KEYS_PROD;
        }
        if (credentials instanceof CyberSourceFlexCredentials && ((CyberSourceFlexCredentials)credentials).getEnvironment() == Environment.TEST) {
            return DIRECT_V1_KEYS_CAS;
        }
        if (credentials instanceof CyberSourceFlexCredentials && ((CyberSourceFlexCredentials)credentials).getEnvironment() == Environment.LIVE) {
            return DIRECT_V1_KEYS_PROD;
        }
        throw new IllegalStateException();
    }

    private static String getDigest(String body) {
        MessageDigest digester = SecurityHelper.getSha256Digester();
        byte[] digest = digester.digest(body.getBytes(StandardCharsets.UTF_8));
        return String.format("SHA-256=%s", Base64.encodeBytes(digest));
    }

    @Override
    public void destroy() throws DestroyFailedException {
        this.sharedSecret.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return this.sharedSecret.isDestroyed();
    }

    protected void finalize() throws Throwable {
        try {
            this.destroy();
        }
        finally {
            super.finalize();
        }
    }
}

