/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.exception;

public class FlexSecurityException extends FlexException {

    public FlexSecurityException(String msg) {
        super(msg);
    }

    public FlexSecurityException(String msg, Exception e) {
        super(msg, e);
    }
}
