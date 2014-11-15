package edu.nyu.cs.cs2580;

import java.util.Vector;

/**
 * @CS2580: implement this class for HW2 to incorporate any additional
 * information needed for your favorite ranker.
 */
public class DocumentIndexed extends Document {
  private static final long serialVersionUID = 9184892508124423115L;
  private Indexer _indexer;
  private Vector<Integer> _freqList = new Vector<Integer>();

  // Constructor.
  public DocumentIndexed(int docid, Indexer indexer) {
    super(docid);
    _indexer = indexer;
  }
  // Set document length.
  public void setLength(int length) { _length = length; }
  // Get document length.
  public int getLength() { return _length; }
  // Set term frequency.
  public void setFreqList(Vector<Integer> freqList) { _freqList = freqList; }
  // Get frequency list.
  public Vector<Integer> getFreqList() { return _freqList; }
}
