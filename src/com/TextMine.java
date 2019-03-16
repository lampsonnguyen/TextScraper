package com;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Connection.Method;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.FileWriter;
/*
 * How to use this code:
 *  googleSearch() will use connectWebSite() but with modified google url to obtain the searched link
 *  connectWebSite(url) will connect to your given http link, and access every link inside
 *  linkStuff() to list all the links inside, just lists and write the list to a text file
 *  extractHTML will only extract the html code in the link
 *  getHostName just grab the web site name. for example: http://www.google.com  =>  name is "google.com"
 *  html2Text will grab the file from extractHTML and delete the html codes
 *  you can use this application without the googleSearch() function, the use connectWebSite()+
 *  String location: location of results
 *  searchPreview() only search and preview related words
 * */
public class TextMine {
	public final static String location = System.getProperty("user.dir") +"/txt/";
    public static void main(String[] arg) throws IOException, URISyntaxException {
    	String[] searchPhrase = {"yahoo"};
    	int pageMax =3;
    	int resultPerPage = 10;
		for(int i = 0;i < pageMax; i++){
			int k = i+1;
			System.out.println("Google Search Page "+k);
			googleSearch(searchPhrase, resultPerPage, i); //google [keyword] [how many result per page, max is 100] [page]
		}
		System.out.println("Result in "+ location);
	}
    
    public static void searchPreview(String searchTerm) {    
	    String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36";
	    int numberOfResultpages = 2; // grabs first two pages of search results
	    String searchUrl = "https://www.google.com/search?";
	
	    Document doc;
	    for (int i = 0; i < numberOfResultpages; i++) {
	
	        try {
	            doc = Jsoup.connect(searchUrl)
	                    .userAgent(userAgent)
	                    .data("q", searchTerm)
	                    .data("tbm", "nws")
	                    .data("start",""+i)
	                    .method(Method.GET)
	                    .referrer("https://www.google.com/").get();
	
	            for (Element result : doc.select("#rso > div")) {
	
	                if(result.select("div.st").size()==0) continue;
	
	                Element h3a = result.select("h3 > a").first();
	
	                String title = h3a.text();
	                String url = h3a.attr("href");
	                String preview = result.select("div.st").first().text();
	
	                // just printing out title and link to demonstate the approach
	                System.out.println(title + " -> " + url + "\n\t" + preview);
	            }
	
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	}
         
    //google [keyword] [how many result per page, max is 100] [page]
    private static void googleSearch(String[] searchPhrase, int resultAmount, int pageResult) throws IOException, URISyntaxException{
    	String google = "http://www.google.com/search?q=";
		String charset = "UTF-8";
		String resultNum = "&num=";
		String pageNum = "&start="; 
		//google search parameters: q=[keyword]  num=[amount of results]  start=[start at result nth], all the param start after '?' and separate by '&'
		
		for(int i = 0; i < searchPhrase.length; i++) {
			try{
				String connectLink = google + URLEncoder.encode(searchPhrase[i], charset) + resultNum + resultAmount + pageNum + (resultAmount*pageResult) ;
				connectWebSite(connectLink);
			}catch(IOException ioe){ 
				System.out.println(ioe);
			}
		}
    }
    
    private static void connectWebSite(String connectLink) throws IOException, URISyntaxException{
    	int j = 0;
    	String userAgent = "Darwin Text Scraper 1.0 (http://Darwin.navy.navair.mil/AI)"; 
    	Elements links = Jsoup.connect(connectLink).maxBodySize(0).timeout(150000).userAgent(userAgent).get().select(".g>.r>a");
		 for (Element link : links) {
			j++;
			String title = link.text();
			String url = link.attr("href"); 
			url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");
			if (!url.startsWith("http")) {continue;} // Ads/news/etc.

			System.out.println("\rTitle: " + title);
			System.out.println("URL: " + url+"\r");
			listStuff(url, j, getHostName(url));
			URL htmlTag = new URL(url);
			extractHTML(htmlTag, j, getHostName(url).replaceAll("/|:", ""));
		}
    }
    
    private static void extractHTML(URL oracle, int order, String name) throws IOException {
    	name = "html-"+order+"-["+name+"].txt";
    	File file = new File(location+name);
		FileWriter fileWriter = new FileWriter(file);
    	BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
 	    String inputLine;
	    while ((inputLine = in.readLine()) != null)
	    {
//	    	System.out.println(inputLine);
	   	 	fileWriter.write(inputLine+"\r");
	    }
	    in.close();
		fileWriter.flush();
		fileWriter.close();
		html2Text(name);
	}

    public static void html2Text(String htmlFile) throws IOException{
    	File file = new File(location+"txt-"+htmlFile);
		FileWriter fw = new FileWriter(file);
    	InputStream is = new FileInputStream(location+htmlFile); 
    	BufferedReader buf = new BufferedReader(new InputStreamReader(is)); 
    	String line = buf.readLine();  
    	StringBuilder sb = new StringBuilder(); 
    	while(line != null){ 
    		sb.append(line).append("\r"); 
    		line = buf.readLine(); 
    		if(line != null){
	    		Document doc = Jsoup.parse(line);
	        	String textContent=doc.text();
	        	String[] tags = new String[]{"html", "div"};
	        	Document thing = Jsoup.parse(line);
	        	for (String tag : tags) {
	        	    for (Element elem : thing.getElementsByTag(tag)) {
	        	        elem.parent().insertChildren(elem.siblingIndex(),elem.childNodes());
	        	        elem.remove();
	        	    }
	        	}
	        	fw.write(textContent+"\r");
//	        	System.out.println(textContent);
    		} 
    		else{ }
    	} 
    	buf.close();
    	//String fileAsString = sb.toString(); 
//    	System.out.println("Contents : " + fileAsString);
		fw.flush();
		fw.close();
    }

	private static void listStuff(String url, int order, String name) throws IOException{
		name = "links-"+order+"-["+name+"].txt";
		File file = new File(location+name);
		FileWriter fw = new FileWriter(file);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("Hello World");
//    	print("Fetching %s...", url); 
    	pw.println("Fetching %s..."+ url);

		Document doc = Jsoup.connect(url).maxBodySize(0).timeout(100000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko)Chrome/15.0.874.120 Safari/535.2").get();
		Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

//        print("\rMedia: (%d)", media.size());
        pw.println("\rMedia: (%d)"+ media.size()+")");
        for (Element src : media) {
            if (src.tagName().equals("img")){
//            	print(" * %s: <%s> %sx%s (%s)", src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"), trim(src.attr("alt"), 20));
                pw.println(" * "+src.tagName()+": <"+src.attr("abs:src")+"> "+src.attr("width")+"x"+src.attr("height")+" ("+trim(src.attr("alt"), 20)+")");
            }
            else{
//            	print(" * %s: <%s>", src.tagName(), src.attr("abs:src"));
            	pw.println(" * "+src.tagName()+": <"+src.attr("abs:src")+">");
            }
        }

//        print("\rImports: (%d)", imports.size());
        pw.println("\rImports: (%d)"+ imports.size()+")");
        for (Element link : imports) {
//            print(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel"));
            pw.println(" * "+link.tagName()+" <"+link.attr("abs:href")+"> ("+link.attr("rel")+")");
        }

//        print("\rLinks: (%d)", links.size());
        pw.println("\rLinks: ("+ links.size()+")");
        for (Element link : links) {
//            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            pw.println(" * a: <"+link.attr("abs:href")+"> ("+trim(link.text(), 35)+")");
        }	
        pw.close();
	}																

    private static String getHostName(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String hostname = uri.getHost();
        // to provide faultproof result, check if not null then return only hostname, without www.
        if (hostname != null) {
            return hostname.startsWith("www.") ? hostname.substring(4) : hostname;
        }
        return hostname;
    }
    
    @SuppressWarnings("unused")
	private static void print(String msg, Object... args) {
        System.out.println("  "+String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width-1) + ".";
        else
            return s;
    }
}