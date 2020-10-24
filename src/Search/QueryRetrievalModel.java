package Search;

import java.io.IOException;
import java.util.ArrayList;
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
//	private long REF; // whole collection size
	
	
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
			int count_wD = 0; 
			if (posting != null) {
				for (int i = 0; i < posting.length; i++) {
					int docId = posting[i][0];
					count_wD = posting[i][1];
					if (!docToTermCountMap.containsKey(docId)) {
						docToTermCountMap.put(docId, new HashMap<>());
					}
					docToTermCountMap.get(docId).put(term, count_wD);
				}
			}
			
		}
		PriorityQueue<Document> heap = new PriorityQueue<>(TopN);
		for (Integer docId : docToTermCountMap.keySet()) {
			double score = 1.0;
			for (String term : queryNorm) {
				Integer count_wD = docToTermCountMap.get(docId).get(term);
				if (count_wD != null) {
					double dirichletSmoothingProb = Dirichlet(term, docId, count_wD);
					score *= dirichletSmoothingProb;
				} else {
					score = 0;
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
		for (int i = 0; i < TopN; i++) {
			res.add(heap.poll());
		}
		return res;
	}
	
	private double Dirichlet(String word, int docId, int count_wD) throws IOException {
		/**
		 * formula: P(w|D) = [count(w, D) + mu * P(w|C)] / (|D| + mu)
		 */
		long count_wC = indexReader.CollectionFreq(word); // count of this word in the whole collection
		long count_C = indexReader.CollectionSize(); // the whole collection word count
		double P_wC = count_wC / count_C;
		int abs_D = indexReader.docLength(docId); // the length of this doc
		return (count_wD + mu * P_wC) / (abs_D + mu);
	}
	
	public static void main(String[] args) throws IOException {
		// Open index
		MyIndexReader ixreader = new MyIndexReader("trectext");
		// Initialize the MyRetrievalModel
		QueryRetrievalModel model = new QueryRetrievalModel(ixreader);
		ExtractQuery queries = new ExtractQuery();
		while (queries.hasNext()) {
			Query aQuery = queries.next();
			System.out.println(aQuery);
			List<Document> results = model.retrieveQuery(aQuery, 2);
			System.out.println(results.size());
		}
	}
}