package Indexing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PostingFileReader {
	private BufferedReader reader;
	private String crtToken;
	private String crtPosting;
	
	public PostingFileReader(String path) throws IOException {
		reader = new BufferedReader(new FileReader(path));
		crtToken = null;
		crtPosting = null;
		getNextTokenInfo();
	}
	
	public String getCrtToken() {
		return crtToken;
	}
	
	public String getCrtPosting() {
		return crtPosting;
	}
	
	public void getNextTokenInfo() throws IOException {
		String token = null;
		String posting = null;
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (token == null) {
				token = line;
			}
			else if (posting == null) {
				posting = line;
			}
		}
		if (token != null && posting != null) {
			crtToken = token;
			crtPosting = posting;
		}
		else { // if token and posting are not completed, it means the postings are run out.
			crtToken = null;
			crtPosting = null;
			reader.close();
		}
	}
	
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
