package edu.nyu.cs.cs2580;

import java.util.Vector;

/**
 * @CS2580: implement this class for HW2 to incorporate any additional
 * information needed for your favorite ranker.
 */
public class DocumentIndexed extends Document {
  private static final long serialVersionUID = 9184892508124423115L;
  private static Indexer _indexer = null;
  private Vector<String> _qv = new Vector<String>(); // Not sure about the API.

  private Vector<Integer> _titleTokens = new Vector<Integer>();
  private Vector<Integer> _bodyFreqs = new Vector<Integer>();

  public DocumentIndexed(int docid, Query q, Indexer indexer) {
    super(docid);
    _qv = q._tokens;
    _indexer = indexer;
  }

  // Get term frequencies in the corpus. These can be used for regularization.
  public static long getTotalTermFrequency() { return _indexer.totalTermFrequency(); }
  public static long getTermFrequency(String s) { return _indexer.corpusTermFrequency(s); }

  // Set and get title tokens.
  public void setTitleTokens(Vector<Integer> titleTokens) { _titleTokens = titleTokens; }
  public Vector<Integer> getTitleTokens() { return _titleTokens; }

  // Set and get term frequencies for a document. This is used to compute the language model probs.
  public void setBodyTokens(Vector<Integer> bodyTokens) {
    for (String qw : _qv) {
      _bodyFreqs.add(_indexer.documentTermFrequency(qw, getUrl()));
    }
  }
  public Vector<Integer> getBodyTokens() { return _bodyFreqs; }

}
