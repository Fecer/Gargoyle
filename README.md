# babyfantastic

### A tiny Spark application to get top 10 most relevent news about a specific query topic.

## Structure
```
.
├── bin
├── data
│   ├── README.md
│   ├── TREC_Washington_Post_collection.v3.example.json		// News  DataSet
│   └── queries.list						// Query DataSet
├── pom.xml
├── resources
├── results							// Query Results
│   └── 1646504628047								
│       ├── finance
│       ├── james_bond
│       └── on_Facebook_IPO
├── src
│   └── uk
│       └── ac
│           └── gla
│               └── dcs
│                   └── bigdata
│                       ├── apps
│                       │   └── AssessedExercise.java		// Main Function
│                       ├── providedfunctions
│                       │   ├── NewsFormaterMap.java
│                       │   └── QueryFormaterMap.java
│                       ├── providedstructures
│                       │   ├── ContentItem.java
│                       │   ├── DocumentRanking.java
│                       │   ├── NewsArticle.java
│                       │   ├── Query.java
│                       │   └── RankedResult.java
│                       ├── providedutilities
│                       │   ├── DPHScorer.java
│                       │   ├── TextDistanceCalculator.java
│                       │   └── TextPreProcessor.java
│                       ├── studentfunctions
│                       │   ├── NewsScoreCalculator.java
│                       │   ├── NewsTokensFormaterFlatMap.java
│                       │   └── maxScoreReducer.java
│                       └── studentstructures
│                           ├── NewsScore.java
│                           ├── NewsTokens.java
│                           └── QueryNewsAVGScore.java
└── target

18 directories, 24 files
```
