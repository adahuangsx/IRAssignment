package Classes;

import java.util.List;

public class Query {
	//you can modify this class

	private String queryContent;	
	private List<String> queryNorm;
	private String topicId;	
	
	public Query(String queryContent, List<String> queryNorm, String id) {
		this.queryContent = queryContent;
		this.queryNorm = queryNorm;
		this.topicId = id;
	}
	
	public Query() {
		
	}
	
	public String GetQueryContent() {
		return queryContent;
	}
	public String GetTopicId() {
		return topicId;
	}
	public void SetQueryContent(String content){
		queryContent=content;
	}	
	public void SetTopicId(String id){
		topicId=id;
	}

	public List<String> getQueryNorm() {
		return queryNorm;
	}

	public void setQueryNorm(List<String> queryNorm) {
		this.queryNorm = queryNorm;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(topicId).append(": \n");
		sb.append(queryContent).append("\n");
		sb.append(queryNorm).append("\n");
		return sb.toString();
	}
}
