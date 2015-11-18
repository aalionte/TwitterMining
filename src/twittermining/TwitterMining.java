package twittermining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TwitterMining {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileWriter myTextFile = new FileWriter("E:/TwitterMining.txt", true);
		
		BufferedWriter myBuffer = new BufferedWriter(myTextFile);
		myBuffer.write("Hello World");
		myBuffer.write("Hello World 2");		
		System.out.println("FINISHED");
	}

}
