package com.wework.utility;

import com.wework.urls.Website;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File Handler for Websearcher
 * @author Riti
 *
 */

public class FileSystemHandler {
    private static final Logger log = Logger.getLogger(FileSystemHandler.class.getName());

    /**
     * Initialize all Websites by providing a text file contains all URLs.
     * @param fileWithURLs an url to a file containing all URLs the search term need to be searched in.
     * @return A List of all URLs.
     */
    public List<Website> initializeMap(String fileWithURLs) throws IOException {
        List<Website> websites = new ArrayList<Website>();
        InputStream is = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(fileWithURLs);
            is=url.openStream();
            String currentLine;
            bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            bufferedReader.readLine();
            while ((currentLine = bufferedReader.readLine()) != null) {
                String[] values = currentLine.split(",");
                Website website = new Website(Integer.parseInt(values[0]), values[1],
                        Integer.parseInt(values[2]), Integer.parseInt(values[3])
                        , Float.parseFloat(values[4]), Float.parseFloat(values[5]));
                websites.add(website);
            }
        } catch (IOException ioe) {
            log.log(Level.SEVERE, "Unable to read data from the File of URL's", ioe);
            throw ioe;
        } finally {
            if (is != null) is.close();
            if (bufferedReader != null) bufferedReader.close();
        }

        return websites;
    }

    /**
     * Initialize the BufferedWriter to the result file.
     * The file will be /results/result_searchTerm_timestamp.txt
     * @param searchTerm
     */
    public BufferedWriter initializeResultFile(String searchTerm) throws Exception {
        File file;
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            file = new File("result_" + URLEncoder.encode(searchTerm, "UTF-8") + "_"
                    + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + ".txt");
            if (file.exists())
                file.delete();
            file.createNewFile();
            fw = new FileWriter(file.getAbsoluteFile(), true);
            bw = new BufferedWriter(fw);
            return bw;
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Result file is not accessible", ex);
            throw ex;
        }
    }
}
