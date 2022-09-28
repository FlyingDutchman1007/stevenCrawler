package controller;

import com.google.common.collect.ImmutableList;
import crawler.BasicCrawler;
import crawler.MyCrawler;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * @ClassName MultipleCrawler
 * @Description TODO
 * @Author Steven
 * @Date 2022/9/19
 * @Version 1.0
 **/
public class MultipleCrawlerController {
    private static final Logger logger = (Logger) LoggerFactory.getLogger(MultipleCrawlerController.class);

    public static void main(String[] args) throws Exception {
        // The folder where intermediate crawl data is stored (e.g. list of urls that are extracted from previously
        // fetched pages and need to be crawled later).
        String crawlStorageFolder = "/2021USC/cs572/hw2/crawler/data";

//        FileOutputStream fs1 = new FileOutputStream("/2021USC/cs572/hw2/results/fetch_LATimes.csv");
//        OutputStreamWriter osw1 = new OutputStreamWriter(fs1, "UTF8");
//        CSVFormat fetchFormat = CSVFormat.DEFAULT.withHeader("URL", "status code");
//        CSVPrinter fetchPrinter = new CSVPrinter(osw1, fetchFormat);
//
//        FileOutputStream fs2 = new FileOutputStream("/2021USC/cs572/hw2/results/visit_LATimes.csv");
//        OutputStreamWriter osw2 = new OutputStreamWriter(fs2, "UTF8");
//        CSVFormat visitFormat = CSVFormat.DEFAULT.withHeader("URL downloaded", "size of the downloaded file", "outlinks found", "resulting content type;");
//        CSVPrinter visitPrinter = new CSVPrinter(osw2, visitFormat);
//
//        FileOutputStream fs3 = new FileOutputStream("/2021USC/cs572/hw2/results/urls_LATimes.csv");
//        OutputStreamWriter osw3 = new OutputStreamWriter(fs3, "UTF8");
//        CSVFormat urlsFormat = CSVFormat.DEFAULT.withHeader("encountered URL", "indicator");
//        CSVPrinter urlsPrinter = new CSVPrinter(osw3, urlsFormat);

        CrawlConfig config1 = new CrawlConfig();
        CrawlConfig config2 = new CrawlConfig();

        // The two crawlers should have different storage folders for their intermediate data.
        config1.setCrawlStorageFolder(crawlStorageFolder + "/crawler1");
        config2.setCrawlStorageFolder(crawlStorageFolder + "/crawler2");

        config1.setPolitenessDelay(1000);
        config2.setPolitenessDelay(2000);

        config1.setMaxPagesToFetch(20);
        config2.setMaxPagesToFetch(50);

        config1.setMaxDepthOfCrawling(16);
        config1.setMaxDepthOfCrawling(16);

        // We will use different PageFetchers for the two crawlers.
        PageFetcher pageFetcher1 = new PageFetcher(config1);
        PageFetcher pageFetcher2 = new PageFetcher(config2);

        // We will use the same RobotstxtServer for both of the crawlers.
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher1);

        CrawlController controller1 = new CrawlController(config1, pageFetcher1, robotstxtServer);
        CrawlController controller2 = new CrawlController(config2, pageFetcher2, robotstxtServer);

        List<String> crawler1Domains = ImmutableList.of("https://www.latimes.com");
        List<String> crawler2Domains = ImmutableList.of("https://en.wikipedia.org/");

        controller1.addSeed("https://www.latimes.com");

        controller2.addSeed("https://en.wikipedia.org/wiki/Main_Page");
        controller2.addSeed("https://en.wikipedia.org/wiki/Obama");
        controller2.addSeed("https://en.wikipedia.org/wiki/Bing");

        CrawlController.WebCrawlerFactory<MyCrawler> factory1 = () -> new MyCrawler(crawler1Domains);
        CrawlController.WebCrawlerFactory<MyCrawler> factory2 = () -> new MyCrawler(crawler2Domains);

        // The first crawler will have 5 concurrent threads and the second crawler will have 7 threads.
        controller1.startNonBlocking(factory1, 1);
        controller2.startNonBlocking(factory2, 5);

        controller1.waitUntilFinish();
        logger.info("Crawler 1 is finished.");

        controller2.waitUntilFinish();
        logger.info("Crawler 2 is finished.");


//        // close the csv printer
//        fetchPrinter.flush();
//        fetchPrinter.close();
//        visitPrinter.flush();
//        visitPrinter.close();
//        urlsPrinter.flush();
//        urlsPrinter.close();

    }
}
