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

	public Set<String> getLinks(String website,String domain) throws IOException, URISyntaxException {
		try {
			
			Elements rootLinks = Jsoup.connect(website).get().select("a[href]");

			Set <String> allLinks = new HashSet <String>();

			for (Element link : rootLinks) {
				String l = getDomainName(link.attr("abs:href")); 
				if( l != null && !isSocialMedia(l) && !domain.contains(l) && !l.contains(domain))
					allLinks.add(l);
			}
			return allLinks;
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	public String getDomainName(String url) {
		try {
			URI uri = new URI(url);
			String domain = uri.getHost();
			return domain.startsWith("www.") ? domain.substring(4) : domain;
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
		return null;
	}

	private boolean isSocialMedia (String domain) {

		String[] socialMedia = {"youtube.com","twitter.com","linkedin.com", "reddit.com","facebook.com",
				"pinterest.com","instagram.com","tumblr.com","flickr.com","snapchat.com","whatsapp.com"};

		for(String s : socialMedia) 
			if(s.equalsIgnoreCase(domain)) 
				return true;

		return false;
	}


}
