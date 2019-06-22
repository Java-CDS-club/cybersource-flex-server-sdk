/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk;

import com.cybersource.flex.sdk.authentication.FlexCredentials;
import com.cybersource.flex.sdk.impl.FlexKeyServiceImpl;
import com.cybersource.flex.sdk.internal.Constants;
import java.net.InetSocketAddress;
import java.net.Proxy;

public final class FlexServiceFactory {

    private FlexServiceFactory() {
        throw new IllegalStateException();
    }

    public static FlexService createInstance(FlexCredentials credentials) {
        return new FlexKeyServiceImpl(credentials, FlexServiceFactory.FlexServiceConfiguration.DEFAULT);
    }

    public static FlexService createInstance(FlexCredentials credentials, FlexServiceConfiguration flexServiceConfiguration) {
        return new FlexKeyServiceImpl(credentials, flexServiceConfiguration);
    }

    public static final class FlexServiceConfiguration {

        public static final FlexServiceConfiguration DEFAULT = new FlexServiceConfigurationBuilder().build();
        public static final FlexServiceConfiguration DEBUG = new FlexServiceConfigurationBuilder().setLoggingEnabled(true).build();

        private final boolean loggingEnabled;
        private final Proxy proxy;
        // configurable timeouts to be added in future

        private FlexServiceConfiguration(boolean loggingEnabled, Proxy proxy) {
            this.loggingEnabled = loggingEnabled;
            this.proxy = proxy;
        }

        public boolean isLoggingEnabled() {
            return loggingEnabled;
        }

        public Proxy getProxy() {
            return proxy;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("version:").append(Constants.SDK_VERSION).append(",");
            sb.append("loggingEnabled:").append(loggingEnabled).append(",");
            sb.append("proxy:").append(proxy).append(",");
            sb.append("}");
            return sb.toString();
        }

    }

    public static final class FlexServiceConfigurationBuilder {

        private boolean loggingEnabled = false;
        private Proxy proxy = Proxy.NO_PROXY;

        public FlexServiceConfiguration build() {
            return new FlexServiceConfiguration(loggingEnabled, proxy);
        }

        public boolean isLoggingEnabled() {
            return loggingEnabled;
        }

        public FlexServiceConfigurationBuilder setLoggingEnabled(boolean loggingEnabled) {
            this.loggingEnabled = loggingEnabled;
            return this;
        }

        public Proxy getProxy() {
            return proxy;
        }

        public FlexServiceConfigurationBuilder setProxy(Proxy proxy) {
            if (proxy == null) {
                this.proxy = Proxy.NO_PROXY;
            } else {
                this.proxy = proxy;
            }
            return this;
        }

        public FlexServiceConfigurationBuilder setNoProxy() {
            setProxy(null);
            return this;
        }

        public FlexServiceConfigurationBuilder setHttpProxy(String hostname, int port) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(hostname, port));
            return this;
        }

        public FlexServiceConfigurationBuilder setSocksProxy(String hostname, int port) {
            proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(hostname, port));
            return this;
        }

    }

}
