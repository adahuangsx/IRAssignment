package Indexing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private int docCount;
	private int termCount;
	private int blockSize = 100000; // the size of a "batch"
	
	private String rootPath;
	private String type;
	
	// <term, [docNum, freq]> map:
	private Map<String, List<int[]>> term2postingMap;
	private Map<String, Integer> docID2numMap;
	private List<String> tmpfileNames;
	
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		docCount = 0;
		termCount = 0;
		term2postingMap = new HashMap<>();
		docID2numMap = new HashMap<>();
		tmpfileNames = new ArrayList<>();
		this.type = type;
		if (type.equals("trectext")) {
			rootPath = Path.IndexTextDir;
		}else {
			rootPath = Path.IndexWebDir;
		}
		new File(rootPath).mkdir(); // create a new file folder
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
		if (docNum % this.blockSize == 0) {
			thrash();
		}
	}
	
	/**
	 * output the current map data and re-init the map
	 */
	private void thrash () {
		if (term2postingMap.size() == 0) {
			return;
		}
		System.out.println("term2postingMap.size():" + term2postingMap.size());
		System.out.println("docCount" + docCount);
		int blockNum = docCount / blockSize;
		String tmpfileName = blockNum + ".tmp";
		tmpfileNames.add(tmpfileName);
		writeTmpPostings(rootPath.)
	}
	/**
	 * 
	 * @param path : the path of tmp files like "/data/1.tmp". containing a block of documents
	 * @throws IOException: when creating new file
	 */
	private void writeTmpPostings(String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}
		FileWriter writer = new FileWriter(file);
		Object[] terms = term2postingMap.entrySet().toArray();
		Arrays.sort(terms);
		for (Object term : terms) {
			writer.write((String)term );
			writer.write("\n");
			for (int[] pair : term2postingMap.get(term)) {
				writer.write(pair[0] + " " + pair[1] + " ");
			}
			writer.write("\n");
			writer.flush();
		}
		writer.close();		
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
	}
	
}
