import java.util.*;
class SuffixTrie extends Trie{

  SuffixTrie(){
    super();
  }

  @Override
  public void insert(String s) {
    s = s.toLowerCase();
    for(int i=0; i<s.length(); ++i)
      if(s.charAt(i) < 'a' || s.charAt(i) >'z') return;
    for(int suf=0; suf<s.length()-1; ++suf){
      TrieNode current = root;
      for(int i=suf; i<s.length(); ++i){
        int index = s.charAt(i) - 'a';
        if(current.children[index] == null) {
          current.children[index] = new TrieNode();
          if(i == s.length() - 1)
            current.children[index].word = s;
        }
        current = current.children[index];
      }
    }
  }


  public static void main(String[] args) {
    SuffixTrie mytrie = new SuffixTrie();
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
    String query = "ly";
    List<String> result = mytrie.query(query);
    //print out result
    for(String s: result) System.out.println(s);
  }
}