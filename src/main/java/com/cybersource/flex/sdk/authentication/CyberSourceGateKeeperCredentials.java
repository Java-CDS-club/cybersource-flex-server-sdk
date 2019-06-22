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
 * Provide authentication to use FLEX API via CYBS CGK Platform.
 */
public final class CyberSourceGateKeeperCredentials extends BaseCGKCredentials {

    /**
     * Default constructor. Please use it, when you want to call setters one by
     * one. Note that you can use fluent API in such case.
     */
    public CyberSourceGateKeeperCredentials() {
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
    public CyberSourceGateKeeperCredentials(Environment environment, String mid, String keyId, byte[] sharedSecret) {
        super(environment, mid, keyId, sharedSecret);
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
    public CyberSourceGateKeeperCredentials(Environment environment, String mid, String keyId, char[] sharedSecret) throws FlexException {
        super(environment, mid, keyId, sharedSecret);
    }

    @Override
    public String toString() {
        return String.format("CyberSourceGateKeeperCredentials[env=%s, mid=%s, keyId=%s, destroyed=%b]", getEnvironment(), getMid(), getKeyId(), isDestroyed());
    }

}
