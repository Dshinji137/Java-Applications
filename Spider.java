import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;

public class Spider {
	private static final int MAX_PAGES_TO_SEARCH = 10;
	private Set<String> pageVisited = new HashSet<String>();
	private List<String> pageToVisit = new LinkedList<String>();
	private List<String> pageFound = new LinkedList<String>();
	
	private String nextURL() {
		String nexturl = this.pageToVisit.remove(0);
		while(this.pageVisited.contains(nexturl) && this.pageToVisit.size() >= 1) {
			nexturl = this.pageToVisit.remove(0);
		}
		return nexturl;
	}
	
	public void search(String url, String searchWord) {
		while(this.pageVisited.size() < MAX_PAGES_TO_SEARCH) {
			String currURL = "";
			SpiderLeg Leg = new SpiderLeg();
			if(this.pageVisited.isEmpty()) currURL = url;
			else currURL = this.nextURL();
			this.pageVisited.add(currURL);
			
			Leg.crawl(currURL);
			boolean success = Leg.searchForWord(searchWord);
			if(success) {
				pageFound.add(currURL);
				System.out.println("...Success... Word "+searchWord+" Found at"+" "+currURL);
			}
			this.pageToVisit.addAll(Leg.getlinks());
		}
		System.out.println("...Done...Visited "+this.pageVisited.size()+" webpages");
		if(this.pageFound.size() != 0) {
			for(int i = 0; i < this.pageFound.size(); i++) {
				System.out.println("word "+searchWord+" Found at "+pageFound.get(i));
			}
		}
		else {
			System.out.println("word "+searchWord+" never found!");
		}
		
	}
}
