import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

public class Crawler {


	public String getDescription(String link) {
		String desc = "";
		try {
			Document doc = Jsoup.connect(link).get();
			desc += doc.title();
			
			//Get title from document object.
			desc += "\n"+doc.select("meta[name=description]").get(0).attr("content");
			return desc;
		}catch(Exception e) {
			//e.printStackTrace();
			//System.err.println("Problem in getDescription: "+e.getMessage());
			return desc;
		}
	}

	public Set<String> getLinks(String website,String domain) throws IOException, URISyntaxException {
		try {

			Elements rootLinks = Jsoup.connect(website).get().select("a[href]");

			Set <String> allLinks = new HashSet <String>();

			for (Element link : rootLinks) {
				String lnk =link.attr("abs:href");
				String dom = getDomainName(lnk); 
				if( dom != null && !isSocialMedia(dom) && !domain.contains(dom) && !dom.contains(domain)) {
					if(!(dom.contains("wikipedia") && lnk.contains("wikipedia")))
						allLinks.add(lnk);
				}
			}
			return allLinks;
		}catch(Exception e) {
			//System.err.println("Problem in getLinks: "+e.getMessage());
			return null;
		}
		
	}

	public String getDomainName(String url) {
		try {
			URI uri = new URI(url);
			String domain = uri.getHost();
			return domain.startsWith("www.") ? domain.substring(4) : domain;
		}catch(Exception e) {
			//System.err.println("Problem in getDomainName: "+e.getMessage());
			return url;
		}
		
	}

	private boolean isSocialMedia (String domain) {
		String[] socialMedia = {"stumbleupon.com","youtube.com","twitter.com","linkedin.com", "reddit.com","facebook.com",
				"pinterest.com","instagram.com","tumblr.com","flickr.com","snapchat.com","whatsapp.com"};
		for(String s : socialMedia) 
			if(s.equalsIgnoreCase(domain)) 
				return true;
		return false;
	}


}
