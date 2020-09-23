package PreProcessData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import Classes.Path;

import java.util.HashMap;
import java.util.Map;

/**
 * This is for INFSCI-2140 in 2020
 *
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader reader;
	private static final String DOC_START = "<DOC>";
	private static final String DOC_END = "</DOC>";
	private static final String DOC_NUMBER_START = "<DOCNO>";
	private static final String DOC_NUMBER_END = "</DOCNO>";
	private static final String TEXT_START = "<TEXT>";
	private static final String TEXT_END = "</TEXT>";
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file existing in Path.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		reader = new BufferedReader(new FileReader(Path.DataTextDir));

	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NTT: remember to close the file that you opened, when you do not use it any more
		boolean inDoc = false;
		boolean inText = false;
		String docNum = null;
		String line = null;
		StringBuilder text = null;
		
		while ((line = reader.readLine()) != null) {
			if (!inDoc && line.equals(DOC_START)) {
				inDoc = true;
			}
			else if (inDoc && line.equals(DOC_END)) {
				inDoc = false;
			}
			else if (inDoc && line.startsWith(DOC_NUMBER_START) && line.endsWith(DOC_NUMBER_END)) {
				// extract the doc number
				String[] docNumParts = line.split("\\s+"); // "\\s" is any kinds of space. "+" is multiple.
				if (docNumParts.length >= 2) {
					docNum = docNumParts[1];
				}
			}
			else if (inDoc && (!inText) && line.equals(TEXT_START)) {
				inText = true;
				text = new StringBuilder();
			}
			else if (inDoc && inText && line.equals(TEXT_END)) {
				// end the text builder
				inText = false;
				Map<String, Object> res = new HashMap<>();
				if (docNum != null) {
					if (text.length() > 0) {
						text.deleteCharAt(text.length() - 1); // remove the last extra space.
					}
					res.put(docNum, text.toString().toCharArray());
					text = null;
					return res;
				}
			}
			else if (inDoc && inText) {
				// read the text
				text.append(line);
				text.append(" "); // need this ???
			}
		}
		reader.close();
		return null;
	}
	
	public static void main(String[] args) throws Exception {
//		// Try to read the text and browse:
//		BufferedReader br = new BufferedReader(new FileReader(Path.DataTextDir));
//		String line = null;
//		int max = 50;
//		while((line = br.readLine()) != null && max > 0) {
//			System.out.println(line);
//			max--;
//		}
		DocumentCollection c = new TrectextCollection();
		Map<String, Object> cm = c.nextDocument();
		System.out.println(cm.keySet());
		System.out.println(cm.values());
	}

}
