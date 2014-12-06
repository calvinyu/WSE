/* TODO
 * 1. convert each word to lower case
 * 2. strip off punctuations
 * 3. build the Tree from only part of the corpus
 */
package edu.nyu.cs.cs2580;

import java.util.*;
import java.io.*;
class SuffixTree implements Serializable{

  class TreeNode implements Serializable{
    int freq;
    Map<Integer, TreeNode> children;
    TreeNode(){
      freq = 0;
      //Key is wordIndex and value is the subtree
    }
  };

  TreeNode root;

  SuffixTree (){
    root = new TreeNode();
  }

  public void insert(List<Integer> ngram, int start, int size) {
    insertIntoTree(ngram, start, start+size);
  }

  protected void insertIntoTree(List<Integer> ngram, int start, int height){
    TreeNode current = root;
    for(int i=start; i<height; ++i){
      int index = ngram.get(i);
      if(current.children == null) current.children = new HashMap<Integer, TreeNode>();
      if(current.children.containsKey(index) == false) {
        current.children.put(index, new TreeNode());
      }
      if(i == height -1) {
        current.children.get(index).freq++;
      }
      current = current.children.get(index);
    }
  }

  public List<Pair<List<Integer>, Integer>> query(List<Integer> ngram) {
    List<Pair<List<Integer>, Integer>> result = new LinkedList<Pair<List<Integer>, Integer>>();
    List<Integer> helper = new ArrayList<Integer>();
    System.out.println("Query count:" + ngram.size());
    traverseTree(ngram, result, root, 0, helper);
    System.out.println("Done traversing!");
    System.out.println("Result count: " + result.size());
    int count = 0;
    System.out.println("First ten results");
    for(Pair<List<Integer>, Integer> pair: result) {
      if(count++>10) break;
      for(Integer list: pair.first) System.out.printf("%d ", list);
      System.out.println("Freq is " + pair.second);
    }
    return result;
  }

  private void traverseTree(List<Integer> query, List<Pair<List<Integer>, Integer>> result, TreeNode root, 
    int index, List<Integer> helper) {
    if(root == null) return;
    if(index < query.size()){
      if(root.children.containsKey(query.get(index))){
        helper.add(query.get(index));
        traverseTree(query, result, root.children.get(query.get(index)), index+1, helper);
      }
    }
    else{
      if(root.children == null){
        List<Integer> ngram = new LinkedList<Integer>();
        for(Integer i:helper) ngram.add(i);
        result.add(new Pair<List<Integer>, Integer>(ngram, root.freq));
        return;
      }
      Set<Integer> keySet = root.children.keySet();
      for(Integer key: keySet){
        helper.add(key);
        traverseTree(query, result, root.children.get(key), index+1, helper);
        helper.remove(index);
      }
    }
  }

  public static void main(String[] args) {
    List<Integer> a = new LinkedList<Integer>();
    a.add(1);
    a.add(2);
    a.add(3);
    a.add(4);
    List<Integer> b = new LinkedList<Integer>();
    b.add(1);
    b.add(2);
    b.add(3);
    b.add(5);
    List<Integer> c = new LinkedList<Integer>();
    c.add(1);
    c.add(2);
    c.add(4);
    c.add(8);
    List<Integer> d = new LinkedList<Integer>();
    d.add(1);
    d.add(4);
    d.add(6);
    d.add(9);
    SuffixTree tree = new SuffixTree();
    tree.insert(a,0,4);
    tree.insert(b,0,4);
    tree.insert(c,0,4);
    tree.insert(d,0,4);
    tree.insert(c,0,4);
    tree.insert(d,0,4);
    tree.insert(d,0,4);

    List<Integer> e = new LinkedList<Integer>();
    List<Pair<List<Integer>, Integer>> result = tree.query(e);
    for(Pair<List<Integer>, Integer> list: result){
      System.out.println(list.second);
      for(Integer i: list.first){
        System.out.printf(" %d", i);
      }
      System.out.println();
    }

  }


}