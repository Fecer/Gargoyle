package uk.ac.gla.dcs.bigdata.apps;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import uk.ac.gla.dcs.bigdata.providedfunctions.NewsFormaterMap;
import uk.ac.gla.dcs.bigdata.providedfunctions.QueryFormaterMap;
import uk.ac.gla.dcs.bigdata.providedstructures.DocumentRanking;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.providedstructures.RankedResult;
import uk.ac.gla.dcs.bigdata.providedutilities.TextDistanceCalculator;
import uk.ac.gla.dcs.bigdata.studentfunctions.NewsScoreCalculator;
import uk.ac.gla.dcs.bigdata.studentfunctions.NewsTokensFormaterFlatMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.TestTokenize;
import uk.ac.gla.dcs.bigdata.studentfunctions.maxScoreReducer;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsScore;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsTokens;
import uk.ac.gla.dcs.bigdata.studentstructures.QueryNewsAVGScore;


/**
 * This is the main class where your Spark topology should be specified.
 * 
 * By default, running this class will execute the topology defined in the
 * rankDocuments() method in local mode, although this may be overriden by
 * the spark.master environment variable.
 * @author Richard
 *
 */
public class AssessedExercise {

	
	public static void main(String[] args) {
		
		
		
		// The code submitted for the assessed exerise may be run in either local or remote modes
		// Configuration of this will be performed based on an environment variable
		String sparkMasterDef = System.getenv("SPARK_MASTER");
		if (sparkMasterDef==null) {
			File hadoopDIR = new File("resources/hadoop/"); // represent the hadoop directory as a Java file so we can get an absolute path for it
			System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath()); // set the JVM system property so that Spark finds it
			sparkMasterDef = "local[2]"; // default is local mode with two executors
		}
		
		String sparkSessionName = "BigDataAE"; // give the session a name
		
		// Create the Spark Configuration 
		SparkConf conf = new SparkConf()
				.setMaster(sparkMasterDef)
				.setAppName(sparkSessionName);
		
		// Create the spark session
		SparkSession spark = SparkSession
				  .builder()
				  .config(conf)
				  .getOrCreate();
	
		
		// Get the location of the input queries
		String queryFile = System.getenv("BIGDATA_QUERIES");
		if (queryFile==null) queryFile = "data/queries.list"; // default is a sample with 3 queries
		
		// Get the location of the input news articles
		String newsFile = System.getenv("BIGDATA_NEWS");
		if (newsFile==null) newsFile = "data/TREC_Washington_Post_collection.v3.example.json"; // default is a sample of 5000 news articles
		
		// Call the student's code
		List<DocumentRanking> results = rankDocuments(spark, queryFile, newsFile);
		
		// Close the spark session
		spark.close();
		
		String out = System.getenv("BIGDATA_RESULTS");
		String resultsDIR = "results/";
		if (out!=null) resultsDIR = out;
		
		// Check if the code returned any results
		if (results==null) System.err.println("Topology return no rankings, student code may not be implemented, skiping final write.");
		else {
			
			// Write the ranking for each query as a new file
			for (DocumentRanking rankingForQuery : results) {
				rankingForQuery.write(new File(resultsDIR).getAbsolutePath());
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(resultsDIR).getAbsolutePath()+"/SPARK.DONE")));
			writer.write(String.valueOf(System.currentTimeMillis()));
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	
	public static List<DocumentRanking> rankDocuments(SparkSession spark, String queryFile, String newsFile) {
		
		// Load queries and news articles
		Dataset<Row> queriesjson = spark.read().text(queryFile);
		Dataset<Row> newsjson = spark.read().text(newsFile); // read in files as string rows, one row per article
		
		newsjson = newsjson.repartition(24);
		
		// Perform an initial conversion from Dataset<Row> to Query and NewsArticle Java objects
		Dataset<Query> queries = queriesjson.map(new QueryFormaterMap(), Encoders.bean(Query.class)); // this converts each row into a Query
		Dataset<NewsArticle> news = newsjson.map(new NewsFormaterMap(), Encoders.bean(NewsArticle.class)); // this converts each row into a NewsArticle
		
		//----------------------------------------------------------------
		// Your Spark Topology should be defined here
		//----------------------------------------------------------------
		
		// 1. FlatMap News to NewsTokens
				Encoder<NewsTokens> newsTokensEncoder = Encoders.bean(NewsTokens.class);
				Dataset<NewsTokens> newsTokens = news.flatMap(new NewsTokensFormaterFlatMap(), newsTokensEncoder);
				List<NewsTokens> newsTokensList = newsTokens.collectAsList();
				
				// 2. Map Query to Query-News Score ( NewsTokens as secondary data)
				Broadcast<List<NewsTokens>> newsTokensBV = JavaSparkContext.fromSparkContext(spark.sparkContext()).broadcast(newsTokensList);
				Encoder<QueryNewsAVGScore> scorePerQueryEncoder = Encoders.bean(QueryNewsAVGScore.class);
				Dataset<QueryNewsAVGScore> scorePerQuery = queries.map(new NewsScoreCalculator(newsTokensBV), scorePerQueryEncoder);
				List<QueryNewsAVGScore> scorePerQueryList = scorePerQuery.collectAsList();

				// 3.1 Build result instance
				List<DocumentRanking> drList = new ArrayList<DocumentRanking>();
				
				// 3.2 Reduce Every NewsScore in QueryNewsAVGScore to find MAX
				int queryNum = scorePerQueryList.size();
				for(int i = 0; i < queryNum; i++) {
					// Build DocumentRanking for every Query 
					System.out.println("For Query:");
					System.out.println(i);
					DocumentRanking curQ = new DocumentRanking();
					curQ.setQuery(scorePerQueryList.get(i).getQuery());
					drList.add(curQ);
					
					List<RankedResult> resList = new ArrayList<RankedResult>();
					curQ.setResults(resList);	// Add resList to curQuery's DocumentRanking
					
					// Find 10 news
					int cnt = 10;		
					List<NewsScore> curNewsList = scorePerQueryList.get(i).getScoreList();
					while(cnt > 0) {
						if(curNewsList.isEmpty())
							break;
						System.out.println("Article Number:");
						System.out.println(11 - cnt);
						Dataset<NewsScore> curNewsDS = spark.createDataset(curNewsList, Encoders.bean(NewsScore.class));
						
						// Get MAX Score from current source dataset
						NewsScore maxNews = curNewsDS.reduce(new maxScoreReducer());
						System.out.println(maxNews.getTitle());
						System.out.println(maxNews.getDPHScore());
						
						
						// Remove MAX from source dataset
						Iterator<NewsScore> k = curNewsList.iterator();
						while(k.hasNext()) {
							NewsScore nsPointer = k.next();
							if(nsPointer.equals(maxNews)) {
								System.out.println("Deleting.");
								k.remove();
							}
						}
						
						// Try to add MAX to res dataset
						boolean insert = true;
						Iterator<RankedResult> resI = resList.iterator();
						while(resI.hasNext()) {
							RankedResult rr = resI.next();
							// Compare MAX with every element in res dataset
							// Calculate TextDistance
							if(TextDistanceCalculator.similarity(rr.getArticle().getTitle(), maxNews.getTitle()) < 0.5) {
								// Discard curMax and find nextMax
								insert = false;
								break;
							}else {
								// Find next rr
							}
						}
						
						if(insert == true) {
							// Put Inside
							RankedResult curRes = new RankedResult(maxNews.getId(), maxNews.getNewsArticle(), maxNews.getDPHScore());
							resList.add(curRes);
							cnt--;
						}else {
							// Find nextMax
						}
						
					}
					
				}
				
				return drList; // replace this with the the list of DocumentRanking output by your topology
				}
	
	
}
