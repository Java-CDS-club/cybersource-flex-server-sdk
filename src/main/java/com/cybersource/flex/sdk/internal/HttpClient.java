/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.internal;

import com.cybersource.flex.sdk.exception.FlexSDKInternalException;
import com.cybersource.flex.sdk.internal.Constants;
import com.cybersource.flex.sdk.internal.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

public class HttpClient {
    private static final String ACCEPT = "Accept";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String USER_AGENT = "User-Agent";
    private static final String APPLICATION_JSON = "application/json; charset=utf-8";
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;
    private static final int MAX_BODY_SIZE = 51200;
    private static volatile SSLSocketFactory sslSocketFactory;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static HttpResponse post(Proxy proxy, String url, Map<String, String> headers, String payload) throws IOException, FlexSDKInternalException {
        HttpURLConnection connection;
        connection = HttpClient.newConnection(proxy, "POST", url, headers);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8);
        try {
            out.write(payload);
        }
        finally {
            HttpClient.close(out);
        }
        return new HttpResponse(connection.getResponseCode(), HttpClient.readResponseBody(connection), connection.getHeaderFields());
    }

    public static HttpResponse get(Proxy proxy, String url, Map<String, String> headers) throws IOException, FlexSDKInternalException {
        HttpURLConnection connection = HttpClient.newConnection(proxy, "GET", url, headers);
        return new HttpResponse(connection.getResponseCode(), HttpClient.readResponseBody(connection), connection.getHeaderFields());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static SSLSocketFactory getSSLSocketFactory() throws FlexSDKInternalException {
        if (sslSocketFactory != null) {
            return sslSocketFactory;
        }
        Class<HttpClient> class_ = HttpClient.class;
        synchronized (HttpClient.class) {
            if (sslSocketFactory == null) {
                try {
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, null, new SecureRandom());
                    // ** MonitorExit[var0] (shouldn't be in output)
                    return new SSLSocketFactoryWrapper(sslContext.getSocketFactory());
                }
                catch (Exception e) {
                    throw new FlexSDKInternalException("Unable to initialize HTTP Client", e);
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return sslSocketFactory;
        }
    }

    private static HttpURLConnection newConnection(Proxy proxy, String method, String url, Map<String, String> headers) throws IOException, FlexSDKInternalException {
        HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection(proxy);
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection)connection).setSSLSocketFactory(HttpClient.getSSLSocketFactory());
        }
        connection.setRequestMethod(method);
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setRequestProperty(ACCEPT, APPLICATION_JSON);
        connection.setRequestProperty(CONTENT_TYPE, APPLICATION_JSON);
        connection.setRequestProperty(USER_AGENT, Constants.FLEX_SERVER_SDK_USER_AGENT);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
        return connection;
    }

    private static String readResponseBody(HttpURLConnection connection) throws IOException {
        String string;
        InputStream is = null;
        try {
            is = connection.getResponseCode() < 200 || connection.getResponseCode() >= 300 ? connection.getErrorStream() : connection.getInputStream();
            if (is == null) {
                throw new IOException("Unable to get InputStream.");
            }
            string = HttpClient.readInputStream(is);
        }
        catch (Throwable throwable) {
            HttpClient.close(is);
            throw new RuntimeException(throwable);
        }
        HttpClient.close(is);
        return string;
    }

    private static String readInputStream(InputStream inputStream) throws IOException {
        int bytesRead;
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        char[] buffer = new char[4096];
        while ((bytesRead = reader.read(buffer)) != -1) {
            result.append(buffer, 0, bytesRead);
            if (result.length() <= 51200) continue;
            throw new IOException("Excessive body detected");
        }
        return result.toString();
    }

    private static void close(InputStream is) {
        if (is == null) {
            return;
        }
        try {
            is.close();
        }
        catch (IOException ioe) {
            ioe.getClass();
        }
    }

    private static void close(Writer w) {
        if (w == null) {
            return;
        }
        try {
            w.close();
        }
        catch (IOException ioe) {
            ioe.getClass();
        }
    }

    private static class SSLSocketFactoryWrapper
    extends SSLSocketFactory {
        private static final String[] SUPPORTED_SECURE_PROTOCOLS = new String[]{"TLSv1.2"};
        private final SSLSocketFactory delegate;

        SSLSocketFactoryWrapper(SSLSocketFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return this.delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return this.delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String string, int i, boolean bln) throws IOException {
            return this.wrap(this.delegate.createSocket(socket, string, i, bln));
        }

        @Override
        public Socket createSocket(String string, int i) throws IOException, UnknownHostException {
            return this.wrap(this.delegate.createSocket(string, i));
        }

        @Override
        public Socket createSocket(String string, int i, InetAddress ia, int i1) throws IOException, UnknownHostException {
            return this.wrap(this.delegate.createSocket(string, i, ia, i1));
        }

        @Override
        public Socket createSocket(InetAddress ia, int i) throws IOException {
            return this.wrap(this.delegate.createSocket(ia, i));
        }

        @Override
        public Socket createSocket(InetAddress ia, int i, InetAddress ia1, int i1) throws IOException {
            return this.wrap(this.delegate.createSocket(ia, i, ia1, i1));
        }

        private Socket wrap(Socket socket) {
            if (socket instanceof SSLSocket) {
                SSLSocket s = (SSLSocket)socket;
                s.setEnabledProtocols(SUPPORTED_SECURE_PROTOCOLS);
            }
            return socket;
        }
    }

}

