package com.wework.processor;

import com.wework.urls.Website;
import org.junit.Test;

import javax.naming.InsufficientResourcesException;
import java.security.InvalidParameterException;

/**
 * @author Riti
 */

public class URLFetcherTest {
    WebSearchCallBackHandlerTester success = new WebSearchCallBackHandlerTester();
    WebSearchFailureTesterHandler failure = new WebSearchFailureTesterHandler();

    @Test(expected = InvalidParameterException.class)
    public void testNullURL() throws InsufficientResourcesException {
        Website s = new Website(1, null, 1, 1, 1, 1);
        URLFetcherThreadFactory.createdAThread(s, success);
    }

    @Test(expected = InvalidParameterException.class)
    public void testEmptyURL() throws InsufficientResourcesException {
        Website s = new Website(1, " ", 1, 1, 1, 1);
        URLFetcherThreadFactory.createdAThread(s, success);
    }

    @Test
    public void testWrongURL() throws InvalidParameterException, InsufficientResourcesException {
        Website s = new Website(1, "\"thisisafakeurl.com/\"", 1, 1, 1, 1);
        URLFetcher f = URLFetcherThreadFactory.createdAThread(s, failure);
        f.run();
    }

    @Test
    public void testCorrectURL() throws InvalidParameterException, InsufficientResourcesException {
        Website s = new Website(1, "\"google.com/\"", 1, 1, 1, 1);
        URLFetcher f = URLFetcherThreadFactory.createdAThread(s, success);
        f.run();
    }

}