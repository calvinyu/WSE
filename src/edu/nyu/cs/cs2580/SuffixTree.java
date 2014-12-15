/* TODO
 * 1. convert each word to lower case
 * 2. strip off punctuations
 * 3. build the Tree from only part of the corpus
 */
package edu.nyu.cs.cs2580;

import java.util.*;
import java.io.*;
class SuffixTree implements Serializable{

  private int bigramThreshold = 100;
  private int trigramThreshold = 10;
  private int leafMinFreq = 5;
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
  public void insert(int index){
    if(root.children == null) root.children = new HashMap<Integer, TreeNode>();
    if(!root.children.containsKey(index)) root.children.put(index, new TreeNode());
    root.children.get(index).freq++;
  }
  public void insert(int first, int second){
    TreeNode current = root;
    if(current.children.get(first).freq > bigramThreshold){
      current = current.children.get(first);
      if(current.children == null) current.children = new HashMap<Integer, TreeNode>();
      if(!current.children.containsKey(second)) current.children.put(second, new TreeNode());
      current.children.get(second).freq++;
    }
  }
  public void insert(int first, int second, int third){
    TreeNode current = root;
    if(current.children.get(first).freq > bigramThreshold){
      current = current.children.get(first);
      if(current.children.get(second).freq > trigramThreshold){
        current = current.children.get(second);
        if(current.children == null) current.children = new HashMap<Integer, TreeNode>();
        if(!current.children.containsKey(third)) current.children.put(third, new TreeNode());
        current.children.get(third).freq++;
      }
    }
  }

  protected void insertIntoTree(List<Integer> ngram, int start, int height){
    TreeNode current = root;
    for(int i=start; i<height; ++i){
      // i is the depth of the tree
      int index = ngram.get(i);
      //each index represents a word
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
  protected void prune(){
    Set<Integer> keySet = root.children.keySet();
    TreeNode current = root;
    TreeNode second;
    for(Integer key: keySet){
      current = root.children.get(key);//root->1
      if(current.children!=null){
        Set<Integer> firstKeySet = current.children.keySet();
        for(Integer firstKey: firstKeySet){
          second = current.children.get(firstKey);
          if(second.children != null){
            Set<Integer> secondKeySet = second.children.keySet();
            int[] duplicateKeySet = new int[secondKeySet.size()];
            int cnt = 0;
            for(Integer secondKey: secondKeySet){
              duplicateKeySet[cnt++] = secondKey;
            }
            for(int duplicateKey: duplicateKeySet){
              if(second.children.get(duplicateKey).freq < leafMinFreq){
                second.children.remove(duplicateKey);
              }
            }
          }
        }
      }
    }
  }
  public List<Pair<List<Integer>, Integer>> query(List<Integer> ngram, List<Integer> prefix) {
    List<Pair<List<Integer>, Integer>> result = new LinkedList<Pair<List<Integer>, Integer>>();
    List<Integer> helper = new ArrayList<Integer>();
    for(int i=0; i<prefix.size(); ++i)
      helper.add(prefix.get(i));
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
      if(root.freq>0){
        List<Integer> ngram = new LinkedList<Integer>();
        for(Integer i:helper) ngram.add(i);
        result.add(new Pair<List<Integer>, Integer>(ngram, root.freq));
      }
      if(root.children == null){
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
    c.add(4);
    List<Integer> d = new LinkedList<Integer>();
    d.add(1);
    d.add(4);
    d.add(6);
    d.add(9);
    SuffixTree tree = new SuffixTree();
    tree.insert(a,0,4);
    tree.insert(b,0,4);
    tree.insert(c,0,2);
    tree.insert(d,0,4);
    tree.insert(c,0,2);
    tree.insert(d,0,4);
    tree.insert(d,0,4);

    List<Integer> e = new LinkedList<Integer>();
    e.add(1);
    e.add(4);
    /*List<Pair<List<Integer>, Integer>> result = tree.query(e);
    for(Pair<List<Integer>, Integer> list: result){
      System.out.println(list.second);
      for(Integer i: list.first){
        System.out.printf(" %d", i);
      }
      System.out.println();
    }
*/
  }


}