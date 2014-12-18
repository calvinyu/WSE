1. Commands:

create symbolic link:
        cd data
        ln -s /home/congyu/cs2580/hw2/instructor/data/wiki
compile (go back to the home directory after creating symbolic link): 
        javac -cp lib/jsoup-1.8.1.jar src/edu/nyu/cs/cs2580/*.java
mine:
        java -cp src:lib/jsoup-1.8.1.jar -Xmx512m edu.nyu.cs.cs2580.SearchEngine  --mode=mining --options=conf/engine.conf
contruct index: 
        java -cp src:lib/jsoup-1.8.1.jar -Xmx512m edu.nyu.cs.cs2580.SearchEngine  --mode=index --options=conf/engine.conf
start server: 
        java -cp src:lib/jsoup-1.8.1.jar -Xmx512m edu.nyu.cs.cs2580.SearchEngine  --mode=serve --port=25813 --options=conf/engine.conf
search query: 
        curl 'localhost:25813/search?query=%22web+searching%22+google&ranker=comprehensive'
PRF query:
        curl 'localhost:25813/prf?query=%22web+searching%22+google&ranker=comprehensive&numdocs=10&numterms=10'

The output of search queries are: <docid><docname><score><pagerank><numviews>.

2. Page rank is trying to reconstruct "real user behaviors", so the closer to numviews, the better the Page rank is (given there is no anomalies in numviews). So, we chose the parameters with the highest Spearman coefficient to be the best Page rank parameter. The parameters that yielded the highest Spearman coefficient in our experiment (up to 4 iterations) was lambda=1.0 and 4 iterations and the value was 0.482. For the parameters given in the assignment, the results were

- lambda=0.1, 1 iteration => 0.471
- lambda=0.9, 1 iteration => 0.471
- lambda=0.1, 2 iterations => 0.468
- lambda=0.9, 2 iterations => 0.470

3. To compute the Spearmen coefficient, run the following command:

$ java -cp src edu.nyu.cs.cs2580.Spearman data/index/pagerank.idx data/index/numviews.idx

The Spearman coefficient with our best Page rank was 4.82.

4. To compute query representations and query similarity, run the following command:

$ rm -f prf*.tsv
$ i=0
$ while read q;
  do i=$((i + 1));
  prfout=prf-$i.tsv;
  curl "http://localhost:25813/prf?query=`echo $q | sed -e s/\ /+/g -e s/\\"/%22/g`&ranker=comprehensive&numdocs=10&numterms=10" > $prfout;
  echo $q:$prfout >> prf.tsv;
  done < queries.tsv
$ java -cp src edu.nyu.cs.cs2580.Bhattacharyya prf.tsv qsim.tsv

The queries in queries.tsv should be of the forms

google
web searching
"web searching"
"web searching" google
