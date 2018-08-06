package com.wework.processor;

import com.wework.urls.Website;
import com.wework.utility.SearchConstants;

import javax.naming.InsufficientResourcesException;
import java.security.InvalidParameterException;

/**
 * Thread factory. Potentially could create not only URLFetcher thread (using Generics).
 *
 * @author Riti
 */

public class URLFetcherThreadFactory {
    private static int freeThreads = SearchConstants.MAX_THREAD_POOL_COUNT;
    private static final Object threadLock = new Object();

    /**
     * Create a thread from the thread pool for URLFetcher.
     *
     * @param website  Website  object
     * @param callback to handle logic after the content is fetched.
     * @return a URLFetcher thread.
     * @throws InsufficientResourcesException
     */
    public static URLFetcher createdAThread(Website website, WebSearchCallBackHandler callback) throws InvalidParameterException, InsufficientResourcesException {
        if (website == null || callback == null) throw new InvalidParameterException();
        synchronized (threadLock) {
            if (freeThreads > 0) {
                freeThreads--;
                return new URLFetcher(website, callback);
            } else
                throw new InsufficientResourcesException();
        }
    }

    public static boolean isFreeThreadAvailable() {
        synchronized (threadLock) {
            return freeThreads > 0;
        }
    }

    public static int getCountOfRunniningThreads() {
        synchronized (threadLock) {
            return SearchConstants.MAX_THREAD_POOL_COUNT - freeThreads;
        }
    }

    public static void releaseThread() {
        synchronized (threadLock) {
            if (freeThreads < SearchConstants.MAX_THREAD_POOL_COUNT)
                freeThreads++;
        }
    }

    public static boolean endAllThreads() {
        synchronized (threadLock) {
            return freeThreads == SearchConstants.MAX_THREAD_POOL_COUNT;
        }
    }

}


