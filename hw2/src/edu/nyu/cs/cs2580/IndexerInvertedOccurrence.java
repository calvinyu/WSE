package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javafx.util.Pair;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedOccurrence extends Indexer {

  private Map<String, Integer> _dictionary = new HashMap<String, Integer>();
  private Vector<String> _terms = new Vector<String>();

  private Vector<Vector<Vector<Pair<Integer, Integer>>>> _postingLists =
      new Vector<Vector<Vector<Pair<Integer, Integer>>>>();

  public IndexerInvertedOccurrence(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
  }

  @Override
  public void loadIndex() throws IOException, ClassNotFoundException {
  }

  @Override
  public Document getDoc(int docid) {
    return null;
  }

  /**
   * In HW2, you should be using {@link DocumentIndexed}.
   */
  @Override
  public DocumentIndexed nextDoc(Query query, int docid) {
    // Check if all the query words exist
    Vector<Integer> nextList = new Vector<Integer>();
    Vector<Integer> freqList = new Vector<Integer>();
    Vector<String> phrases = new Vector<String>();
    for (String token : query._tokens) {
      String[] words = token.split(" ");
      for (String word : words) {
        if (!_dictionary.containsKey(word)) return null;
        if (words.length > 1) phrases.add(token);
        int nextIdx = next(word, docid);
        if (nextIdx == Integer.MAX_VALUE) return null;
        else {
          nextList.add(_postingLists.get(_dictionary.get(word)).get(nextIdx).get(0).getKey());
          freqList.add(_postingLists.get(_dictionary.get(word)).get(nextIdx).size());
        }
      }
    }
    int maxId = Collections.max(nextList);
    if (maxId == Collections.min(nextList)) {
      // check if the document contains the phrases
      for (String phrase : phrases) {
        if (!phraseSearch(phrase, maxId)) return nextDoc(query, maxId - 1);
      }
      // query is satisfied!!!
      DocumentIndexed doc = (DocumentIndexed) getDoc(maxId);
      doc.setTermFrequencyList(freqList);
      return doc;
    }
    return nextDoc(query, maxId - 1);
  }

  private boolean phraseSearch(String phrase, int docid) {
    Vector<Vector<Pair<Integer, Integer>>> phraseList = new Vector<Vector<Pair<Integer, Integer>>>();
    for (String word : phrase.split(" ")) {
      phraseList.add(_postingLists.get(_dictionary.get(word)).get(next(word, docid - 1)));
    }
    int prevPos = -1;
    int pos = -1;
    while (pos < Integer.MAX_VALUE) {
      boolean result = true;
      for (int i = 0; i < phraseList.size(); i++) {
        if (i == 0) prevPos = nextPos(phraseList.get(i), prevPos);
        else {
          pos = nextPos(phraseList.get(i), prevPos);
          if (pos != prevPos - 1) {
            prevPos = pos - i;
            result = false;
            break;
          }
        }
      }
      if (result) return true;
    }
    return false;
  }

  private int next(String word, int docid) {
    if (!_dictionary.containsKey(word)) return Integer.MAX_VALUE;
    Vector<Vector<Pair<Integer, Integer>>> docList = _postingLists.get(_dictionary.get(word));
    if (docList.get(docList.size() - 1).get(0).getKey() <= docid) return Integer.MAX_VALUE;
    if (docList.get(0).get(0).getKey() > docid) return 0;
    // Returns the index for the docid. This is different from next() in IndexInvertedDocOnly.
    return binarySearch(docList, 0, docList.get(docList.size() - 1).get(0).getKey(), docid);
  }

  private int binarySearch(Vector<Vector<Pair<Integer, Integer>>> docList, int low, int high, int docid) {
    while (high - low > 1) {
      int mid = (high - low) / 2;
      if (docList.get(mid).get(0).getKey() < docid) { low = mid; }
      else { high = mid; }
    }
    return high;
  }

  private int nextPos(Vector<Pair<Integer, Integer>> posList, int pos) {
    if (posList.get(posList.size() - 1).getValue() <= pos) { return Integer.MAX_VALUE; }
    if (posList.get(0).getValue() > pos) { return Integer.MAX_VALUE; }
    return posList.get(binarySearchPos(posList, 0, posList.get(0).getValue(), pos)).getValue();
  }

  private int binarySearchPos(Vector<Pair<Integer, Integer>> posList, int low, int high, int pos) {
    while (high - low > 1) {
      int mid = (high - low) / 2;
      if (posList.get(mid).getValue() < pos) { low = mid; }
      else { high = mid; }
    }
    return high;
  }

  @Override
  public int corpusDocFrequencyByTerm(String term) {
    return 0;
  }

  @Override
  public int corpusTermFrequency(String term) {
    return 0;
  }

  @Override
  public int documentTermFrequency(String term, String url) {
    SearchEngine.Check(false, "Not implemented!");
    return 0;
  }
}
