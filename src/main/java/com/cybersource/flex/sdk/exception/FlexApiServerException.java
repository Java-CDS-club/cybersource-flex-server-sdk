/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.exception;

import com.cybersource.flex.sdk.model.FlexErrorResponse;

public class FlexApiServerException extends FlexApiException {

    public FlexApiServerException(int status, FlexErrorResponse flexErrorResponse, String vcCorrelationId) {
        super(status, flexErrorResponse, vcCorrelationId);
    }
}
