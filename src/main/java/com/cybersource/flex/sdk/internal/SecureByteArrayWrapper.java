/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.internal;

import com.cybersource.flex.sdk.internal.SecurityHelper;
import com.cybersource.flex.sdk.internal.TEA;
import java.util.concurrent.locks.ReentrantLock;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public final class SecureByteArrayWrapper
implements Destroyable {
    private volatile boolean destroyed = false;
    private final byte[] keypad = new byte[16];
    private final byte[] datapad;
    private final byte[] data;
    private final ReentrantLock lock = new ReentrantLock();

    public SecureByteArrayWrapper(byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("data must be not null and not empty.");
        }
        byte[] key = null;
        try {}
        catch (Throwable throwable) {
            SecurityHelper.randomize(data);
            SecurityHelper.randomize(key);
            throw new RuntimeException(throwable);
        }
        this.data = SecurityHelper.pad(data, 64);
        this.datapad = new byte[data.length];
        SecurityHelper.randomize(this.datapad);
        SecurityHelper.randomize(this.keypad);
        key = SecurityHelper.jvmFingerprint();
        SecureByteArrayWrapper.xor(key, this.keypad);
        this.encrypt(key);
        SecurityHelper.randomize(data);
        SecurityHelper.randomize(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getData() {
        if (this.destroyed) {
            throw new IllegalStateException("Credentials were destroyed");
        }
        byte[] key = null;
        try {
            byte[] retVal;
            key = SecurityHelper.jvmFingerprint();
            SecureByteArrayWrapper.xor(key, this.keypad);
            this.lock.lock();
            try {
                this.decrypt(key);
                retVal = SecurityHelper.unpad(this.data);
                this.encrypt(key);
            }
            finally {
                this.lock.unlock();
            }
            byte[] arrby = retVal;
            return arrby;
        }
        finally {
            SecurityHelper.randomize(key);
        }
    }

    private void encrypt(byte[] key) {
        SecureByteArrayWrapper.xor(this.data, this.datapad);
        for (int i = 0; i < this.data.length; i += 8) {
            TEA.encrypt(this.data, key, i);
        }
    }

    private void decrypt(byte[] key) {
        for (int i = 0; i < this.data.length; i += 8) {
            TEA.decrypt(this.data, key, i);
        }
        SecureByteArrayWrapper.xor(this.data, this.datapad);
    }

    private static void xor(byte[] data, byte[] pad) {
        for (int i = 0; i < data.length; ++i) {
            byte[] arrby = data;
            int n = i;
            arrby[n] = (byte)(arrby[n] ^ pad[i % pad.length]);
        }
    }

    protected void finalize() throws Throwable {
        this.internalDestroy();
        super.finalize();
    }

    @Override
    public void destroy() throws DestroyFailedException {
        this.internalDestroy();
    }

    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    private void internalDestroy() {
        this.destroyed = true;
        SecurityHelper.randomize(this.data);
        SecurityHelper.randomize(this.datapad);
        SecurityHelper.randomize(this.keypad);
    }
}

