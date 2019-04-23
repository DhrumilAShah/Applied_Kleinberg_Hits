import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Hits {
	static FileReader fr;
	static Map<Integer,Integer[]> graph;
	static int[][] adjMtrx;
	static int vertices;
	static double[] hub;
	static double[] auth;
	static final double initValue = 1.0;

	public static void main(String[] args) throws Exception {
		graph = new HashMap<Integer,Integer[]>();

		fr = new FileReader("machine_learning_root_set");

		System.out.println("Building graph...");

		buildGraph();

		//		System.out.println(graph.size());	
		//		Iterator it = graph.entrySet().iterator();
		//		while (it.hasNext()) {
		//			Map.Entry pair = (Map.Entry)it.next();
		//			//System.out.println(pair.getKey() + " = " + pair.getValue());
		//			Integer[] i = (Integer[])pair.getValue();
		//			for(int k=0; k<i.length; k++) {
		//				System.out.println("--"+(int)pair.getKey()+"--"+(int)i[k]);
		//			}
		//		}

		initMtrx();
		initHubAuth();

		System.out.println(adjMtrx.length);	
		printMtrx();



		//loop{
		//print hub and auth

		computeAuth();
		computeHub();



		//}

	}

	static double[] computeAuth() {
		double sum;
		double[] temp = new double[auth.length];
		for(int i=0; i <auth.length; i++) {
			sum = 0.0;
			for(int a=0; a<adjMtrx.length; a++) {
				if(adjMtrx[a][i] == 1) {
					sum += hub[a];
				}
			}
			temp[i] = sum;
		}
		return temp;
	}
	
	static double[] computeHub() {
		double sum;
		double[] temp = new double[hub.length];
		for(int i=0; i <hub.length; i++) {
			sum = 0.0;
			for(int a=0; a<adjMtrx.length; a++) {
				if(adjMtrx[i][a] == 1)	{
					sum += auth[a];
				}
			}
			temp[i] = sum;
		}	
		return temp;
	}
	


	static void initHubAuth() {
		for(int j=0; j<vertices; j++) {
			hub[j] = initValue;
			auth[j] = initValue;
		}
	}

	static void printMtrx() {
		for(int l=0; l<adjMtrx.length; l++) {
			for(int y=0; y<adjMtrx[l].length; y++) 
				System.out.print(adjMtrx[l][y]);
			System.out.println();
		}
	}


	static void initMtrx(){
		try{
			adjMtrx = new int[vertices][vertices];
			Iterator it = graph.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				Integer[] i = (Integer[])pair.getValue();
				for(int k=0; k<i.length; k++) {
					adjMtrx[(int)pair.getKey()][(int)i[k]] = 1;
					System.out.println((int)pair.getKey()+"--"+(int)i[k]);
				}
			}
		}catch(Exception e) {
			e.printStackTrace();	
		}
	}



	static void buildGraph(){
		try {
			Crawler crwl = new Crawler();
			//Map<Integer,Integer[]> graph = new HashMap<Integer,Integer[]>();
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
			vertices = counter-1;
			//System.out.println("-->>"+vertices);

			Iterator it = allLinks.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				if(pair.getValue() != null) {
					Set<String> links = (Set<String>) pair.getValue();
					Integer[] intLinks = new Integer[links.size()]; 
					counter = 0;
					for(String s : links) 
						intLinks[counter++] = conversionGraph.get(s);
					//System.out.println(conversionGraph.get((String) pair.getKey())+"--"+intLinks.toString());
					graph.put(conversionGraph.get((String) pair.getKey()) , intLinks );
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
