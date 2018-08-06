package com.wework.utility;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @author Riti
 */

public class FileSystemHandlerTest {

    @Test
    public void initializeMap() throws IOException {
        FileSystemHandler fileHandler = new FileSystemHandler();
        assertNotNull(fileHandler.initializeMap("https://raw.githubusercontent.com/bahlani/wework-websearcher/master/test_urls_file.txt"));
    }

    @Test
    public void initializeResultFile() throws Exception {
        FileSystemHandler fileHandler = new FileSystemHandler();
        fileHandler.initializeResultFile(SearchConstants.DEFAULT_SEARCH_TEXT);
        String pathToScan = ".";
        String fileThatYouWantToFilter;
        File folderToScan = new File(pathToScan); // import -> import java.io.File;
        File[] listOfFiles = folderToScan.listFiles();
        int count=0;
        if(listOfFiles!=null){
        for (File listOfFile : listOfFiles) {

            if (listOfFile.isFile()) {
                fileThatYouWantToFilter = listOfFile.getName();
                if (fileThatYouWantToFilter.startsWith("result_Social")
                        && fileThatYouWantToFilter.endsWith(".txt")) {
                    count++;
                }
            }
        }
        assertTrue(count>0);
    }
    }
}