package Indexing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private int docCount;
	private int mergedTermCount; // used when merging ???
	private static final int blockSize = 120000; // the size of a "batch"
	private static final int termBlockSize = 1000; // the size of a term batch
	static final String indexFileSuffix = "_index.result";
	static final String docIDFileName = "docIDMap.map";
	static final String termFileIDName = "termFileID.map";
	
	private String rootPath;
	private String type;
	
	// <term, [docNum, freq]> map:
	private Map<String, List<int[]>> term2postingMap;
	private Map<String, Integer> docID2numMap; // <"XINHUA000000101", 56>
	private Map<String, Integer> term2fileMap; // <"abc", 1(1.tmp)>
	private List<String> tmpfileNames;
	
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		docCount = 0;
		mergedTermCount = 0;
		term2postingMap = new HashMap<>();
		docID2numMap = new HashMap<>();
		term2fileMap = new HashMap<>();
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
		for (String word : String.valueOf(content).split("\\s+")) {
			if (word.length() > 0) {
				wordsFreq.put(word, wordsFreq.getOrDefault(word, 0) + 1);
			}
//			else {
//				System.out.print("a word is empty. docno: " + docno);
//			}
		}
		// add these data into the main map
		for (String word : wordsFreq.keySet()) {
			if (!term2postingMap.containsKey(word)) {
				term2postingMap.put(word, new ArrayList<>());
			}
			term2postingMap.get(word).add(new int[] {docNum, wordsFreq.get(word)});
		}
		if ((docNum + 1) % blockSize == 0) {
			thrash();
		}
	}
	
	/**
	 * output the current map data and re-init the map
	 * @throws IOException 
	 */
	private void thrash() throws IOException {
		if (term2postingMap.size() == 0) {
			return;
		}
//		System.out.println("term2postingMap.size(): " + term2postingMap.size());
//		System.out.println("docCount: " + docCount);
		int blockNum = docCount / blockSize;
		String tmpfileName = blockNum + ".tmp";
		tmpfileNames.add(tmpfileName);
		writeTmpPostings(rootPath + tmpfileName);
		// re-init the Map
		term2postingMap = new HashMap<>();
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
		Object[] terms = term2postingMap.keySet().toArray();
		Arrays.sort(terms);
		for (Object term : terms) {
			writer.write((String) term);
			writer.write("\n");
			for (int[] pair : term2postingMap.get(term)) {
				writer.write(pair[0] + " " + pair[1] + " ");
			}
			writer.write("\n");
			writer.flush();
		}
		writer.close();		
	}
	
	/**
	 * 
	 * @return The current token batch file. generate a file called "1_index.result"
	 * @throws IOException 
	 */
	private FileWriter getNewTokenIndexWriter() throws IOException {
		File file = new File(rootPath + (mergedTermCount / termBlockSize) + indexFileSuffix);
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		}
		return new FileWriter(file);
	}
	
	private void mergeTmp() throws IOException {
		FileWriter writer = getNewTokenIndexWriter();
		List<PostingFileReader> readers = new ArrayList<>();
		for (String tmpfileName : tmpfileNames) {
			readers.add(new PostingFileReader(rootPath + tmpfileName));
		}
		String prevToken = null; //  to mark the last processed token.
		while (true) {
			String minToken = null;
			PostingFileReader minReader = null;
			
			// go thru all the posting files to find the min token (like "aaa")
			for (PostingFileReader reader : readers) {
				String crtToken = reader.getCrtToken();
				if (crtToken != null) {
					if (minToken == null || (minToken != null && crtToken.compareTo(minToken) < 0)) {
						minReader = reader;
						minToken = crtToken;
					}
				}
			}
			if (minReader == null) {
				// fail to locate a min token, which means the content is run out.
				break;
			}
			if (minToken.equals(prevToken)) {
				/**
				 * This happens when the minToken in tmp1 also exists in, say, tmp2.
				 * Currently tmp2 is the minReader, and the token in tmp2 is the minToken:
				 * Then I only need to concat the tmp2's postinglist.
				 */
				writer.write(minReader.getCrtPosting() + " ");
				writer.flush();
			}
			else {
				/**
				 * This happens when a new token is being processed.
				 * The writer needs to be the next, and close the last.
				 */
				if(minToken.equals("acow")) {
					System.out.println("FOUND IT!");
				}
				mergedTermCount++;
				this.term2fileMap.put(minToken, mergedTermCount / termBlockSize);
				if (mergedTermCount % termBlockSize == 0) {
					writer.close();
					writer = getNewTokenIndexWriter();
					prevToken = null;
				}
				if (prevToken != null) {
					writer.write("\n");
				}
				writer.write(minToken + "\n");
				writer.write(minReader.getCrtPosting() + " ");
				writer.flush();
				prevToken = minToken;
			}
			minReader.getNextTokenInfo(); // who small, who moves forward.
		}
		// The break happens when the content is run out. Then close the current writer.
		writer.close();
	}
	
	private void deleteTmpfiles() {
		for (String tmpfileName : tmpfileNames) {
			new File(rootPath + tmpfileName).delete();
		}
	}
	
	private void writeMap(String path, Map<String, Integer> map) throws IOException {
        File file = new File(path);
		file.delete();
		file.createNewFile();
        FileWriter writer = new FileWriter(file);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
        	writer.write((String) entry.getKey());
        	writer.write(" " + entry.getValue());
        	writer.write("\n");
        	writer.flush();
        }
        writer.close();
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		thrash(); // get a few rest done
		mergeTmp();
		deleteTmpfiles();
		writeMap(rootPath + termFileIDName, term2fileMap);
		writeMap(rootPath + docIDFileName, docID2numMap);
		System.out.println("term count:" + mergedTermCount);
		
		
	}
	
	public static void main(String[] args) throws Exception {
		String filetype = "trecweb";
		PreProcessedCorpusReader t = new PreProcessedCorpusReader(filetype);
		MyIndexWriter w = new MyIndexWriter(filetype);
		Map<String, Object> doc = null;
		int i = 0;
		while ((doc = t.NextDocument()) != null) {
			String docNo = doc.keySet().iterator().next();
			char[] content = (char[]) doc.get(docNo);
			w.IndexADocument(docNo, content);
			i++;
		}
		w.Close();
	}
}
