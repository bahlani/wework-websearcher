package com.wework.processor;

import com.wework.urls.Website;

/**
 * Callback method for fetching the web content
 * We need this callback interface to keep URLFetcher Thread for fetching content only
 * @author Riti
 */

public interface WebSearchCallBackHandler {
    void webSearcherCallBack(Website site, String content) throws Exception;
}