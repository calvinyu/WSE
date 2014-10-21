package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
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
public class IndexerInvertedDoconly extends Indexer implements Serializable{

  private static final long serialVersionUID = 5868794811532351325L;

  private Map<String, Integer> _dictionary = new HashMap<String, Integer>();

  private Vector<String> _terms = new Vector<String>();

  private int[][] _postingsList;

  // private Map<Integer, Integer> _termDocFrequency = new HashMap<Integer, Integer>();

  // private Map<Integer, Integer> _termCorpusFrequency = new HashMap<Integer, Integer>();

  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();

  private Vector<Integer> _termDocFrequency = new Vector<Integer>();

  public IndexerInvertedDoconly(){ };

  public IndexerInvertedDoconly(Options options) {
    super(options);
    System.out.println("Using Indexer: " + this.getClass().getSimpleName());
  }

  @Override
  public void constructIndex() throws IOException {
    String corpusFile = _options._corpusPrefix;
    System.out.println("Construct index from: " + corpusFile);
    File folder = new File(corpusFile);
    File[] listOfFiles = folder.listFiles();
    // First run: Determine the size of the array
    System.out.println("First round!!");
    int cnt =0;
    for (File file : listOfFiles) {
      cnt++;
      if(cnt%100==0) System.out.println(cnt);
      processDocument(file);
    }
    // Initialize the postings list
    _postingsList = new int[_termDocFrequency.size()][];
    for (int i = 0; i < _termDocFrequency.size(); i++) {
      _postingsList[i] = new int[_termDocFrequency.get(i)];
      _termDocFrequency.set(i, 0);
    }
    // Second run: Create the posting list
    System.out.println("Second round!!");
    cnt=0;
    for(File file : listOfFiles){
      cnt++;
      if(cnt%100==0) System.out.println(cnt);
      createPostingsList(file);
    }
    // Convert posting lists to posting arrays
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
    String content = DOM.select("#bodyContent").text().toLowerCase();
    content = Remove.remove(content);

    updateStatistics(content);
  }

  private void updateStatistics(String content){
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
        _termDocFrequency.add(0);
        _dictionary.put(word, idx);
      }
      uniqueTerms.add(idx);
    }
    s.close();
    for (int i : uniqueTerms) {
      _termDocFrequency.set(i, _termDocFrequency.get(i) + 1);
    }
    _totalTermFrequency++;
  }

  private void createPostingsList(File file) throws IOException {
    Document DOM = Jsoup.parse(file, "UTF-8", "");
    String content = DOM.select("#bodyContent").text().toLowerCase();
    content = Remove.remove(content);

    int docid = _documents.size();
    DocumentIndexed doc = new DocumentIndexed(_documents.size(), this);
    doc.setTitle(file.getName());
    _documents.add(doc);
    ++_numDocs;

    HashSet<Integer> uniqueTerms = new HashSet<Integer>();
    Scanner s = new Scanner(content);  // Uses white space by default.
    while (s.hasNext()) {
      String word = s.next().toLowerCase();
      int idx = _dictionary.get(word);
      uniqueTerms.add(idx);
    }
    s.close();
    for (int i : uniqueTerms) {
      _postingsList[i][_termDocFrequency.get(i)] = docid;
      _termDocFrequency.set(i, _termDocFrequency.get(i) + 1);
    }
  }

  @Override
  public void loadIndex() throws IOException, ClassNotFoundException {
    String indexFile = _options._indexPrefix + "/corpus.idx";
    System.out.println("Load index from: " + indexFile);

    ObjectInputStream reader =
        new ObjectInputStream(new FileInputStream(indexFile));
    IndexerInvertedDoconly loaded = (IndexerInvertedDoconly) reader.readObject();

    this._documents = loaded._documents;
    // Compute numDocs and totalTermFrequency b/c Indexer is not serializable.
    this._numDocs = _documents.size();
    // for (Integer freq : loaded._termCorpusFrequency.values()) {
    //   this._totalTermFrequency += freq;
    // }
    this._postingsList = loaded._postingsList;
    this._dictionary = loaded._dictionary;
    this._terms = loaded._terms;
    // this._termCorpusFrequency = loaded._termCorpusFrequency;
    // this._termDocFrequency = loaded._termDocFrequency;
    this._totalTermFrequency = loaded._totalTermFrequency;
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
    Vector<Integer> nextList = new Vector<Integer>();
    for (String word : query._tokens) {
      int nextPos = next(word, docid);
      if (nextPos == Integer.MAX_VALUE) { return null; }
      else nextList.add(nextPos);
    }
    int maxPos = Collections.max(nextList);
    if (maxPos == Collections.min(nextList)) {
      return getDoc(nextList.get(0));
    }
    return nextDoc(query, maxPos - 1);
  }

  private int next(String word, int docid) {
    if (!_dictionary.containsKey(word)) { return Integer.MAX_VALUE; }
    int[] docList = _postingsList[_dictionary.get(word)];
    if (docList[docList.length - 1] <= docid) { return Integer.MAX_VALUE; }
    if (docList[0] > docid) { return docList[0]; }
    return docList[binarySearch(docList, 0, docList.length - 1, docid)];
  }

  private int binarySearch(int[] docList, int low, int high, int docid) {
    while (high - low > 1) {
      int mid = (high + low) / 2;
      if (docList[mid] <= docid) { low = mid; }
      else { high = mid; }
    }
    return high;
  }

  @Override
  public int corpusDocFrequencyByTerm(String term) {
    return _postingsList[_dictionary.get(term)].length;
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
