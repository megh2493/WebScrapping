package crawlerproject1;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.opencsv.CSVWriter;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	public static void main(String[] args) throws Exception {
		 String crawlStorageFolder = "C:\\Users\\Meghana Sudhan\\Desktop\\Meghana\\Classes\\Information Retrieval\\data\\crawl";
		 CSVWriter visitCSV = null;
		 CSVWriter urlsCSV = null;
		 CSVWriter fetchCSV = null;
		 int numberOfCrawlers = 1;
		 CrawlConfig config = new CrawlConfig();
		 config.setCrawlStorageFolder(crawlStorageFolder);
		 config.setMaxPagesToFetch(20000);
		 config.setPolitenessDelay(100);
		 config.setIncludeHttpsPages(true);
		 config.setMaxDownloadSize(1073741824);
		 config.setConnectionTimeout(3600000);
		 config.setMaxDepthOfCrawling(16);
		 
		 config.setIncludeBinaryContentInCrawling(true);
		 /*
		 * Instantiate the controller for this crawl.
		 */
		 PageFetcher pageFetcher = new PageFetcher(config);
		 RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		 RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		 robotstxtConfig.setEnabled(false);
		 CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		 /*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		 
		 controller.addSeed("https://www.nydailynews.com/");
		
		 String fetch_csv = "fetch_ny.csv";
			String visit_csv = "visit_ny.csv";
			String urls_csv = "urls_ny.csv";
			try {
				String[] fetchedHeaders="URL,StatusCode".split(",");
				String[] visitedHeaders="URL,File Size,Outgoing links Count,Content Type".split(",");
				String[] urlsHeader="URL, Does it reside in domain?".split(",");
				
				fetchCSV = new CSVWriter(new FileWriter(fetch_csv,true));
				visitCSV = new CSVWriter(new FileWriter(visit_csv,true));
				urlsCSV = new CSVWriter(new FileWriter(urls_csv,true));
				
				fetchCSV.writeNext(fetchedHeaders);
				visitCSV.writeNext(visitedHeaders);
				urlsCSV.writeNext(urlsHeader);
				
				fetchCSV.close();
				visitCSV.close();
				urlsCSV.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		 /*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		 controller.start(MyCrawler.class, numberOfCrawlers);
		 
		 try {
			 Writer writer = null;

			 try {
				 writer = new BufferedWriter(new OutputStreamWriter(
				           new FileOutputStream("output_ny.txt"), "utf-8"));
				     
				     writer.write("Name: Meghana Madhsudhan\n");
				     writer.write("USC ID: 9460668562\n");
				     writer.write("News site crawled: www.nydailynews.com/\n\n");
				     
				     writer.write("Fetch Statistics\n");
				     writer.write("=================\n");
				     writer.write("# fetches attempted:"+MyCrawler.fetches_attempted+"\n");
					 writer.write("# fetches succeded:"+MyCrawler.fetches_succeded+"\n");
					 writer.write("# fetches aborted:"+MyCrawler.fetches_aborted+"\n\n");
					 
					 writer.write("Outgoing URLs\n");
					 writer.write("==============\n");
					 writer.write("Total URLs extracted : " + MyCrawler.urlCount+"\n");
					 writer.write("# of unique URLs : " + (MyCrawler.uniqueURLs_within.size()+MyCrawler.uniqueURLs_outside.size())+"\n");
					 writer.write("# of unique URLs within domain : " + MyCrawler.uniqueURLs_within.size()+"\n");
					 writer.write("# of unique URLs outside domain: " + MyCrawler.uniqueURLs_outside.size()+"\n\n");
					 
					 writer.write("Status Codes\n");
					 writer.write("=============\n");
					 
					 for(int key:MyCrawler.statusCodes.keySet()) {
						 if(key == 200) {
							 writer.write(key+ " OK: " +MyCrawler.statusCodes.get(key)+"\n");
						 }
						 else if(key == 301) {
							 writer.write(key+ " Moved Permanently: " +MyCrawler.statusCodes.get(key)+"\n");
						 }
						 else if(key == 401) {
							 writer.write(key+ " Unauthorized: " +MyCrawler.statusCodes.get(key)+"\n");
						 }
						 else if(key == 403) {
							 writer.write(key+ " Forbidden: " +MyCrawler.statusCodes.get(key)+"\n");
						 }
						 else if(key == 404) {
							 writer.write(key+ " Not Found: " +MyCrawler.statusCodes.get(key)+"\n");
						 }
					 }
					 
					 writer.write("\n");
					 writer.write("File Sizes\n");
					 writer.write("===========\n");
					 for(String key:MyCrawler.sizeRanges.keySet()) {
						 writer.write(key+ ": " +MyCrawler.sizeRanges.get(key)+"\n");
					 }
					 
					 writer.write("\n");
					 writer.write("Content Types\n");
					 writer.write("==============\n");
					 for(String key:MyCrawler.contentTypeCount.keySet()) {
						 writer.write(key+ "->" + MyCrawler.contentTypeCount.get(key)+"\n");
					 }
			 } catch (IOException ex) {
			   // report
			 } finally {
			    try {writer.close();} catch (Exception ex) {/*ignore*/}
			 }
			 
		 } catch(Exception e) {
			 
		 }
	}
}
