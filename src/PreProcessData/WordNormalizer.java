package PreProcessData;
import Classes.Stemmer;

/**
 * This is for INFSCI-2140 in 2020
 *
 */
public class WordNormalizer {
	//you can add essential private methods or variables

	// YOU MUST IMPLEMENT THIS METHOD
	public char[] lowercase( char[] chars ) {
		//transform the uppercase characters in the word to lowercase
		for (int i = 0; i < chars.length; i++) {
			chars[i] = Character.toLowerCase(chars[i]);
		}
		return chars;
	}

	public String stem(char[] chars)
	{
		//use the stemmer in Classes package to do the stemming on input word, and return the stemmed word
		Stemmer s = new Stemmer();
		s.add(chars, chars.length);
		s.stem();
		return s.toString();
	}
	
	public static void main(String[] args) throws Exception {
		WordNormalizer s = new WordNormalizer();
		char[] word="plausible".toCharArray();
		System.out.println(s.stem(word));
	}

}
