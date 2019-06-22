/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

import java.io.Serializable;

public class FlexPublicKey implements Serializable {

    private String keyId;
    private DerPublicKey der;
    private JsonWebKey jwk;

    public FlexPublicKey() {
    }

    public FlexPublicKey(String keyId, DerPublicKey der, JsonWebKey jwk) {
        this.keyId = keyId;
        this.der = der;
        this.jwk = jwk;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public DerPublicKey getDer() {
        return der;
    }

    public void setDer(DerPublicKey der) {
        this.der = der;
    }

    public JsonWebKey getJwk() {
        return jwk;
    }

    public void setJwk(JsonWebKey jwk) {
        this.jwk = jwk;
    }

    @Override
    public String toString() {
        return String.format("FlexPublicKey[keyId=%s]", keyId);
    }

}
