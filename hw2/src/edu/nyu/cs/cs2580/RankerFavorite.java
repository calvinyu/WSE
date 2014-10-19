package edu.nyu.cs.cs2580;

import java.util.*;

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
    _indexer = (IndexerInvertedDoconly) _indexer;
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    //retrieve relavant docs
    Document nextDoc = _indexer.nextDoc(query, -1);// not sure about api
    while(nextDoc != null){
    	System.out.println("in loop");
    	retrieval_results.add(runquery(query, nextDoc));
      nextDoc = _indexer.nextDoc(query, nextDoc._docid);
    }
	System.out.println("finish loop");
    
    Collections.sort(retrieval_results);
    return retrieval_results;  
  }

  public ScoredDocument runquery(Query query, Document doc){
	System.out.println("in run query");
    Vector<Double> lmprob = new Vector<Double>();
    createLmprob(doc, query, 0.5, lmprob);
    double score = language_model_score(lmprob);
    return new ScoredDocument(doc, score);
  }

  private void createLmprob(Document d, Query query,
   double lamb, Vector<Double> lmprob) {
		System.out.println("in create lmprob");

    DocumentIndexed doc = (DocumentIndexed) d;
    int length = doc.getDocLength();
    //HashMap< Integer, Vector<Integer>> body = doc._body;
    // Build query vector, it should support phrase query.
    //Vector < Integer > bv = doc.getBodyTokens();
    Vector < String > qv = query._tokens;
    HashMap <Integer, Integer> qmap = countFrequency(qv);
    for(int index: qmap.keySet()){
      double score = doc.getTermFrequency(index);
      String s = ((IndexerInvertedDoconly)_indexer).getTermByIndex(index);
      // Add query words to language model probability map.
      score /= length;
      // Smoothing.
      long tf = doc.getTermFrequency(index);
      long totalTf = doc.totalTermFrequency();
      score = lamb * score + (1 - lamb) * ( tf / totalTf );
      lmprob.add(score);
    }
  }
  private HashMap<Integer, Integer> countFrequency(Vector<String> vs){
		System.out.println("in cnt freq");

    HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
    for(String s: vs){
		System.out.println("in cnt freq: for");

      int index = ((IndexerInvertedDoconly)_indexer).getIndexByTerm(s);
		System.out.println("in cnt freq");

      if(!map.containsKey(index)){
        map.put(index, 0);
      }
      map.put(index, map.get(index)+1);
    }
    return map;
  }
  private double language_model_score(Vector < Double > lmprob) {
    double lm_score = 0.;
    for (Double score : lmprob) { lm_score += Math.log(score); }
    return lm_score;
  }
}
