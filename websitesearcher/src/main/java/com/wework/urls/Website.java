package com.wework.urls;

import com.wework.utility.SearchConstants;

import java.security.InvalidParameterException;

/**
 * POJO for Website
 * @author Riti
 */

public class Website {
    private int rank;
    private String url;
    private int linkingRootDomains;
    private int externalLinks;
    private float mozRank;
    private float mozTrust;

    public Website(int rank, String url, int linkingRootDomains, int externalLinks, float mozRank, float mozTrust) {
        this.rank = rank;
        if (url == null || url.trim().length() == 0) throw new InvalidParameterException("URL cannot be NULL");
        this.url = SearchConstants.URL_SUFFIX + url.substring(1, url.length() - 2);
        this.linkingRootDomains = linkingRootDomains;
        this.externalLinks = externalLinks;
        this.mozRank = mozRank;
        this.mozTrust = mozTrust;
    }

    public int getRank() {
        return rank;
    }

    public String getUrl() {
        return url;
    }

    public int getLinkingRootDomains() {
        return linkingRootDomains;
    }

    public int getExternalLinks() {
        return externalLinks;
    }

    public float getMozRank() {
        return mozRank;
    }

    public float getMozTrust() {
        return mozTrust;
    }

    @Override
    public String toString() {
        return url;
    }
}
