package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
//import java.util.List;

import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;

public class NewsScore implements Serializable{


	private static final long serialVersionUID = 2265723765321050131L;
	
	String id;
    double DPHScore;
    String title;
    NewsArticle news;
    
    public NewsScore() {};
	
	public NewsScore(String id, double DPHScore, String title, NewsArticle news) {
		super();
		this.id = id;
		this.DPHScore = DPHScore;
		this.title = title;
		this.news = news;
	}
	
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
	
	public double getDPHScore() {
		return DPHScore;
	}
	
	public void setDPHScore(double DPHScore) {
		this.DPHScore = DPHScore;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		
		if(!(obj instanceof NewsScore))
			return false;
		
		NewsScore ns = (NewsScore)obj;
		
		return Double.compare(ns.getDPHScore(), this.getDPHScore()) == 0 &&
				ns.getId().equals(this.getId()) && 
				ns.getTitle().equals(this.getTitle());
	}
    


}
