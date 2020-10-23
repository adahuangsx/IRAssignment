package Classes;

public class Query {
	//you can modify this class

	private String queryContent;	
	private String queryNorm;
	private String topicId;	
	
	public Query(String queryContent, String queryNorm, String id) {
		this.queryContent = queryContent;
		this.queryNorm = queryNorm;
		this.topicId = id;
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

	public String getQueryNorm() {
		return queryNorm;
	}

	public void setQueryNorm(String queryNorm) {
		this.queryNorm = queryNorm;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(topicId).append(": \n");
		sb.append(queryContent).append("\n");
		sb.append(queryNorm);
		return sb.toString();
	}
}
