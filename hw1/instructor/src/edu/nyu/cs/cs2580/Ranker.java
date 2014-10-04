package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Collections;
import java.lang.Math;

class Ranker {
  // Test for github
  private Index _index;
  private Double[] beta = {200., 0.05, 0.005, 0.000005};

  public Ranker(String index_source){
    _index = new Index(index_source);
  }

  @SuppressWarnings("unchecked")
  public Vector < ScoredDocument > runquery(String query, String ranker_type){
    Vector < ScoredDocument > retrieval_results = new Vector < ScoredDocument > ();
    for (int i = 0; i < _index.numDocs(); ++i){
      retrieval_results.add(runquery(query, i, ranker_type));
    }
    Collections.sort(retrieval_results);
    return retrieval_results;
  }

  public ScoredDocument runquery(String query, int did, String ranker_type){
    // Build query vector
    Scanner s = new Scanner(query);
    Vector < String > qv = new Vector < String > ();
    while (s.hasNext()){
      String term = s.next();
      qv.add(term);
    }

    // Get the document vector. For hw1, you don't have to worry about the
    // details of how index works.
    Document d = _index.getDoc(did);
    Vector < String > bv = d.get_body_vector();

    HashMap < String, Double > d_tfidf;
    HashMap < String, Double > q_tfidf;
    HashMap < String, Double > lmprob;
    double score = 0.;
    if (ranker_type.equals("cosine")) {
      d_tfidf = createTfidf(bv);
      q_tfidf = createTfidf(qv);
      score = cosine_score(d_tfidf, q_tfidf);
    } else if (ranker_type.equals("QL")){
      lmprob = createLmprob(bv, qv, 0.5);
      score = language_model_score(qv, lmprob);
    } else if (ranker_type.equals("phrase")) {
      score = bigram_score(bv, qv);
    } else if (ranker_type.equals("numviews")) {
      score = d.get_numviews();
    } else {
      // Return linear score by default
      d_tfidf = createTfidf(bv);
      q_tfidf = createTfidf(qv);
      lmprob = createLmprob(bv, qv, 0.5);
      score = beta[0] * cosine_score(d_tfidf, q_tfidf) +
          beta[1] * language_model_score(qv, lmprob) +
          beta[2] * bigram_score(bv, qv) + beta[3] * d.get_numviews();
    }

    return new ScoredDocument(did, d.get_title_string(), score);
  }
  private HashMap <String, Double> createTfidf(Vector < String > v) {
    HashMap < String, Double > tfidf = new HashMap < String, Double >();
    // Create term frequency map.
    for (String word : v) {
      if (!tfidf.containsKey(word)) tfidf.put(word, 0.);
      tfidf.put(word, tfidf.get(word) + 1.);
    }
    // Create tfidf map.
    double l2 = 0.;
    for (String s : tfidf.keySet()) {
      double idf = Math.log((_index.numDocs() * 1.) / Document.documentFrequency(s));
      double tmp_tfidf = tfidf.get(s) * idf;
      tfidf.put(s, tmp_tfidf);
      l2 += tmp_tfidf * tmp_tfidf;
    }
    // L2-normalize the tfidf map.
    for (String s : tfidf.keySet()) { tfidf.put(s, tfidf.get(s) / l2); }

    return tfidf;
  }

  private HashMap <String, Double> createLmprob(Vector < String > bv, Vector < String > qv, double lamb) {
    HashMap < String, Double > lmprob = new HashMap < String, Double >();
    // Create term frequency map.
    for (String s : bv) {
      if (!lmprob.containsKey(s)) lmprob.put(s, 0.);
      lmprob.put(s, lmprob.get(s) + 1.);
    }
    // Create language model probability map.
    for (String s : lmprob.keySet()) {
      lmprob.put(s, lmprob.get(s) / bv.size());
    }
    // Add query words to language model probability map.
    for (String s : qv) {
      if (!lmprob.containsKey(s)) lmprob.put(s, 0.);
    }
    // Smoothing.
    for (String s : lmprob.keySet()) {
      lmprob.put(s, lamb * lmprob.get(s) + ((1. - lamb) * Document.termFrequency(s)) / Document.termFrequency());
    }
    return lmprob;
  }

  private double cosine_score(HashMap < String, Double > d_tfidf, HashMap < String, Double > q_tfidf) {
    double cos_score = 0.;
    for (String word : q_tfidf.keySet()) {
      if (d_tfidf.containsKey(word)) { cos_score += d_tfidf.get(word) * q_tfidf.get(word); }
    }
    return cos_score;
  }

  private double language_model_score(Vector < String > qv, HashMap < String, Double > lmprob) {
    double lm_score = 0.;
    for (String word : qv) { lm_score += Math.log(lmprob.get(word)); }
    return lm_score;
  }

  private double bigram_score(Vector < String > bv, Vector < String > qv) {
    double qb_score = 0.;
    if (qv.size() == 1) {
      for (String b : bv) {
        if (qv.get(0).equals(b)) { qb_score += 1.; }
      }
    } else {
      for (int i = 0; i < qv.size() - 1; i++) {
        for (int j = 0; j < bv.size() - 1; j++) {
          if (qv.get(i).equals(bv.get(j)) && qv.get(i + 1).equals(bv.get(j + 1))) { qb_score += 1.; }
        }
      }
    }
    return qb_score;
  }
}
