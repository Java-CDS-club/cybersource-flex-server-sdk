/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.internal;

import com.cybersource.flex.sdk.internal.SecurityHelper;
import java.security.MessageDigest;

public class SHA256HMAC {
    private final MessageDigest digester = SecurityHelper.getSha256Digester();
    private static final byte IPAD = 54;
    private static final byte OPAD = 92;
    private final byte[] i_key_pad = new byte[64];
    private final byte[] o_key_pad = new byte[64];

    public SHA256HMAC(byte[] key) {
        if (key == null) {
            throw new IllegalArgumentException();
        }
        key = this.conditionKey(key);
        this.initPads(key);
        this.reset();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] conditionKey(byte[] key) {
        try {
            if (key.length > 64) {
                byte[] arrby = this.conditionKey(this.digester.digest(key));
                return arrby;
            }
            byte[] shortenedKey = new byte[64];
            System.arraycopy(key, 0, shortenedKey, 0, key.length);
            for (int i = key.length; i < 64; ++i) {
                shortenedKey[i] = 0;
            }
            byte[] i = shortenedKey;
            return i;
        }
        finally {
            SecurityHelper.randomize(key);
        }
    }

    private void initPads(byte[] key) {
        try {
            System.arraycopy(key, 0, this.i_key_pad, 0, 64);
            System.arraycopy(key, 0, this.o_key_pad, 0, 64);
            SHA256HMAC.xorPad(this.i_key_pad, (byte)54);
            SHA256HMAC.xorPad(this.o_key_pad, (byte)92);
        }
        finally {
            SecurityHelper.randomize(key);
        }
    }

    private void reset() {
        try {
            this.digester.update(this.i_key_pad);
        }
        finally {
            SecurityHelper.randomize(this.i_key_pad);
        }
    }

    public SHA256HMAC update(byte[] data) {
        this.digester.update(data);
        return this;
    }

    public byte[] digest() {
        byte[] tmp = null;
        try {
            tmp = this.digester.digest();
            this.digester.update(this.o_key_pad);
            this.digester.update(tmp);
        }
        finally {
            SecurityHelper.randomize(tmp);
            SecurityHelper.randomize(this.o_key_pad);
        }
        return this.digester.digest();
    }

    private static void xorPad(byte[] pad, byte b) {
        int i = 0;
        while (i < pad.length) {
            byte[] arrby = pad;
            int n = i++;
            arrby[n] = (byte)(arrby[n] ^ b);
        }
    }

    protected void finalize() throws Throwable {
        SecurityHelper.randomize(this.i_key_pad);
        SecurityHelper.randomize(this.o_key_pad);
        super.finalize();
    }
}

