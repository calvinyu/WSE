package edu.nyu.cs.cs2580;

import java.io.File;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @CS2580: implement this class for HW2 to handle phrase. If the raw query is
 * ["new york city"], the presence of the phrase "new york city" must be
 * recorded here and be used in indexing and ranking.
 */
public class QueryPhrase extends Query {
  private static Set<String> set = null;

  public QueryPhrase(String query) {
    super(query);
  }

  @Override
  public void processQuery() {
    if(set == null) set = QueryPhrase.readStopWords();
    if (_query == null) { return; }
    System.out.println(_query);
    Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    Matcher regexMatcher = regex.matcher(_query);
    while (regexMatcher.find()) {
      if (regexMatcher.group(1) != null) {
        // Add double-quoted string without the quotes
        String word = regexMatcher.group(1);
        if(!removeBadWord(word).equals(""))
          _tokens.add(removeBadWord(regexMatcher.group(1)));
      } else if (regexMatcher.group(2) != null) {
        // Add single-quoted string without the quotes
        String word = regexMatcher.group(2);
        if(!removeBadWord(word).equals(""))
          _tokens.add(removeBadWord(regexMatcher.group(2)));
      } else {
        // Add unquoted wordString word = regexMatcher.group(1);
        String word = regexMatcher.group();
        if(!removeBadWord(word).equals(""))
          _tokens.add(removeBadWord(regexMatcher.group()));
      }
    }
  }
  private String removeBadWord(String s){
    String ret = "";
    String[] res = s.split(" ");
    boolean first = true;
    for(String st: res){
      if(!isStopWord(st) && st.length()>=3 && st.length()<=20 ){
        if(first) {ret += st; first = false;}
        else ret += " " + st;
      }
    }
    System.out.println("After process is " + ret);
    return ret;
  }
  private boolean isStopWord(String s){
    return set.contains(s);
  }
  private static Set<String> readStopWords(){
    String filePath = "data/stopwords/stopwords.txt";
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
       String word = scanner.nextLine();
       System.out.println(word);
       set.add(word);
     }
     scanner.close();
    return set;
  }
}
