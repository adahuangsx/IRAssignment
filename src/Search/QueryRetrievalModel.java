package Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;
	
	private double mu = 2000.0; // the mu in Dirichlet smoothing formula
	
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
	}
	
	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		Map<Integer, Map<String, Integer>> docToTermCountMap = new HashMap<>();
		List<String> queryNorm = aQuery.getQueryNorm();
		for (String term : queryNorm) {
			int[][] posting = indexReader.getPostingList(term);
			if (posting != null) {
				for (int i = 0; i < posting.length; i++) {
					int docId = posting[i][0];
					int count_wD = posting[i][1];
					if (!docToTermCountMap.containsKey(docId)) {
						docToTermCountMap.put(docId, new HashMap<>());
					}
					docToTermCountMap.get(docId).put(term, count_wD);
				}
			}
		}
		// get inverted map [doc -> term, freq]
		PriorityQueue<Document> heap = new PriorityQueue<>(TopN); // get top-K problem
		for (Integer docId : docToTermCountMap.keySet()) {
			double score = 1.0;
			for (String term : queryNorm) {
				Integer count_wD = docToTermCountMap.get(docId).get(term);
				count_wD = count_wD == null ? 0 : count_wD;
				double dirichletSmoothingProb = Dirichlet(term, docId, count_wD);
				if (dirichletSmoothingProb > 0.0) {
					score *= dirichletSmoothingProb; // non-appearing words are dropped.
				}
			}
			Document doc = new Document(String.valueOf(docId), indexReader.getDocno(docId), score);
			int cnt = TopN;
			if (cnt > 0) {
				heap.offer(doc);
			}
			else {
				if (doc.compareTo(heap.peek()) < 0) {
					heap.poll();
					heap.offer(doc);
				}
			}
			cnt--;
		}
		List<Document> res = new ArrayList<>();
		for (int i = 0; i < Math.min(TopN, heap.size()); i++) {
			res.add(heap.poll());
		}
		return res;
	}
	
	/**
	 * formula: P(w|D) = [count(w, D) + mu * P(w|C)] / (|D| + mu)
	 */
	private double Dirichlet(String word, int docId, int count_wD) throws IOException {
		long count_wC = indexReader.CollectionFreq(word); // count of this word in the whole collection
		long count_C = indexReader.CollectionSize(); // the whole collection word count
		double P_wC = ((double)count_wC) / count_C;
		int abs_D = indexReader.docLength(docId); // the length of this doc
		double res = (count_wD + mu * P_wC) / (abs_D + mu);
		return res;
	}
	

	
	public static void main(String[] args) throws Exception {
		// Open index
		MyIndexReader ixreader = new MyIndexReader("trectext");
		// Initialize the MyRetrievalModel
		QueryRetrievalModel model = new QueryRetrievalModel(ixreader);
		ExtractQuery queries = new ExtractQuery();
//		while (queries.hasNext()) {
//			Query aQuery = queries.next();
//			System.out.println(aQuery);
//			List<Document> results = model.retrieveQuery(aQuery, 2);
//		}
		
		// test Dysphagia
		String word = "dysphagia";
		long count_wC = ixreader.CollectionFreq(word); // count of this word in the whole collection
		long count_C = ixreader.CollectionSize(); // the whole collection word count
		double P_wC = count_wC / count_C;
		int abs_D = ixreader.docLength(1); // the length of this doc
		System.out.println((0 + model.mu * P_wC) / (abs_D + model.mu));
	}
}