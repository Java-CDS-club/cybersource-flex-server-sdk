/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

public class Link {

    private static final String GET = "GET";

    private String href, title, method;

    public Link(String href) {
        this(href, GET);
    }

    public Link(String href, String method) {
        this(href, method, null);
    }

    public Link(String href, String method, String title) {
        this.href = href;
        this.title = title;
        this.method = method;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Link link = (Link) o;

        if (getHref() != null ? !getHref().equals(link.getHref()) : link.getHref() != null) {
            return false;
        }
        if (getTitle() != null ? !getTitle().equals(link.getTitle()) : link.getTitle() != null) {
            return false;
        }
        return !(getMethod() != null ? !getMethod().equals(link.getMethod()) : link.getMethod() != null);

    }

    @Override
    public int hashCode() {
        int result = getHref() != null ? getHref().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getMethod() != null ? getMethod().hashCode() : 0);
        return result;
    }
}
