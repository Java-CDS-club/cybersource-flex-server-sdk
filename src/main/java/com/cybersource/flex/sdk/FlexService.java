/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk;

import com.cybersource.flex.sdk.exception.FlexException;
import com.cybersource.flex.sdk.model.EncryptionType;
import com.cybersource.flex.sdk.model.FlexPublicKey;
import com.cybersource.flex.sdk.model.FlexToken;
import com.cybersource.flex.sdk.model.KeysRequestParameters;
import java.security.PublicKey;
import java.util.Map;
import javax.security.auth.Destroyable;

public interface FlexService extends Destroyable {

    /**
     *
     * @return Flex-API specific limited time use public RSA key. The encryption
     * defaults to RSA with OEAP padding, @see EncryptionType.RsaOaep256.
     * @throws FlexException
     */
    FlexPublicKey createKey() throws FlexException;

    /**
     *
     * @param params
     * @return Flex-API specific limited time use public RSA key.
     * @throws FlexException
     */
    FlexPublicKey createKey(KeysRequestParameters params) throws FlexException;

    /**
     *
     * @param encryptionType
     * @return Flex-API limited time use public RSA key. The key can be used to
     * encrypt a credit card number using the algorithm specified by
     * encryptionType.
     * @throws FlexException
     */
    FlexPublicKey createKey(EncryptionType encryptionType) throws FlexException;

    /**
     * Convenience method to create Flex Public RSA Key. Particularly designed
     * to support Flex microform.
     *
     * @param encryptionType Encryption method to be used to protect PAN, for
     * example @see EncryptionType.RsaOaep256
     * @param targetOrigin target origin (e.g. https://shop.example.com - must
     * be HTTPS unless localhost), used to serve Flex microform.
     * @return Flex-API limited time use public RSA key. The key is specifically
     * enabled to use with microform in page served from provided targetOrigin
     * domain.
     * @throws FlexException
     */
    FlexPublicKey createKey(EncryptionType encryptionType, String targetOrigin) throws FlexException;

    /**
     *
     * @param keyId
     * @return the FlexPublicKey instance
     * @throws FlexException
     */
    FlexPublicKey retrieveKey(String keyId) throws FlexException;

    /**
     *
     * @param flexPublicKey The Flex public key, i.e. retrieved via one of
     * createKey methods.
     * @return RSA public key native to Java
     * @throws FlexException
     */
    PublicKey decodePublicKey(FlexPublicKey flexPublicKey) throws FlexException;

    /**
     *
     * @param flexKey the serialised FlexPublicKey instance
     * @param token the serialised FlexToken response
     * @return whether the FlexToken signature is valid
     * @throws FlexException
     */
    boolean verify(String flexKey, String token) throws FlexException;

    /**
     *
     * @param flexPublicKey the flexPublicKey instance
     * @param token the serialised FlexToken response
     * @return whether the FlexToken signature is valid
     * @throws FlexException
     */
    boolean verify(FlexPublicKey flexPublicKey, String token) throws FlexException;

    /**
     *
     * @param flexPublicKey the FlexPublicKey instance
     * @param token the FlexToken instance
     * @return whether the FlexToken signature is valid
     * @throws FlexException
     */
    boolean verify(FlexPublicKey flexPublicKey, FlexToken token) throws FlexException;

    /**
     *
     * @param flexPublicKey the FlexPublicKey instance
     * @param postParams a Map containing the FlexToken fields
     * @return whether the FlexToken signature is valid
     * @throws FlexException
     */
    boolean verify(FlexPublicKey flexPublicKey, Map postParams) throws FlexException;
}
