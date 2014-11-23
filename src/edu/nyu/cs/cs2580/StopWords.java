package edu.nyu.cs.cs2580;

import java.util.*;
import java.io.*;

class StopWords {
  //store all stopwords
  private static Set<String> set = null;
  
  /*
   * param s: stopword candidate
   * @returns true if s is a stopword
   */
  public static boolean isStopWord(String s){
    if( set == null ) set = readStopWords("data/stopwords/stopwords.txt");
    return set.contains(s);
  }

  /*
   * param s: stopword phrase candidate
   * @returns stopwords stripped phrase
   */
  public static String removeBadWord(String s){
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

  /*
   * param filePath: path of stopwords file
   * @returns a set of stopwords
   */
  private static Set<String> readStopWords(String filePath){
    File stopwordFile = new File(filePath);
    Scanner scanner = null;
    try{
      scanner = new Scanner(stopwordFile);
    }
    catch(Exception e){
      System.out.println("File open failed:" + filePath);
    }
    Set<String> set = new TreeSet<String>();
    while(scanner.hasNextLine()){
      String word = scanner.nextLine();
      set.add(word);
    }
    scanner.close();
    return set;
  }
}