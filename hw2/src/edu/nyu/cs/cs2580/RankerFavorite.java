package edu.nyu.cs.cs2580;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.lang.Math;

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
  _indexer = (IndexerInvertedCompressed) _indexer;
    //_indexer = (IndexerInvertedOccurrence) _indexer;
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    //retrieve relavant docs
    //System.out.println("b4 getting next doc");
    Document nextDoc = _indexer.nextDoc(query, -1);
    while(nextDoc != null){
      //System.out.println("getting next doc");
      retrieval_results.add(runquery(query, nextDoc));
      nextDoc = _indexer.nextDoc(query, nextDoc._docid);
    }
    Collections.sort(retrieval_results);
    Collections.reverse(retrieval_results);
    Vector<ScoredDocument> tenResult = new Vector<ScoredDocument>();
    List<ScoredDocument> list = new LinkedList<ScoredDocument>();
    list = retrieval_results.subList(0, Math.min(10, retrieval_results.size()));
    for(ScoredDocument s: list) tenResult.add(s);
    return tenResult;  
  }

  public ScoredDocument runquery(Query query, Document doc){
    //System.out.println("running query");
    Vector<Double> lmprob = new Vector<Double>();
    createLmprob(doc, query, 0.5, lmprob);
    double score = language_model_score(lmprob);
    return new ScoredDocument(doc, score);
  }

  private void createLmprob(Document d, Query query,
   double lamb, Vector<Double> lmprob) {
    //System.out.println("creating lmprob");
    DocumentIndexed doc = (DocumentIndexed) d;
    int length = doc.getDocLength();
    if(length == 0){
      System.out.println("Doc is length is zero, ERROR\nUsing 1 for now!!");
      length = 1;
    }
    Vector<String> queryString = new Vector<String>();
    Vector<String> queryPhrase = new Vector<String>();
    for(String s:query._tokens){
      String[] phrase = s.split(" ");
      if(phrase.length > 1){
        queryPhrase.add(s);
      }
      for(String subs: phrase)
        queryString.add(subs);
    }
    for(String s: queryPhrase) queryString.add(s); 
    
    //System.out.println("length: " + length);
    Vector<Integer> freqlist = doc.getFreqList();
    //System.out.println("newq len = " + queryString.size());
    for(String i: queryString) System.out.printf(i+ " ");
    //System.out.println();
    //System.out.println("f len = " + freqlist.size());
    //System.out.println();
    for(int i=0; i<freqlist.size(); ++i){
      //System.out.println("KEn " + freqlist.get(i));
      double score = freqlist.get(i)/(double)length;
      //System.out.println("getting string");
      String s = queryString.get(i);
      //System.out.println("string: " + s);
      if(s.contains(" ")){
        lmprob.add(score);
        //System.out.println("score of " + s + " is " + score );
      }
      else{
        int index = ((IndexerInvertedCompressed) _indexer)._dictionary.get(s);
        //int index = ((IndexerInvertedOccurrence) _indexer)._dictionary.get(s);
        // Smoothing.
        double tf = ((IndexerInvertedCompressed) _indexer)._termCorpusFrequency[index];
        double totalTf = ((IndexerInvertedCompressed) _indexer).totalTermFrequency();
        //double tf = ((IndexerInvertedOccurrence) _indexer)._termCorpusFrequency[index];
        //double totalTf = ((IndexerInvertedOccurrence) _indexer).totalTermFrequency();
        score = lamb * score + (1 - lamb) * ( tf / totalTf );
        lmprob.add(score);
      }
    }
  }

  private double language_model_score(Vector < Double > lmprob) {
    //System.out.println("calculating");
    double lm_score = 0.;
    for (Double score : lmprob) { lm_score += Math.log(score); }
    return lm_score;
  }
}
