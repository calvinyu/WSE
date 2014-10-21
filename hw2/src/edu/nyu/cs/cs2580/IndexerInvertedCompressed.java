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

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedCompressed extends Indexer {
	public Map<String, Integer> _dictionary = new HashMap<String, Integer>();
	  
	private Vector<String> _terms = new Vector<String>();

	private Stopwords _stopwords = new Stopwords();

	private short[][] _postingsList;

	private short[][] _docLists;

	private short[][] _docTermFrequency;
	
	private Byte[][] _compressedList;

	private int[] _termDocFrequency;

	public int[] _termCorpusFrequency;

	private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();
  
  public IndexerInvertedCompressed(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
	    Vector<Integer> tmpTermDocFrequency = new Vector<Integer>();
	    Vector<Integer> tmpTermCorpusFrequency = new Vector<Integer>();
	    String corpusFile = _options._corpusPrefix + "/wiki";
	    System.out.println("Construct index from: " + corpusFile);
	    File folder = new File(corpusFile);
	    File[] listOfFiles = folder.listFiles();
	    // First run: Determine the size of the array, initialize the attributes
	    System.out.println("First round");
	    int cnt = 0;
	    for (File file : listOfFiles) {
	      cnt++;
	      if(cnt%100==0) System.out.println(cnt);
	      processDocument(file, tmpTermDocFrequency, tmpTermCorpusFrequency);
	    }
	    // Put tmp values into arrays
	    _termDocFrequency = new int[_terms.size()];
	    _termCorpusFrequency = new int[_terms.size()];
	    for (int i = 0; i < _terms.size(); i++) {
	      _termDocFrequency[i] = tmpTermDocFrequency.get(i);
	      _termCorpusFrequency[i] = tmpTermCorpusFrequency.get(i);
	    }
	    tmpTermDocFrequency.clear();
	    tmpTermCorpusFrequency.clear();
	    // Initialize the document list
	    _docLists = new short[_terms.size()][];
	    _docTermFrequency = new short[_terms.size()][];
	    _postingsList = new short[_terms.size()][];
	    for (int i = 0; i < _terms.size(); i++) {
	      _docLists[i] = new short[_termDocFrequency[i]];
	      _docTermFrequency[i] = new short[_termDocFrequency[i]];
	      _postingsList[i] = new short[_termCorpusFrequency[i]];
	      _termDocFrequency[i] = 0;
	      _termCorpusFrequency[i] = 0;
	    }

	    // Second run : Create the postings list
	    cnt = 0;
	    for (File file : listOfFiles) {
	      cnt++;
	      if(cnt%100==0) System.out.println(cnt);
	      createPostingsList(file);
	    }
	    
	    // Compress three lists into compressed vector
	    Vector<Vector<Byte>> _compressedVector = new Vector<Vector<Byte>>();
	    for(int i=0;i<_terms.size();i++){
	    	_compressedVector.set(i, encodePosList(_docLists[i], 
	    			_termDocFrequency[i], _postingsList[i], 
	    			_termCorpusFrequency[i], _docTermFrequency[i], _termDocFrequency[i]));
	    	_compressedList[i] = new Byte[_compressedVector.get(i).size()];
	    	for(int j=0;j<_compressedVector.get(i).size();j++){
	    		_compressedList[i][j] = _compressedVector.get(i).get(j);
	    	}
	    }
	    
	    // delete useless lists
	    _compressedVector.clear(); _docLists = null;
	    _docTermFrequency = null; _postingsList = null;
	    
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

	  private void processDocument(File file, Vector<Integer> docFrequency, Vector<Integer> termFrequency) throws IOException {
	    Document DOM = Jsoup.parse(file, "UTF-8", "");
	    String content = DOM.select("#bodyContent").text().toLowerCase();
	    content = Remove.remove(content);

	    updateStatistics(content, docFrequency, termFrequency);
	  }

	  private void updateStatistics(String content, Vector<Integer> docFrequency, Vector<Integer> termFrequency){
	    HashSet<Integer> uniqueTerms = new HashSet<Integer>();
	    Scanner s = new Scanner(content);  // Uses white space by default.
	    while (s.hasNext()) {
	      String word = s.next();
	      if (_stopwords.wordsList.contains(word) || word.length() < 3 || word.length() > 20) continue;
	      int idx;
	      if (_dictionary.containsKey(word)) {
	        idx = _dictionary.get(word);
	      } else {
	        idx = _terms.size();
	        _terms.add(word);
	        docFrequency.add(0);
	        termFrequency.add(0);
	        _dictionary.put(word, idx);
	      }
	      // for each term add its count.
	      termFrequency.set(idx, termFrequency.get(idx) + 1);
	      uniqueTerms.add(idx);
	      _totalTermFrequency++;
	    }
	    s.close();
	    for (int i : uniqueTerms) {
	      docFrequency.set(i, docFrequency.get(i) + 1);
	    }
	  }

	  private void createPostingsList(File file) throws IOException {
	    Document DOM = Jsoup.parse(file, "UTF-8", "");
	    String content = DOM.select("#bodyContent").text().toLowerCase();
	    content = Remove.remove(content);

	    HashMap<Integer, Vector<Integer>> uniqueTerms = new HashMap<Integer, Vector<Integer>>();
	    int docid = _documents.size();
	    DocumentIndexed doc = new DocumentIndexed(_documents.size(), this);
	    doc.setTitle(file.getName());
	    _documents.add(doc);
	    _numDocs++;

	    Scanner s = new Scanner(content);  // Uses white space by default.
	    int offset = Short.MIN_VALUE;
	    while (s.hasNext()) {
	      String word = s.next();
	      if (_stopwords.wordsList.contains(word) || word.length() < 3 || word.length() > 20) continue;
	      int idx = _dictionary.get(word);
	      if (!uniqueTerms.containsKey(idx)) uniqueTerms.put(idx, new Vector<Integer>());
	      uniqueTerms.get(idx).add(offset);
	      offset++;
	    }
	    s.close();
	    for (int i : uniqueTerms.keySet()) {
	      _docLists[i][_termDocFrequency[i]] = (short) docid;
	      _docTermFrequency[i][_termDocFrequency[i]] = (short) uniqueTerms.get(i).size();
	      _termDocFrequency[i]++;
	      for (int j : uniqueTerms.get(i)) {
	        _postingsList[i][_termCorpusFrequency[i]] = (short) j;
	        _termCorpusFrequency[i]++;
	      }
	    }
	    if (offset > 32767) System.out.println(docid + " " + offset);
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
	    for (Integer freq : loaded._termCorpusFrequency) {
	      this._totalTermFrequency += freq;
	    }
	    //this._postingsList = loaded._postingsList;
	    this._compressedList = loaded._compressedList;
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
    Vector<Integer> nextIdxList = new Vector<Integer>();
    Vector<Short> nextList = new Vector<Short>();
    Vector<Integer> freqList = new Vector<Integer>();
    Vector<String> phrases = new Vector<String>();
    for (String token : query._tokens) {
      String[] words = token.split(" ");
      for (String word : words) {
        if (!_dictionary.containsKey(word)) return null;
        int nextIdx = next(word, docid);
        if (words.length > 1) {
          phrases.add(token);
          nextIdxList.add(nextIdx);
        }
        if (nextIdx == Integer.MAX_VALUE) return null;
        else {
          nextList.add(_docLists[_dictionary.get(word)][nextIdx]);
          freqList.add((int) _docTermFrequency[_dictionary.get(word)][nextIdx]);
        }
      }
    }
    int maxId = (int)Collections.max(nextList);
    if (maxId == Collections.min(nextList)) {
      // check if the document contains the phrases
      Vector<Integer> phraseFreqs = new Vector<Integer>();
      for (String phrase : phrases) {
        int freq = phraseSearch(phrase, nextIdxList);
        if (freq == 0) { return nextDoc(query, maxId); }
        phraseFreqs.add(freq);
      }
      // query is satisfied!!!
      DocumentIndexed doc = getDoc(maxId);
      freqList.addAll(phraseFreqs);
      doc.setTermFrequencyList(freqList);
      return doc;
    }
    return nextDoc(query, maxId - 1);
  }
  
  private int phraseSearch(String phrase, Vector<Integer> nextIdx) {
    Vector<short[]> phraseList = new Vector<short[]>();
    String[] phraseWords = phrase.split(" ");
    for (int i = 0; i < phraseWords.length; i++) {
      String word = phraseWords[i];
      phraseList.add(createPosList(word, nextIdx.get(i), createOffsets(word)));
    }
    int prevPos = (int) Short.MIN_VALUE - 1;
    int pos = (int) Short.MIN_VALUE - 1;
    int freqCount = 0;
    while (pos < Integer.MAX_VALUE) {
      for (int i = 0; i < phraseList.size(); i++) {
        if (i == 0) {
          prevPos = nextPos(phraseList.get(i), prevPos);
        }
        else {
          pos = nextPos(phraseList.get(i), prevPos);
          if (pos != prevPos + 1) {
            prevPos = pos - i + 1;
            break;
          }
          prevPos = pos;
        }
      }
      if (pos == prevPos) {
        freqCount++;
        prevPos = pos - phraseList.size() + 1;
      }
    }
    return freqCount;
  }
  
  private int next(String word, int docid) {
    if (!_dictionary.containsKey(word)) return Integer.MAX_VALUE;
    short[] docList = _docLists[_dictionary.get(word)];
    if (docList[docList.length - 1] <= docid) return Integer.MAX_VALUE;
    if (docList[0] > docid) return 0;
    return binarySearch(docList, 0, docList.length - 1, docid);
  }

  private int binarySearch(short[] docList, int low, int high, int docid) {
    while (high - low > 1) {
      int mid = (high - low) / 2 + low;
      if (docList[mid] <= docid) { low = mid; }
      else { high = mid; }
    }
    return high;
  }

  private int nextPos(short[] posList, int pos) {
    if (posList[posList.length - 1] <= pos) { return Integer.MAX_VALUE; }
    if (posList[0] > pos) { return posList[0]; }
    return posList[binarySearch(posList, 0, posList.length - 1, pos)];
  }

  private int[] createOffsets(String word) {
    if (!_dictionary.containsKey(word)) return null;
    int idx = _dictionary.get(word);
    //short[] docIds = _docLists[idx];
    short[] docFrequency = _docTermFrequency[idx];
    int[] offsets = new int[docFrequency.length];
    for (int i = 1; i < offsets.length; i++) {
      offsets[i] = offsets[i - 1] + docFrequency[i - 1];
    }
    return offsets;
  }

  private short[] createPosList(String word, int docIdx, int[] offsets) {
    int idx = _dictionary.get(word);
    short[] allPosList = _postingsList[idx];
    int start = offsets[docIdx];
    int end = docIdx == offsets.length - 1 ? allPosList.length : offsets[docIdx + 1];
    short[] posList = new short[end - start];
    for (int i = 0; i < end - start; i++) {
      posList[i] = allPosList[start + i];
    }
    return posList;
  }

  private Vector<Short> nextPosList(String word, int docid) {
    if (!_dictionary.containsKey(word)) return null;
    Vector<Short> docLists = new Vector<Short>();
    Vector<Short> docTermFrequency = new Vector<Short>();
    Vector<Short> postingsList = new Vector<Short>();
    int index = _dictionary.get(word);
    Byte[] encodedList =  _compressedList[index];
    return decodeList(encodedList, postingsList, docLists, docTermFrequency, docid);
  }

  private Vector<Short> decodeList(Byte[] list, Vector<Short> postingsList,
      Vector<Short> docLists, Vector<Short> docTermFrequency, int docid){
    Vector<Short> output = new Vector<Short>();
    //parsing
      short[] sList = decode(list);
      for(int i=0; i<sList.length;){
        //Doc id
        if(i == 0 )
          docLists.add(sList[i++]);
        else 
          docLists.add((short) (docLists.get(docLists.size()-1) + sList[i++]));
        //frequency
        short len = sList[i++];
        docTermFrequency.add(len);
        //posting list
        for(int j=0; j<len; ++j){
          if(docLists.get(docLists.size()-1) == docid){
            output.add(sList[i]);
          }
          if(j==0)
            postingsList.add(sList[i++]);
          else
            postingsList.add((short) (postingsList.get(postingsList.size()-1) + sList[i++]));
        }
      }
      return output;
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

  private Vector<Byte> encodePosList(short[] docList, int L1, short[] postingsList, int L2,
                         short[] docTermFrequency, int L3) {
    short[] output = new short[L1 + L2 + L3];
    int counterOfPos = 0;
    for (int i = 0; i < L1; i++) {
      int place = 0;
      // get docid
      if(i==0){
    	  output[place] = docList[i];
      } else {
    	  output[place] = (short) (docList[i] - docList[i-1]);
      }
      place++;
      // get times of occurrences
      output[place++] = docTermFrequency[i];
      // put corresponding number of offsets into output
      // and do delta compression
      for(int j=0;j < docTermFrequency[i];j++){
    	  output[place++] = 
    	    (short) ((j==0) ? postingsList[counterOfPos] : 
    	    	(postingsList[counterOfPos] - postingsList[counterOfPos-1]));
    	  counterOfPos++;
      }
    }
    return encode(output);
  }

  private Vector<Byte> encode(short[] output) {
	Vector<Byte> op1 = new Vector<Byte>();
    for (short i : output) {
      while (i >= 128) {
        op1.add((byte) (i & 0x7F));
        i >>>= 7;
      }
      op1.add((byte) (i | 0x80));
    }
    return op1;
  }
  

  private short[] decode(Byte[] input){
    short[] output = new short[input.length];
    for(int i=0; i<input.length; i+=2){
          output[i] = (short) (input[i]<<8 | input[i+1]);
    }
    return output;
  }
}
/* commands

compile: 
        javac -cp lib/jsoup-1.8.1.jar src/edu/nyu/cs/cs2580/*.java
contruct index: 
        java -cp src:lib/jsoup-1.8.1.jar -Xmx512m edu.nyu.cs.cs2580.SearchEngine  --mode=index  --options=conf/engine.conf
start server: 
        java -cp src:lib/jsoup-1.8.1.jar -Xmx512m edu.nyu.cs.cs2580.SearchEngine  --mode=serve --port=25813 --options=conf/engine.conf
query: 
        curl 'localhost:25813/search?query=web&ranker=occurence'

*/