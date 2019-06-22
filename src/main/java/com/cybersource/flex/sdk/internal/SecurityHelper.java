/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.internal;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;

public class SecurityHelper {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static void destroy(Destroyable d) {
        try {
            d.destroy();
        }
        catch (DestroyFailedException dfe) {
            System.currentTimeMillis();
        }
    }

    public static void randomize(byte[] data) {
        if (data != null) {
            SECURE_RANDOM.nextBytes(data);
        }
    }

    public static void randomize(char[] data) {
        for (int i = 0; i < data.length; ++i) {
            data[i] = (char)SECURE_RANDOM.nextInt();
        }
    }

    public static MessageDigest getSha256Digester() {
        try {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new IllegalArgumentException(nsae);
        }
    }

    public static byte[] getSha256Digest(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data to hash cannot be null");
        }
        MessageDigest messageDigest = SecurityHelper.getSha256Digester();
        return messageDigest.digest(data);
    }

    public static byte[] getSha256Digest(String data) {
        return SecurityHelper.getSha256Digest(data.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] jvmFingerprint() {
        int i;
        MessageDigest digester = SecurityHelper.getSha256Digester();
        digester.update(ManagementFactory.getCompilationMXBean().getName().getBytes());
        digester.update(ManagementFactory.getOperatingSystemMXBean().getArch().getBytes());
        digester.update((byte)ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
        int hashCode = SECURE_RANDOM.getClass().hashCode();
        for (int i2 = 4; i2 >= 0; --i2) {
            digester.update((byte)(hashCode & 255));
            hashCode >>= 8;
        }
        digester.update(ManagementFactory.getOperatingSystemMXBean().getName().getBytes());
        digester.update(ManagementFactory.getOperatingSystemMXBean().getVersion().getBytes());
        digester.update("Z6X4KAGHg4jsSeGhDDj7PdkjbPmZ8ZCa".getBytes());
        digester.update(ManagementFactory.getRuntimeMXBean().getName().getBytes());
        long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
        for (i = 7; i >= 0; --i) {
            digester.update((byte)(startTime & 255L));
            startTime >>= 8;
        }
        digester.update("VRUZE8ezHGJYH73A8U4faPLAGDAvJp8C".getBytes());
        digester.update(ManagementFactory.getRuntimeMXBean().getSpecName().getBytes());
        digester.update(ManagementFactory.getRuntimeMXBean().getSpecVendor().getBytes());
        digester.update(ManagementFactory.getRuntimeMXBean().getSpecVersion().getBytes());
        hashCode = Object.class.hashCode();
        for (i = 4; i >= 0; --i) {
            digester.update((byte)(hashCode & 255));
            hashCode >>= 8;
        }
        digester.update(ManagementFactory.getRuntimeMXBean().getVmName().getBytes());
        digester.update(ManagementFactory.getRuntimeMXBean().getVmVendor().getBytes());
        digester.update(ManagementFactory.getRuntimeMXBean().getVmVersion().getBytes());
        return digester.digest();
    }

    public static byte[] pad(byte[] data, int blockSize) {
        if (data == null) {
            throw new IllegalArgumentException("Null data not allowed.");
        }
        if (blockSize <= 0 || blockSize % 8 != 0) {
            throw new IllegalArgumentException("Bad block size. Must be positive and a multiple of 8.");
        }
        if ((blockSize /= 8) > 64) {
            throw new IllegalArgumentException("Unusually large blocksize of more than 512 bits");
        }
        byte[] retVal = new byte[blockSize * (data.length / blockSize + 1)];
        byte padValue = (byte)(retVal.length - data.length);
        for (int i = 0; i < retVal.length; ++i) {
            retVal[i] = i < data.length ? data[i] : (byte)padValue;
        }
        return retVal;
    }

    public static byte[] unpad(byte[] data) {
        byte[] retVal = new byte[data.length - data[data.length - 1]];
        for (int i = 0; i < retVal.length; ++i) {
            retVal[i] = data[i];
        }
        return retVal;
    }
}

