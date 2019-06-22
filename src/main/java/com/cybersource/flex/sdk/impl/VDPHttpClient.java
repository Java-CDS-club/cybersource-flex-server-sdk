/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.FlexServiceFactory;
import com.cybersource.flex.sdk.authentication.Environment;
import com.cybersource.flex.sdk.authentication.VisaDeveloperCenterCredentials;
import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.exception.FlexSDKInternalException;
import com.cybersource.flex.sdk.impl.CGKHttpClient;
import com.cybersource.flex.sdk.impl.DigestHelper;
import com.cybersource.flex.sdk.impl.FlexHttpClient;
import com.cybersource.flex.sdk.impl.VDPSignatureHelper;
import com.cybersource.flex.sdk.internal.HttpClient;
import com.cybersource.flex.sdk.internal.HttpResponse;
import com.cybersource.flex.sdk.internal.SecureByteArrayWrapper;
import com.cybersource.flex.sdk.internal.SecurityHelper;
import java.io.IOException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.DestroyFailedException;

public class VDPHttpClient
implements FlexHttpClient {
    private final FlexServiceFactory.FlexServiceConfiguration flexServiceConfiguration;
    private final Logger logger = Logger.getLogger(CGKHttpClient.class.getName());
    private static final String FLEX_V1_KEYS_PRODUCTION = "https://api.visa.com/cybersource/payments/flex/v1/keys";
    private static final String FLEX_V1_KEYS_SANDBOX = "https://sandbox.api.visa.com/cybersource/payments/flex/v1/keys";
    private final Environment environment;
    private final String apiKey;
    private final SecureByteArrayWrapper sharedSecret;
    private final String url;

    VDPHttpClient(VisaDeveloperCenterCredentials credentials, FlexServiceFactory.FlexServiceConfiguration flexServiceConfiguration) {
        try {
            this.flexServiceConfiguration = flexServiceConfiguration;
            this.environment = credentials.getEnvironment();
            this.apiKey = credentials.getApiKey();
            this.sharedSecret = new SecureByteArrayWrapper(credentials.getSharedSecret());
            this.url = this.getKeysUrl();
            if (flexServiceConfiguration.isLoggingEnabled()) {
                this.logger.log(Level.INFO, "VDP Http client initialized with following credentials: {0}.", credentials);
            }
        }
        finally {
            SecurityHelper.destroy(credentials);
        }
    }

    @Override
    public HttpResponse postForKey(String body) throws FlexException {
        try {
            String queryString = "apikey=" + this.apiKey;
            HashMap<String, String> headers = new HashMap<String, String>();
            String xPayToken = VDPSignatureHelper.generateXpaytoken(VDPHttpClient.getResourcePath(this.url), queryString, body, this.sharedSecret.getData());
            headers.put("X-Pay-Token", xPayToken);
            if (this.flexServiceConfiguration.isLoggingEnabled()) {
                this.logger.log(Level.INFO, "Signature for POST to {0} POST generated: {1}. Request payload:\n{2}.", new Object[]{this.url, headers, body});
            }
            HttpResponse httpResponse = HttpClient.post(this.flexServiceConfiguration.getProxy(), this.url + "?" + queryString, headers, body);
            DigestHelper.verifyResponseDigest(httpResponse, false);
            return httpResponse;
        }
        catch (IOException ioe) {
            throw new FlexSDKInternalException(String.format("IO error on attempt to generate key (%s: %s).", ioe.getClass(), ioe.getMessage()), ioe);
        }
    }

    @Override
    public HttpResponse getForKey(String keyId) throws FlexException {
        try {
            String getKeyUrl = this.url + "/" + keyId;
            String queryString = "apikey=" + this.apiKey;
            HashMap<String, String> headers = new HashMap<String, String>();
            String xPayToken = VDPSignatureHelper.generateXpaytoken(VDPHttpClient.getResourcePath(getKeyUrl), queryString, "", this.sharedSecret.getData());
            headers.put("X-Pay-Token", xPayToken);
            if (this.flexServiceConfiguration.isLoggingEnabled()) {
                this.logger.log(Level.INFO, "Signature for GET from {0} generated: {1}. Flex key id: {2}.", new Object[]{this.url, headers, keyId});
            }
            HttpResponse httpResponse = HttpClient.get(this.flexServiceConfiguration.getProxy(), getKeyUrl + "?" + queryString, headers);
            DigestHelper.verifyResponseDigest(httpResponse, false);
            return httpResponse;
        }
        catch (IOException ioe) {
            throw new FlexSDKInternalException(String.format("IO error on attempt to retrieve key (%s: %s).", ioe.getClass(), ioe.getMessage()), ioe);
        }
    }

    private String getKeysUrl() {
        switch (this.environment) {
            case LIVE: {
                return FLEX_V1_KEYS_PRODUCTION;
            }
            case TEST: {
                return FLEX_V1_KEYS_SANDBOX;
            }
        }
        throw new IllegalStateException("nonexistent case");
    }

    private static String getResourcePath(String url) {
        return url.substring(url.lastIndexOf("/cybersource/") + "/cybersource/".length());
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

