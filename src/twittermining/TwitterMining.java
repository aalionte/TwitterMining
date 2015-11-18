package twittermining;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TwitterMining {
	
	// FILE PATH: CHANGE HERE 
	//private static String fileJsonInput = "E:/TwitterMining/data/NewYorkOneWeek/NewYork-2015-2-23";
	//private static String fileTxtOutput = "E:/Github/TwitterMining/TwitterMining.txt";
	
	private static String fileJsonInput = "/Users/huydinh/Cours/TPT32/Project/dataAthensWeek/NewYorkOneWeek/NewYork-2015-2-23";
	private static String fileTxtOutput = "/Users/huydinh/Documents/workspace/TwitterMining/data/processedData/TwitterMining.txt";
	
	public static void main(String[] args) throws IOException, ParseException {	
		
		//processData();
		buildGraph();
		
	}

	private static void processData() throws IOException, ParseException {
		// TODO Auto-generated method stub
		FileReader myFile = new FileReader(fileJsonInput);
		BufferedReader myBuf = new BufferedReader(myFile);
		BufferedWriter myWriter = new BufferedWriter(new FileWriter(fileTxtOutput));
		
		JSONParser myParser = new JSONParser();			
		String line;
		int counter = 0;
		List<String> arrayHashtags = new ArrayList<String>();
		
		// Read line by line from the input file.
		while((line = myBuf.readLine()) != null){
			// Parse the input line into JSON format
			Object obj = myParser.parse(line);
			JSONObject aTwit = (JSONObject) obj;
			
			// Get Entities key and the key hashtags
			JSONObject entity = (JSONObject) aTwit.get("entities");
			JSONArray hashtags = (JSONArray) entity.get("hashtags");
			
			String hashtagValue;			
			
			if(hashtags.size() != 0){
				
				String listHashtagsOfTwit = "";
				// output the hastags of 1 twit
				for(int i = 0; i < hashtags.size(); i++){
					hashtagValue = ((JSONObject) hashtags.get(i)).get("text").toString();
					listHashtagsOfTwit += hashtagValue + " ";
				}
				if(arrayHashtags.indexOf(listHashtagsOfTwit) < 0){
					counter++;
					// Store a new listHashtags to an array to check the duplication
					arrayHashtags.add(listHashtagsOfTwit);
					
					// Write to file txt
					myWriter.write(listHashtagsOfTwit);
					myWriter.newLine();
					
					System.out.println(listHashtagsOfTwit);
				}
			}						
		}
		myWriter.close();
		System.out.println("**********");		
		System.out.println(counter + " twits");
		System.out.println("FINISHED");
	}

	private static void buildGraph() throws IOException {
		int count = 0;
		SimpleWeightedGraph<String, DefaultWeightedEdge> hashtagGraph = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		Set<Set<String>> allHashtagSet = new HashSet<Set<String>>();
		
		List<String> lines = Files.readAllLines(Paths.get(fileTxtOutput), StandardCharsets.UTF_8);
		for (int i = 0; i < lines.size(); i++) {
			List<String> hashtagList = Arrays.asList(lines.get(i).split(" "));
			for (int u = 0; u < hashtagList.size(); u++)
				hashtagList.set(u, hashtagList.get(u).toLowerCase());
			
			Set<String> hashtagSet = new HashSet<String>(hashtagList);
			if (!allHashtagSet.contains(hashtagSet)) {
				count++;
				allHashtagSet.add(hashtagSet);
				hashtagList = new ArrayList<String>(hashtagSet);
				
				for (int u = 0; u < hashtagList.size(); u++)
					if (!hashtagGraph.containsVertex(hashtagList.get(u)))
						hashtagGraph.addVertex(hashtagList.get(u));
				
				
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
		
        System.out.println(hashtagGraph.toString());
        
        
        for (DefaultWeightedEdge edge: hashtagGraph.edgeSet()) {
        	if (hashtagGraph.getEdgeWeight(edge) > 10.0) {
        		System.out.print(edge.toString() + " ");
        		System.out.println(hashtagGraph.getEdgeWeight(edge));
        	}
        }
        
        System.out.println(count);
	}

	
}
