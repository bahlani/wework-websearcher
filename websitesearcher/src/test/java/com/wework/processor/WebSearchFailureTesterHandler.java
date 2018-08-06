package com.wework.processor;

import com.wework.urls.Website;


import static org.junit.Assert.assertNull;

/**
 * @author Riti
 */

public class WebSearchFailureTesterHandler implements WebSearchCallBackHandler {

	@Override
	public void webSearcherCallBack(Website site, String content) {
		assertNull(content);
	}
}
