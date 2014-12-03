/* TODO
 * 1. convert each word to lower case
 * 2. strip off punctuations
 * 3. build the Tree from only part of the corpus
 */
import java.util.*;
class SuffixTree {

  class TreeNode {
    int freq;
    Map<Integer, TreeNode> children;
    TreeNode(){
      freq = 0;
      //Key is wordIndex and value is the subtree
      children = new HashMap<Integer, TreeNode>();
    }
  };

  TreeNode root;

  SuffixTree (){
    root = new TreeNode();
  }

  public void insert(List<Integer> ngram) {
    insertIntoTree(ngram, 0, ngram.size());
  }

  protected void insertIntoTree(List<Integer> ngram, int start, int height){
    TreeNode current = root;
    for(int i=start; i<height; ++i){
      int index = ngram.get(i);
      if(current.children.containsKey(index) == false) {
        current.children.put(index, new TreeNode());
      }
      if(i == height -1) {
        current.children.get(index).freq++;
      }
      current = current.children.get(index);
    }
  }

  public List<List<Integer>> query(List<Integer> ngram) {
    List<List<Integer>> result = new LinkedList<List<Integer>>();
    List<Integer> helper = new ArrayList<Integer>();

    traverseTree(ngram, result, root, 0, helper);
    return result;
  }

  private void traverseTree(List<Integer> query, List<List<Integer>> result, TreeNode root, 
    int index, List<Integer> helper) {
    if(root == null) return;
    if(index < query.size()){
      if(root.children.containsKey(query.get(index))){
        helper.add(query.get(index));
        traverseTree(query, result, root.children.get(query.get(index)), index+1, helper);
      }
    }
    else{
      Set<Integer> keySet = root.children.keySet();
      for(Integer key: keySet){
        if(helper.size() <= index) helper.add(key);
        else helper.set(index, key);
        traverseTree(query, result, root.children.get(key), index+1, helper);
      }
      if(root.children.size()==0){
        List<Integer> ngram = new LinkedList<Integer>();
        for(Integer i:helper) ngram.add(i);
        result.add(ngram);
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
    tree.insert(a);
    tree.insert(b);
    tree.insert(c);
    tree.insert(d);

    List<Integer> e = new LinkedList<Integer>();
    e.add(1);
    e.add(2);
    List<List<Integer>> result = tree.query(e);
    for(List<Integer> list: result){
      for(Integer i: list){
        System.out.printf(" %d", i);
      }
      System.out.println();
    }

  }


}