package PreProcessData;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Classes.*;

/**
 * This is for INFSCI-2140 in 2020
 *
 */

public class StopWordRemover {
	//you can add essential private methods or variables.
	
	TrieNode root = new TrieNode();
	
	private class TrieNode {
		Map<Character, TrieNode> children = new HashMap<>();
		boolean isStopWord;
	}
	
	/**
	 * Helper function: decide whether a word is within this Trie.
	 * @param word		the word to search
	 * @return			true if the word is found in this Trie;
	 */
	private boolean found(char[] word) {
		TrieNode crt = root;
		for (int i = 0; i < word.length; i++) {
			TrieNode next = crt.children.get(word[i]);
			if (next == null) {
				return false;
			}
			crt = next;
		}
		return crt.isStopWord;
	}
	
	/**
	 * Helper function: insert the word into the Trie. Used when building the stopword Trie.
	 * @param word		the word to insert
	 */
	private void insert(String word) {
		TrieNode crt = root;
		for (int i = 0; i < word.length(); i++) {
			TrieNode next = crt.children.get(word.charAt(i));
			if (next == null) {
				next = new TrieNode();
				crt.children.put(word.charAt(i), next);
			}
			crt = next;
		}
		crt.isStopWord = true;
	}

	public StopWordRemover( ) {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.StopwordDir
		try {
			BufferedReader reader = new BufferedReader(new FileReader(Path.StopwordDir));
			
			// build the trie tree
			String line = null;
			while ((line = reader.readLine()) != null) {
				insert(line.trim());
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not
		return found(word);
	}
	
	public static void main(String[] args) throws Exception {
		char[] word = "dog".toCharArray();
		StopWordRemover r = new StopWordRemover();
		System.out.println(r.isStopword(word));
	}
}
