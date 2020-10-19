package Indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

/** helper class to read index files
 * @author Sixuan Huang
 *
 */
public class IndexFileReader {

	private BufferedReader reader;
	private String currentToken = null;
	private String currentPairStr = null;
	public IndexFileReader(String path) throws IOException {
		reader = new BufferedReader(new FileReader(path));
		getNext();
	}
	
	public void getNext() throws IOException {
		// load the next token and its pair string;
		String token = null;
		String pairStr = null;
		String line = null;
		while ((token == null || pairStr == null) && (line = reader.readLine()) != null) {
			if (token == null) 
				token = line;
			else 
				pairStr = line;
		}
		if (token != null && pairStr != null) {
			currentToken = token;
			currentPairStr = pairStr;
		}
		else { // if token and posting are not completed, it means the postings are run out.
			currentToken = null;
			currentPairStr = null;
			reader.close();	
		}
	}

	public String getCurrentToken() {
		return currentToken;
	}

	public String getCurrentPairStr() {
		return currentPairStr;
	}
	
	public static void main(String[] args) throws Exception {
		IndexFileReader reader = new IndexFileReader(Path.IndexWebDir + "0.tmp");
		while (reader.getCurrentToken() != null) {
			System.out.println(reader.getCurrentToken());
			System.out.println(reader.getCurrentPairStr());
			reader.getNext();
		}
	}
}
