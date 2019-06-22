/**
 * Copyright (c) 2017 by CyberSource
 */
package com.cybersource.flex.sdk.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LinkMap {

    private Map<String, Link> linkMap;
    private Map<String, List<Link>> linkListMap;

    public LinkMap() {
        linkMap = new LinkedHashMap<String, Link>();
        linkListMap = new LinkedHashMap<String, List<Link>>();
    }

    public Link getLink(String linkName) {
        return linkMap.get(linkName);
    }

    public void setLink(String linkName, Link link) {
        linkMap.put(linkName, link);
    }

    public void addEmptyLink(String linkName) {
        linkMap.put(linkName, null);
    }

    public List<Link> getLinkList(String linksName) {
        List<Link> linkList = linkListMap.get(linksName);
        if (linkList == null) {
            linkList = new ArrayList<Link>();
            linkListMap.put(linksName, linkList);
        }
        return linkList;
    }

    public void addLinkToList(String linksName, Link link) {
        List<Link> linkList = linkListMap.get(linksName);
        if (linkList == null) {
            linkList = new ArrayList<Link>();
        }
        linkList.add(link);
        linkListMap.put(linksName, linkList);
    }

    public void addLinksToList(String linksName, List<Link> links) {
        List<Link> linkList = linkListMap.get(linksName);
        if (linkList == null) {
            linkList = new ArrayList<Link>();
        }
        linkList.addAll(links);
        linkListMap.put(linksName, linkList);
    }

    public void addEmptyLinkList(String linksName) {
        linkListMap.put(linksName, new ArrayList<Link>(0));
    }
}
