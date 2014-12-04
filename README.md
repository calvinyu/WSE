Web Search Engine Homework
===
Project API
<ol>
<li> Brower to QueryHandler API<br>
1. the url should be /suggest?query=sth&type=term or phrase, phrase means requesting phrases, term means requesting for single words.<br>
2. QueryHandler creates a ranker and call ranker.SuggestQuery(String partial_query, String type).<br>
</li>
</ol>


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

Project Report Requirement
<ul>
<li>
Motivation & Related Works
<ul>
<li>
•  What problem are you solving?
</li>
<li>
•  What assumptions are you making?
</li>
<li>
•  What does the system do?
</li>
<li>
•  What papers and/or systems have you looked at?
</li>
</ul>
</li>
<li>
Design & Architecture
<ul>
<li>
•  How did you arrive at your final design? What other alternatives were considered?
</li>
<li>
•  What are the major components of your system?
</li>
</ul>
</li>
<li>
Implementation
<ul>
<li>
•  Details of each individual components, e.g., algorithms, APIs, etc.
</li>
<li>
•  Include how to run your code
</li>
</ul>
</li>
<li>
Evaluation:
<ul>
<li>
•  Quality and/or performance
</li>
<li>
•  Dataset (both corpora and queries) used for evaluation
</li>
<li>
•  Baseline approaches compared against
</li>
<li>
•  Effects of changing various parameters
</li>
</ul>
</li>
￼</ul>
