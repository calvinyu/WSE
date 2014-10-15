package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW2.
 */
public class IndexerInvertedDoconly extends Indexer {
	  
  private Map<String, Integer> _dictionary = new HashMap<String, Integer>();
	  
  private Vector<String> _terms = new Vector<String>();

  private Map<Integer, Integer> _termDocFrequency = new HashMap<Integer, Integer>();

  private Map<Integer, Integer> _termCorpusFrequency = new HashMap<Integer, Integer>();

  private Vector<Document> _documents = new Vector<Document>();

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
	    /*BufferedReader reader = new BufferedReader(new FileReader(file));
	    try {
	      String title = file.getName();
	      String line = null;
	      while ((line = reader.readLine()) != null) {
	        processDocument(title, line);
	      }
	      reader.close();
	    } finally {
	      reader.close();
	    }*/
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

  private void processDocument(File file) {
	  BufferedReader reader = new BufferedReader(new FileReader(file));
	  String line = "";
	  Vector<Integer> titleTokens = new Vector<Integer>();
	  readTermVector(file.getName(), titleTokens);
	  
	  Vector<Integer> bodyTokens = new Vector<Integer>();
	  while ((line = reader.readLine()) != null) {
		  readTermVector(line, bodyTokens);
	  }

	  //int numViews = Integer.parseInt(s.next());
	  reader.close();

	  Document doc = new Document(_documents.size());
	  doc.setTitle(file.getName());
	  //doc.setNumViews(numViews);
	  doc.setTitleTokens(titleTokens);
	  doc.setBodyTokens(bodyTokens);
	  _documents.add(doc);
	  ++_numDocs;

	  Set<Integer> uniqueTerms = new HashSet<Integer>();
	  updateStatistics(doc.getTitleTokens(), uniqueTerms);
	  updateStatistics(doc.getBodyTokens(), uniqueTerms);
	  for (Integer idx : uniqueTerms) {
	    _termDocFrequency.put(idx, _termDocFrequency.get(idx) + 1);
	  }
  }
  
  private void readTermVector(String content, Vector<Integer> tokens) {
	    Scanner s = new Scanner(content);  // Uses white space by default.
	    while (s.hasNext()) {
	      String token = s.next();
	      int idx = -1;
	      if (_dictionary.containsKey(token)) {
	        idx = _dictionary.get(token);
	      } else {
	        idx = _terms.size();
	        _terms.add(token);
	        _dictionary.put(token, idx);
	        _termCorpusFrequency.put(idx, 0);
	        _termDocFrequency.put(idx, 0);
	      }
	      tokens.add(idx);
	    }
	    return;
	}
  
@Override
  public void loadIndex() throws IOException, ClassNotFoundException {
  }

  @Override
  public Document getDoc(int docid) {
    return null;
  }

  /**
   * In HW2, you should be using {@link DocumentIndexed}
   */
  @Override
  public Document nextDoc(Query query, int docid) {
    return null;
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
