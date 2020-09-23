package Indexing;

import java.io.IOException;
import java.util.Map;

public class PreProcessedCorpusReader {

	
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
	}
	

	public Map<String, Object> NextDocument() throws IOException {
		// read a line for docNo and a line for content, put into the map with <docNo, content>
		return null;
	}

}
