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

  public List<String> suggestUnigram(Query query, int num) {
    System.out.println(query._query);
    System.out.println(num);
    List<Pair<String, Integer>> unigramQueries =
        ((IndexerInvertedCompressed) _indexer).getWordSuggestion(query._query);
    Collections.sort(unigramQueries, new Comparator<Pair<String, Integer>>() {
      @Override
      public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
        if (o1.second > o2.second) return 1;
        else return -1;
      }
    });
    List<String> rankedSuggestions = new ArrayList<String>();
    for (int i = 0; i < Math.min(num, unigramQueries.size()); i++) {
      rankedSuggestions.add(unigramQueries.get(i).first);
    }
    return rankedSuggestions;
  }

  public List<String> suggestNgrams(Query query, int num) {
    System.out.println(query._query);
    System.out.println(num);
    List<Pair<List<Integer>, Integer>> ngramQueries =
        ((IndexerInvertedCompressed) _indexer).getNgramSuggestion(query._tokens);
    Collections.sort(ngramQueries, new Comparator<Pair<List<Integer>, Integer>>() {
      @Override
      public int compare(Pair<List<Integer>, Integer> o1, Pair<List<Integer>, Integer> o2) {
        // TODO: Tune this ranking method
        if (o1.second * o1.first.size() > o2.second * o2.first.size()) return 1;
        else return -1;
      }
    });
    List<String> rankedSuggestions = new ArrayList<String>();
    for (int i = 0; i < Math.min(num, ngramQueries.size()); i++) {
      String extendedQuery = "";
      for (Integer j : ngramQueries.get(i).first) {
        extendedQuery += ((IndexerInvertedCompressed) _indexer)._terms.get(j) + " ";
      }
      rankedSuggestions.add(extendedQuery.substring(0, extendedQuery.length() - 1));
    }
    return rankedSuggestions;
  }

  @Override
  public String 
  expandQuery(Vector<ScoredDocument> docs, String query, int numDocs, int numTerms){
    return "";
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
