package crawler;

import com.google.common.collect.ImmutableList;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @ClassName MyController
 * @Description TODO
 * @Author Steven
 * @Date 2022/9/17
 * @Version 1.0
 **/
public class MyCrawler extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(BasicCrawler.class);

    private static final Pattern FILTERS = Pattern.compile(
            ".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4" +
                    "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
//    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");

    private final List<String> myCrawlDomains;

    public MyCrawler(List<String> myCrawlDomains) throws Exception {
        this.myCrawlDomains = ImmutableList.copyOf(myCrawlDomains);
    }

    private String URL_HEADING = "https://www.latimes.com/";

    FileOutputStream fs1 = new FileOutputStream("/2021USC/cs572/hw2/results/fetch_LATimes.csv");
    OutputStreamWriter osw1 = new OutputStreamWriter(fs1, "UTF8");
    CSVFormat fetchFormat = CSVFormat.DEFAULT.withHeader("URL", "status code");
    CSVPrinter fetchPrinter = new CSVPrinter(osw1, fetchFormat);


    FileOutputStream fs2 = new FileOutputStream("/2021USC/cs572/hw2/results/visit_LATimes.csv");
    OutputStreamWriter osw2 = new OutputStreamWriter(fs2, "UTF8");
    CSVFormat visitFormat = CSVFormat.DEFAULT.withHeader("URL downloaded", "size of the downloaded file", "outlinks found", "resulting content type;");
    CSVPrinter visitPrinter = new CSVPrinter(osw2, visitFormat);


    FileOutputStream fs3 = new FileOutputStream("/2021USC/cs572/hw2/results/urls_LATimes.csv");
    OutputStreamWriter osw3 = new OutputStreamWriter(fs3, "UTF8");
    CSVFormat urlsFormat = CSVFormat.DEFAULT.withHeader("encountered URL", "indicator");
    CSVPrinter urlsPrinter = new CSVPrinter(osw3, urlsFormat);



    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "https://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches()
                && href.startsWith(URL_HEADING);
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL().replace(",","_");
        System.out.println("URL: " + url);

        // generate fetch record
        List<String> fetchRecord = new ArrayList<>();
        fetchRecord.add(url);
        fetchRecord.add(String.valueOf(page.getStatusCode()));
//        fetchRecords.add(fetchRecord);

            try {
                fetchPrinter.printRecord(fetchRecord);
                fetchPrinter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }



        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            // generate visit record
            List<String> visitRecord = new ArrayList<>();
            visitRecord.add(url);
            visitRecord.add(String.valueOf(page.getContentData().length));
            visitRecord.add(String.valueOf(links.size()));
            visitRecord.add(page.getContentType());
//            visitRecords.add(visitRecord);

                try{
                    visitPrinter.printRecord(visitRecord);
                    visitPrinter.flush();
                }catch (Exception e){
                    e.printStackTrace();
                }


            // generate url record
            try{
                for(WebURL link : links){
                    String indicator = "OK";
                    String externalUrl = link.getURL().replace(",","_");

                    if(!externalUrl.startsWith(URL_HEADING)){
                        indicator = "N_OK";
                    }

                    List<String> urlRecord = new ArrayList<>();
                    urlRecord.add(externalUrl);
                    urlRecord.add(indicator);

                    urlsPrinter.printRecord(urlRecord);
                    urlsPrinter.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
        }
    }

    public void testIO() throws Exception{

        FileOutputStream fos = new FileOutputStream("/2021USC/cs572/hw2/data/test.csv");
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");

        CSVFormat csvFormat = CSVFormat.DEFAULT.withHeader("姓名", "年龄", "家乡", "性别");
        CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);

        // 第二种方式设置头部信息
//        csvPrinter = CSVFormat.DEFAULT.withHeader("姓名", "年龄", "家乡", "性别").print(osw);

        List<String> stringList = new ArrayList<>();
        stringList.add("张三");
        stringList.add("20");
        stringList.add("shanghai");
        stringList.add("nan");

        for (int i = 0; i < 10; i++) {
//            csvPrinter.printRecord("张三", 20, "上海", "男");
            csvPrinter.printRecord(stringList);
        }
        csvPrinter.flush();
        csvPrinter.close();
    }

    public static void main(String[] args) throws Exception {
        List<String> myCrawlDomains = new ArrayList<>();
        MyCrawler myc = new MyCrawler(myCrawlDomains);

        myc.testIO();
    }
}
