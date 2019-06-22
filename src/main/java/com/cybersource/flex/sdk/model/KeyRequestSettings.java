/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

public class KeyRequestSettings {

    private Integer unmaskedLeft;
    private Integer unmaskedRight;
    private Boolean enableBillingAddress;
    private String currency;
    private Boolean enableAutoAuth;

    public int getUnmaskedLeft() {
        return unmaskedLeft;
    }

    public void setUnmaskedLeft(int unmaskedLeft) {
        this.unmaskedLeft = unmaskedLeft >= 0 ? unmaskedLeft : 0;
    }

    public int getUnmaskedRight() {
        return unmaskedRight;
    }

    public void setUnmaskedRight(int unmaskedRight) {
        this.unmaskedRight = unmaskedRight >= 0 ? unmaskedRight : 0;
    }

    public Boolean isEnableBillingAddress() {
        return enableBillingAddress;
    }

    public void setEnableBillingAddress(boolean enableBillingAddress) {
        this.enableBillingAddress = enableBillingAddress;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean isEnableAutoAuth() {
        return enableAutoAuth;
    }

    public void setEnableAutoAuth(boolean enableAutoAuth) {
        this.enableAutoAuth = enableAutoAuth;
    }

}
