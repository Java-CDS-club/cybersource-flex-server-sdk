/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

import java.io.Serializable;

/**
 * A JSON Web Key (JWK) is a JavaScript Object Notation (JSON) data structure
 * that represents a cryptographic key. The members of the object represent
 * properties of the key, including its value.
 */
public class JsonWebKey implements Serializable {

    private String kty; // keyType;
    private String use; // keyUse;
    private String kid; // keyId;
    private String n;   // modulus;
    private String e;   // exponent;

    public JsonWebKey() {
    }

    public JsonWebKey(String keyType, String keyUse, String keyId, String modulus, String exponent) {
        this.kty = keyType;
        this.use = keyUse;
        this.kid = keyId;
        this.n = modulus;
        this.e = exponent;
    }

    /**
     * The "kty" (key type) parameter identifies the cryptographic algorithm
     * family used with the key, such as "RSA" or "EC". "kty" values should
     * either be registered in the IANA "JSON Web Key Types" registry
     * established by [JWA] or be a value that contains a Collision- Resistant
     * Name. The "kty" value is a case-sensitive string. This member MUST be
     * present in a JWK.
     *
     * @return "kty" (Key Type) Parameter
     */
    public String getKty() {
        return kty;
    }

    public void setKty(String keyType) {
        this.kty = keyType;
    }

    /**
     * The "use" (public key use) parameter identifies the intended use of the
     * public key. The "use" parameter is employed to indicate whether a public
     * key is used for encrypting data or verifying the signature on data.
     *
     * Values defined by this specification are:
     *
     * o "sig" (signature) o "enc" (encryption)
     *
     * Other values MAY be used. The "use" value is a case-sensitive string. Use
     * of the "use" member is OPTIONAL, unless the application requires its
     * presence.
     *
     * When a key is used to wrap another key and a public key use designation
     * for the first key is desired, the "enc" (encryption) key use value is
     * used, since key wrapping is a kind of encryption. The "enc" value is also
     * to be used for public keys used for key agreement operations.
     *
     * @return "use" (Public Key Use) Parameter
     */
    public String getUse() {
        return use;
    }

    public void setUse(String keyUse) {
        this.use = keyUse;
    }

    /**
     * The "kid" (key ID) parameter is used to match a specific key. This is
     * used, for instance, to choose among a set of keys within a JWK Set during
     * key rollover. The structure of the "kid" value is unspecified. When "kid"
     * values are used within a JWK Set, different keys within the JWK Set
     * SHOULD use distinct "kid" values. (One example in which different keys
     * might use the same "kid" value is if they have different "kty" (key type)
     * values but are considered to be equivalent alternatives by the
     * application using them.) The "kid" value is a case-sensitive string. Use
     * of this member is OPTIONAL. When used with JWS or JWE, the "kid" value is
     * used to match a JWS or JWE "kid" Header Parameter value.
     *
     * @return "kid" (Key ID) Parameter
     */
    public String getKid() {
        return kid;
    }

    public void setKid(String keyId) {
        this.kid = keyId;
    }

    /**
     * RSA key modulus "n".
     *
     * @return "n" key modulus
     */
    public String getN() {
        return n;
    }

    public void setN(String modulus) {
        this.n = modulus;
    }

    /**
     * RSA key public exponent "e".
     *
     * @return "e" public exponent.
     */
    public String getE() {
        return e;
    }

    public void setE(String exponent) {
        this.e = exponent;
    }

    /**
     * Returns a JSON String representation of the object
     *
     * @return a JSON String representation of the object.
     */
    public String toJsonString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"kty\":\"").append(getKty()).append("\",");
        sb.append("\"use\":\"").append(getUse()).append("\",");
        sb.append("\"kid\":\"").append(getKid()).append("\",");
        sb.append("\"n\":\"").append(getN()).append("\",");
        sb.append("\"e\":\"").append(getE()).append("\"");
        sb.append("}");
        return sb.toString();
    }

}
