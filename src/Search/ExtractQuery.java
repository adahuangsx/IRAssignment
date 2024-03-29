package Search;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Classes.Path;
import Classes.Query;
import PreProcessData.StopWordRemover;
import PreProcessData.WordNormalizer;
import PreProcessData.WordTokenizer;

public class ExtractQuery {
	
	private static final String TOP_START = "<top>";
	private static final String TOP_END = "</top>";
	
	private static final String TITLE = "<title>";
	private static final String DESCRI = "<desc>";
	private static final String NARRI = "<narr>";
	
	private static final String NUM = "<num>";
	
	private BufferedReader reader;
	
//	private boolean endOfFile = false;
	// this doesn't work because null Query may be return.
	
	private int index;
	private List<Query> queries;

	public ExtractQuery() throws Exception {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		reader = new BufferedReader(new FileReader(Path.TopicDir));
		queries = getAllQueries(); // not a good practice...
		index = 0;
	}
	
	public boolean hasNext() {
		return index < queries.size();
	}
	public Query next() {
		return queries.get(index++);
	}
	
	private List<Query> getAllQueries() throws IOException{
		List<Query> res = new ArrayList<>();
		String line = null;
		boolean isTopic = false;
		boolean isDesc = false;
		boolean isNarr = false;
		String title = null;
		String queryNum = null;
		StringBuilder desc = null;
		StringBuilder narr = null;
		StringBuilder content = null;
		while ((line = reader.readLine()) != null) {
			if (line.trim().equals(TOP_START)) {
				isTopic = true;
			}
			else if (line.trim().equals(TOP_END)) {
				isTopic = false;
				content = new StringBuilder();
				content.append(title);
//				content.append(" ");
//				content.append(desc);
//				content.append(" ");
//				content.append(narr);
				
				// normalize it
				res.add(new Query(content.toString(), normalizeQuery(content.toString().toCharArray()), queryNum));
			}
			else if (isTopic && line.startsWith(NUM)) {
				queryNum = line.substring(NUM.length()).trim();
			}
			else if (isTopic && line.startsWith(TITLE)) {
				title = line.substring(TITLE.length()).trim();
			}
			else if (isTopic && line.startsWith(DESCRI)) {
				isDesc = true;
				isNarr = false;
				desc = new StringBuilder();
			}
			else if (isTopic && line.startsWith(NARRI)) {
				isDesc = false;
				isNarr = true;
				narr = new StringBuilder();
			}
			else if (isTopic && isDesc) {
				desc.append(line.trim());
			}
			else if (isTopic && isNarr) {
				narr.append(line.trim());
			}
		}
		reader.close();
		return res;
	}
	
	private List<String> normalizeQuery(char[] content) {
		// loading stopword list and initiate the StopWordRemover and WordNormalizer class
		StopWordRemover stopwordRemoverObj = new StopWordRemover();
		WordNormalizer normalizerObj = new WordNormalizer();
		WordTokenizer tokenizer = new WordTokenizer(content);
		List<String> res = new ArrayList<>();
		// initiate a word object, which can hold a word
		char[] word = null;
		// process the document word by word iteratively
		while ((word = tokenizer.nextWord()) != null) {
			// each word is transformed into lowercase
			word = normalizerObj.lowercase(word);
			
			// filter out stopword, and only non-stopword will be written
			// into result file
			if (!stopwordRemoverObj.isStopword(word)) {
				res.add(normalizerObj.stem(word));
				//stemmed format of each word is written into result file
			}
		}
		return res;
	}
	
	public static void main(String[] args) throws Exception {
		ExtractQuery t = new ExtractQuery();
		Query q = null;
		while (t.hasNext()) {
			q = t.next();
			System.out.println(q);
		}
	}
}
