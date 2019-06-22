/*
 * Decompiled with CFR 0.139.
 */
package com.cybersource.flex.sdk.impl;

import com.cybersource.flex.sdk.exception.FlexSDKInternalException;
import com.cybersource.flex.sdk.model.DerPublicKey;
import com.cybersource.flex.sdk.model.EncryptionType;
import com.cybersource.flex.sdk.model.ErrorLinks;
import com.cybersource.flex.sdk.model.FlexErrorResponse;
import com.cybersource.flex.sdk.model.FlexPublicKey;
import com.cybersource.flex.sdk.model.FlexToken;
import com.cybersource.flex.sdk.model.JsonWebKey;
import com.cybersource.flex.sdk.model.KeyRequestSettings;
import com.cybersource.flex.sdk.model.KeysRequestParameters;
import com.cybersource.flex.sdk.model.Link;
import com.cybersource.flex.sdk.repackaged.JSONArray;
import com.cybersource.flex.sdk.repackaged.JSONException;
import com.cybersource.flex.sdk.repackaged.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class JsonHelper {
    static JSONObject toJson(KeysRequestParameters params) {
        JSONObject retVal = new JSONObject();
        retVal.put("encryptionType", params.getEncryptionType().toString());
        JsonHelper.addFieldIfPresent(retVal, "targetOrigin", params.getTargetOrigin());
        KeyRequestSettings settings = params.getSettings();
        if (settings != null) {
            JSONObject settingsJson = new JSONObject();
            JsonHelper.addFieldIfPresent(settingsJson, "unmaskedLeft", settings.getUnmaskedLeft());
            JsonHelper.addFieldIfPresent(settingsJson, "unmaskedRight", settings.getUnmaskedRight());
            JsonHelper.addFieldIfPresent(settingsJson, "enableBillingAddress", settings.isEnableBillingAddress());
            JsonHelper.addFieldIfPresent(settingsJson, "currency", settings.getCurrency());
            JsonHelper.addFieldIfPresent(settingsJson, "enableAutoAuth", settings.isEnableAutoAuth());
            retVal.put("settings", settingsJson);
        }
        return retVal;
    }

    private static void addFieldIfPresent(JSONObject json, String key, Object value) {
        if (value != null) {
            json.put(key, value);
        }
    }

    static FlexPublicKey parseFlexPublicKey(String body) throws FlexSDKInternalException {
        try {
            if (body == null || body.length() <= 0) {
                throw new JSONException("JSON body must not be empty");
            }
            JSONObject json = new JSONObject(body);
            FlexPublicKey retVal = new FlexPublicKey();
            retVal.setKeyId(json.getString("keyId"));
            JSONObject jsonDer = json.optJSONObject("der");
            if (jsonDer == null) {
                throw new JSONException("der is expected");
            }
            String format = jsonDer.getString("format");
            String algorithm = jsonDer.getString("algorithm");
            String publicKey = jsonDer.getString("publicKey");
            retVal.setDer(new DerPublicKey(format, algorithm, publicKey));
            JSONObject jsonJwk = json.optJSONObject("jwk");
            if (jsonJwk != null) {
                String kty = jsonJwk.getString("kty");
                String e = jsonJwk.getString("e");
                String use = jsonJwk.getString("use");
                String kid = jsonJwk.getString("kid");
                String n = jsonJwk.getString("n");
                retVal.setJwk(new JsonWebKey(kty, use, kid, n, e));
            }
            return retVal;
        }
        catch (JSONException e) {
            throw new FlexSDKInternalException("Error parsing FLEX API key response: [" + e.getMessage() + "]");
        }
    }

    static FlexToken parseFlexTokenResponse(String body) throws FlexSDKInternalException {
        try {
            JSONObject embedded;
            if (body == null || body.length() <= 0) {
                throw new JSONException("JSON body must not be empty");
            }
            JSONObject json = new JSONObject(body);
            FlexToken token = new FlexToken();
            token.setKeyId(json.getString("keyId"));
            token.setToken(json.getString("token"));
            token.setMaskedPan(json.getString("maskedPan"));
            token.setCardType(json.getString("cardType"));
            token.setTimestamp(json.getLong("timestamp"));
            JSONObject discoverableServices = JsonHelper.getObjectOrNullFromJson(json, "discoverableServices");
            if (discoverableServices != null) {
                token.setDiscoverableServices(JsonHelper.parseArbitraryJsonObject(discoverableServices));
            }
            if ((embedded = JsonHelper.getObjectOrNullFromJson(json, "_embedded")) != null) {
                token.setEmbedded(JsonHelper.parseArbitraryJsonObject(embedded));
            }
            token.setSignedFields(json.getString("signedFields"));
            token.setSignature(json.getString("signature"));
            return token;
        }
        catch (JSONException e) {
            throw new FlexSDKInternalException("Error parsing FLEX API token response: [" + e.getMessage() + "]");
        }
    }

    static FlexErrorResponse parseFlexErrorResponse(String body) throws FlexSDKInternalException {
        try {
            JSONObject responseStatusObject;
            JSONObject linksObject;
            FlexErrorResponse.ResponseStatus responseStatus;
            if (body == null || body.length() <= 0) {
                throw new JSONException("JSON body must not be empty");
            }
            JSONObject json = new JSONObject(body);
            JSONObject jSONObject = responseStatusObject = json.has("responseStatus") ? json.getJSONObject("responseStatus") : null;
            if (responseStatusObject != null) {
                JSONObject embedded;
                int status = responseStatusObject.getInt("status");
                responseStatus = new FlexErrorResponse.ResponseStatus(status);
                responseStatus.setReason(JsonHelper.getStringOrNullFromJson(responseStatusObject, "reason"));
                responseStatus.setMessage(JsonHelper.getStringOrNullFromJson(responseStatusObject, "message"));
                responseStatus.setCorrelationId(JsonHelper.getStringOrNullFromJson(responseStatusObject, "correlationId"));
                JSONArray detailsArray = JsonHelper.getArrayOrNullFromJson(responseStatusObject, "details");
                if (detailsArray != null) {
                    for (int i = 0; i < detailsArray.length(); ++i) {
                        JSONObject element = detailsArray.getJSONObject(i);
                        String location = element.getString("location");
                        String message = element.getString("message");
                        responseStatus.addDetail(new FlexErrorResponse.ResponseStatus.ResponseStatusDetail(location, message));
                    }
                }
                if ((embedded = JsonHelper.getObjectOrNullFromJson(responseStatusObject, "_embedded")) != null) {
                    responseStatus.setEmbedded(JsonHelper.parseArbitraryJsonObject(embedded));
                }
            } else {
                throw new JSONException("responseStatus is expected");
            }
            FlexErrorResponse errorResponse = new FlexErrorResponse(responseStatus);
            JSONObject jSONObject2 = linksObject = json.has("_links") ? json.getJSONObject("_links") : null;
            if (linksObject != null) {
                errorResponse.getLinks().setSelf(JsonHelper.parseLink(JsonHelper.getObjectOrNullFromJson(linksObject, "self")));
                errorResponse.getLinks().addDocumentationLinks(JsonHelper.parseLinksArray(JsonHelper.getArrayOrNullFromJson(linksObject, "documentation")));
                errorResponse.getLinks().addNextLinks(JsonHelper.parseLinksArray(JsonHelper.getArrayOrNullFromJson(linksObject, "next")));
            }
            return errorResponse;
        }
        catch (JSONException e) {
            throw new FlexSDKInternalException("Error parsing FLEX API error response: [" + e.getMessage() + "]");
        }
    }

    static Map<String, Object> parseArbitraryJsonObject(JSONObject json) throws FlexSDKInternalException {
        try {
            JsonObjectParseEntry currentJsonObjectParseEntry;
            LinkedHashMap<String, Object> parsed = new LinkedHashMap<String, Object>();
            ConcurrentLinkedQueue<JsonObjectParseEntry> jsonObjectParseQueue = new ConcurrentLinkedQueue<JsonObjectParseEntry>();
            ConcurrentLinkedQueue<ValueParseEntry> valueParseQueue = new ConcurrentLinkedQueue<ValueParseEntry>();
            jsonObjectParseQueue.add(new JsonObjectParseEntry(new MapOrList(parsed), json));
            while ((currentJsonObjectParseEntry = (JsonObjectParseEntry)jsonObjectParseQueue.poll()) != null) {
                ValueParseEntry currentValueParseEntry;
                MapOrList mapOrListJsonObject = currentJsonObjectParseEntry.getMapOrList();
                JSONObject jsonObjectCurrent = currentJsonObjectParseEntry.getJsonObject();
                for (String key : jsonObjectCurrent.keySet()) {
                    valueParseQueue.add(new ValueParseEntry(mapOrListJsonObject, key, jsonObjectCurrent.isNull(key) ? null : jsonObjectCurrent.get(key)));
                }
                while ((currentValueParseEntry = (ValueParseEntry)valueParseQueue.poll()) != null) {
                    MapOrList mapOrListValueToParse = currentValueParseEntry.getMapOrList();
                    String key = currentValueParseEntry.getKey();
                    Object value = currentValueParseEntry.getValue();
                    if (value == null) {
                        mapOrListValueToParse.add(key, value);
                        continue;
                    }
                    if (value instanceof String || value instanceof Number || value instanceof Boolean) {
                        mapOrListValueToParse.add(key, value);
                        continue;
                    }
                    if (value instanceof JSONObject) {
                        LinkedHashMap<String, Object> valueMap = new LinkedHashMap<String, Object>();
                        jsonObjectParseQueue.offer(new JsonObjectParseEntry(new MapOrList(valueMap), (JSONObject)value));
                        mapOrListValueToParse.add(key, valueMap);
                        continue;
                    }
                    if (!(value instanceof JSONArray)) continue;
                    JSONArray jsonArray = (JSONArray)value;
                    ArrayList<Object> valueList = new ArrayList<Object>();
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        valueParseQueue.add(new ValueParseEntry(new MapOrList(valueList), null, jsonArray.isNull(i) ? null : jsonArray.get(i)));
                    }
                    mapOrListValueToParse.add(key, valueList);
                }
            }
            return parsed;
        }
        catch (RuntimeException e) {
            throw new FlexSDKInternalException("Error parsing JSON structure: [" + e.getMessage() + "]");
        }
    }

    private static Link parseLink(JSONObject linkObject) {
        if (linkObject == null) {
            return null;
        }
        return new Link(linkObject.getString("href"), linkObject.getString("method"), linkObject.getString("title"));
    }

    private static List<Link> parseLinksArray(JSONArray linksArray) {
        ArrayList<Link> links = new ArrayList<Link>();
        if (linksArray != null) {
            for (int i = 0; i < linksArray.length(); ++i) {
                links.add(JsonHelper.parseLink(linksArray.getJSONObject(i)));
            }
        }
        return links;
    }

    private static String getStringOrNullFromJson(JSONObject jsonObject, String key) {
        return jsonObject.has(key) ? (jsonObject.isNull(key) ? null : jsonObject.getString(key)) : null;
    }

    private static JSONArray getArrayOrNullFromJson(JSONObject jsonObject, String key) {
        return jsonObject.has(key) ? (jsonObject.isNull(key) ? null : jsonObject.getJSONArray(key)) : null;
    }

    private static JSONObject getObjectOrNullFromJson(JSONObject jsonObject, String key) {
        return jsonObject.has(key) ? (jsonObject.isNull(key) ? null : jsonObject.getJSONObject(key)) : null;
    }

    private static class ValueParseEntry {
        private final MapOrList mapOrList;
        private final String key;
        private final Object value;

        public ValueParseEntry(MapOrList mapOrList, String key, Object value) {
            this.mapOrList = mapOrList;
            this.key = key;
            this.value = value;
        }

        public MapOrList getMapOrList() {
            return this.mapOrList;
        }

        public String getKey() {
            return this.key;
        }

        public Object getValue() {
            return this.value;
        }
    }

    private static class JsonObjectParseEntry {
        private final MapOrList mapOrList;
        private final JSONObject jsonObject;

        public JsonObjectParseEntry(MapOrList mapOrList, JSONObject jsonObject) {
            this.mapOrList = mapOrList;
            this.jsonObject = jsonObject;
        }

        public MapOrList getMapOrList() {
            return this.mapOrList;
        }

        public JSONObject getJsonObject() {
            return this.jsonObject;
        }
    }

    private static class MapOrList {
        private Map<String, Object> map;
        private List<Object> list;

        public MapOrList(Map<String, Object> map) {
            this.map = map;
        }

        public MapOrList(List<Object> list) {
            this.list = list;
        }

        public void add(String key, Object value) {
            if (this.map != null) {
                this.map.put(key, value);
            } else {
                this.list.add(value);
            }
        }
    }

}

