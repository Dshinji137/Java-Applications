import java.util.List;
import java.util.LinkedList;
import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SpiderLeg {
	private List<String> links = new LinkedList<String>();
	private static final String USER_AGENT = "13.0.782.112";
	
	private Document htmlDoc;
	
	public void crawl(String url) {
		try {
			Connection connection = Jsoup.connect(url).userAgent(USER_AGENT);
			Document document = connection.get();
			this.htmlDoc = document;
			if(connection.response().statusCode() == 200) {
				System.out.println("Received web page at "+url);
			}
			if(!connection.response().contentType().contains("text/html")) {
				System.out.println("...Failure... Retrieved something other than HTML");
				return;
			}
			Elements linksOnPage = htmlDoc.select("a[href]");
			System.out.println("Found "+linksOnPage.size()+" links");
			for(Element link : linksOnPage) {
				this.links.add(link.absUrl("href"));
			}
		}
		catch(IOException ioe) {
			System.out.println("Errors in out HTTP Request"+ioe);
		}
		return;
	}
	public boolean searchForWord(String searchWord) {
		System.out.println("Searching for the word "+searchWord);
		String text = this.htmlDoc.body().text();
		return text.toLowerCase().contains(searchWord.toLowerCase());
	}
	public List<String> getlinks() {
		return this.links;
	}
	
	
}
