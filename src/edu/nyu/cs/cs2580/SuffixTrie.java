package edu.nyu.cs.cs2580;

import java.util.*;
class SuffixTrie extends Trie{

  SuffixTrie(){
    super();
  }

  @Override
  public void insert(String s) {
    s = s.toLowerCase();
    if(!isValidWord(s)) return;
    for(int suf=0; suf<s.length()-1; ++suf){
      //Threr might be conflictions at the leave.
      insertIntoTrie(s, suf);
    }
  }


  public static void main(String[] args) {
    SuffixTrie mytrie = new SuffixTrie();
    mytrie.insert("Calvin");
    mytrie.insert("Carlta");
    mytrie.insert("Tin");
    mytrie.insert("Tim");
    mytrie.insert("result");
    mytrie.insert("Carlos");
    // read copcus from standard input
    Scanner kb = new Scanner(System.in);
    while(kb.hasNext()) {
      mytrie.insert(kb.next());
    }
    //set query word
    String query = "lt";
    List<Pair<String, Integer>> result = mytrie.query(query);
    //print out result
    for(Pair<String, Integer> s: result) System.out.println(s.first + " " + s.second);
  }
}