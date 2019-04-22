import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Hits {
	static FileReader fr;
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		
		
		Map<Integer,Integer[]> graph = new HashMap<Integer,Integer[]>();

		fr = new FileReader("deep_learning_root_set");
		
		System.out.println("Building graph...");

		graph = buildGraph();

		//		Iterator it = graph.entrySet().iterator();
		//		int sum = 0;
		//		while (it.hasNext()) {
		//			Map.Entry pair = (Map.Entry)it.next();
		//			System.out.println(pair.getKey() + " = " + pair.getValue());
		//
		//		}
		//		System.out.println(sum+"--"+graph.size());

	}

	static Map<Integer,Integer[]> buildGraph() throws Exception{
		Crawler crwl = new Crawler();
		Map<Integer,Integer[]> graph = new HashMap<Integer,Integer[]>();
		Map<String, Set<String>> allLinks = new HashMap<String, Set<String>>();
		Map<String,Integer> conversionGraph = new HashMap<String,Integer>();
		
		int counter = 0;
		String website = null;
		
		while((website=fr.getNextValue()) != null) {
			String domain = crwl.getDomainName(website);
			if(domain!=null) {
				conversionGraph.put(domain,counter++);
				Set<String> links = crwl.getLinks(website,domain);
				allLinks.put(domain,links);
				for(String s : links) { 
					conversionGraph.put(s,counter++);
				}
			}
		}
		
		Iterator it = allLinks.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			if(pair.getValue() != null) {
				Set<String> links = (Set<String>) pair.getValue();
				Integer[] intLinks = new Integer[links.size()]; 
				counter = 0;
				for(String s : links) 
					intLinks[counter++] = conversionGraph.get(s);
				System.out.println(conversionGraph.get((String) pair.getKey())+"--"+intLinks.toString());
				graph.put(conversionGraph.get((String) pair.getKey()) , intLinks );
			}
		}
		
		return graph;
	}


}
