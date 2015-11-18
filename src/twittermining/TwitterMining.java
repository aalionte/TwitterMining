package twittermining;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TwitterMining {
	public static void main(String[] args) throws IOException, ParseException {
		
		// FILE PATH: CHANGE HERE 
		
		String fileJsonInput = "E:/TwitterMining/data/NewYorkOneWeek/NewYork-2015-2-23";
		String fileTxtOutput = "E:/Github/TwitterMining/TwitterMining.txt";
		
		// *******************************************
		
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

}
