/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

import java.util.*;

public class FlexErrorResponse {

    private ResponseStatus responseStatus;
    private ErrorLinks links = new ErrorLinks();

    public FlexErrorResponse(ResponseStatus responseStatus) {
        if (responseStatus == null) {
            throw new IllegalArgumentException("ResponseStatus must not be null");
        }
        this.responseStatus = responseStatus;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public ErrorLinks getLinks() {
        return links;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FlexErrorResponse{");
        sb.append("responseStatus=").append(responseStatus);
        sb.append('}');
        return sb.toString();
    }

    public static class ResponseStatus {

        private int status;
        private String reason;
        private String message;
        private String correlationId;
        private List<ResponseStatusDetail> details = new ArrayList<ResponseStatusDetail>();
        private Map<String, Object> embedded = new LinkedHashMap<String, Object>();

        public ResponseStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public List<ResponseStatusDetail> getDetails() {
            return Collections.unmodifiableList(details);
        }

        public void setDetails(List<ResponseStatusDetail> details) {
            this.details = details;
        }

        public void addDetail(ResponseStatusDetail detail) {
            details.add(detail);
        }

        public Map<String, Object> getEmbedded() {
            return embedded;
        }

        public void setEmbedded(Map<String, Object> embedded) {
            this.embedded = embedded;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ResponseStatus{");
            sb.append("status=").append(status);
            sb.append(", reason='").append(reason).append('\'');
            sb.append(", message='").append(message).append('\'');
            sb.append(", correlationId='").append(correlationId).append('\'');
            sb.append(", details=").append(details);
            sb.append(", embedded=").append(embedded);
            sb.append('}');
            return sb.toString();
        }

        public static class ResponseStatusDetail {

            private String location;
            private String message;

            public ResponseStatusDetail(String location, String message) {
                this.location = location;
                this.message = message;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            @Override
            public String toString() {
                final StringBuilder sb = new StringBuilder("ResponseStatusDetail{");
                sb.append("location='").append(location).append('\'');
                sb.append(", message='").append(message).append('\'');
                sb.append('}');
                return sb.toString();
            }
        }
    }
}
