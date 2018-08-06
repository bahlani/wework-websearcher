package com.wework.processor;

import com.wework.urls.Website;
import com.wework.utility.SearchConstants;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * URLFetcher thread to fetch data from a website
 * @author Riti
 */

public class URLFetcher extends Thread {
    private Website site;
    private WebSearchCallBackHandler callback;
    private static final Logger log = Logger.getLogger(URLFetcher.class.getName());

    /**
     * Constructor
     * @param website Website that going to fetch
     * @param callback Callback method will be called with the Website object and the content from it. *Note: Content can be null if any exception encountered.
     */
    public URLFetcher(Website website, WebSearchCallBackHandler callback) {
        if (website == null || callback == null)
            throw new InvalidParameterException("Both parameters are required for the constructor");
        this.site = website;
        this.callback = callback;
    }

    @Override
    public void run() {
        URL url = null;
        try {
            boolean redirected = false;
            int responseCode = 0;
            int tries = 0;
            String location = site.getUrl();
            HttpURLConnection urlConn = null;
            do {
                url = new URL(location);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(SearchConstants.CONNECTION_TIMEOUT);
                urlConn.setReadTimeout(SearchConstants.READ_TIMEOUT);
                urlConn.setUseCaches(false);
                urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
                urlConn.setRequestMethod("GET");
                urlConn.setInstanceFollowRedirects(true);
                urlConn.setAllowUserInteraction(false);
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                responseCode = urlConn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM) { //handle 302 and 301 redirect.
                    redirected = true;
                    location = urlConn.getHeaderField("Location");
                    tries++;
                } else
                    redirected = false;
            } while (redirected && tries < 3);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try {
                    InputStream is = urlConn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = br.readLine()) != null)
                        response.append(inputLine);

                    String content = response.toString();
                    callback.webSearcherCallBack(site, content);

                } catch (Exception e) {
                    log.log(Level.WARNING, "Website:" + site.getUrl() + " failed fetching.", e);
                    try {
                        this.callback.webSearcherCallBack(site, null); //any exception, null is returned as the content.
                    } catch (Exception e1) {
                        log.log(Level.SEVERE, "Unable to Search website " + site.toString(), e1);
                    }
                }

            } else this.callback.webSearcherCallBack(site, null);
        } catch (Exception e) {
            log.log(Level.WARNING, "Website:" + site.getUrl() + " failed fetching.", e);
            try {
                this.callback.webSearcherCallBack(site, null); //any exception, null is returned as the content.
            } catch (Exception e1) {
                log.log(Level.SEVERE, "Unable to Search website " + site.toString(), e1);
            }
        }

    }
}