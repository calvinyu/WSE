package edu.nyu.cs.cs2580;

import java.util.Vector;
import java.util.HashMap;

/**
 * @CS2580: implement this class for HW2 to incorporate any additional
 * information needed for your favorite ranker.
 */
public class DocumentIndexed extends Document {
  private static final long serialVersionUID = 9184892508124423115L;
  private Indexer _indexer;
  private Vector<Integer> _freqList = new Vector<Integer>();

  // constructor.
  public DocumentIndexed(int docid, Indexer indexer) {
    super(docid);
    _indexer = indexer;
  }
  // set document length (to compute language model probabilities).
  public void setLength(Vector<Integer> doc) {
    length = doc.size();
  }
  // set termFrequency.
  public void setTermFrequencyList(Vector<Integer> freqList) { _freqList = freqList; }
  // get term frequency.
  public int getTermFrequency(int i) {
    return 0;
  }
  
  public long totalTermFrequency(){
	  return _indexer.totalTermFrequency();
  }
}
