package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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

  private Map<Integer, Vector<Integer>> _postingLists =
		  new HashMap<Integer, Vector<Integer>>();

  private Map<Integer, Integer> _termDocFrequency = new HashMap<Integer, Integer>();

  private Map<Integer, Integer> _termCorpusFrequency = new HashMap<Integer, Integer>();

  private Vector<DocumentIndexed> _documents = new Vector<DocumentIndexed>();

  public IndexerInvertedDoconly(){ };
  
  public IndexerInvertedDoconly(Options options) {
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
	  String line = DOM.select("#bodyContent").text();
	  
	  DocumentIndexed doc = new DocumentIndexed(_documents.size(), this);
	  HashSet<Integer> uniqueTerms = new HashSet<Integer>();
	  
	  updateStatistics(line, uniqueTerms);
	  //int numViews = Integer.parseInt(s.next());
	  
	  //doc.setNumViews(numViews);
	  doc.setTitle(file.getName());
	  
	  _documents.add(doc);
	  ++_numDocs;
	  
	  //all words that appear in this doc,
	  for (Integer idx : uniqueTerms) {
	    _termDocFrequency.put(idx, _termDocFrequency.get(idx) + 1);
	  }
  }
  
  private void updateStatistics(String content, HashSet<Integer> uniqueTerms){
	  Scanner s = new Scanner(content);  // Uses white space by default.
	    while (s.hasNext()) {
	      String word = s.next();
	      int idx = -1;
	      if (_dictionary.containsKey(word)) {
	        idx = _dictionary.get(word);
	      } else {
	        idx = _terms.size();
	        _terms.add(word);
	        _postingLists.put(idx, new Vector<Integer>());
	        _dictionary.put(word, idx);
	        _termCorpusFrequency.put(idx, 0);
	        _termDocFrequency.put(idx, 0);
	      }
	      _postingLists.get(idx).add(_documents.size());
	      uniqueTerms.add(idx);
	      _termCorpusFrequency.put(idx, _termCorpusFrequency.get(idx) + 1);
	      ++_totalTermFrequency;
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
    int idx = _dictionary.get(query._query);
    if (!_postingLists.containsKey(idx)) { return null; }
    Vector<Integer> postingList = _postingLists.get(idx);
    if (postingList.get(postingList.size() - 1) < docid) { return null; }
    if (postingList.get(0) > docid) { return getDoc(postingList.get(0)); }
    return getDoc(postingList.get(binarySearch(idx, 0, postingList.size(), docid)));
  }

  private int binarySearch(int idx, int low, int high, int docid) {
    Vector<Integer> postingList = _postingLists.get(idx);
    while (high - low > 1) {
      int mid = (high - low) / 2;
      if (postingList.get(mid) <= docid) { low = mid; }
      else { high = mid; }
    }
    return low;
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
