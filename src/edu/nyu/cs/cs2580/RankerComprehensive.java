package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

import edu.nyu.cs.cs2580.QueryHandler.CgiArguments;
import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3 based on your {@code RankerFavorite}
 * from HW2. The new Ranker should now combine both term features and the
 * document-level features including the PageRank and the NumViews. 
 */
public class RankerComprehensive extends Ranker {

  public RankerComprehensive(Options options,
      CgiArguments arguments, Indexer indexer) {
    super(options, arguments, indexer);
    System.out.println("Using Ranker: " + this.getClass().getSimpleName());
  }

  @Override
  public Vector<ScoredDocument> runQuery(Query query, int numResults) {
    return null;
  }

  @Override
  public String expandQuery(Vector<ScoredDocument> docs, String query,
		int numDocs, int numTerms) {
	  // dictionary contains all words and their count
	  HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
	  // get top docs
	  int[] topDocs = new int[numDocs];
	  for (int i=0; i<numDocs; i++){
		  topDocs[i] = docs.get(i).get_doc()._docid;
	  }
	  // get the path
	  String corpusFile = _options._corpusPrefix;
	  System.out.println(corpusFile);
	  File folder = new File(corpusFile);
	  File[] listOfFiles = new File[numDocs];
	  File[] f = folder.listFiles();
	  // iterate over the corpus to get these files
	  for (int i=0; i<numDocs; i++){
		  for (int j=0; j<f.length;j++){
			  if (j==topDocs[i])
				  listOfFiles[i] = f[j];
		  }
	  }
	  // get the content of the files
	  String content = "";
	  try {
		  for (File fi : listOfFiles){
			  Document DOM = Jsoup.parse(fi, "UTF-8", "");
			  content += DOM.select("#bodyContent").text().toLowerCase();
		  }
		  content = Remove.remove(content);
	  } catch (IOException e) {e.printStackTrace();}
	  // count terms (removing stopwords first)
	  Stopwords stopwords = new Stopwords();
	  Scanner s = new Scanner(content);    // Uses white space by default.
	  while (s.hasNext()) {
	      String word = s.next();
	      if (stopwords.wordsList.contains(word) || word.length() < 3 || word.length() > 20)
			  continue;
	      // if already exist, add its count; else create a new one
	      if (dictionary.containsKey(word)){
	    	  dictionary.put(word, dictionary.get(word)+1);
	      } else dictionary.put(word, 1);
	  }
	  s.close();
	  // get top terms
	  dictionary = sortByValues(dictionary);
	  Set<Entry<String, Integer>> set = dictionary.entrySet();
      Iterator<Entry<String, Integer>> iterator = set.iterator();
      int totalFrequency = 0;
      for (int i=0; i<numTerms; i++) {
          Entry<String,Integer> me = (Entry<String,Integer>)iterator.next();
          totalFrequency += me.getValue();
      }
      
      // write results into a string to return to user
      String result = "";
      iterator = set.iterator();
      for (int i=0; i<numTerms; i++) {
	         Entry<String,Integer> me = (Entry<String,Integer>)iterator.next();
	         result += (me.getKey() + "\t" + (double) me.getValue()/totalFrequency + "\n");
	  }
      return result;
      
      // write the statistics to file
      /* File file = new File("data/prf/" + query);
      if (!file.exists())
		try {
			file.createNewFile();
		} catch (IOException e) { }
	  try {
		PrintWriter pw = new PrintWriter(file);
	    // iterate the hashmap to write
		iterator = set.iterator();
	    for (int i=0; i<numTerms; i++) {
	         Entry<String,Integer> me = (Entry<String,Integer>)iterator.next();
	         pw.write(me.getKey() + "\t" + (double) me.getValue()/totalFrequency + "\n");
	    }
	    pw.flush(); pw.close();
	  } catch (FileNotFoundException e) { }
	  return "Writing to file";
	  */
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private HashMap<String,Integer> sortByValues(HashMap map) { 
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
}
