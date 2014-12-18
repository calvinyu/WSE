package edu.nyu.cs.cs2580;

import java.util.*;
import java.util.Map.Entry;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2 based on a refactoring of your favorite
 * Ranker (except RankerPhrase) from HW1. The new Ranker should no longer rely
 * on the instructors' {@link IndexerFullScan}, instead it should use one of
 * your more efficient implementations.
 */
public class RankerFavorite extends Ranker {

  public RankerFavorite(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector<ScoredDocument> retrieval_results = new Vector<ScoredDocument>();
    Document nextDoc = _indexer.nextDoc(query, -1);
    while(nextDoc != null){
      retrieval_results.add(runQuery(query, nextDoc));
      nextDoc = _indexer.nextDoc(query, nextDoc._docid);
    }
    Collections.sort(retrieval_results);
    Collections.reverse(retrieval_results);
    Vector<ScoredDocument> tenResult = new Vector<ScoredDocument>();
    List<ScoredDocument> list;
    // return at most 10 documents
    list = retrieval_results.subList(0, Math.min(10, retrieval_results.size()));
    for(ScoredDocument s : list) tenResult.add(s);
    return tenResult;
  }

  public ScoredDocument runQuery(Query query, Document doc){
    Vector<Double> lmprob = new Vector<Double>();
    createLmprob(doc, query, 0.5, lmprob);
    double score = language_model_score(lmprob);
    return new ScoredDocument(doc, score);
  }

  /**
   * @return List that contains ranked unigram suggestions
   */
  public List<String> suggestUnigram(Query query, int num) {
    List<Pair<String, Integer>> unigramQueries =
        ((IndexerInvertedCompressed) _indexer).getWordSuggestion(query._query);
    Collections.sort(unigramQueries, new Comparator<Pair<String, Integer>>() {
      public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        if (o1.second < o2.second) return 1;
        else if (o1.second > o2.second) return -1;
        return 0;
      }
    });
    List<String> rankedSuggestions = new ArrayList<String>();
    for (int i = 0; i < Math.min(num, unigramQueries.size()); i++) {
      rankedSuggestions.add(unigramQueries.get(i).first);
    }
    return rankedSuggestions;
  }

  /**
   * @return List that contains ranked n-gram suggestions
   */
  public List<String> suggestNgrams(Query query, int num) {
    System.out.println(query._query);
    System.out.println(num);
    List<Pair<List<Integer>, Integer>> tmpNgramQueries =
        ((IndexerInvertedCompressed) _indexer).getNgramSuggestion(query._tokens);
    List<Pair<List<Integer>, Integer>> ngramQueries =
        new LinkedList<Pair<List<Integer>, Integer>>();
    for (Pair<List<Integer>, Integer> p : tmpNgramQueries) {
      String lastWord = ((IndexerInvertedCompressed) _indexer)._terms.get((p.first.get(p.first.size() - 1)));
      if (!StopWords.isStopWord(lastWord)) ngramQueries.add(p);
    }
    Collections.sort(ngramQueries, new Comparator<Pair<List<Integer>, Integer>>() {
      @Override
      public int compare(Pair<List<Integer>, Integer> o1, Pair<List<Integer>, Integer> o2) {
        if (o1.second * (o1.first.size() - 1) < o2.second * (o2.first.size() - 1)) return 1;
        else if (o1.second * (o1.first.size() - 1) > o2.second * (o2.first.size() - 1)) return -1;
        return 0;
      }
    });
    List<String> rankedSuggestions = new ArrayList<String>();
    for (int i = 0; i < Math.min(num, ngramQueries.size()); i++) {
      String extendedQuery = "";
      for (Integer j : ngramQueries.get(i).first) {
        extendedQuery += ((IndexerInvertedCompressed) _indexer)._terms.get(j) + " ";
      }
      System.out.println(extendedQuery.substring(0, extendedQuery.length() - 1));
      rankedSuggestions.add(extendedQuery.substring(0, extendedQuery.length() - 1));
    }
    return rankedSuggestions;
  }

  /**
   * @return List that contains ranked previously searched user queries
   */
  public List<String> suggestLoggedQuery(Query query, int num) {
    System.out.println("suggestLoggedQuery");
    List<Pair<String, Integer>> unigramQueries =
        ((IndexerInvertedCompressed) _indexer).getUserLogSuggestion(query._query);
    for (Pair<String, Integer> p : unigramQueries) {
      System.out.println(p.first);
    }
    Collections.sort(unigramQueries, new Comparator<Pair<String, Integer>>() {
      public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        if (o1.second < o2.second) return 1;
        else if (o1.second > o2.second) return -1;
        return 0;
      }
    });
    List<String> rankedSuggestions = new ArrayList<String>();
    for (int i = 0; i < Math.min(num, unigramQueries.size()); i++) {
      rankedSuggestions.add(unigramQueries.get(i).first);
    }
    return rankedSuggestions;
  }

  @Override
  public String expandQuery(Vector<ScoredDocument> docs, String query,
          int numDocs, int numTerms) {
	  // dictionary contains all words and their count
	  HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	  // get top docs
	  for(int i=0; i<numDocs; i++){
		  int docid = docs.get(i).getDoc()._docid;
		  for (int j=0;j<20;j++){
			  // get the term by looking up _terms
			  String term =((IndexerInvertedCompressed) _indexer)._terms.get(
					  ((IndexerInvertedCompressed) _indexer)._docBody[docid][2*j]);
			  // get the number of appearances
			  int count = ((IndexerInvertedCompressed) _indexer)._docBody[docid][2*j+1];
			  // 	if exists, add the count, else create an entry
			  if(dictionary.containsKey(term)){
				  dictionary.put(term, dictionary.get(term)+count);
			  } else dictionary.put(term, count);
		  }
	  }
	  // get top terms
	  dictionary = sortByValues(dictionary);
	  Set<Entry<String, Integer>> set = dictionary.entrySet();
	  Iterator<Entry<String, Integer>> iterator = set.iterator();

	  // write results into a string to return to user
	  iterator = set.iterator();
	  String result = "";
	  for (int i=0; i<numTerms; i++) {
		  Entry<String,Integer> me = iterator.next();
		  result += " " + me.getKey();
	  }
	  return result.substring(1);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private HashMap<String,Integer> sortByValues(HashMap map) { 
	  List list = new LinkedList(map.entrySet());
	  // Defined Comparator here
	  Collections.sort(list, new Comparator() {
		  public int compare(Object o1, Object o2) {
			  return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(
					  ((Map.Entry) (o1)).getValue());
		  }
	  });
	  HashMap sortedHashMap = new LinkedHashMap();
	  for (Iterator it = list.iterator(); it.hasNext();) {
		  Map.Entry entry = (Map.Entry) it.next();
		  sortedHashMap.put(entry.getKey(), entry.getValue());
	  }
	  return sortedHashMap;
  }
  
	private void createLmprob(Document d, Query query,
                            double lamb, Vector<Double> lmprob) {
    // Cast Document into DocumentIndexed
    DocumentIndexed doc = (DocumentIndexed) d;
    // Get document length
    int length = doc.getLength();
    // Create query strings
    Vector<String> queryString = new Vector<String>();
    Vector<String> queryPhrase = new Vector<String>();
    for (String s : query._tokens){
      String[] phrase = s.split(" ");
      if (phrase.length > 1) queryPhrase.add(s);
      queryString.addAll(Arrays.asList(phrase));
    }
    queryString.addAll(queryPhrase);
    // get list of frequencies
    Vector<Integer> freqlist = doc.getFreqList();
    // Calculate the score
    for (int i = 0; i < freqlist.size(); ++i){
      double score = freqlist.get(i) / (double)length;
      String s = queryString.get(i);
      if(s.contains(" ")) lmprob.add(score);
      else {
        int index = ((IndexerInvertedCompressed) _indexer)._dictionary.get(s);
        // Smoothing.
        double tf = ((IndexerInvertedCompressed) _indexer)._termCorpusFrequency[index];
        double totalTf = _indexer.totalTermFrequency();
        score = lamb * score + (1 - lamb) * ( tf / totalTf );
        lmprob.add(score);
      }
    }
  }

  private double language_model_score(Vector<Double> lmprob) {
    double lm_score = 0.;
    for (Double score : lmprob) { lm_score += Math.log(score); }
    return lm_score;
  }
}
