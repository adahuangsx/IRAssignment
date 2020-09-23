package Indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {
	private BufferedReader reader;
	
	
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		reader = new BufferedReader(new FileReader(Path.ProvidedResultHM1 + type));
	}
	

	public Map<String, Object> NextDocument() throws IOException {
		// read a line for docNo and a line for content, put into the map with <docNo, content>
		Map<String, Object> res = new HashMap<>();
		String line = null;
		int max = 100;
		while ((line = reader.readLine()) != null && max > 0) {
			String docNo = line.trim();
			String content = reader.readLine();
			res.put(docNo, content);
			return res;
		}  
		reader.close();
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		String filetype = "trecweb";
		PreProcessedCorpusReader t = new PreProcessedCorpusReader(filetype);
		Map<String, Object> doc = new HashMap<>();
		int c = 0;
		while ((doc = t.NextDocument()) != null) {
			c++;
		}
		System.out.println(c + " files in " + filetype);
	}

}
