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

import javafx.util.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedCompressed extends Indexer {
  private int SKIPSIZE = 10;
  
  private static HashMap<String, Integer> _dictionary = new HashMap<String, Integer>();
  
  private static Vector<String> _terms = new Vector<String>();
  
  private Vector<Vector<Vector<Pair<Integer, Integer>>>> _postingLists =
      new Vector<Vector<Vector<Pair<Integer, Integer>>>>();
  
  private Vector<Vector<Byte>> _compressedPostingLists = new Vector<Vector<Byte>>();
  
  private Vector<Vector<Pair<Integer, Integer>>> _skipLists =
      new Vector<Vector<Pair<Integer, Integer>>>();
  
  private Map<Integer, Integer> _termDocFrequency = new HashMap<Integer, Integer>();

  private Map<Integer, Integer> _termCorpusFrequency = new HashMap<Integer, Integer>();

  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
  
  
  public IndexerInvertedCompressed(Options options) {
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

		//compress the postingList
		for (int i=0; i<_postingLists.size()-1;i++){
			_compressedPostingLists.set(i, encodePosList(_postingLists.get(i), _skipLists.get(i)));
		}
		//delete the postingList
		_postingLists.clear();
		
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
	  Document DOM =  Jsoup.parse(file, "UTF-8", "");
	  String content = DOM.select("#bodyContent").text();

	  DocumentIndexed doc = new DocumentIndexed(_documents.size(), this);
	  HashSet<Integer> uniqueTerms = new HashSet<Integer>();
	  Vector<Integer> termVector = new Vector<Integer>();
	  updateStatistics(content, uniqueTerms, termVector);

	  doc.setTitle(file.getName());
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
	    IndexerInvertedCompressed loaded = (IndexerInvertedCompressed) reader.readObject();

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
   * In HW2, you should be using {@link DocumentIndexed}
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
        if (words.length > 1) phrases.add(token);
        Pair<Integer, Vector<Integer>> idPosList = nextPosList(word, docid);
        if (idPosList == null) return null;
        nextList.add(idPosList.getKey());
        freqList.add(idPosList.getValue().size());
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
    Vector<Vector<Integer>> phraseList = new Vector<Vector<Integer>>();
    for (String word : phrase.split(" ")) {
      phraseList.add(nextPosList(word, docid - 1).getValue());
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

  private Pair<Integer, Vector<Integer>> nextPosList(String word, int docid) {
    if (!_dictionary.containsKey(word)) return null;
    Vector<Pair<Integer, Integer>> docList = _skipLists.get(_dictionary.get(word));
    if (docList.get(docList.size() - 1).getKey() <= docid) return null;
    if (docList.get(0).getKey() > docid) {
      return decodePosList(_compressedPostingLists.get(_dictionary.get(word)), 0, docid);
    }
    // Returns the index of the skip pointer.
    int startPos = binarySearch(docList, 0, docList.get(docList.size() - 1).getKey(), docid);
    return decodePosList(_compressedPostingLists.get(_dictionary.get(word)), startPos, docid);
  }

  private int binarySearch(Vector<Pair<Integer, Integer>> docList, int low, int high, int docid) {
    while (high - low > 1) {
      int mid = (high - low) / 2;
      if (docList.get(mid).getKey() < docid) { low = mid; }
      else { high = mid; }
    }
    return low;
  }

  private int nextPos(Vector<Integer> posList, int pos) {
    if (posList.get(posList.size() - 1) <= pos) return Integer.MAX_VALUE;
    if (posList.get(0) > pos) return Integer.MAX_VALUE;
    return posList.get(binarySearchPos(posList, 0, posList.get(0), pos));
  }

  private int binarySearchPos(Vector<Integer> posList, int low, int high, int pos) {
    while (high - low > 1) {
      int mid = (high - low) / 2;
      if (posList.get(mid) < pos) { low = mid; }
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

  /**
   * @CS2580: Implement this for bonus points.
   */
  @Override
  public int documentTermFrequency(String term, String url) {
    return 0;
  }

  private Vector<Byte> encodePosList(Vector<Vector<Pair<Integer, Integer>>> docList,
                                     Vector<Pair<Integer, Integer>> skipPointer) {
    Vector<Integer> output = new Vector<Integer>();
    int pointer = 0;
    int prevId = 0;
    int counter = 0;
    for (int i = 0; i < docList.size(); i++) {
      Vector<Pair<Integer, Integer>> posList = docList.get(i);
      int currentId = posList.get(0).getKey();
      output.add(currentId - prevId);
      prevId = currentId;
      output.add(posList.size());
      if (counter % SKIPSIZE == 0) {
        skipPointer.add(new Pair<Integer, Integer>(currentId, pointer));
      }
      int prevPos = 0;
      for (Pair<Integer, Integer> pos : posList) {
        output.add(pos.getValue() - prevPos);
        prevPos = pos.getValue();
      }
      pointer += 2 + posList.size();
      counter++;
    }
    return encode(output);
  }

  private Vector<Byte> encode(Vector<Integer> input) {
    Vector<Byte> output = new Vector<Byte>();
    for (int i : input) {
      while (i >= 128) {
        output.add((byte) (i & 0x7F));
        i >>>= 7;
      }
      output.add((byte) (i | 0x80));
    }
    return output;
  }

  private int[] decode(Vector<Byte> input, int startPos) {
    int i = startPos;
    int result = ((int) input.get(i) & 0x7F);
    while (((int) input.get(i) & 0x80) == 0) {
      i += 1;
      int unsignedByte = (int) input.get(i) & 0x7F;
      result |= (unsignedByte << (7 * (i - startPos)));
    }
    int[] posResult = {i + 1, result};
    return posResult;
  }

  private Pair<Integer, Vector<Integer>> decodePosList(Vector<Byte> input, int startPos, int docid) {
    Vector<Integer> posList = new Vector<Integer>();
    int pos = startPos;
    while (decode(input, pos)[1] <= docid) {
      int[] posId = decode(input, pos);
      int[] posSize = decode(input, posId[0]);
      pos = posSize[0];
      for (int i = 0; i < posSize[1]; i++) {
        int[] posPos = decode(input, pos);
        pos = posPos[0];
      }
    }
    int[] posId = decode(input, pos);
    int[] posSize = decode(input, posId[0]);
    int[] prevPos = {posSize[0], 0};
    for (int i = 0; i < posSize[1]; i++) {
      int[] posPos = decode(input, prevPos[0]);
      prevPos[0] = posPos[0];
      prevPos[1] = posPos[1] + prevPos[1];
      posList.add(prevPos[1]);
    }
    return new Pair<Integer, Vector<Integer>>(posId[1], posList);
  }
  
  public String getTermByIndex(int index){
	  return _terms.get(index);
  }
  
  public int getIndexByTerm(String s){
	  return _dictionary.get(s);
  }
  
  public int getTermCorpusFrequency(int index){
    return _termCorpusFrequency.get(index);
  }
}
