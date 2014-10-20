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
    _indexer = (IndexerInvertedCompressed) _indexer;
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    //retrieve relavant docs
    System.out.println("b4 getting next doc");
    Document nextDoc = _indexer.nextDoc(query, -1);
    while(nextDoc != null){
      System.out.println("getting next doc");
      retrieval_results.add(runquery(query, nextDoc));
      nextDoc = _indexer.nextDoc(query, nextDoc._docid);
    }
    Collections.sort(retrieval_results);
    return retrieval_results;  
  }

  public ScoredDocument runquery(Query query, Document doc){
    System.out.println("running query");
    Vector<Double> lmprob = new Vector<Double>();
    createLmprob(doc, query, 0.5, lmprob);
    double score = language_model_score(lmprob);
    return new ScoredDocument(doc, score);
  }

  private void createLmprob(Document d, Query query,
   double lamb, Vector<Double> lmprob) {
    System.out.println("creating lmprob");
    DocumentIndexed doc = (DocumentIndexed) d;
    int length = doc.getDocLength();
    Vector<Integer> freqlist = doc.getFreqList();
    for(int i=0; i<freqlist.size(); ++i){
      double score = freqlist.get(i)/length;
      String s = query._tokens.get(i);
      int index = ((IndexerInvertedCompressed) _indexer)._dictionary.get(s);
      // Smoothing.
      int tf = ((IndexerInvertedCompressed) _indexer)._termCorpusFrequency[index];
      long totalTf = ((IndexerInvertedCompressed) _indexer).totalTermFrequency();
      score = lamb * score + (1 - lamb) * ( tf / totalTf );
      lmprob.add(score);
    }
  }

  private double language_model_score(Vector < Double > lmprob) {
    System.out.println("calculating");
    double lm_score = 0.;
    for (Double score : lmprob) { lm_score += Math.log(score); }
    return lm_score;
  }
}
