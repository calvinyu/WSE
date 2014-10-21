package edu.nyu.cs.cs2580;

import java.io.File;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Representation of a user query.
 * 
 * In HW1: instructors provide this simple implementation.
 * 
 * In HW2: students must implement {@link QueryPhrase} to handle phrases.
 * 
 * @author congyu
 * @auhtor fdiaz
 */
public class Query {
  public String _query = null;
  public Vector<String> _tokens = new Vector<String>();
  private static Set<String> set = null;
  
  public Query(String query) {
    _query = query.replace("+", " ");
  }

  public void processQuery() {
    if(set == null) set = Query.readStopWords();
    if (_query == null) {
      return;
    }
    Scanner s = new Scanner(_query);
    while (s.hasNext()) {
      String word = s.next();
      if(!isStopWord(word)) _tokens.add(word);
    }
    s.close();

  }
  private boolean isStopWord(String s){
    return set.contains(s);
  }
  private static Set<String> readStopWords(){
    String filePath = "data/stopwords/long_stopwords.txt";
    File stopwordFile = new File(filePath);
    Scanner scanner = null;
    try{
      scanner = new Scanner(stopwordFile);
    }
    catch(Exception e){
      System.out.println("File open failed:" + filePath);
    };
     Set<String> set = new TreeSet<String>();
     while(scanner.hasNextLine()){
       set.add(scanner.nextLine());
     }
     scanner.close();
    return set;
  }
}
