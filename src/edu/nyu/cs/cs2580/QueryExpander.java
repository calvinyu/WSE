package edu.nyu.cs.cs2580;

import java.util.*;

/**
 * Created by kh21 on 2014/11/24.
 */
public class QueryExpander {
  public static String expandQuery(Vector<ScoredDocument> docs, int numTerms,
                                   IndexerInvertedCompressed indexer) {
    // dictionary contains all words and their count
    HashMap<String, Integer> dictionary = new HashMap<String, Integer>();
    // get top docs
    for(int i = 0; i < docs.size(); i++) {
      int docid = docs.get(i).getDoc()._docid;
      int[] body = indexer._docBody[docid];
      for (int j = 0; j < body.length; j++) {
        // get the term by looking up _terms
        String term = indexer._terms.get(body[j]);
        j++;
        // get the number of appearances
        int count = body[j];
        // if exists, add the count, else create an entry
        if (dictionary.containsKey(term)) {
          dictionary.put(term, dictionary.get(term) + count);
        } else dictionary.put(term, count);
      }
    }
    // get top terms
    dictionary = sortByValues(dictionary);
    Set<Map.Entry<String, Integer>> set = dictionary.entrySet();
    Iterator<Map.Entry<String, Integer>> iterator = set.iterator();
    int totalFrequency = 0;
    for (int i=0; i<numTerms; i++) {
      Map.Entry<String,Integer> me = iterator.next();
      totalFrequency += me.getValue();
    }

    // write results into a string to return to user
    String result = "";
    iterator = set.iterator();
    for (int i=0; i<numTerms; i++) {
      Map.Entry<String,Integer> me = iterator.next();
      result += (me.getKey() + "\t" + (double) me.getValue()/totalFrequency + "\n");
    }
    return result;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static HashMap<String,Integer> sortByValues(HashMap map) {
    List list = new LinkedList(map.entrySet());
    // Defined Comparator here
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
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
