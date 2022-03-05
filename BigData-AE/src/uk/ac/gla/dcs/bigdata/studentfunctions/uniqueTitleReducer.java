package uk.ac.gla.dcs.bigdata.studentfunctions;

import java.util.List;

import org.apache.spark.api.java.function.ReduceFunction;

import uk.ac.gla.dcs.bigdata.studentstructures.NewsScore;

public class uniqueTitleReducer implements ReduceFunction<List<NewsScore>> {

	private static final long serialVersionUID = -8746537089432635325L;

	@Override
	public List<NewsScore> call(List<NewsScore> v1, List<NewsScore> v2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
