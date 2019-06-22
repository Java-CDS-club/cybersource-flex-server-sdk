/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.authentication;

import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.internal.SecurityHelper;
import com.cybersource.flex.sdk.repackaged.Base64;
import java.io.IOException;

import javax.security.auth.DestroyFailedException;

/**
 * Default authentication credentials to use FLEX API.
 */
public abstract class BaseCGKCredentials implements FlexCredentials {

    private volatile boolean destroyed = false;
    private Environment environment;
    private String mid;
    private String keyId;
    private byte[] sharedSecret;

    /**
     * Default constructor. Please use it, when you want to call setters one by
     * one. Note that you can use fluent API in such case.
     */
    BaseCGKCredentials() {
    }

    /**
     * Constructor that can be used to pass all authentication information in
     * one call. Successfully invoked constructor produces object that is ready
     * for use (i.e. to create @see FlexService instance).
     *
     * @param environment environment to which credentials relate
     * @param mid merchant ID
     * @param keyId shared secret key ID used to identify credentials
     * @param sharedSecret byte array containing raw shared secret. Note that
     * the array referenced by this parameter will be overridden after it is
     * used to initialize FlexService instance.
     */
    BaseCGKCredentials(Environment environment, String mid, String keyId, byte[] sharedSecret) {
        this.environment = environment;
        this.mid = mid;
        this.keyId = keyId;
        setSharedSecret(sharedSecret); // field not set directly to ensure memory is overridden after use.
    }

    /**
     * Constructor that can be used to pass all authentication information in
     * one call. Successfully invoked constructor produces object that is ready
     * for use (i.e. to create @see FlexService instance).
     *
     * @param environment environment to which credentials relate
     * @param mid merchant ID
     * @param keyId shared secret key ID used to identify credentials
     * @param sharedSecret character array containing base64 encoded shared
     * secret. Note that the array referenced by this parameter will be
     * overridden after it is used to initialize FlexService instance.
     */
    BaseCGKCredentials(Environment environment, String mid, String keyId, char[] sharedSecret) throws FlexException {
        this.environment = environment;
        this.mid = mid;
        this.keyId = keyId;
        setSharedSecret(sharedSecret); // field not set directly to ensure memory is overridden after use.
    }

    /**
     * Retrieves environment
     *
     * @return environment referenced by this credentials instance
     */
    public final Environment getEnvironment() {
        return environment;
    }

    /**
     * Sets the environment
     *
     * @param environment
     * @return this instance to facilitate fluent API usage.
     */
    public final BaseCGKCredentials setEnvironment(Environment environment) {
        this.environment = environment;
        return this;
    }

    /**
     * Convenience method to set environment using String literal, rather than
     * enum instance.
     *
     * @param environment "CAS" or "PRODUCTION".
     * @return this instance to facilitate fluent API usage.
     */
    public final BaseCGKCredentials setEnvironment(String environment) {
        this.environment = Environment.valueOf(environment);
        return this;
    }

    /**
     * @return merchant identifier - mid
     */
    public final String getMid() {
        return mid;
    }

    /**
     * @param mid
     * @return this instance to facilitate fluent API usage.
     */
    public final BaseCGKCredentials setMid(String mid) {
        this.mid = mid;
        return this;
    }

    /**
     * @return id of symmetric or asymmetric key or authentication key -
     * providing authentication to the CyberSource Gate Keeper
     */
    public final String getKeyId() {
        return keyId;
    }

    /**
     * @param keyId
     * @return this instance to facilitate fluent API usage.
     */
    public final BaseCGKCredentials setKeyId(String keyId) {
        this.keyId = keyId;
        return this;
    }

    /**
     * @return CGK HMAC shared secret
     * @throws IllegalStateException If the credentials were destroyed
     */
    public final byte[] getSharedSecret() {
        if (destroyed) {
            throw new IllegalStateException("Credentials were destroyed");
        }
        return sharedSecret;
    }

    /**
     * @param sharedSecret byte array containing shared secret. Note that array
     * elements can lie within the range 0x00 to 0xFF (-128 to +127). Please
     * make no assumption that shared secret byte array can be used to construct
     * valid String object.
     * @return this instance to facilitate fluent API usage.
     */
    public final BaseCGKCredentials setSharedSecret(byte[] sharedSecret) {
        try {
            this.sharedSecret = sharedSecret.clone();
            return this;
        } finally {
            SecurityHelper.randomize(sharedSecret);
        }
    }

    /**
     * @param encodedSharedSecret base64 encoded shared secret
     * @return this instance to facilitate fluent API usage.
     * @throws com.cybersource.flex.sdk.exception.FlexException
     */
    public final BaseCGKCredentials setSharedSecret(char[] encodedSharedSecret) throws FlexException {
        return setSharedSecret(decodeSharedSecret(encodedSharedSecret));
    }

    private static byte[] decodeSharedSecret(char[] encodedSharedSecret) throws FlexException {
        final byte[] sharedSecret = new byte[encodedSharedSecret.length];
        try {
            for (int i = 0; i < sharedSecret.length; i++) {
                if (encodedSharedSecret[i] > 128) {
                    throw new FlexException("non ASCII character");
                }
                sharedSecret[i] = (byte) encodedSharedSecret[i];
            }
            return Base64.decode(sharedSecret);
        } catch (IOException ioe) {
            throw new FlexException("Error during Base64 decoding");
        } finally {
            SecurityHelper.randomize(encodedSharedSecret);
            SecurityHelper.randomize(sharedSecret);
        }
    }

    @Override
    protected final void finalize() throws Throwable {
        internalDestroy();
        super.finalize();
    }

    @Override
    public final void destroy() throws DestroyFailedException {
        internalDestroy();
    }

    @Override
    public final boolean isDestroyed() {
        return destroyed;
    }

    private void internalDestroy() {
        this.destroyed = true;

        this.environment = null;
        this.keyId = null;
        this.mid = null;
        SecurityHelper.randomize(this.sharedSecret);
    }

}
