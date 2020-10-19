package Indexing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import Classes.Path;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
	// maps between document name and id
	private Map<String, Integer> nameToIdMap;
	private Map<Integer, String> idToNameMap;
	// map from token to token file id
	private Map<String, Integer> tokenFileIdMap;
	private String type;
	private String rootPath;
	
	public MyIndexReader( String type ) throws IOException {
		/**
		 * read the index files generated in task 1
		 * remember to close them when you finish using them
		 * use appropriate structure to store your index
		 * set up the type and corresponding folder path
		 */
		
		this.type = type;
		if (type.equals("trectext"))
			rootPath = Path.IndexTextDir;
		else 
			rootPath = Path.IndexWebDir;
		nameToIdMap = parseStrIntMap(rootPath + MyIndexWriter.DocIdMapName);
		tokenFileIdMap = parseStrIntMap(rootPath + MyIndexWriter.TokenFileIdName);
		// reverse the name to id map
		idToNameMap = new HashMap<>();
		for (Entry entry : nameToIdMap.entrySet()) 
			idToNameMap.put((int) entry.getValue(), (String) entry.getKey());
//		System.out.println(nameToIdMap.size());
//		System.out.println(idToNameMap.size());
//		System.out.println(tokenFileIdMap.size());
	}
	
	private static Map<String, Integer> parseStrIntMap(String path) throws IOException {
		Map<String, Integer> map = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] pairs = line.split("\\s+");
			map.put(pairs[0], Integer.parseInt(pairs[1]));
		}
		reader.close();
		return map;
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) {
		return nameToIdMap.get(docno);
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) {
		return idToNameMap.get(docid);
	}
	
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		// get the file id of this token
		if (tokenFileIdMap.containsKey(token)) {
			int tokenFileId = tokenFileIdMap.get(token);
			String path = rootPath + MyIndexWriter.calIndexPath(tokenFileId);
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String currentToken = null;
			String currentPosting = null;
			while ((currentToken = reader.readLine()) != null && (currentPosting = reader.readLine()) != null) {
				if (currentToken.equals(token)) {
					// find the token case; parse the line into 2-d array
					String[] nums = currentPosting.split("\\s+");
					int[][] result = new int[nums.length / 2][2];
					for (int i = 0; i < result.length; i++) {
						result[i][0] = Integer.parseInt(nums[2 * i]);
						result[i][1] = Integer.parseInt(nums[2 * i + 1]);
					}
					reader.close();
					return result;
				}
			}
			reader.close();
		}
		return null;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		int[][] result = GetPostingList(token);
		if (result == null) {
			return 0;
		}
		return result.length;
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		int[][] result = GetPostingList(token);
		if (result == null) {
			return 0;
		}
		int num = 0;
		for (int i = 0; i < result.length; i++)
			num += result[i][1];
		return num;
	}
	
	public void Close() throws IOException {
		nameToIdMap = null;
		tokenFileIdMap = null;
		// reverse the name to id map
		idToNameMap = null;
	}
	
	
	public static void main(String[] args) throws Exception {
		MyIndexReader reader = new MyIndexReader("trectext");
		reader.Close();
	}
	
}