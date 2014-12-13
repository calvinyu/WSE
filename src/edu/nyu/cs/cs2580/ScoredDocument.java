package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Document with score.
 * 
 * @author fdiaz
 * @author congyu
 */
class ScoredDocument implements Comparable<ScoredDocument> {
  private edu.nyu.cs.cs2580.Document _doc;
  private double _score;
  private float _pagerank;
  private int _numviews;

  public ScoredDocument(edu.nyu.cs.cs2580.Document doc, double score) {
    _doc = doc;
    _score = score;
  }

  public ScoredDocument(edu.nyu.cs.cs2580.Document doc, double score, float pagerank, int numviews) {
    _doc = doc;
    _score = score;
    _pagerank = pagerank;
    _numviews = numviews;
  }

  public edu.nyu.cs.cs2580.Document getDoc() { return _doc; }
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

  @Override
  public int compareTo(ScoredDocument o) {
    if (this._score == o._score) {
      return 0;
    }
    return (this._score > o._score) ? 1 : -1;
  }

  public String asHtmlResult(String query) throws IOException {
	// append title and hyperlink information
    StringBuffer buf = new StringBuffer();
    String hyperlink1 = "file:///D:/In%20USA/Courses/Web%20Search%20Engines/" + 
    		"Workspace/SearchEngine" + "/data/wiki/" + _doc.getTitle();
    buf.append("<p><a href=" + hyperlink1
          + " target=\"_blank\" style=\"text-decoration: none\">");
    buf.append(_doc.getTitle()).append("</a></p>");
    
    // append snippet
    // snippet style
    buf.append("<span style=\"font-size:14px; width:60%\">");
    // read file
    String prefix = SearchEngine.OPTIONS._corpusPrefix;
    File correspondingFile = new File(prefix + "/" + _doc.getTitle());
    // process file
    Document DOM = Jsoup.parse(correspondingFile, "UTF-8", "");
    String content = DOM.select("#bodyContent").text().toLowerCase();
    // find snippet
	if(content.contains(" " + query + " ")){
		addToBuf(buf, content, query);
	} else if (query.contains(" ")){
		// else should also try to present something if it's a phrase
		String firstPart = query.substring(0, query.lastIndexOf(" "));
		if(content.contains(firstPart)){
			addToBuf(buf, content, firstPart);
		} else {
			String secondPart = query.substring(query.indexOf(" ") + 1);
			if (content.contains(secondPart))
				addToBuf(buf, content, secondPart);
		}
	}
	// wrap and return it
	buf.append("</span>");
    return buf.toString();
  }

  public static void addToBuf(StringBuffer buf, String content, String query) {
	  int index = content.indexOf(query);
	  int start = content.substring(0, index).lastIndexOf(". ");
	  if(start != -1){
		buf.append(content.substring(start + 1, index) + "<b><em>" + query + "</em></b>"
				+ content.substring(index + query.length(), 
						index + query.length() + 40) + "...");
	  } else {
		buf.append("<em><b>" + query + "</b></em>" + content.substring(index + 
				query.length(), index + query.length() + 40)  + " ... ");
	  }
  }

}
