package twittermining;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import jdk.nashorn.internal.ir.WhileNode;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.UndirectedWeightedSubgraph;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class TwitterMining {
	
	// For QUOC: CHANGE HERE
	private static String userDataPath = "E:/TwitterMining/data/";
	private static String userGitPath = "E:/Github/TwitterMining/";
	
	// For HUY: CHANGE HERE
//	private static String userDataPath = "/Users/huydinh/Cours/TPT32/Project/dataAthensWeek";
//	private static String userGitPath = "";

	// FILE PATH 
	private static String [] folderName = {"TESTDATA","NewYorkOneWeek", "Oscars", "ParisSearchFeb", "ParisSearchJan"};
	private static String targetFolder =  userDataPath + folderName[1]; // CHANGE idx HERE !!!!!!!
	
	private static String fileTxtOutput = userGitPath + "TwitterMining.txt";
	private static String fileEdgeOutput = userGitPath + "Edge.txt";
	private static String fileUserOutput = userGitPath + "User.txt";
	private static HashMap<String, Double> hashmapVertexWeight = new HashMap<String, Double>(); 
	
	
	// GLOBAL VARIABLES
	static List<String> arrayHashtags = new ArrayList<String>();
	static int counter = 0;	
	static SimpleWeightedGraph<String, DefaultWeightedEdge> hashtagGraph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	static int numOfDenseSubgraph = 0;
	static List<UndirectedWeightedSubgraph<String, DefaultWeightedEdge>> denseSubgraphList = new ArrayList<UndirectedWeightedSubgraph<String, DefaultWeightedEdge>>();

	public static void main(String[] args) throws IOException, ParseException {		
		long startTime = System.currentTimeMillis();		
		
		readFileFromFolder(targetFolder);		
		buildGraph();
		findSubgraphWithNodesUnder(10);
		
//		UndirectedWeightedSubgraph<String, DefaultWeightedEdge> aSubgraph = new UndirectedWeightedSubgraph<String, DefaultWeightedEdge>(hashtagGraph, hashtagGraph.vertexSet(), hashtagGraph.edgeSet());
//
//		findDenseSubgraph(aSubgraph, 10);
//		System.out.println(aSubgraph.toString());
		
//		System.out.println("No.Dense subgraph: " + numOfDenseSubgraph);
//		
//		double max = 0.0;
//		int ind = -1;
//		for (int i = 0; i < denseSubgraphList.size(); i++) {
//			double weight = 0.0;
//			for (DefaultWeightedEdge edge: denseSubgraphList.get(i).edgeSet()) {
//				weight = weight + denseSubgraphList.get(i).getEdgeWeight(edge);
//			}
//			weight = weight / denseSubgraphList.get(i).vertexSet().size();
//			if (weight > max) {
//				max = weight;
//				ind = i;
//			}
//			
//		}
//		System.out.println(max);
//		System.out.println(denseSubgraphList.get(ind).toString());
		
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Running time: " + totalTime/1000 + "s");
	}
	
	private static void readFileFromFolder(String folderPath) throws IOException, ParseException{			
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		
		System.out.println("Start process folder " + folder.getName());
		System.out.println("************************************");
		
		List<String> listOfFilesToProcess = new ArrayList<String>();
		
		// Run thru the folder to get the filename
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				listOfFilesToProcess.add(listOfFiles[i].getPath());
			} 
		}
		
		// Start process each file in the folder
		int flag = 0;
		while(flag < listOfFilesToProcess.size()){
			processData(listOfFilesToProcess.get(flag));
			System.out.println("Finish refining data from " + listOfFilesToProcess.get(flag));
			System.out.println(counter + " twits");
			flag++;
		}
		
		System.out.println("**********");		
		System.out.println("FINISHED " + folder.getName());
	}

	private static void processData(String jsonFilePath) throws IOException, ParseException {
		// TODO Auto-generated method stub
		FileReader myFile = new FileReader(jsonFilePath);
		BufferedReader myBuf = new BufferedReader(myFile);
		BufferedWriter myWriter = new BufferedWriter(new FileWriter(fileTxtOutput, true));
		BufferedWriter myWriterUser = new BufferedWriter(new FileWriter(fileUserOutput, true));

		JSONParser myParser = new JSONParser();
		String line;
		

		// Read line by line from the input file.
		while ((line = myBuf.readLine()) != null) {
			// Parse the input line into JSON format
			Object obj = myParser.parse(line);
			JSONObject aTwit = (JSONObject) obj;

			// Get Entities key and the key hashtags
			JSONObject entity = (JSONObject) aTwit.get("entities");
			JSONArray hashtags = (JSONArray) entity.get("hashtags");
			
			JSONObject user = (JSONObject) aTwit.get("user");
		//	System.out.println(user);
			
			String hashtagValue;

			if (hashtags.size() != 0) {
				String listHashtagsOfTwit = "";
				// output the hastags of 1 twit
				for (int i = 0; i < hashtags.size(); i++) {
					hashtagValue = ((JSONObject) hashtags.get(i)).get("text").toString();
					listHashtagsOfTwit += hashtagValue + " ";
				}

				counter++;
				// Store a new listHashtags to an array to check the
				// duplication
				arrayHashtags.add(listHashtagsOfTwit);

				// Write to file txt
				myWriterUser.write(user.get("id").toString());
				myWriterUser.newLine();
				
				myWriter.write(listHashtagsOfTwit);
				myWriter.newLine();				
			}
		}
		myBuf.close();
		myWriter.close();
		myWriterUser.close();
	}

	
	private static void buildGraph() throws IOException {
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("Start building graph");
		
		Set<Set<String>> allHashtagSet = new HashSet<Set<String>>();
		
		List<String> lines = Files.readAllLines(Paths.get(fileTxtOutput), StandardCharsets.ISO_8859_1);
		for (int i = 0; i < lines.size(); i++) {
			List<String> hashtagList = Arrays.asList(lines.get(i).split(" "));
			for (int u = 0; u < hashtagList.size(); u++)
				hashtagList.set(u, hashtagList.get(u).toLowerCase());
			
			Set<String> hashtagSet = new HashSet<String>(hashtagList);
			if (!allHashtagSet.contains(hashtagSet)) {
				allHashtagSet.add(hashtagSet);
				hashtagList = new ArrayList<String>(hashtagSet);
				
				for (int u = 0; u < hashtagList.size(); u++)
					if (!hashtagGraph.containsVertex(hashtagList.get(u))){
						hashtagGraph.addVertex(hashtagList.get(u));
					}
				
				
				for (int u = 0; u < hashtagList.size() - 1; u++)
					for (int v = u + 1; v < hashtagList.size(); v++) {
						if (!hashtagGraph.containsEdge(hashtagList.get(u), hashtagList.get(v)))
							hashtagGraph.addEdge(hashtagList.get(u), hashtagList.get(v));
						else {
							DefaultWeightedEdge edge = hashtagGraph.getEdge(hashtagList.get(u), hashtagList.get(v));
							hashtagGraph.setEdgeWeight(edge, hashtagGraph.getEdgeWeight(edge) + 1.0);
						}
					}
			}
		}
//		BufferedWriter myBuf = new BufferedWriter(new FileWriter(fileEdgeOutput, true));
//		for(DefaultWeightedEdge edge : hashtagGraph.edgeSet()){
//			if(hashtagGraph.getEdgeWeight(edge) >= 50){
//				myBuf.write(edge + " " + hashtagGraph.getEdgeWeight(edge));
//				myBuf.newLine();
//			}
//			
//		}
//		myBuf.close();
		
		calVertexWeight();		
		System.out.println(hashtagGraph.vertexSet().size() + " vertex");
		System.out.println(hashtagGraph.edgeSet().size() + " edges");
		System.out.println("FINISHED Building Graph");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");	
     
	}
	
	private static void calVertexWeight() throws IOException {
		System.out.println("Calulating Vertex Weight");
		hashmapVertexWeight.clear();
		
		// Calculate the weight for each
		for (String curVertex : hashtagGraph.vertexSet()) {
			Set<DefaultWeightedEdge> listEdges = hashtagGraph
					.edgesOf(curVertex);
			double sumWeight = 0;
			// Sum up the weight from each edge
			for (DefaultWeightedEdge item : listEdges) {
				sumWeight += hashtagGraph.getEdgeWeight(item);
			}
			hashmapVertexWeight.put(curVertex, sumWeight);
		}
		
//		System.out.println(hashtagGraph.vertexSet().size() + " vertex");
//		System.out.println(hashtagGraph.edgeSet().size() + " edges");
//		System.out.println(hashmapVertexWeight.size() + " hashmap records");
		System.out.println("FINISHED");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
	private static void deleteVertexWithWeightSmaller(Double threshold) throws IOException{
		Iterator<Map.Entry<String, Double>> iteratorMap = hashmapVertexWeight.entrySet().iterator();
		Map.Entry tmpEntry;
		// Get the Vertex with the Weight smaller than the threshold
		while(iteratorMap.hasNext()){
			tmpEntry = iteratorMap.next();
			if(Double.parseDouble(tmpEntry.getValue().toString()) <= threshold) {
				// Remove the vertex that has the weight is smaller than the threshold
				hashtagGraph.removeVertex(tmpEntry.getKey().toString());
			}
		}		
		calVertexWeight();
	}
	
	private static void findSubgraphWithNodesUnder(int numOfNode) throws IOException{
		double step = 0.0;
		double densityOfSubgraph = 0;
		double GraphDensity = 0;
		double min = 0;
		SimpleWeightedGraph<String, DefaultWeightedEdge> hashtagTmpGraph = null;
		
		// Run the remove process until the graph G has less than numOfNode
		while(hashtagGraph.vertexSet().size() > numOfNode){			
			step += 1.0;
			deleteVertexWithWeightSmaller(step);							
		}
		
		hashtagTmpGraph = (SimpleWeightedGraph<String, DefaultWeightedEdge>) hashtagGraph.clone();
		double weightH = 0;
		double weightG = 0;
		
		while (hashtagGraph.edgeSet().size() > 1) {			
			min = java.util.Collections.min(hashmapVertexWeight.values());
			deleteVertexWithWeightSmaller(min);
			weightG = 0;
			weightH = 0;
			
			for(DefaultWeightedEdge edge : hashtagTmpGraph.edgeSet()){
				weightH += hashtagTmpGraph.getEdgeWeight(edge);
			}
			
			for(DefaultWeightedEdge edge : hashtagGraph.edgeSet()){
				weightG += hashtagGraph.getEdgeWeight(edge);
			}
			
			GraphDensity = weightG*1.0 / (hashtagGraph.vertexSet().size()*1.0);
			densityOfSubgraph = weightH*1.0 / (hashtagTmpGraph.vertexSet().size()*1.0);
			
			System.out.println("density of subgraph H: " +densityOfSubgraph);
			System.out.println("density of graph G: " +GraphDensity);
			
			
			if(GraphDensity > densityOfSubgraph){
				hashtagTmpGraph = (SimpleWeightedGraph<String, DefaultWeightedEdge>) hashtagGraph.clone();
			}
		}
		hashtagGraph = (SimpleWeightedGraph<String, DefaultWeightedEdge>) hashtagTmpGraph.clone();
		System.out.println("The wanted subgraph: " + hashtagGraph.vertexSet().toString());
	}
	
	private static void findDenseSubgraph(UndirectedWeightedSubgraph<String, DefaultWeightedEdge> aGraph, int numOfNode) throws IOException{
		if (aGraph.edgeSet().size() <= numOfNode) {
			numOfDenseSubgraph++;
			denseSubgraphList.add(aGraph);
			return;
		}
		
//		double density = 0.0;
//		if (aGraph.vertexSet().size() <= numOfNode) {
//			for (DefaultWeightedEdge item: aGraph.edgeSet()) {
//				density = density + aGraph.getEdgeWeight(item);
//			}
//			density = density / aGraph.vertexSet().size();
//			
//		}
		// Calculate the weight
		double minWeight = 1000000;
		String minVertex = "";
		
		for (String curVertex : aGraph.vertexSet()) {
			Set<DefaultWeightedEdge> listEdges = aGraph.edgesOf(curVertex);
			double sumWeight = 0;
			// Sum up the weight from each edge
			for (DefaultWeightedEdge item : listEdges) {
				sumWeight += aGraph.getEdgeWeight(item);
			}
			if (sumWeight < minWeight) {
				minWeight = sumWeight;
				minVertex = curVertex;
			}
		}
		
		aGraph.removeVertex(minVertex);
		
		ConnectivityInspector<String, DefaultWeightedEdge> connect = new ConnectivityInspector<String, DefaultWeightedEdge>(aGraph);
		List<Set<String>> connectedComponents = connect.connectedSets();
		
		for (int i = 0; i < connectedComponents.size(); i++) {
			Set<String> newVertexSet = connectedComponents.get(i);
			Set<DefaultWeightedEdge> newEdgeSet = new HashSet<DefaultWeightedEdge>();
			for (DefaultWeightedEdge item: aGraph.edgeSet()) {
				if (newVertexSet.contains(aGraph.getEdgeSource(item)) || newVertexSet.contains(aGraph.getEdgeTarget(item))) {
					newEdgeSet.add(item);
				}
			}
			UndirectedWeightedSubgraph<String, DefaultWeightedEdge> aSubgraph = new UndirectedWeightedSubgraph<String, DefaultWeightedEdge>(aGraph, newVertexSet, newEdgeSet);
			findDenseSubgraph(aSubgraph, numOfNode);
		}
		
	}
	
	
}
