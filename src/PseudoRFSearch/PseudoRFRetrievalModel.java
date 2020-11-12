package PseudoRFSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.*;
//import Search.*;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	private static final int mu = 2000;

	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
	}

	/**
	 * Search for the topic with pseudo relevance feedback in 2020 Fall assignment 4.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')


		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);


		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results = new ArrayList<Document>();

		return results;
	}

	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: 
		// P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore = new HashMap<String,Double>();
		
		// In HW4, the queries are all normalized.
		String[] tokens = aQuery.GetQueryContent().split("\\s+");
		Set<Integer> docIDs = new HashSet<>();
		
		// to get feedback docs
		QueryRetrievalModel model = new QueryRetrievalModel(this.ixreader);
		List<Document> feedbkDocs = model.retrieveQuery(aQuery, TopK);
		int totalLen = 0; // top K documents' total length
		for (Document doc : feedbkDocs) {
			int docid = Integer.parseInt(doc.docid());
			totalLen += this.ixreader.docLength(docid);
			docIDs.add(docid);
		}
		
		// to get count(token, doc)
		for (String token : tokens) {
			int cw_D = 0;
			int cw_C = 0;
			int[][] postingList = this.ixreader.getPostingList(token);
			for (int[] pair : postingList) {
				int docid = pair[0];
				int freq = pair[1];
				if (docIDs.contains(docid)) {
					cw_D += freq;
				}
				cw_C += freq;
			}
			// calculate the dirichlet smoothing
			double pw_C = (double) cw_C / totalLen;
			double prob = (double)(cw_D + this.mu * pw_C) / (totalLen + this.mu); // formula
			
			TokenRFScore.put(token, prob);
		}
		
		return TokenRFScore;
	}


}
