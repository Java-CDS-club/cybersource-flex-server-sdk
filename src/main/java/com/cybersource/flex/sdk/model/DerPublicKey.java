/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

import java.io.Serializable;

public class DerPublicKey implements Serializable {

    private String format;
    private String algorithm;
    private String publicKey;

    public DerPublicKey() {
    }

    public DerPublicKey(String format, String algorithm, String publicKey) {
        this.format = format;
        this.algorithm = algorithm;
        this.publicKey = publicKey;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
