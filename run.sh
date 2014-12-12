javac -cp lib/jsoup-1.8.1.jar src/edu/nyu/cs/cs2580/*.java
java -cp src:lib/jsoup-1.8.1.jar -Xmx10G edu.nyu.cs.cs2580.SearchEngine --mode=index --options=conf/engine.conf
java -cp src:lib/jsoup-1.8.1.jar -Xmx10G edu.nyu.cs.cs2580.SearchEngine --mode=serve --options=conf/engine.conf --port=25813 
