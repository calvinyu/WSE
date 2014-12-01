import java.util.*;

class Trie {
  
  class TrieNode {
    TrieNode[] children;
    String word;
    public TrieNode() {
      children = new TrieNode[26];
    }
  };

  TrieNode root;

  Trie (){
    root = new TrieNode();
  }

  public void insert(String s) {
    s = s.toLowerCase();

    for(int i=0; i<s.length(); ++i)
      if(s.charAt(i) < 'a' || s.charAt(i) >'z') return;

    TrieNode current = root;
    for(int i=0; i<s.length(); ++i){
      int index = s.charAt(i) - 'a';
      if(current.children[index] == null) {
        current.children[index] = new TrieNode();
        if(i == s.length() - 1)
          current.children[index].word = s;
      }
      current = current.children[index];
    }
  }

  public List<String> query(String s) {
    s = s.toLowerCase();
    List<String> result = new LinkedList<String>();
    traverseTrie(s, result, root, 0);
    return result;
  }

  private void traverseTrie(String s, List<String> result, TrieNode root, int index) {
    if(root == null) return;
    if(index < s.length()){
      if(root.children[s.charAt(index)-'a'] != null){
        traverseTrie(s, result, root.children[s.charAt(index)-'a'], index+1);
      }
    }
    else if(root.word == null) {
      for(int i=0; i<26; ++i) {
        if(root.children[i] != null) {
          traverseTrie(s, result, root.children[i], index+1);
        }
      }
    }
    else {
      result.add(root.word);
    }
  }

  public static void main(String[] args) {
    Trie mytrie = new Trie();
    mytrie.insert("Calvin");
    mytrie.insert("Carl");
    mytrie.insert("Tin");
    mytrie.insert("Tim");
    mytrie.insert("Carlos");
    // read copcus from standard input
    Scanner kb = new Scanner(System.in);
    while(kb.hasNext()) {
      mytrie.insert(kb.next());
    }
    //set query word
    String query = "a";
    List<String> result = mytrie.query(query);
    //print out result
    for(String s: result) System.out.println(s);
  }

}