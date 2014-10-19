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
  private HashMap< Integer, Vector<Integer>> _body = new HashMap< Integer, Vector<Integer>>();
  private Vector<Integer> _freqList = new Vector<Integer>();

  // constructor.
  public DocumentIndexed(int docid, Indexer indexer) {
    super(docid);
    _indexer = indexer;
  }

  // set body table.
  public void setBody(Vector<Integer> doc) {
    for (int i = 0; i < doc.size(); i++) {
      int ind = doc.get(i);
      if (!_body.containsKey(ind)) {_body.put(ind, new Vector<Integer>()); }
      _body.get(ind).add(i);
    }
  }
  // set document length (to compute language model probabilities).
  public void setLength(Vector<Integer> doc) {
    length = doc.size();
  }
  // set termFrequency.
  public void setTermFrequencyList(Vector<Integer> freqList) { _freqList = freqList; }
  // get term frequency.
  public int getTermFrequency(int i) {
    return _body.get(i).size();
  }
  // get term positions (for phrases).
  public Vector<Integer> getTermPositions(int i) {
    return _body.get(i);
  }
  public long totalTermFrequency(){
	  return _indexer.totalTermFrequency();
  }
}