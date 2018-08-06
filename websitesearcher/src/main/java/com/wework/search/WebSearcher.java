package com.wework.search;

import com.wework.processor.URLFetcher;
import com.wework.processor.URLFetcherThreadFactory;
import com.wework.processor.WebSearchCallBackHandler;
import com.wework.urls.Website;
import com.wework.utility.FileSystemHandler;
import com.wework.utility.SearchConstants;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import javax.naming.InsufficientResourcesException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author Riti
 */

/**
 * Coding Challenge for WeWork
 * Given a list of urls in urls.txt: https://s3.amazonaws.com/fieldlens-public/urls.txt, write a program that will fetch each page and determine whether a search term exists on the page (this search can be a really rudimentary regex - this part isn't too important).
 * You can make up the search terms. Ignore the addition information in the urls.txt file.
 * Constraints
 * 		Search is case insensitive
 * 		Should be concurrent.
 * 		But! It shouldn't have more than 20 HTTP requests at any given time.
 * 		The results should be writted out to a file results.txt
 * 		Avoid using thread pooling libraries like Executor, ThreadPoolExecutor, Celluloid, or Parallel streams.
 */
public class WebSearcher implements WebSearchCallBackHandler {
    @Option(name = "-searchText", required = false, usage = "String to be searched in websites.")
    private String searchText = SearchConstants.DEFAULT_SEARCH_TEXT;
    @Option(name = "-fileWithURLs", required = false, usage = "Link to the file with website details to search the text")
    private String fileWithURLs = SearchConstants.DEFAULT_URL_FILE;

    private static final Logger log = Logger.getLogger(WebSearcher.class.getName());
    private BlockingQueue<Website> queue = new LinkedBlockingQueue<>();

    private final Object lock_matchingSites_bw = new Object();
    private BufferedWriter bw = null;

    /**
     * @param args
     * You can use following configs:<br/>
     * -searchText SearchTerm<br/>
     * -fileWithURLs URL to a websites file.
     */
    public static void main(String[] args) {
        try {
            new WebSearcher().start(args);
        } catch (Exception e) {
            //This can be improvised by handling the application in a suitable manner.
            System.exit(-1);
        }
    }

    private void start(String[] args) throws Exception {
        if (!parseArgs(args)) return;
        FileSystemHandler fileSystemHandler = new FileSystemHandler();
        List<Website> websitesToBeVisited = fileSystemHandler.initializeMap(fileWithURLs);
        searchText(fileSystemHandler, websitesToBeVisited);

    }

    /**
     * This method uses as Blocking Queue to determine pages that need to scanned to find the search term.
     * Results are stored in and the result will be saved in /results/result_searchTerm_timestamp.txt
     * @param fileSystemHandler used to manage writing to result file.
     * @param websitesToBeVisited is the list of unvisited website
     * @throws InvalidParameterException
     */
    private void searchText(FileSystemHandler fileSystemHandler, List<Website> websitesToBeVisited) throws Exception {
        bw = fileSystemHandler.initializeResultFile(searchText);
        addWebsitesToQueueForProcessing(websitesToBeVisited);
        startSearching();
    }

    /**
     * This method adds unvisited websites to a Blocking Queue
     * @param websitesToBeVisited is the list of unvisited website
     */
    private void addWebsitesToQueueForProcessing(List<Website> websitesToBeVisited) {
        queue.addAll(websitesToBeVisited);
    }

    /**
     * Main dispatcher method for operation queue.
     */
    private synchronized void startSearching() throws Exception {
        while (queue.size() > 0) { //more sites to go
            if (URLFetcherThreadFactory.isFreeThreadAvailable()) { //have available thread to handle it.
                Website s = this.queue.poll();
                try {
                    URLFetcher f = URLFetcherThreadFactory.createdAThread(s, this);
                    f.start();
                } catch (InsufficientResourcesException e) {
                    log.log(Level.SEVERE, "Not Enough Resources", e);
                    throw e;
                }
            } else { //not enough resources at this moment, wait for a sec.
                try {
                    this.wait(1000);
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "Main Thread Interrupted.", e);
                    throw e;
                }
            }
        }
        while (!URLFetcherThreadFactory.endAllThreads()) { //if still have running child thread, wait for a sec.
            try {
                this.wait(1000);
            } catch (InterruptedException e) {
                log.log(Level.SEVERE, "Main Thread Interrupted.", e);
                throw e;
            }
        }
        try {
            bw.close(); //We went through everything, let's close the BufferWriter.
        } catch (IOException e) {
            log.log(Level.SEVERE, "Result file is not accessible.", e);
            throw e;
        }
    }

    private boolean parseArgs(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            log.log(Level.SEVERE, "Java " + getClass().getSimpleName() + " [options...]", e);
            parser.printUsage(System.err);
            return false;
        }
        return true;
    }

    /**
     * Callback method for searching the web content if the returning content contains search term (case sensitive),
     * we will write the website URL into the BufferWriter of Result file.
     */
    @Override
    public void webSearcherCallBack(Website site, String content) throws Exception {
        if (content != null && Pattern.compile(searchText).matcher(content).find()) {//Pattern matcher is case sensitive
            synchronized (lock_matchingSites_bw) {
                try {
                    bw.write(site + "\n");
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Failed to write to result file for site: " + site.toString(), e);
                    throw e;
                }
                log.log(Level.INFO, ("Website:" + site.getUrl() + " MATCHED"));
            }
        } else{
            log.log(Level.INFO, ("Website:" + site.getUrl() + " NOT MATCHED"));
        }
        URLFetcherThreadFactory.releaseThread();
        log.log(Level.INFO, ("Website:" + site.getUrl() + " PROCESSED."));
        log.log(Level.INFO, ("Number of running threads:" + URLFetcherThreadFactory.getCountOfRunniningThreads()));
    }
}
