package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;

import uk.ac.gla.dcs.bigdata.providedstructures.ContentItem;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedutilities.TextPreProcessor;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsTokens;

public class NewsTokensFormaterMap implements MapFunction<NewsArticle,NewsTokens> {

	private static final long serialVersionUID = -2362307580297109882L;
	
	private transient TextPreProcessor processor;

	@Override
	public NewsTokens call(NewsArticle value) throws Exception {
		
		if (processor==null) processor = new TextPreProcessor();
		
		String resId = value.getId();
		String textBuffer = value.getTitle();	
		List<ContentItem> contents = value.getContents();
		int cnt = 0;	// Paragraph nums
		
		Iterator<ContentItem> i = contents.iterator();
		while(i.hasNext()) {
			if (cnt >= 5) break;
			
			ContentItem cur = i.next();
			if (cur.getSubtype() != null) {
				if(cur.getSubtype().equals("paragraph")) {
					textBuffer += cur.getContent();	// Concat all paragraph
					cnt++;
				}	
			}
		}
		
		List<String> newsTerms = processor.process(textBuffer);
		
//		Iterator<String> j = newsTerms.iterator();
//		while(j.hasNext()) {
//			System.out.println(j.next().toString());
//		}
		
		return new NewsTokens(resId, value.getTitle(), newsTerms);
	}
	
	
}
