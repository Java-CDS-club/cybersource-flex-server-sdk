/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.internal.HttpResponse;
import javax.security.auth.Destroyable;

public interface FlexHttpClient
extends Destroyable {
    public HttpResponse postForKey(String var1) throws FlexException;

    public HttpResponse getForKey(String var1) throws FlexException;
}

