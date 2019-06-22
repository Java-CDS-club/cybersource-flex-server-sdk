/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

import java.util.List;

public class ErrorLinks {

    private static final String SELF = "self";
    private static final String DOCUMENTATION = "documentation";
    private static final String NEXT = "next";

    private LinkMap linkMap;

    public ErrorLinks() {
        linkMap = new LinkMap();
        linkMap.addEmptyLink(SELF);
        linkMap.addEmptyLinkList(DOCUMENTATION);
        linkMap.addEmptyLinkList(NEXT);
    }

    public Link getSelf() {
        return linkMap.getLink(SELF);
    }

    public void setSelf(Link self) {
        linkMap.setLink(SELF, self);
    }

    public List<Link> getDocumentation() {
        return linkMap.getLinkList(DOCUMENTATION);
    }

    public void addDocumentationLinks(List<Link> links) {
        linkMap.addLinksToList(DOCUMENTATION, links);
    }

    public List<Link> getNext() {
        return linkMap.getLinkList(NEXT);
    }

    public void addNextLinks(List<Link> links) {
        linkMap.addLinksToList(NEXT, links);
    }
}
