/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.exception;

import com.cybersource.flex.sdk.model.FlexErrorResponse;

public class FlexApiException extends FlexException {

    private final int status;
    private final FlexErrorResponse flexErrorResponse;
    private final String vcCorrelationId;

    public FlexApiException(int status, FlexErrorResponse flexErrorResponse, String vcCorrelationId) {
        super(String.format("FLEX API returned a status of [%d]", status));
        this.status = status;
        this.flexErrorResponse = flexErrorResponse;
        this.vcCorrelationId = vcCorrelationId;
    }

    public int getStatus() {
        return status;
    }

    public FlexErrorResponse getFlexErrorResponse() {
        return flexErrorResponse;
    }

    public String getVcCorrelationId() {
        return vcCorrelationId;
    }
}
