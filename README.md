Web Search Engine Homework
===
Instructions  
<ol>
<li> Compile<br>
  at root directory of this repository(i.e. directory that consists folders lib, src and data)<br>
  $ javac -cp lib/jsoup-1.8.1.jar src/edu/nyu/cs/cs2580/*.java<br>
</li>
<li> Document Qualitiy Computation<br>
  at the same directory<br><br>
  <ul>
  Mining Mode<br>
  $ java -cp src edu.nyu.cs.cs2580.SearchEngine --mode=mining --options=conf/engine.conf<br><br>
  </ul>
  <ul>
  Indexing mode<br>
  $ java -cp src:lib/jsoup-1.8.1.jar edu.nyu.cs.cs2580.SearchEngine --mode=index --options=conf/engine.conf<br><br>
  </ul>
  <ul>
  Serving mode<br>
  $ java -cp src:lib/jsoup-1.8.1.jar edu.nyu.cs.cs2580.SearchEngine --mode=serve --options=conf/engine.conf --port=25813<br><br>
  </ul>
</li>
<li> Spearman coefficient<br>
$ java -cp src edu.nyu.cs.cs2580.Spearman data/index/pagerank.idx data/index/numviews.idx<br>

</li>
</ol>

