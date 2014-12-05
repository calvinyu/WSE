package edu.nyu.cs.cs2580;

import java.util.*;

/**
 * Document with score.
 * 
 * @author fdiaz
 * @author congyu
 */
class ScoredDocument implements Comparable<ScoredDocument> {
  private Document _doc;
  private double _score;
  private float _pagerank;
  private int _numviews;

  public ScoredDocument(Document doc, double score) {
    _doc = doc;
    _score = score;
  }

  public ScoredDocument(Document doc, double score, float pagerank, int numviews) {
    _doc = doc;
    _score = score;
    _pagerank = pagerank;
    _numviews = numviews;
  }

  public Document getDoc() { return _doc; }
  public double getScore() { return _score; }
  public void setScore(double score) { _score = score; }
  public void setPagerank(float pagerank) { _pagerank = pagerank; }
  public void setNumviews(int numviews) { _numviews = numviews; }

  public String asTextResult() {
    StringBuffer buf = new StringBuffer();
    buf.append(_doc._docid).append("\t");
    buf.append(_doc.getTitle()).append("\t");
    buf.append(_score).append("\t");
    buf.append(_pagerank).append("\t");
    buf.append(_numviews).append("\t");
    return buf.toString();
  }

  /**
   * @CS2580: Student should implement {@code asHtmlResult} for final project.
   */
  public String asHtmlResult() {
    return "";
  }

  @Override
  public int compareTo(ScoredDocument o) {
    if (this._score == o._score) {
      return 0;
    }
    return (this._score > o._score) ? 1 : -1;
  }

  public String asHtmlResult(String query) {
  StringBuffer buf = new StringBuffer();
  Date dd = new Date();
  String sessionid = "S-"+String.valueOf(dd.getTime()).substring(5);
  String hyperlink1 = "sessionid=" + sessionid + "&action=click&query="
    + query + "&docid=" + _doc._docid;
  buf.append("<p style=\"font-size:14px; margin-top:14px\">"
      + "<a href=\"http://localhost:25813/log?" + hyperlink1
          + "\" target=\"_blank\" style=\"text-decoration: none\">");
  buf.append(_doc.getTitle()).append("</a></p>");
  return buf.toString();
  }
}