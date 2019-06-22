/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

public class KeysRequestParameters {

    private EncryptionType encryptionType;
    private String targetOrigin;
    private KeyRequestSettings settings;

    public KeysRequestParameters() {
        this(EncryptionType.RsaOaep256);
    }

    public KeysRequestParameters(EncryptionType encryptionType) {
        this(encryptionType, null, null);
    }

    public KeysRequestParameters(EncryptionType encryptionType, String targetOrigin) {
        this(encryptionType, targetOrigin, null);
    }

    public KeysRequestParameters(EncryptionType encryptionType, KeyRequestSettings settings) {
        this(encryptionType, null, settings);
    }

    public KeysRequestParameters(EncryptionType encryptionType, String targetOrigin, KeyRequestSettings settings) {
        this.encryptionType = encryptionType;
        this.targetOrigin = targetOrigin;
        this.settings = settings;
    }

    public EncryptionType getEncryptionType() {
        return encryptionType;
    }

    public void setEncryptionType(EncryptionType encryptionType) {
        this.encryptionType = encryptionType;
    }

    public String getTargetOrigin() {
        return targetOrigin;
    }

    public void setTargetOrigin(String targetOrigin) {
        this.targetOrigin = targetOrigin;
    }

    public KeyRequestSettings getSettings() {
        return settings;
    }

    public void setSettings(KeyRequestSettings settings) {
        this.settings = settings;
    }

}
