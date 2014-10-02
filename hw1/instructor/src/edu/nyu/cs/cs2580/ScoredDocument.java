package edu.nyu.cs.cs2580;

import java.util.Comparator;

// @CS2580: this class should not be changed.
class ScoredDocument implements Comparator {
  public int _did;
  public String _title;
  public double _score;

  ScoredDocument(int did, String title, double score){
    _did = did;
    _title = title;
    _score = score;
  }

  public int compare(Object o1, Object o2) {
    double diff = ((ScoredDocument) o1)._score - ((ScoredDocument) o2)._score;
    if (diff > 0) { return 1; }
    else if (diff < 0) { return -1; }
    else { return 0; }
  }

  String asString(){
    return new String(
      Integer.toString(_did) + "\t" + _title + "\t" + Double.toString(_score));
  }
}
