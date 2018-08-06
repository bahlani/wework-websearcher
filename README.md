# wework-websearcher
Website Searcher 

Given a list of urls in urls.txt: https://s3.amazonaws.com/fieldlens-public/urls.txt, write a program that will fetch each page and determine whether a search term exists on the page (this search can be a really rudimentary regex - this part isn't too important).  

You can make up the search terms. Ignore the addition information in the urls.txt file.  
Constraints: 
Search is case insensitive Should be concurrent. 
But! It shouldn't have more than 20 HTTP requests at any given time. 
The results should be writted out to a file results.txt 
Avoid using thread pooling libraries like Executor, ThreadPoolExecutor, Celluloid, or Parallel streams. The solution must be written in Kotlin or Java.  Sample urls.txt: https://s3.amazonaws.com/fieldlens-public/urls.txt  The solution must be able to be run from the command line (dont assume JDK is available): java -jar ./websitesearcher.jar

Getting Started Simply run it by command line: java -jar ./websitesearcher.jar [-options] where options include: -searchText the Search Term (Default: Social) -fileWithURLs a file url which contains a list of urls as input (Default: https://s3.amazonaws.com/fieldlens-public/urls.txt).

JAVA 1.8 and Maven required for build and run test cases.

Running the tests: mvn test

Built With Maven - Dependency Management

Version: 0.1

Author Riti Bahlani
