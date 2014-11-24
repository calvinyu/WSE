package edu.nyu.cs.cs2580;

import java.lang.Math;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3 based on your {@code RankerFavorite}
 * from HW2. The new Ranker should now combine both term features and the
 * document-level features including the PageRank and the NumViews. 
 */
public class RankerComprehensive extends RankerFavorite {
  private Double[] beta = {0.1, 100., 0.01};

  public RankerComprehensive(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    // load page rank
    float[] pageRank;
    try { pageRank = (float[]) _indexer._corpusAnalyzer.load(); }
    catch (Exception e) {
      System.out.println("Could not load page rank...");
      pageRank = new float[_indexer._numDocs];
    }
    // load numviews
    int[] numviews;
    try { numviews = (int[]) _indexer._logMiner.load(); }
    catch (Exception e) {
      System.out.println("Could not load numviews...");
      numviews = new int[_indexer._numDocs];
    }
    Vector<ScoredDocument> retrieval_results = new Vector<ScoredDocument>();
    Document nextDoc = _indexer.nextDoc(query, -1);
    while(nextDoc != null){
      ScoredDocument scoredDoc = runQuery(query, nextDoc);
      // add page rank and numviews
      int docid = nextDoc._docid;
      double score = beta[0] * scoredDoc.getScore();
      float pr = pageRank[docid];
      int nv = numviews[docid];
      scoredDoc.setScore(beta[0] * scoredDoc.getScore() + beta[1] * pr + beta[2] * Math.log(nv + 1));
      scoredDoc.setPagerank(pr);
      scoredDoc.setNumviews(nv);
      retrieval_results.add(scoredDoc);
      nextDoc = _indexer.nextDoc(query, nextDoc._docid);
    }
    Collections.sort(retrieval_results);
    Collections.reverse(retrieval_results);
    Vector<ScoredDocument> topResults = new Vector<ScoredDocument>();
    List<ScoredDocument> list;
    list = retrieval_results.subList(0, Math.min(numResults, retrieval_results.size()));
    for(ScoredDocument s : list) topResults.add(s);
    return topResults;
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
