package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;

import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedutilities.DPHScorer;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsScore;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsTokens;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryNewsAVGScore;

public class NewsScoreCalculator implements MapFunction<Query,QueryNewsAVGScore>{
	
	private static final long serialVersionUID = 491936734933422543L;
	
	// Global Data
	Broadcast<List<NewsTokens>> newsTokensBV;
	
	
	public NewsScoreCalculator(Broadcast<List<NewsTokens>> newsTokensBV) {
		super();
		this.newsTokensBV = newsTokensBV;
	}

	@Override
	public QueryNewsAVGScore call(Query value) throws Exception {
		List<String> qTermList = value.getQueryTerms();
		List<NewsTokens> newsTokens = newsTokensBV.value();
		
		// Cal 5.totalDocsInCorpus lv1
		long totalDocsInCorpus = newsTokens.size();			// doc nums in corpus
		int qtSize = qTermList.size();						// all query-terms nums
		
		// Cal 4.averageDocumentLengthInCorpus lv1
		double totalDocumentLengthInCorpus = 0;
		double averageDocumentLengthInCorpus = 0;
		
		System.out.println(value.getOriginalQuery());
		List<Integer> buf3 = new ArrayList<>((int)totalDocsInCorpus);	// To Store 3.
		for(int i = 0; i < totalDocsInCorpus; i++) {
			// Cal 3.currentDocumentLength lv3
			int termNumsInDocs = newsTokens.get(i).getTokens().size();	// number of terms in each doc
//			System.out.println(termNumsInDocs);
			buf3.add(termNumsInDocs); 	// store 3
			totalDocumentLengthInCorpus += termNumsInDocs;
		}
		averageDocumentLengthInCorpus = (double) totalDocumentLengthInCorpus / totalDocsInCorpus;
//		System.out.println(averageDocumentLengthInCorpus);

		List<ArrayList<Double>> scoreBuf = new ArrayList<ArrayList<Double>>(qtSize);
		// for each query term
		for(int i = 0; i < qtSize; i++) {
			String curQueryTerm = value.getQueryTerms().get(i);
			
			short termCnt = value.getQueryTermCounts()[i];
			
			int totalTermFrequencyInCorpus = 0;
			
			List<Short> buf1 = new ArrayList<Short>((int) totalDocsInCorpus);
			// for each news
			for(int j = 0; j < totalDocsInCorpus; j++) {
				// Cal 1.termFrequencyInCurrentDocument lv3
				short termFrequencyInCurrentDocument = (short) Collections.frequency(newsTokens.get(j).getTokens(), curQueryTerm);
//				System.out.println(termFrequencyInCurrentDocument);
				buf1.add(termFrequencyInCurrentDocument);
				// Cal 2.totalTermFrequencyInCorpus lv2
				totalTermFrequencyInCorpus += termFrequencyInCurrentDocument;
			}
//			System.out.println(totalTermFrequencyInCorpus);
			
			// for each qterm-news get DPHScore
			List<Double> perTermScore = new ArrayList<Double>((int) totalDocsInCorpus);
			for(int j = 0; j < totalDocsInCorpus; j++) {
//				System.out.println(buf1.get(j));
//				System.out.println(totalTermFrequencyInCorpus);
//				System.out.println(buf3.get(j));
//				System.out.println(averageDocumentLengthInCorpus);
//				System.out.println(totalDocsInCorpus);
				perTermScore.add(DPHScorer.getDPHScore(buf1.get(j), totalTermFrequencyInCorpus, buf3.get(j), averageDocumentLengthInCorpus, totalDocsInCorpus));
				if(!perTermScore.get(j).isNaN())
					System.out.println(perTermScore.get(j));
			}
			scoreBuf.add((ArrayList<Double>) perTermScore);
			
		}
		
		// Build QueryNewsAVGScore
		List<NewsScore> newsScoreList = new ArrayList<NewsScore>((int) totalDocsInCorpus);
		for(int j = 0; j < totalDocsInCorpus; j++) {
			
			String _id = newsTokens.get(j).getId();
			
			double _score = 0;
			double _scoreNum = 0;
			for(int i = 0; i < qtSize; i++) {
				_score += scoreBuf.get(i).get(j);
				_scoreNum += value.getQueryTermCounts()[i];
			}
			_score /= (double)_scoreNum;
			String _title = newsTokens.get(j).getTitle();
			newsScoreList.add(new NewsScore(_id, _score, _title));
		}
		
		QueryNewsAVGScore res = new QueryNewsAVGScore(value, newsScoreList);
//		for(int i = 0; i < res.getScoreList().size(); i++) {
//			System.out.println(res.getScoreList().get(i).getTitle());
//			System.out.println(res.getScoreList().get(i).getDPHScore());
//		}
		
		return res;
	}

}
