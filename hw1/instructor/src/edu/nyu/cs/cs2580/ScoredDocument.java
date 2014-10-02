package edu.nyu.cs.cs2580;

import java.util.Comparator;

// @CS2580: this class should not be changed.
class ScoredDocument implements Comparable {
  public int _did;
  public String _title;
  public double _score;

  ScoredDocument(int did, String title, double score){
    _did = did;
    _title = title;
    _score = score;
  }

  public int compareTo(Object o) {
    double diff = this._score - ((ScoredDocument) o)._score;
    if (diff > 0) { return 1; }
    else if (diff < 0) { return -1; }
    else { return 0; }
  }

  String asString(){
    return new String(
      Integer.toString(_did) + "\t" + _title + "\t" + Double.toString(_score));
  }
}
