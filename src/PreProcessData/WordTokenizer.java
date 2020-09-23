package PreProcessData;

/**
 * This is for INFSCI-2140 in 2020
 *
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	private char[] text;
	private int index;

	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		text = texts.clone();
		index = 0;
		
	}

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document
		if (index >= text.length) {
			return null;
		}
		
		while (index < text.length && !Character.isLetter(text[index])) {
			index++; // start
		}
		StringBuilder sb = new StringBuilder();
		while (index < text.length && Character.isLetter(text[index])) {
			sb.append(text[index++]);
		}
		if (sb.length() > 0) {
			return sb.toString().toCharArray();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		String arr = "I am a ... 1,200 35a..4test";
		WordTokenizer wt = new WordTokenizer(arr.toCharArray());
		char[] word;
		while ((word = wt.nextWord()) != null) {
			System.out.println(word);
		}
	}
}
