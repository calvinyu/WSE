package edu.nyu.cs.cs2580;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


class LogTrie extends Trie implements Serializable {

  class TrieNode implements Serializable{
    TrieNode[] children;
    String word;
    int freq;
    public TrieNode() {
      children = new TrieNode[27];
      freq = 0;
    }
  };

  TrieNode root;

  LogTrie (){
    root = new TrieNode();
  }

  public void insert(String s) {
    s = s.toLowerCase();
    if(!isValidWord(s)) return;
    insertIntoTrie(s, 0);
  }

  protected boolean isValidWord(String s) {
    for(int i=0; i<s.length(); ++i) {
      System.out.println(s.charAt(i));
      if ((s.charAt(i) < 'a' || s.charAt(i) > 'z') && s.charAt(i) != ' ') return false;
    }
    return true;
  }

  protected void insertIntoTrie(String s, int start){
    TrieNode current = root;
    for(int i=start; i<s.length(); ++i){
      int index = s.charAt(i) == ' ' ? 26 : s.charAt(i) - 'a';
      if(current.children[index] == null) {
        current.children[index] = new TrieNode();
      }
      if(i == s.length() - 1){
        current.children[index].freq++;
        current.children[index].word = s;
      }
      current = current.children[index];
    }
  }

  public List<Pair<String, Integer>> query(String s) {
    System.out.println("In function: " + s);
    s = s.toLowerCase();
    List<Pair<String, Integer>> result = new LinkedList<Pair<String, Integer>>();
    traverseTrie(s, result, root, 0);
    return result;
  }

  protected void traverseTrie(String s, List<Pair<String, Integer>> result, TrieNode root, int index) {
    if(root == null) return;
    if(index < s.length()){
      int charInd = s.charAt(index) == ' ' ? 26 : s.charAt(index)-'a';
      if(root.children[charInd] != null){
        traverseTrie(s, result, root.children[charInd], index+1);
      }
    }
    else{
      for(int i=0; i<27; ++i) {
        if(root.children[i] != null) {
          traverseTrie(s, result, root.children[i], index+1);
        }
      }
      if(root.word != null) result.add(new Pair<String, Integer>(root.word, root.freq));
    }
  }
}
