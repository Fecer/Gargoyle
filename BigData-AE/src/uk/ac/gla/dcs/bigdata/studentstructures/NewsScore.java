package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
//import java.util.List;

public class NewsScore implements Serializable{


	private static final long serialVersionUID = 2265723765321050131L;
	
	String id;
    double DPHScore;
    String title;
    
    public NewsScore() {};
	
	public NewsScore(String id, double DPHScore, String title) {
		super();
		this.id = id;
		this.DPHScore = DPHScore;
		this.title = title;
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
    


}
