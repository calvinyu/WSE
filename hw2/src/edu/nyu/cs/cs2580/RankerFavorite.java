package edu.nyu.cs.cs2580;

import java.util.Vector;

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
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    //retrieve relavant docs
    Document nextDoc = _indexer.nextDoc(query, -1);// not sure abou api
    while(nextDoc != null){
      retrieval_results.add(runquery(query, nextDoc));
      nextDoc = _indexer.nextDoc(query, nextDoc._docid);
    }
    Collections.sort(retrieval_results);
    return retrieval_results;  
  }

  public ScoredDocument runquery(Query query, Document doc){
    Vector < Double > lmprob;
    double score = 0.;
    lmprob = createLmprob(doc, query, 0.5);
    score = language_model_score(lmprob);
    return new ScoredDocument(doc, score);
  }

  private Vector <Double> createLmprob(Document d, Query query, double lamb) {
    DocumentIndexed doc = (DocumentIndexed) d;
    Vector < Double > lmprob = new Vector < Double >();
    int length = doc.getDocLength();
    // Build query vector, it should support phrase query.
    Vector < Integer > bv = doc.getBodyTokens();
    Vector < String > qv = query._tokens;
    for ( int i = 0; i < bv.size(); ++i ) {
      double score = bv.get(i);
      String s = qv.get(i);
      // Add query words to language model probability map.
      score /= length;
      // Smoothing.
      long tf = DocumentIndexed.getTermFrequency(s);
      long totalTf = DocumentIndexed.getTotalTermFrequency();
      score = lamb * score + (1 - lamb) * ( tf / totalTf );
      lmprob.add(score);
    }
    return lmprob;
  }

  private double language_model_score(Vector < Double > lmprob) {
    double lm_score = 0.;
    for (Double score : lmprob) { lm_score += Math.log(score); }
    return lm_score;
  }
}
