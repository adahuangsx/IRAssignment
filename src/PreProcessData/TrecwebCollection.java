package PreProcessData;

import java.util.HashMap;
import java.util.Map;
import Classes.Path;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This is for INFSCI-2140 in 2020
 *
 */
public class TrecwebCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader reader;
	private static final String DOC_START = "<DOC>";
	private static final String DOC_END = "</DOC>";
	private static final String DOC_NUMBER_START = "<DOCNO>";
	private static final String DOC_NUMBER_END = "</DOCNO>";
	private static final String TEXT_START = "</DOCHDR>";
	
	private static final String DOCHDR_START = "<DOCHDR>";
	private static final String DOCHDR_END = "</DOCHDR>";

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		// This constructor should open the file existing in Path.DataWebDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		reader = new BufferedReader(new FileReader(Path.DataWebDir));		
	}

	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		// this method should load one document from the corpus, and return this document's number and content.
		// the returned document should never be returned again.
		// when no document left, return null
		// NT: the returned content of the document should be cleaned, all html tags should be removed.
		// NTT: remember to close the file that you opened, when you do not use it any more
		boolean inDoc = false;
		boolean inText = false;
		String docNum = null;
		String line = null;
		StringBuilder text = null;
		Map<String, Object> res = new HashMap<>();
		while ((line = reader.readLine()) != null) {
			if (!inDoc && line.equals(DOC_START)) {
				inDoc = true;
			}
			else if (inDoc && line.equals(DOC_END)) {
				// end the text builder
				inDoc = false;
				inText = false;
				if (docNum != null && text != null) {
					text.deleteCharAt(text.length() - 1); // remove the last extra space.
					res.put(docNum, removeHtmlTags(text.toString()).toCharArray());
					return res;
				}
			}
			else if (inDoc && line.startsWith(DOC_NUMBER_START) && line.endsWith(DOC_NUMBER_END)) {
				// extract the doc number
				// web text has no spaces
				int start = line.indexOf(DOC_NUMBER_START) + DOC_NUMBER_START.length();
				int end = line.indexOf(DOC_NUMBER_END);
				docNum = line.substring(start, end);
			}
			else if (inDoc && line.equals(DOCHDR_END)) {
				inText = true;
				text = new StringBuilder();
			}
			else if (inDoc && inText) {
				// extract and compose the text
				text.append(removeHtmlTags(line));
				text.append(" ");
			}
		}
		reader.close();		
		return null;
	}
	
	private String removeHtmlTags(String str) {
		return str.replaceAll("<[^>]+>", " ").replaceAll("//s+", " ");
	}

	public static void main(String[] args) throws Exception {
		// Try to read the text and browse:
		// Path.ResultHM1 + "trectext"
//		BufferedReader br = new BufferedReader(new FileReader("D:\\Fall 2020\\INFSCI 2140 INFORMATION RETRIEVAL\\result.trectext"));
//		String line = null;
//		int max = 50;
//		while((line = br.readLine()) != null && max > 0) {
//			System.out.println(line);
//			max--;
//		}
		
//		System.out.println("=======================");
//		DocumentCollection c = new TrecwebCollection();
//		Map<String, Object> cm = c.nextDocument();
//		System.out.println(cm.keySet());
//		System.out.println(cm.values());
//		
//		line = "<DOCNO>lists-000-0000000</DOCNO>";
//		int start = line.indexOf(DOC_NUMBER_START) + DOC_NUMBER_START.length();
//		int end = line.indexOf(DOC_NUMBER_END);
//		System.out.println(line.substring(start, end));
		
		String a = "";
		System.out.print(a == null);
		int i = 1200;
		String aa = Integer.toString(i);
		aa = "0021";
		char[] cc = new char[] {'0','0','1'};
		System.out.println(String.valueOf(cc));
	}
}
