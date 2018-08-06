package com.wework.search;

import com.wework.processor.URLFetcherThreadFactory;
import org.junit.Test;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * @author Riti
 */

public class WebSearcherTest {
    private static final Logger log = Logger.getLogger(WebSearcherTest.class.getName());

    @Test
    public void testMain() throws IOException, InterruptedException {
        String[] args = new String[]{"-searchText", "face", "-fileWithURLs", "https://raw.githubusercontent.com/bahlani/wework-websearcher/master/test_urls_file.txt"};
        WebSearcher.main(args);
        while (!URLFetcherThreadFactory.endAllThreads()) {
            this.wait(1000);
        }
        File[] f = checkResultFileForSearchTerm(new File("./"), "face");
        assertTrue(f.length > 0);
        try (BufferedReader br = new BufferedReader(new FileReader(f[0]))) {
            assertTrue(br.readLine().contains("facebook.com"));
        }
    }


    private static File[] checkResultFileForSearchTerm(File directory, final String searchTerm) {
        return directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                try {
                    return pathname.getName().startsWith("result_" + URLEncoder.encode(searchTerm, "UTF-8") + "_" + new SimpleDateFormat("yyyy_MM_dd_HH").format(new Date()))
                            && pathname.getName().endsWith(".txt");
                } catch (UnsupportedEncodingException e) {
                    log.log(Level.SEVERE, "Unable to verify the search term in result file: ", e);
                    return false;
                }
            }
        });
    }
}

