package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
  
  private Map<Integer, Integer> _termDocFrequency = new HashMap<Integer, Integer>();

  private Map<Integer, Integer> _termCorpusFrequency = new HashMap<Integer, Integer>();

  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
  
  public IndexerInvertedOccurrence(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
	  String corpusFile = _options._corpusPrefix + "/wiki";
	    System.out.println("Construct index from: " + corpusFile);
	    File folder = new File(corpusFile);
	    File[] listOfFiles = folder.listFiles();
	    for(File file : listOfFiles){
	      processDocument(file);
	    }
	    System.out.println(
	        "Indexed " + Integer.toString(_numDocs) + " docs with " +
	            Long.toString(_totalTermFrequency) + " terms.");

	    String indexFile = _options._indexPrefix + "/corpus.idx";
	    System.out.println("Store index to: " + indexFile);
	    ObjectOutputStream writer =
	        new ObjectOutputStream(new FileOutputStream(indexFile));
	    writer.writeObject(this);
	    writer.close();
	  }

  private void processDocument(File file) throws IOException {
	  Document DOM = Jsoup.parse(file, "UTF-8", "");
	  String content = DOM.select("#bodyContent").text();

	  DocumentIndexed doc = new DocumentIndexed(_documents.size(), this);
	  HashSet<Integer> uniqueTerms = new HashSet<Integer>();
	  Vector<Integer> termVector = new Vector<Integer>();
	  updateStatistics(content, uniqueTerms, termVector);

	  doc.setTitle(file.getName());
	  doc.setBody(termVector);
	  doc.setLength(termVector);
	  _documents.add(doc);
	  ++_numDocs;

	  //all words that appear in this doc,
	  for (Integer idx : uniqueTerms) {
	    _termDocFrequency.put(idx, _termDocFrequency.get(idx) + 1);
	  }
	}
	  
  private void updateStatistics(String content, HashSet<Integer> uniqueTerms, Vector<Integer> termVector){
   Scanner s = new Scanner(content);  // Uses white space by default.
   int offset = 0;
   while (s.hasNext()) {
     String word = s.next();
     int idx = -1;
     if (_dictionary.containsKey(word)) {
       idx = _dictionary.get(word);
     } else {
     idx = _terms.size();
     _terms.add(word);
     _postingLists.add(new Vector<Vector<Pair<Integer, Integer>>>());
     _dictionary.put(word, idx);
     _termCorpusFrequency.put(idx, 0);
     _termDocFrequency.put(idx, 0);
     }
   // check if the postingList already has this docid, if no then add.
     int docid = _documents.size();
     
     // find the corresponding Vector of this index of term
     Vector<Vector<Pair<Integer, Integer>>> temp = _postingLists.get(idx);
     // if the docid has been inserted, get the vector, add the pair and then set back
     if(temp.get(temp.size()-1).get(0).getKey() == docid){
    	 Vector<Pair <Integer, Integer>> cur = temp.get(temp.size()-1);
    	 cur.add(new Pair<Integer, Integer>(docid, offset));
    	 temp.set(temp.size()-1, cur);
     } else { // if docid is new, create a vector of pair, add the pair in the vector, and add the vector
    	 Vector<Pair <Integer, Integer>> cur = new Vector<Pair <Integer, Integer>>();
    	 cur.add(new Pair<Integer, Integer>(docid, offset));
    	 temp.add(cur);
     }
     _postingLists.set(idx, temp);
     uniqueTerms.add(idx);
     termVector.add(idx);
     _termCorpusFrequency.put(idx, _termCorpusFrequency.get(idx) + 1);
     ++_totalTermFrequency;
     ++offset;
   }
  }

  @Override
   public void loadIndex() throws IOException, ClassNotFoundException {
	    String indexFile = _options._indexPrefix + "/corpus.idx";
	    System.out.println("Load index from: " + indexFile);

	    ObjectInputStream reader =
	        new ObjectInputStream(new FileInputStream(indexFile));
	    IndexerInvertedOccurrence loaded = (IndexerInvertedOccurrence) reader.readObject();

	    this._documents = loaded._documents;
	    // Compute numDocs and totalTermFrequency b/c Indexer is not serializable.
	    this._numDocs = _documents.size();
	    for (Integer freq : loaded._termCorpusFrequency.values()) {
	      this._totalTermFrequency += freq;
	    }
	    this._postingLists = loaded._postingLists;
	    this._dictionary = loaded._dictionary;
	    this._terms = loaded._terms;
	    this._termCorpusFrequency = loaded._termCorpusFrequency;
	    this._termDocFrequency = loaded._termDocFrequency;
	    reader.close();

	    System.out.println(Integer.toString(_numDocs) + " documents loaded " +
	        "with " + Long.toString(_totalTermFrequency) + " terms!");
	  }

  @Override
  public DocumentIndexed getDoc(int docid) {
    return _documents.get(docid);
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
    return _postingLists.get(_dictionary.get(term)).size();
  }

  @Override
  public int corpusTermFrequency(String term) {
    return _termCorpusFrequency.get(_dictionary.get(term));
  }

  @Override
  public int documentTermFrequency(String term, String url) {
    SearchEngine.Check(false, "Not implemented!");
    return 0;
  }
}
