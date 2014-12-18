Here is the usage of the search engine:

Step  1: unzip source.zip
Step  2: cd source
Step  3: Then compile code
	javac -cp lib/jsoup-1.8.1.jar src/edu/nyu/cs/cs2580/*.java
Step  4: Build index before running the server
	java -cp src:lib/jsoup-1.8.1.jar -Xmx6G edu.nyu.cs.cs2580.SearchEngine --mode=index --options=conf/engine.conf
Step  5: To start to server listening at port 25813
	java -cp src:lib/jsoup-1.8.1.jar -Xmx6G  edu.nyu.cs.cs2580.SearchEngine --mode=serve --options=conf/engine.conf --port=25813
Step 6: Alternatively, step 3 through 5 can be combined using a single command
	./run.sh or sh run.sh
Step 7: Open web/home.html to start enjoying our search engine