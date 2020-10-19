package Indexing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	// static variables 
	private static final int ParseNum = Integer.MAX_VALUE;
	private static final int DocBlockSize = 120000;
	// block size of tokens
	private static final int TokenBlockSize = 1000;
	private static final String IndexFileSuffix = "_index.result";
	public static final String DocIdMapName = "docIdMap.map";
	public static final String TokenFileIdName = "tokenFileId.map";
	// instance variables
	private int docCnt;
	private int tokenCnt;
	private String type;
	private String rootPath;
	// maps between document name and id
	private Map<String, Integer> nameToIdMap; // <"XINHUA000000101", 56>
	// map from token to document id and frequency pairs; each pair is parsed into an integer
	private Map<String, ArrayList<int[]>> tokenPairsMap; // <term, [docNum, freq]> map
	// map from token to token file id
	private Map<String, Integer> tokenFileIdMap; // <"abc", 1(1.tmp)>
	// filename list
	private List<String> tmpFilenames;

	
	public static String calIndexPath(int tokenFileId) {
		// calculate the path of index file given the given token id
		return tokenFileId + IndexFileSuffix;
	}
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		// these variables will hold during the whole life of this writer
		docCnt = 0;	
		tokenCnt = 0;
		// set up the type and corresponding folder path
		this.type = type;
		if (type.equals("trectext"))
			rootPath = Path.IndexTextDir;
		else 
			rootPath = Path.IndexWebDir;
		// create the directory related to the rootPath if not exist
		new File(rootPath).mkdir();
		nameToIdMap = new HashMap<>();
		tokenFileIdMap = new HashMap<>();
		tokenFileIdMap.put("a", 666);
		tmpFilenames = new ArrayList<>();
		// this variable will be re-initiated after the data is written to a tmp each time
		tokenPairsMap = new HashMap<>();
	}
	
	public void IndexADocument(String docno, char[] content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		// update the map between name and id
		nameToIdMap.put(docno, docCnt);
		// initialize the counters of each word
		Map<String, Short> wordCnts = new HashMap<>();
		for (String word : String.valueOf(content).split("\\s+")) {
			if (word.length() > 0)
				wordCnts.put(word, (short) (wordCnts.getOrDefault(word, (short) 0) + 1));
		}
//		System.out.println(wordCnts);
		// convert the wordCnts map to update the tokenPairsMap
		for (String word : wordCnts.keySet()) {
			if (!tokenPairsMap.containsKey(word) ) 
				// initiate the list if not already have one
				tokenPairsMap.put(word, new ArrayList<>());
			// update the list
			tokenPairsMap.get(word).add(new int[] {docCnt, wordCnts.get(word)});
		}
		//System.out.println(tokenPairsMap);
		if ((docCnt + 1) % DocBlockSize == 0) 
			// thrash the block data
			thrash();
		// update the docCnt at the end
		docCnt++;
	}
	
	/**
	 * output the current map data and re-init the map
	 * @throws IOException 
	 */
	private void thrash() throws IOException {
		// write the current data to a tmp file and re-init the data structure
		if (tokenPairsMap.size() > 0) {
//			System.out.println(tokenPairsMap.size());
			int blockIdx = docCnt / DocBlockSize;
//			System.out.println(blockIdx);
//			System.out.println(tokenPairsMap);
			// calculate the filename and generate the full path
			String filename = blockIdx + ".tmp";
			tmpFilenames.add(filename);		
			// write the tokenPairsMap to the tmp file
			writeTokenPairsMap(rootPath + filename);
			// clear the tokenPairsMap
			tokenPairsMap = new HashMap<>();			
		}
	}
	
	/**
	 * 
	 * @param path : the path of tmp files like "/data/1.tmp". containing a block of documents
	 * @throws IOException: when creating new file
	 */
	private void writeTokenPairsMap(String path) throws IOException {
        File file = new File(path);
		file.delete();
		file.createNewFile();
        FileWriter writer = new FileWriter(file);
        Entry<String, ArrayList<int[]>> entry;
        Object[] keys = tokenPairsMap.keySet().toArray();
        Arrays.sort(keys);;
        for (Object key : keys) {
        	writer.write((String) key);
        	writer.write("\n");
        	for (int[] pair : (ArrayList<int[]>) tokenPairsMap.get(key)) 
            	writer.write(pair[0] + " " + pair[1] + " ");
        	writer.write("\n");
        	writer.flush();
        }
        writer.close();
	}
	
	private void writeStringToIntMap(String path, Map<String, Integer> map) throws IOException {
        File file = new File(path);
		file.delete();
		file.createNewFile();
        FileWriter writer = new FileWriter(file);
        for (Entry entry : map.entrySet()) {
        	writer.write((String) entry.getKey());
        	writer.write(" " + entry.getValue());
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
	private FileWriter getNewIndexWriter() throws IOException {
        File file = new File(rootPath + calIndexPath(tokenCnt / TokenBlockSize));
		file.delete();
		file.createNewFile();
        FileWriter writer = new FileWriter(file);
        return writer;
	}
	
	private void mergeTmp() throws IOException {
		// merge all tmp file and write to one big one
        FileWriter writer = getNewIndexWriter();
        ArrayList<IndexFileReader> readers = new ArrayList<>();
        for (String filename : tmpFilenames) 
        	readers.add(new IndexFileReader(rootPath + filename));
        String prevToken = null;
        while (true) {
        	IndexFileReader minReader = null;
        	String minToken = null;
        	// go through all file to get the min token
        	for (IndexFileReader reader : readers) {
        		String token = reader.getCurrentToken();
        		if ((minToken == null && token != null) || (minToken != null && token != null && token.compareTo(minToken) < 0)) {
        			// update the min reader and token in this round 
        			minReader = reader;
        			minToken = token;
        		}
        	}
        	if (minReader == null) 
        		// no reader has remaining content case
        		break;
        	// write to the merged file and take a step in the minReader
        	if (minToken.equals(prevToken)) {
        		/**
				 * This happens when the minToken in tmp1 also exists in, say, tmp2.
				 * Currently tmp2 is the minReader, and the token in tmp2 is the minToken:
				 * Then I only need to concat the tmp2's postinglist.
				 */
        		writer.write(minReader.getCurrentPairStr());
        		writer.write(" ");
        		writer.flush();
        	}
        	else {
        		/**
				 * This happens when a new token is being processed.
				 * The writer needs to be the next, and close the last.
				 */
        		tokenCnt++;
        		// set the map from token to token file id
        		this.tokenFileIdMap.put(minToken, tokenCnt / TokenBlockSize);
//        		System.out.println(tokenFileIdMap);
        		// check whether to update the token and prevToken
        		if (tokenCnt % TokenBlockSize == 0) {
        			writer.close();
        			writer = getNewIndexWriter();
        			prevToken = null;
        		}
        		if (prevToken != null)
        			writer.write("\n");
        		writer.write(minToken);
        		writer.write("\n");
        		writer.write(minReader.getCurrentPairStr());
        		writer.write(" ");
        		writer.flush();
        		prevToken = minToken;
        	}
        	minReader.getNext(); // who small, who moves forward.
        }
        // The break happens when the content is run out. Then close the current writer.
        writer.close();
	}
	
	private void deleteTmp() throws IOException  {
		for (String filename : tmpFilenames) 
	        new File(rootPath + filename).delete();
	}
	
 	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
 		thrash(); // get a few rest done
 		mergeTmp();
 		deleteTmp();
 		writeStringToIntMap(rootPath + DocIdMapName, nameToIdMap);
 		writeStringToIntMap(rootPath + TokenFileIdName, tokenFileIdMap);
 		nameToIdMap = null;
 		tokenFileIdMap = null;
	}

	
	public static void main(String[] args) throws Exception {
		PreProcessedCorpusReader pcr = new PreProcessedCorpusReader("trectext");
		MyIndexWriter writer = new MyIndexWriter("trectext");
		Map<String, Object> result = null;
		int i = 0;
		while ((result = pcr.NextDocument()) != null && i < ParseNum) {
			String key = result.keySet().iterator().next();
			writer.IndexADocument(key, (char[]) (result.get(key)));
			i++;
		}
		writer.Close();
	}
	
}
