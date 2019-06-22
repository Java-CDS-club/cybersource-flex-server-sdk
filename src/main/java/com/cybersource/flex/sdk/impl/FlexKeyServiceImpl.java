/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.FlexService;
import com.cybersource.flex.sdk.FlexServiceFactory;
import com.cybersource.flex.sdk.authentication.BaseCGKCredentials;
import com.cybersource.flex.sdk.authentication.FlexCredentials;
import com.cybersource.flex.sdk.authentication.VisaDeveloperCenterCredentials;
import com.cybersource.flex.sdk.exception.FlexApiClientException;
import com.cybersource.flex.sdk.exception.FlexApiServerException;
import com.cybersource.flex.sdk.exception.FlexEncodingException;
import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.exception.FlexSDKInternalException;
import com.cybersource.flex.sdk.impl.CGKHttpClient;
import com.cybersource.flex.sdk.impl.FlexHttpClient;
import com.cybersource.flex.sdk.impl.JsonHelper;
import com.cybersource.flex.sdk.impl.VDPHttpClient;
import com.cybersource.flex.sdk.internal.HttpResponse;
import com.cybersource.flex.sdk.model.DerPublicKey;
import com.cybersource.flex.sdk.model.EncryptionType;
import com.cybersource.flex.sdk.model.FlexErrorResponse;
import com.cybersource.flex.sdk.model.FlexPublicKey;
import com.cybersource.flex.sdk.model.FlexToken;
import com.cybersource.flex.sdk.model.KeysRequestParameters;
import com.cybersource.flex.sdk.repackaged.Base64;
import com.cybersource.flex.sdk.repackaged.JSONObject;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.DestroyFailedException;

public class FlexKeyServiceImpl
implements FlexService {
    private final FlexServiceFactory.FlexServiceConfiguration flexServiceConfiguration;
    private final Logger logger = Logger.getLogger(FlexKeyServiceImpl.class.getName());
    private static final String V_C_CORRELATION_ID = "v-c-correlation-id";
    private final FlexHttpClient httpClient;

    public FlexKeyServiceImpl(FlexCredentials credentials, FlexServiceFactory.FlexServiceConfiguration flexServiceConfiguration) {
        this.flexServiceConfiguration = flexServiceConfiguration;
        if (flexServiceConfiguration.isLoggingEnabled()) {
            this.logger.log(Level.INFO, "Initialization of Flex Service with following configuration {0}.", this.flexServiceConfiguration);
        }
        if (credentials instanceof BaseCGKCredentials) {
            this.httpClient = new CGKHttpClient((BaseCGKCredentials)credentials, this.flexServiceConfiguration);
        } else if (credentials instanceof VisaDeveloperCenterCredentials) {
            this.httpClient = new VDPHttpClient((VisaDeveloperCenterCredentials)credentials, this.flexServiceConfiguration);
        } else {
            throw new IllegalArgumentException("Suspicious authentication");
        }
    }

    @Override
    public FlexPublicKey createKey() throws FlexException {
        return this.createKey(new KeysRequestParameters());
    }

    @Override
    public FlexPublicKey createKey(KeysRequestParameters params) throws FlexException {
        JSONObject reqJson = JsonHelper.toJson(params);
        HttpResponse resp = this.httpClient.postForKey(reqJson.toString());
        if (this.flexServiceConfiguration.isLoggingEnabled()) {
            this.logger.log(Level.INFO, "Response received from Flex: {0}.", resp);
        }
        String body = resp.getBody();
        if (resp.isErrorResponse()) {
            this.handleErrorResponse(resp, body);
        }
        return JsonHelper.parseFlexPublicKey(body);
    }

    @Override
    public FlexPublicKey createKey(EncryptionType encryptionType) throws FlexException {
        return this.createKey(new KeysRequestParameters(encryptionType));
    }

    @Override
    public FlexPublicKey createKey(EncryptionType encryptionType, String targetOrigin) throws FlexException {
        return this.createKey(new KeysRequestParameters(encryptionType, targetOrigin));
    }

    @Override
    public FlexPublicKey retrieveKey(String keyId) throws FlexException {
        HttpResponse resp = this.httpClient.getForKey(keyId);
        if (this.flexServiceConfiguration.isLoggingEnabled()) {
            this.logger.log(Level.INFO, "Response received from Flex: {0}.", resp);
        }
        String body = resp.getBody();
        if (resp.isErrorResponse()) {
            this.handleErrorResponse(resp, body);
        }
        return JsonHelper.parseFlexPublicKey(body);
    }

    @Override
    public PublicKey decodePublicKey(FlexPublicKey flexKey) throws FlexException {
        try {
            byte[] keyBytes = Base64.decode(flexKey.getDer().getPublicKey());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
            return publicKey;
        }
        catch (IOException ioe) {
            throw new FlexEncodingException("Unable to decode public key value");
        }
        catch (NoSuchAlgorithmException e) {
            throw new FlexSDKInternalException(e);
        } catch (InvalidKeySpecException e) {
            throw new FlexSDKInternalException(e);
        }
    }

    @Override
    public boolean verify(String flexKey, String tokenResponse) throws FlexException {
        return this.verify(JsonHelper.parseFlexPublicKey(flexKey), JsonHelper.parseFlexTokenResponse(tokenResponse));
    }

    @Override
    public boolean verify(FlexPublicKey flexKey, String tokenResponse) throws FlexException {
        return this.verify(flexKey, JsonHelper.parseFlexTokenResponse(tokenResponse));
    }

    @Override
    public boolean verify(FlexPublicKey flexKey, FlexToken token) throws FlexException {
        PublicKey publicKey = this.decodePublicKey(flexKey);
        return this.validateTokenSignature(publicKey, this.getSignedFieldsValues(token), token.getSignature());
    }

    @Override
    public boolean verify(FlexPublicKey flexKey, Map postParams) throws FlexException {
        PublicKey publicKey = this.decodePublicKey(flexKey);
        String signedFields = (String)postParams.get("signedFields");
        StringBuilder sb = new StringBuilder();
        for (String k : signedFields.split(",")) {
            sb.append(',');
            sb.append(postParams.get("" + k));
        }
        String signedValues = sb.substring(1);
        String signature = (String)postParams.get("signature");
        return this.validateTokenSignature(publicKey, signedValues, signature);
    }

    private String getSignedFieldsValues(FlexToken token) throws FlexSDKInternalException {
        String[] signedFieldKeys = token.getSignedFields().split(",");
        StringBuilder sb = new StringBuilder();
        for (String key : signedFieldKeys) {
            try {
                sb.append(",").append(new PropertyDescriptor(key, FlexToken.class).getReadMethod().invoke(token, new Object[0]));
            }
            catch (Exception e) {
                throw new FlexSDKInternalException(e.getMessage());
            }
        }
        sb.deleteCharAt(0);
        return sb.toString();
    }

    private boolean validateTokenSignature(PublicKey publicKey, String signedFields, String signature) throws FlexSDKInternalException, FlexEncodingException {
        try {
            Signature signInstance = Signature.getInstance("SHA512withRSA");
            signInstance.initVerify(publicKey);
            signInstance.update(signedFields.getBytes());
            return signInstance.verify(Base64.decode(signature));
        }
        catch (IOException ioe) {
            throw new FlexEncodingException("Unable to decode signature");
        }
        catch (GeneralSecurityException e) {
            throw new FlexSDKInternalException(e.getMessage());
        }
    }

    private void handleErrorResponse(HttpResponse resp, String body) throws FlexException {
        FlexErrorResponse flexErrorResponse = null;
        if (body != null && body.trim().length() > 0) {
            flexErrorResponse = JsonHelper.parseFlexErrorResponse(body);
        }
        String vcCorrelationId = resp.getHeaderFirstValue(V_C_CORRELATION_ID);
        int status = resp.getStatus();
        if (status / 100 == 5) {
            throw new FlexApiServerException(status, flexErrorResponse, vcCorrelationId);
        }
        throw new FlexApiClientException(status, flexErrorResponse, vcCorrelationId);
    }

    @Override
    public boolean isDestroyed() {
        return this.httpClient.isDestroyed();
    }

    @Override
    public void destroy() throws DestroyFailedException {
        this.httpClient.destroy();
    }

    protected void finalize() throws Throwable {
        try {
            this.destroy();
        }
        finally {
            super.finalize();
        }
    }
}

