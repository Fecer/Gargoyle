package uk.ac.gla.dcs.bigdata.studentstructures;

import java.io.Serializable;
import java.util.List;

//import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;

public class NewsTokens implements Serializable{
	
	private static final long serialVersionUID = -2374545335021590334L;
	
	String id;
	List<String> tokens;//tokens of title + five (or less)paragraph
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getTokens() {
		return tokens;
	}
	
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
	
	
	public NewsTokens() {};
	
	public NewsTokens(String id, List<String> tokens) {
		super();
		this.id = id;
		this.tokens = tokens;
	}
	
	
	

}


