import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Hits {

	static FileReader fr;
	static Map<Integer,Integer[]> graph;
	static Map<String,Integer> conversionGraph;
	static int[][] adjMtrx;
	static int vertices;
	static double[] hub;
	static double[] auth;
	static double[] prevHub;
	static double[] prevAuth;
	static final double initValue = 1.0;
	static double errRate = 100000;
	static int numOfPages = 5;
	static ArrayList<Integer> topAuthIndex; 
	static ArrayList<Integer> topHubIndex;
	static int iterations = 1500;
	static Crawler crwl;

	public static void main(String[] args) throws Exception {

		graph = new HashMap<Integer,Integer[]>();

		fr = new FileReader("big_data_analytics_root_set");

		crwl = new Crawler();

		//System.out.println("Building graph...");

		buildGraph();

		initMtrx();
		initHubAuth();

		for(int i=0; i<iterations; i++){
			prevHub = hub;
			prevAuth = auth;

			auth = computeAuth();
			hub = computeHub();

			double[] dash=computeDash(hub,auth);//0=hub, 1=auth

			auth = scaleMtrx(dash[1],auth);
			hub = scaleMtrx(dash[0],hub);

			if(didItConverge()) break;
		}

		topAuthIndex = new ArrayList<Integer> (numOfPages); 
		topHubIndex = new ArrayList<Integer> (numOfPages); 

		setTopAuthIndex();
		setTopHubIndex();

		System.out.println("--------------------Authority--------------------");
		System.out.println();
		for(int l : topAuthIndex) {
			
			String lnk = getLink(l);
			System.out.println(lnk);
			System.out.println(crwl.getDescription(lnk));
			System.out.println("Authority : "+auth[l] +"     Hub : "+hub[l]);
			System.out.println();

		}
		System.out.println("--------------------Hub--------------------");
		System.out.println();
		for(int l : topHubIndex) {
			
			String lnk = getLink(l);
			System.out.println(lnk);
			System.out.println(crwl.getDescription(lnk));
			System.out.println("Authority : "+auth[l] +"     Hub : "+hub[l]);
			System.out.println();
		}

	}

	public static String getLink(int index) {

		Iterator it = conversionGraph.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			int i = (int)pair.getValue();
			//System.out.println(i+"-->>>"+index);
			if(i==index)
				return (String)pair.getKey();
		}
		return null;
	}

	public static void setTopAuthIndex(){ 
		for(int k=0; k<numOfPages; k++) {
			double maxValue = -1; 
			int index = -1;
			for(int i=0; i<auth.length; i++){ 
				if(!topAuthIndex.contains(i)) {
					if(auth[i] > maxValue){ 
						maxValue = auth[i]; 
						index = i;
					}
				} 
			} 
			if(index == -1 ) break;
			topAuthIndex.add(k,index);
		}
	}

	public static void setTopHubIndex(){ 
		for(int k=0; k<numOfPages; k++) {
			double maxValue = -1; 
			int index = -1;
			for(int i=0; i<hub.length; i++){ 
				if(!topHubIndex.contains(i)) {
					if(hub[i] > maxValue){ 
						maxValue = hub[i]; 
						index = i;
					}
				} 
			}
			if(index == -1 ) break;
			topHubIndex.add(k,index);
		}
	}

	static boolean didItConverge() {
		//errRate = Math.pow(10, (iter * -1));
		for (int i = 0; i < auth.length; i++) {

			if ((int)Math.floor(auth[i] * errRate) != (int)Math.floor(prevAuth[i] * errRate) || 
					(int)Math.floor(hub[i] * errRate) != (int)Math.floor(prevHub[i] * errRate)) 
				return false;
		}
		//System.out.println("Converged!");
		return true;
	}

	static double[] scaleMtrx(double num, double[] mtrx) {
		int size = mtrx.length;
		double[] temp = new double[size];
		for(int i=0; i<size; i++) { 
			temp[i] = mtrx[i] / num;
			if(Double.isNaN(temp[i]) || Double.isInfinite(temp[i]))	temp[i] = 0.0;
		}
		return temp;
	}

	static double[] computeDash(double[] u, double[] v) {
		double sum1 = 0.0;
		double sum2 = 0.0;
		for(int i=0; i<hub.length; i++) {
			sum1 += (double)Math.pow(hub[i],2);
			sum2 += (auth[i] * auth[i]);
		}
		return new double[] {Math.sqrt(sum1),Math.sqrt(sum2)};
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
		hub = new double[vertices];
		auth = new double[vertices];
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
				int key = (int)pair.getKey();
				//System.out.println(vertices+"--"+key+"--"+i.length);
				for(int k=0; k<i.length; k++) {
					//System.out.println("--"+(int)i[k]);
					adjMtrx[key][(int)i[k]] = 1;
				}
			}
		}catch(Exception e) {
			e.printStackTrace();	
		}
	}

	static void buildGraph(){
		try {

			//Map<Integer,Integer[]> graph = new HashMap<Integer,Integer[]>();
			Map<String, Set<String>> allLinks = new HashMap<String, Set<String>>();
			conversionGraph = new HashMap<String,Integer>();

			int counter = 0;
			String website = null;

			while((website=fr.getNextValue()) != null) {
				String domain = crwl.getDomainName(website);
				if(domain!=null && website!=null) {
					//System.out.println(website+"--"+counter);
					conversionGraph.put(website,counter++);
					Set<String> links = crwl.getLinks(website,domain);
					if(links!=null) {
						allLinks.put(website,links);
						for(String s : links) { 
							//if(s!=null) {
							//System.out.println(s+"--"+counter);
							if(!conversionGraph.containsKey(s))
								conversionGraph.put(s,counter++);
							//}
						}
					}
				}
			}
			vertices = counter;

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
