package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;

//import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;

public class NewsTokens implements Serializable{
	
	private static final long serialVersionUID = -2374545335021590334L;
	
	String id;
	String title;
	List<String> tokens;//tokens of title + five (or less)paragraph
	NewsArticle news;
	
	public NewsArticle getNewsArticle() {
		return news;
	}
	
	public void setNewsArticle(NewsArticle news) {
		this.news = news;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTokens() {
		return tokens;
	}
	
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
	
	
	public NewsTokens() {};
	
	public NewsTokens(String id, String title, List<String> tokens, NewsArticle news) {
		super();
		this.id = id;
		this.title = title;
		this.tokens = tokens;
		this.news = news;
	}
	
	
	

}


