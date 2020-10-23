package Search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import Classes.Path;
import Classes.Query;

public class ExtractQuery {
	
	private static final String TOP_START = "<top>";
	private static final String TOP_END = "</top>";
	
	private static final String TITLE = "<title>";
	private static final String DESCRI = "<desc>";
	private static final String NARRI = "<narr>";
	
	private static final String NUM = "<num>";
	
	private BufferedReader reader;
	
	private boolean endOfFile = false;

	public ExtractQuery() throws FileNotFoundException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		reader = new BufferedReader(new FileReader(Path.TopicDir));
	}
	
	public boolean hasNext() {
		return !endOfFile;
	}
	
	public Query next() throws IOException{
		String line = null;
		boolean isTopic = false;
		boolean isDesc = false;
		boolean isNarr = false;
		String title = null;
		String queryNum = null;
		StringBuilder desc = null;
		StringBuilder narr = null;
		StringBuilder content = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			if (line.trim().equals(TOP_START)) {
				isTopic = true;
			}
			else if (line.trim().equals(TOP_END)) {
				isTopic = false;
				content.append(title);
				content.append(" ");
				content.append(desc);
				content.append(" ");
				content.append(narr);
			}
			else if (isTopic && line.startsWith(NUM)) {
				queryNum = line.substring(NUM.length()).trim();
			}
			else if (isTopic && line.startsWith(DESCRI)) {
				isDesc = true;
				isNarr = false;
				desc = new StringBuilder(line.substring(DESCRI.length()).trim());
			}
			else if (isTopic && line.startsWith(NARRI)) {
				isDesc = false;
				isNarr = true;
				desc = new StringBuilder(line.substring(NARRI.length()).trim());
			}
			else if (isTopic && isDesc) {
				desc.append(line.trim());
			}
			else if (isTopic && isNarr) {
				narr.append(line.trim());
			}
		}
	}
}
