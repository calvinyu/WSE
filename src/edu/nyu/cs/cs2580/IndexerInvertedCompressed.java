package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedCompressed extends Indexer implements Serializable {
  public Map<String, Integer> _dictionary = new HashMap<String, Integer>();

  public Vector<String> _terms = new Vector<String>();

  public int[][] _docBody;

  private byte[][] _compressedList;

  private int[] _termDocFrequency;

  public int[] _termCorpusFrequency;

  public HashMap<Integer, Vector<Integer>> _tmpPostingsList = new HashMap<Integer, Vector<Integer>>();

  public HashMap<Integer, Vector<Integer>> _tmpDocList = new HashMap<Integer, Vector<Integer>>();

  public HashMap<Integer, Vector<Integer>> _tmpDocTermFrequency = new HashMap<Integer, Vector<Integer>>();

  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();

  public IndexerInvertedCompressed(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
    Vector<Integer> tmpTermDocFrequency = new Vector<Integer>();
    Vector<Integer> tmpTermCorpusFrequency = new Vector<Integer>();
    String corpusFile = _options._corpusPrefix;
    System.out.println("Construct index from: " + corpusFile);
    File folder = new File(corpusFile);
    File[] listOfFiles = folder.listFiles();
    // First run: Determine the size of the array, initialize the attributes
    System.out.println("First round!!!");
    int cnt = 0;for (File file : listOfFiles) {
      if(cnt%100==0) System.out.println(cnt);
      processDocument(file, tmpTermDocFrequency, tmpTermCorpusFrequency);
      cnt++;
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
    short[][] docLists = new short[_terms.size()][];
    short[][] docTermFrequency = new short[_terms.size()][];
    short[][] postingsList = new short[_terms.size()][];
    for (int i = 0; i < _terms.size(); i++) {
      docLists[i] = new short[_termDocFrequency[i]];
      docTermFrequency[i] = new short[_termDocFrequency[i]];
      postingsList[i] = new short[_termCorpusFrequency[i]];
      _termDocFrequency[i] = 0;
      _termCorpusFrequency[i] = 0;
    }

    // Second run : Create the postings list
    System.out.println("Second round!!!");
    cnt = 0;
    for (File file : listOfFiles) {
      if(cnt%100==0) System.out.println(cnt);
      createPostingsList(file, docLists, docTermFrequency, postingsList);
      cnt++;
    }

    // Compress three lists into compressed vector
    _compressedList = new byte[_terms.size()][];
    for (int i = 0; i < _terms.size(); i++) {
      int[] tmpCompressed = new int[_termCorpusFrequency[i] + 2 * _termDocFrequency[i]];
      int docCount = 0;
      int posCount = 0;
      int prevId = (short) 0;
      int prevPos = (int) Short.MIN_VALUE;
      for (int j = 0; j < _termDocFrequency[i]; j++) {
        tmpCompressed[docCount] = (docLists[i][j] - prevId);
        tmpCompressed[docCount + 1] = docTermFrequency[i][j];
        prevId = docLists[i][j];
        docCount += 2;
        for (int k = 0; k < docTermFrequency[i][j]; k++) {
          tmpCompressed[docCount] = postingsList[i][posCount] - prevPos;
          docCount++;
          posCount++;
        }
      }
      docLists[i] = null;
      docTermFrequency[i] = null;
      postingsList[i] = null;
      _compressedList[i] = Encoder.encode(tmpCompressed);
    }

    // delete useless lists
    docLists = null;
    docTermFrequency = null;
    postingsList = null;

    // Third run: Create the document body
    // we know the number of docs, so initialize the docBody
    _docBody = new int[_numDocs][];
    System.out.println("Third round!!!");
    cnt = 0;
    for (File file : listOfFiles) {
      if (cnt % 100 == 0) System.out.println(cnt);
      createDocBody(file, cnt);
      cnt++;
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

  private void processDocument(File file, Vector<Integer> docFrequency, Vector<Integer> termFrequency) throws IOException {
    Document DOM = Jsoup.parse(file, "UTF-8", "");
    String content = DOM.select("#bodyContent").text().toLowerCase();
    content = Remove.remove(content);

    updateStatistics(content, docFrequency, termFrequency);
  }

  private void updateStatistics(String content, Vector<Integer> docFrequency, Vector<Integer> termFrequency) {
    HashSet<Integer> uniqueTerms = new HashSet<Integer>();
    Scanner s = new Scanner(content);  // Uses white space by default.
    while (s.hasNext()) {
      String word = s.next();
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

  private void createPostingsList(File file, short[][] docLists, short[][] docTermFrequency, short[][] postingsList)
      throws IOException {
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
    int docsize = 0;
    while (s.hasNext()) {
      String word = s.next();
      int idx = _dictionary.get(word);
      if (!uniqueTerms.containsKey(idx)) uniqueTerms.put(idx, new Vector<Integer>());
      uniqueTerms.get(idx).add(offset);
      offset++;
      docsize++;
    }
    doc.setLength(docsize);
    s.close();
    for (int i : uniqueTerms.keySet()) {
      docLists[i][_termDocFrequency[i]] = (short) docid;
      docTermFrequency[i][_termDocFrequency[i]] = (short) uniqueTerms.get(i).size();
      _termDocFrequency[i]++;
      for (int j : uniqueTerms.get(i)) {
        postingsList[i][_termCorpusFrequency[i]] = (short) j;
        _termCorpusFrequency[i]++;
      }
    }
    if (offset > 32767) System.out.println(docid + " " + offset);
  }

  private void createDocBody(File file, int cnt) throws IOException {
    Document DOM = Jsoup.parse(file, "UTF-8", "");
    String content = DOM.select("#bodyContent").text().toLowerCase();
    content = Remove.remove(content);

    HashMap<Integer, Integer> uniqueTerms = new HashMap<Integer, Integer>();
    Scanner s = new Scanner(content);  // Uses white space by default.
    while (s.hasNext()) {
      String word = s.next();
      if (StopWords.isStopWord(word)) continue;
      int idx = _dictionary.get(word);
      if (!uniqueTerms.containsKey(idx)) uniqueTerms.put(idx, 0);
      uniqueTerms.put(idx, uniqueTerms.get(idx) + 1);
    }
    s.close();
    HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
    for (int i : uniqueTerms.keySet()) {
      if(uniqueTerms.get(i) > 2) hm.put(i, uniqueTerms.get(i));
    }
    _docBody[cnt] = new int[hm.size() * 2];
    int i = 0;
    for (int idx : hm.keySet()) {
      _docBody[cnt][2 * i] = idx;
      _docBody[cnt][2 * i + 1] = hm.get(idx);
      i++;
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private HashMap<Integer,Integer> sortByValues(HashMap map) {
    List list = new LinkedList(map.entrySet());
    // Defined Comparator here
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o2)).getValue())
            .compareTo(((Map.Entry) (o1)).getValue());
      }
    });
    HashMap sortedHashMap = new LinkedHashMap();
    for (Iterator it = list.iterator(); it.hasNext();) {
      Map.Entry entry = (Map.Entry) it.next();
      sortedHashMap.put(entry.getKey(), entry.getValue());
    }
    return sortedHashMap;
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
    this._docBody = loaded._docBody;
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
    Vector<Vector<Integer>> nextIdxList = new Vector<Vector<Integer>>();
    Vector<Integer> tmpNextIdx = new Vector<Integer>();
    Vector<Integer> nextList = new Vector<Integer>();
    Vector<Integer> freqList = new Vector<Integer>();
    Vector<String> phrases = new Vector<String>();
    for (String token : query._tokens) {
      String[] words = token.split(" ");
      if (words.length > 1) phrases.add(token);
      for (int i = 0; i < words.length; i++) {
        String word = words[i];
        if (!_dictionary.containsKey(word)) return null;
        if (!_tmpDocList.containsKey(_dictionary.get(word))) {
          Vector<Integer> docList = new Vector<Integer>();
          Vector<Integer> postingsList = new Vector<Integer>();
          Vector<Integer> docTermFrequency = new Vector<Integer>();
          Encoder.decode(_compressedList[_dictionary.get(word)], postingsList, docList, docTermFrequency);
          _tmpDocList.put(_dictionary.get(word), docList);
          _tmpPostingsList.put(_dictionary.get(word), postingsList);
          _tmpDocTermFrequency.put(_dictionary.get(word), docTermFrequency);
        }
        int nextIdx = next(word, docid, _tmpDocList.get(_dictionary.get(word)));
        if (words.length > 1) {
          tmpNextIdx.add(nextIdx);
          if (i == words.length - 1) {
            nextIdxList.add(tmpNextIdx);
            tmpNextIdx = new Vector<Integer>();
          }
        }
        if (nextIdx == Integer.MAX_VALUE) return null;
        else {
          nextList.add(_tmpDocList.get(_dictionary.get(word)).get(nextIdx));
          freqList.add(_tmpDocTermFrequency.get(_dictionary.get(word)).get(nextIdx));
        }
      }
    }
    int maxId = (int) Collections.max(nextList);
    if (maxId == Collections.min(nextList)) {
      // check if the document contains the phrases
      Vector<Integer> phraseFreqs = new Vector<Integer>();
      int phraseCount = 0;
      for (int i = 0; i < phrases.size(); i++) {
        String phrase = phrases.get(i);
        int freq = phraseSearch(phrase, nextIdxList.get(i));
        if (freq == 0) return nextDoc(query, maxId);
        phraseFreqs.add(freq);
      }
      // query is satisfied!!!
      DocumentIndexed doc = getDoc(maxId);
      freqList.addAll(phraseFreqs);
      doc.setFreqList(freqList);
      return doc;
    }
    return nextDoc(query, maxId - 1);
  }

  private int phraseSearch(String phrase, Vector<Integer> nextIdx) {
    Vector<int[]> phraseList = new Vector<int[]>();
    String[] phraseWords = phrase.split(" ");
    for (int i = 0; i < phraseWords.length; i++) {
      String word = phraseWords[i];
      phraseList.add(createPosList(word, nextIdx.get(i), createOffsets(word)));
    }
    int prevPos = -1;
    int pos = -1;
    int freqCount = 0;
    while (pos < Integer.MAX_VALUE) {
      for (int i = 0; i < phraseList.size(); i++) {
        if (i == 0) {
          prevPos = nextPos(phraseList.get(i), prevPos);
        } else {
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

  private int next(String word, int docid, Vector<Integer> docList) {
    if (!_dictionary.containsKey(word)) return Integer.MAX_VALUE;
    if (docList.get(docList.size() - 1) <= docid) return Integer.MAX_VALUE;
    if (docList.get(0) > docid) return 0;
    return binarySearch(docList, 0, docList.size() - 1, docid);
  }

  private int binarySearch(Vector<Integer> docList, int low, int high, int docid) {
    while (high - low > 1) {
      int mid = (high - low) / 2 + low;
      if (docList.get(mid) <= docid) {
        low = mid;
      } else {
        high = mid;
      }
    }
    return high;
  }

  private int binarySearch(int[] docList, int low, int high, int docid) {
    while (high - low > 1) {
      int mid = (high - low) / 2 + low;
      if (docList[mid] <= docid) {
        low = mid;
      } else {
        high = mid;
      }
    }
    return high;
  }

  private int nextPos(int[] posList, int pos) {
    if (posList[posList.length - 1] <= pos) {
      return Integer.MAX_VALUE;
    }
    if (posList[0] > pos) {
      return posList[0];
    }
    return posList[binarySearch(posList, 0, posList.length - 1, pos)];
  }

  private int[] createOffsets(String word) {
    if (!_dictionary.containsKey(word)) return null;
    int idx = _dictionary.get(word);
    Vector<Integer> docFrequency = _tmpDocTermFrequency.get(idx);
    int[] offsets = new int[docFrequency.size()];
    for (int i = 1; i < offsets.length; i++) {
      offsets[i] = offsets[i - 1] + docFrequency.get(i - 1);
    }
    return offsets;
  }

  private int[] createPosList(String word, int docIdx, int[] offsets) {
    int idx = _dictionary.get(word);
    int start = offsets[docIdx];
    Vector<Integer> postingsList = _tmpPostingsList.get(idx);
    int end = docIdx == offsets.length - 1 ? postingsList.size() : offsets[docIdx + 1];
    int[] posList = new int[end - start];
    for (int i = 0; i < end - start; i++) {
      posList[i] = postingsList.get(start + i);
    }
    return posList;
  }

  public void removeTmpLists(){
    _tmpDocList = new HashMap<Integer, Vector<Integer>>();
    _tmpPostingsList = new HashMap<Integer, Vector<Integer>>();
    _tmpDocTermFrequency = new HashMap<Integer, Vector<Integer>>();
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
  public int documentTermFrequency(String term, int docid) {
    return 0;
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
        curl 'localhost:25813/search?query=grammy&ranker=conjunctive'

*/