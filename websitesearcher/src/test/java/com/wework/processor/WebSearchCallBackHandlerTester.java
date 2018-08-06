package com.wework.processor;

import com.wework.urls.Website;

import static org.junit.Assert.assertNotNull;

/**
 * @author Riti
 */

public class WebSearchCallBackHandlerTester implements WebSearchCallBackHandler {

	@Override
	public void webSearcherCallBack(Website site, String content) {
		assertNotNull(content);
	}
}
