package com.wework.processor;

import com.wework.urls.Website;
import com.wework.utility.SearchConstants;
import org.junit.Test;

import javax.naming.InsufficientResourcesException;
import java.security.InvalidParameterException;

import static org.junit.Assert.*;

/**
 * @author Riti
 */

public class URLFetcherThreadFactoryTest {

    private Website site=new Website(1,"google.com", 1, 1, 1, 1);
    private WebSearchCallBackHandlerTester callback=new WebSearchCallBackHandlerTester();

    @Test(expected = InvalidParameterException.class)
    public void testNullForWebsiteAndCallback() throws InsufficientResourcesException {
        URLFetcherThreadFactory.createdAThread(null, null);
    }

    @Test(expected = InvalidParameterException.class)
    public void testNullForCallback() throws InsufficientResourcesException {
        URLFetcherThreadFactory.createdAThread(site, null);
    }

    @Test(expected = InvalidParameterException.class)
    public void testNullForWebsite() throws InsufficientResourcesException {
        URLFetcherThreadFactory.createdAThread(null, callback);
    }


    @Test
    public void testClearAllThread() {
        int c=URLFetcherThreadFactory.getCountOfRunniningThreads();
        for(int i=0; i<c; i++) {
            assertFalse(URLFetcherThreadFactory.endAllThreads());
            URLFetcherThreadFactory.releaseThread();
            assertEquals(c-i-1, URLFetcherThreadFactory.getCountOfRunniningThreads());
        }
        assertTrue(URLFetcherThreadFactory.endAllThreads());
    }

    @Test
    public void testURLFetcherThreadFactoryTest() throws InvalidParameterException, InsufficientResourcesException {
        testClearAllThread();
        for(int i=0; i<SearchConstants.MAX_THREAD_POOL_COUNT; i++) {
            assertTrue(URLFetcherThreadFactory.isFreeThreadAvailable());
            URLFetcherThreadFactory.createdAThread(site, callback);
            assertEquals(i+1, URLFetcherThreadFactory.getCountOfRunniningThreads());
        }
        testClearAllThread();
    }
}