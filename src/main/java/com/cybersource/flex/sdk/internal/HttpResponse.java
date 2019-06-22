/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.internal;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class HttpResponse {
    private final int status;
    private final String body;
    private final Map<String, List<String>> headers = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);

    public HttpResponse(int status, String body, Map<String, List<String>> headers) {
        this.status = status;
        String string = this.body = body != null ? body : "";
        if (headers != null) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                if (entry.getKey() == null) continue;
                this.headers.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public int getStatus() {
        return this.status;
    }

    public String getBody() {
        return this.body;
    }

    public boolean hasHeader(String key) {
        return this.headers.containsKey(key);
    }

    public List<String> getHeaderValues(String key) {
        return this.headers.get(key);
    }

    public String getHeaderFirstValue(String key) {
        List<String> values = this.headers.get(key);
        return values != null && values.size() > 0 ? values.get(0) : null;
    }

    public boolean isErrorResponse() {
        int range = this.status / 100;
        return range >= 4 && range <= 5;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName()).append(", ");
        sb.append("HTTP status: ").append(this.status).append(".\n");
        for (Map.Entry<String, List<String>> e : this.headers.entrySet()) {
            sb.append(e.getKey()).append(": ").append(e.getValue()).append('\n');
        }
        sb.append('\n').append(this.body);
        return sb.toString();
    }
}

