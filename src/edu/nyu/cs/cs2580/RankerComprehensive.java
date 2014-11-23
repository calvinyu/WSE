package edu.nyu.cs.cs2580;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3 based on your {@code RankerFavorite}
 * from HW2. The new Ranker should now combine both term features and the
 * document-level features including the PageRank and the NumViews. 
 */
public class RankerComprehensive {
  /*
  private Double[] beta = {1., 0.01, 0.0001};

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
      scoredDoc.setScore(beta[0] * scoredDoc.getScore() + beta[1] * pr + beta[2] * nv);
      scoredDoc.setPagerank(pr);
      scoredDoc.setNumviews(nv);
      retrieval_results.add(scoredDoc);
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

  private String expandQuery(Vector<ScoredDocument> docs, String query,
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
        // if exists, add the count, else create an entry
        if(dictionary.containsKey(term)){
          dictionary.put(term, dictionary.get(term)+count);
        } else dictionary.put(term, count);
      }
    }

    // get top terms
    dictionary = sortByValues(dictionary);
    Set<Entry<String, Integer>> set = dictionary.entrySet();
    Iterator<Entry<String, Integer>> iterator = set.iterator();
    int totalFrequency = 0;
    for (int i=0; i<numTerms; i++) {
      Entry<String,Integer> me = iterator.next();
      totalFrequency += me.getValue();
    }

    // write results into a string to return to user
    String result = "";
    iterator = set.iterator();
    for (int i=0; i<numTerms; i++) {
      Entry<String,Integer> me = iterator.next();
      result += (me.getKey() + "\t" + (double) me.getValue()/totalFrequency + "\n");
    }
    return result;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private HashMap<String,Integer> sortByValues(HashMap map) { 
    List list = new LinkedList(map.entrySet());
    // Defined Comparator here
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
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
  }*/
}
