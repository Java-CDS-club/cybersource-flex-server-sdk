/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.internal;

public final class TEA {
    public static final int BLOCK_SIZE_BITS = 64;
    public static final int BLOCK_SIZE_BYTES = 8;
    public static final int KEY_SIZE_BITS = 128;
    public static final int KEY_SIZE_BYTES = 16;

    private TEA() {
        throw new IllegalStateException("This is fully static and stateless cipher");
    }

    public static long encrypt(int v0, int v1, int k0, int k1, int k2, int k3) {
        int sum = 0;
        int delta = -1640531527;
        for (int i = 0; i < 32; ++i) {
            v1 += ((v0 += (v1 << 4) + k0 ^ v1 + (sum += delta) ^ (v1 >>> 5) + k1) << 4) + k2 ^ v0 + sum ^ (v0 >>> 5) + k3;
        }
        return TEA.pack(v0, v1);
    }

    public static long decrypt(int v0, int v1, int k0, int k1, int k2, int k3) {
        int sum = -957401312;
        int delta = -1640531527;
        for (int i = 0; i < 32; ++i) {
            v0 -= ((v1 -= (v0 << 4) + k2 ^ v0 + sum ^ (v0 >>> 5) + k3) << 4) + k0 ^ v1 + sum ^ (v1 >>> 5) + k1;
            sum -= delta;
        }
        return TEA.pack(v0, v1);
    }

    public static void encrypt(int[] v, int[] k) {
        int v0 = v[0];
        int v1 = v[1];
        int k0 = k[0];
        int k1 = k[1];
        int k2 = k[2];
        int k3 = k[3];
        long result = TEA.encrypt(v0, v1, k0, k1, k2, k3);
        TEA.unpack(result, v);
    }

    public static void decrypt(int[] v, int[] k) {
        int v0 = v[0];
        int v1 = v[1];
        int k0 = k[0];
        int k1 = k[1];
        int k2 = k[2];
        int k3 = k[3];
        long result = TEA.decrypt(v0, v1, k0, k1, k2, k3);
        TEA.unpack(result, v);
    }

    public static void encrypt(byte[] v, byte[] k) {
        TEA.encrypt(v, k, 0);
    }

    public static void encrypt(byte[] v, byte[] k, int vpos) {
        int v0 = TEA.pack(v[vpos + 0], v[vpos + 1], v[vpos + 2], v[vpos + 3]);
        int v1 = TEA.pack(v[vpos + 4], v[vpos + 5], v[vpos + 6], v[vpos + 7]);
        int k0 = TEA.pack(k[0], k[1], k[2], k[3]);
        int k1 = TEA.pack(k[4], k[5], k[6], k[7]);
        int k2 = TEA.pack(k[8], k[9], k[10], k[11]);
        int k3 = TEA.pack(k[12], k[13], k[14], k[15]);
        long result = TEA.encrypt(v0, v1, k0, k1, k2, k3);
        TEA.unpack(result, v, vpos);
    }

    public static void decrypt(byte[] v, byte[] k) {
        TEA.decrypt(v, k, 0);
    }

    public static void decrypt(byte[] v, byte[] k, int vpos) {
        int v0 = TEA.pack(v[vpos + 0], v[vpos + 1], v[vpos + 2], v[vpos + 3]);
        int v1 = TEA.pack(v[vpos + 4], v[vpos + 5], v[vpos + 6], v[vpos + 7]);
        int k0 = TEA.pack(k[0], k[1], k[2], k[3]);
        int k1 = TEA.pack(k[4], k[5], k[6], k[7]);
        int k2 = TEA.pack(k[8], k[9], k[10], k[11]);
        int k3 = TEA.pack(k[12], k[13], k[14], k[15]);
        long result = TEA.decrypt(v0, v1, k0, k1, k2, k3);
        TEA.unpack(result, v, vpos);
    }

    private static long pack(int a, int b) {
        return (long)a << 32 | (long)b & 0xFFFFFFFFL;
    }

    private static void unpack(long l, int[] i) {
        i[0] = (int)(l >>> 32);
        i[1] = (int)l;
    }

    private static int pack(byte a, byte b, byte c, byte d) {
        return a << 24 | (b & 255) << 16 | (c & 255) << 8 | d & 255;
    }

    private static void unpack(long l, byte[] b, int pos) {
        b[pos + 0] = (byte)(l >>> 56);
        b[pos + 1] = (byte)(l >>> 48);
        b[pos + 2] = (byte)(l >>> 40);
        b[pos + 3] = (byte)(l >>> 32);
        b[pos + 4] = (byte)(l >>> 24);
        b[pos + 5] = (byte)(l >>> 16);
        b[pos + 6] = (byte)(l >>> 8);
        b[pos + 7] = (byte)l;
    }
}

