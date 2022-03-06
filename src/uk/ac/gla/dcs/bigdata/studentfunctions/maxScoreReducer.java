package uk.ac.gla.dcs.bigdata.studentfunctions;

import org.apache.spark.api.java.function.ReduceFunction;

import uk.ac.gla.dcs.bigdata.studentstructures.NewsScore;

public class maxScoreReducer implements ReduceFunction<NewsScore> {

	private static final long serialVersionUID = -8746537089432635325L;

	@Override
	public NewsScore call(NewsScore v1, NewsScore v2) throws Exception {
		// Get the bigger value
		return v1.getDPHScore() > v2.getDPHScore() ? v1 : v2;
	}

}
