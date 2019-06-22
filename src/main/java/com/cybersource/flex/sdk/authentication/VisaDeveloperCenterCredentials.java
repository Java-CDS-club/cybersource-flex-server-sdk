/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.authentication;

import com.cybersource.flex.sdk.internal.SecurityHelper;
import javax.security.auth.DestroyFailedException;

/**
 * Provide authentication to use FLEX API via Visa Developer Center. The Flex
 * Service will use https://sandbox.api.visa.com and https://api.visa.com.
 */
public final class VisaDeveloperCenterCredentials implements FlexCredentials {

    private volatile boolean destroyed = false;
    private Environment environment;
    private String apiKey;
    private byte[] sharedSecret;

    /**
     *
     */
    public VisaDeveloperCenterCredentials() {
    }

    /**
     *
     * @param environment
     * @param apiKey
     * @param sharedSecret
     */
    public VisaDeveloperCenterCredentials(Environment environment, String apiKey, char[] sharedSecret) {
        this.environment = environment;
        this.apiKey = apiKey;
        this.sharedSecret = convert(sharedSecret);
    }

    @Override
    public String toString() {
        return String.format("VisaDeveloperCenterCredentials[env=%s, apiKey=%s, destroyed=%b]", environment, apiKey, destroyed);
    }

    /**
     *
     * @return environment referenced by this credentials instance
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     *
     * @param environment
     * @return this instance to facilitate fluent API usage.
     */
    public VisaDeveloperCenterCredentials setEnvironment(Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     *
     * @param environment
     * @return this instance to facilitate fluent API usage.
     */
    public VisaDeveloperCenterCredentials setEnvironment(String environment) {
        this.environment = Environment.valueOf(environment);
        return this;
    }

    /**
     *
     * @return VDP API key
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     *
     * @param apiKey
     * @return this instance to facilitate fluent API usage.
     */
    public VisaDeveloperCenterCredentials setApiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     *
     * @return VDP HMAC shared secret
     * @throws IllegalStateException if the credentials were destroyed
     */
    public byte[] getSharedSecret() {
        if (destroyed) {
            throw new IllegalStateException("Credentials were destroyed");
        }
        return sharedSecret;
    }

    /**
     *
     * @param sharedSecret
     * @return this instance to facilitate fluent API usage.
     */
    public VisaDeveloperCenterCredentials setSharedSecret(char[] sharedSecret) {
        this.sharedSecret = convert(sharedSecret);
        return this;
    }

    private byte[] convert(char[] chars) {
        try {
            byte[] retVal = new byte[chars.length];
            for (int i = 0; i < chars.length; i++) {
                retVal[i] = (byte) chars[i]; // VDP shared secret is lower half of ASCII.
            }
            return retVal;
        } finally {
            SecurityHelper.randomize(chars);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        internalDestroy();
        super.finalize();
    }

    @Override
    public void destroy() throws DestroyFailedException {
        internalDestroy();
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    private void internalDestroy() {
        this.apiKey = null;
        this.environment = null;
        SecurityHelper.randomize(this.sharedSecret);
        this.destroyed = true;
    }
}
