javac -cp lib/jsoup-1.8.1.jar src/edu/nyu/cs/cs2580/*.java
java -cp src edu.nyu.cs.cs2580.SearchEngine --mode=mining --options=conf/engine.conf
java -cp src edu.nyu.cs.cs2580.Spearman data/index/pagerank.idx data/index/numviews.idx
