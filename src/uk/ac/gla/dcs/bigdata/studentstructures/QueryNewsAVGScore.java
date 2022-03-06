package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

import uk.ac.gla.dcs.bigdata.providedstructures.Query;

public class QueryNewsAVGScore implements Serializable{
	
	
	private static final long serialVersionUID = 8125514795623491914L;

	Query query;
	List<NewsScore> scoreList;
	
    public QueryNewsAVGScore() {};
	
	public QueryNewsAVGScore(Query query, List<NewsScore>scoreList) {
		super();
		this.query = query;
		this.scoreList = scoreList;
	}
	
	public Query getQuery() {
		return query;
	}
	
	public void setQuery(Query query) {
		this.query = query;
	}
	
	public List<NewsScore> getScoreList() {
		return scoreList;
	}
	
	public void setScoreList(List<NewsScore> scoreList) {
		this.scoreList = scoreList;
	}
	
}
