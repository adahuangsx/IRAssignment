package Indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private int docCount;
	private int termCount;
	private int blockSize = 100000; // the size of a "batch"
	
	// <term, [docNum, freq]> map:
	private Map<String, List<int[]>> term2postingMap;
	private Map<String, Integer> docID2numMap;
	
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		docCount = 0;
		termCount = 0;
		term2postingMap = new HashMap<>();
		docID2numMap = new HashMap<>();
	}
	
	public void IndexADocument(String docno, char[] content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		// A new document comes..
		docID2numMap.put(docno, this.docCount++);
		/* For example:
		 * docno: "XINHUA000000101"; docNum: "56"
		 */
		int docNum = docID2numMap.get(docno);
		// a word frequency statistics in this document
		Map<String, Integer> wordsFreq = new HashMap<>();
		for (String word : String.valueOf(content).split("//s+")) {
			wordsFreq.put(word, wordsFreq.getOrDefault(word, 0) + 1);
		}
		// add these data into the main map
		for (String word : wordsFreq.keySet()) {
			if (!term2postingMap.containsKey(word)) {
				term2postingMap.put(word, new ArrayList<>());
			}
			term2postingMap.get(word).add(new int[] {docNum, wordsFreq.get(word)});
		}
		
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
	}
	
}
