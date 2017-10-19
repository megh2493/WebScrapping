package crawlerproject1;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import com.opencsv.CSVWriter;

public class MyCrawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|"
			 + "|mp3|mp3|zip|gz))$");
	

	 public static Set<String> uniqueURLs = new HashSet<String>();
	 public static Set<String> uniqueURLs_within = new HashSet<String>();
	 public static Set<String> uniqueURLs_outside = new HashSet<String>();

	 CSVWriter visitCSV = null;
	 CSVWriter urlsCSV = null;
	 CSVWriter fetchCSV = null;
	 
	 
	 public static int urlCount = 0;
	 
	 public static int fetches_attempted = 0;
	 public static int fetches_succeded = 0;
	 public static int fetches_aborted = 0;
	 
	 public static HashMap<Integer, Integer> statusCodes = new HashMap<Integer, Integer>();
	 public static HashMap<String, Integer> sizeRanges = new HashMap<String, Integer>();
	 public static HashMap<String, Integer> contentTypeCount = new HashMap<String, Integer>();
	 
	 @Override
	 public void onStart() {
		 
			String fetch_csv = "fetch_ny.csv";
			String visit_csv = "visit_ny.csv";
			String urls_csv = "urls_ny.csv";
			try {
				
				fetchCSV = new CSVWriter(new FileWriter(fetch_csv,true));
				visitCSV = new CSVWriter(new FileWriter(visit_csv,true));
				urlsCSV = new CSVWriter(new FileWriter(urls_csv,true));
				
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}
		
	 
	 @Override
	 public synchronized void onBeforeExit() {
		
			 try {
				 fetchCSV.close();
				 visitCSV.close();
				 urlsCSV.close();	 
				 
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
		 
	    }
	 
	/**
	 * This method receives two parameters. The first parameter is the page
	 * in which we have discovered this new url and the second parameter is
	 * the new url. You should implement this function to specify whether
	 * the given url should be crawled or not (based on your crawling logic).
	 * In this example, we are instructing the crawler to ignore urls that
	 * have css, js, git, ... extensions and to only accept urls that start
	 * with "http://www.viterbi.usc.edu/". In this case, we didn't need the
	 * referringPage parameter to make the decision.
	 */
	 @Override
	
	 public synchronized boolean shouldVisit(Page referringPage, WebURL url) {
		 
		 String href = url.getURL().toLowerCase();
		 urlCount++;
		 String[] data = new String[2];
		 data[0] = href;
		 if(((href.startsWith("https://www.nydailynews.com/"))
				 || (href.startsWith("http://www.nydailynews.com/")))){
			 uniqueURLs_within.add(href);
			 data[1] = "OK";
		 }
		 else{
			 uniqueURLs_outside.add(href);
			 data[1] = "N_OK";
		 }
		 urlsCSV.writeNext(data);
		 boolean resData = ((href.startsWith("https://www.nydailynews.com/")) || (href.startsWith("http://www.nydailynews.com/"))) &&
				 !FILTERS.matcher(href).matches();
		 
		 return resData;
		  
		 
	 }
	 
	 @Override
	  protected synchronized void handlePageStatusCode(WebURL urlData, int statusCodeData, String statusDescription) {
		 
		 fetches_attempted++;
		 statusCodes.put(statusCodeData, statusCodes.getOrDefault(statusCodeData, 0) + 1);
		 if((statusCodeData + "" ).startsWith("2")) {
			  fetches_succeded++;
		  } else {
			  fetches_aborted++;
		  }
		 
	     String[] data = { urlData.toString(), Integer.toString(statusCodeData)};
	     
	     fetchCSV.writeNext(data);
	    
	  }
	 
	 /**
	  * This function is called when a page is fetched and ready
	  * to be processed by your program.
	  */
	  @Override
	  public synchronized void visit(Page page) {
		  String url = page.getWebURL().getURL();
		  
		  String html = null;
		  Set<WebURL> links = null;
		  String contentType = null;
		  int contentSize = 0;
		  
		  
		  if (page.getParseData() instanceof HtmlParseData) {
			  	HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			  	String text = htmlParseData.getText();
			  	html = htmlParseData.getHtml();
			    links = htmlParseData.getOutgoingUrls();
			    contentType = page.getContentType();
			    
			    String[] data = { url, Integer.toString(page.getContentData().length), Integer.toString(links.size()), contentType};
			    visitCSV.writeNext(data);
			    
		  } else {
			  links = page.getParseData().getOutgoingUrls();
			  contentType = page.getContentType();
			  
			  
			  String[] data = { url, Integer.toString(page.getContentData().length), Integer.toString(links.size()), contentType}; 
			  visitCSV.writeNext(data);
		  }
		  
		  
		  
		  contentSize = page.getContentData().length;
		  
		  
		  if(contentSize <= 1024) {
			  sizeRanges.put("less than 1KB", sizeRanges.getOrDefault("less than 1KB", 0)+1);
		  } else if(contentSize > 1024 && contentSize <= 10240) {
			  sizeRanges.put("1KB - 10KB", sizeRanges.getOrDefault("1KB - 10KB", 0)+1);
		  } else if(contentSize > 10240 && contentSize <= 102400) {
			  sizeRanges.put("10KB - 100KB", sizeRanges.getOrDefault("10KB - 100KB", 0)+1);
		  } else if(contentSize > 102400 && contentSize <= 1024000) {
			  sizeRanges.put("100KB - 1MB", sizeRanges.getOrDefault("100KB - 1MB", 0)+1);
		  } else {
			  sizeRanges.put("greater than 1MB", sizeRanges.getOrDefault("greater than 1MB", 0)+1);
		  }
		 
		  if(contentType != null) {
			  if(contentType.contains("text/html")) {
				  contentTypeCount.put("text/html", contentTypeCount.getOrDefault("text/html", 0) + 1);
			  } else if(contentType.contains("image/gif")) {
				  contentTypeCount.put("image/gif", contentTypeCount.getOrDefault("image/gif", 0) + 1);
			  } else if(contentType.contains("image/jpeg")) {
				  contentTypeCount.put("image/jpeg", contentTypeCount.getOrDefault("image/jpeg", 0) + 1);
			  } else if(contentType.contains("image/png")) {
				  contentTypeCount.put("image/png", contentTypeCount.getOrDefault("image/png", 0) + 1);
			  } else if(contentType.contains("application/pdf")) {
				  contentTypeCount.put("application/pdf", contentTypeCount.getOrDefault("application/pdf", 0) + 1);
			  } 
		  } 
	  }
	  
	  
	  
	  
	  
}

