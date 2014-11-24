package edu.nyu.cs.cs2580;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3.
 */
public class CorpusAnalyzerPagerank extends CorpusAnalyzer {
  public CorpusAnalyzerPagerank(Options options) {
    super(options);
  }

  /**
   * This function processes the corpus as specified inside {@link _options}
   * and extracts the "internal" graph structure from the pages inside the
   * corpus. Internal means we only store links between two pages that are both
   * inside the corpus.
   * 
   * Note that you will not be implementing a real crawler. Instead, the corpus
   * you are processing can be simply read from the disk. All you need to do is
   * reading the files one by one, parsing them, extracting the links for them,
   * and computing the graph composed of all and only links that connect two
   * pages that are both in the corpus.
   * 
   * Note that you will need to design the data structure for storing the
   * resulting graph, which will be used by the {@link compute} function. Since
   * the graph may be large, it may be necessary to store partial graphs to
   * disk before producing the final graph.
   *
   * @throws IOException
   */
  @Override
  public void prepare() throws IOException {
    // Retrieves the set of documents
    File corpusDir = new File(_options._corpusPrefix);
    HashMap<String, Integer> docNames = new HashMap<String, Integer>();
    int docid = 0;
    for (File file : corpusDir.listFiles()) {
      docNames.put(file.getName(), docid);
      docid++;
    }
    _options._docNames = docNames;
    // initialize the adjacency list
    int[][] adjacencyList = new int[docNames.size()][];
    // Construct adjacency list
    docid = 0;
    for (File file : corpusDir.listFiles()) {
      HeuristicLinkExtractor linkExtractor = new HeuristicLinkExtractor(file);
      String target;
      Vector<Integer> tmpTargetList = new Vector<Integer>();
      while ((target = linkExtractor.getNextInCorpusLinkTarget()) != null) {
        if (docNames.containsKey(target)) tmpTargetList.add(docNames.get(target));
      }
      adjacencyList[docid] = new int[tmpTargetList.size()];
      for (int i = 0; i < tmpTargetList.size(); i++) {
        adjacencyList[docid][i] = tmpTargetList.get(i);
      }
      docid++;
    }
    //handle redirect
    handleRedirect(adjacencyList, docNames);
    String graphFile = _options._indexPrefix + "/graph.idx";
    System.out.println("Store corpus graph to: " + graphFile);
    ObjectOutputStream writer =
        new ObjectOutputStream(new FileOutputStream(graphFile));
    writer.writeObject(adjacencyList);
    writer.close();
    // TODO Possibly, this implementation might yield memory error.
  }
  /**
   * This function modifies the redirected links to their final destination.
   **/
  private void handleRedirect(int[][] adjacencyList, HashMap<String, Integer> docNames)
   throws IOException{
    //read files
    File corpusDir = new File(_options._corpusPrefix);
    //set links to -1 if it's not a redirect page
    int links[] = new int[docNames.size()];
    for(int i=0; i<links.length; ++i) links[i] = -1;
    int docid = 0;
    //look into the file to see if it's a redirect file
    System.out.println("getting redirection");
    for (File file : corpusDir.listFiles()) {
      HeuristicLinkExtractor linkExtractor = new HeuristicLinkExtractor(file);
      String target = linkExtractor.getRedirectedTarget();
      if(target != null && docNames.containsKey(target)) links[docid] = docNames.get(target);
      docid++;
    }
    System.out.println("done getting redirection");
    //points all redirecttion to the correct page
    System.out.println("DFS starting");
    for(int i=0; i<links.length; ++i) if(links[i]!= -1) links[i] = dfs(links, i);
    System.out.println("DFS ending");
    //modify adj
    System.out.println("modifying redirection...");
    for(int i=0; i<adjacencyList.length; ++i){
      for(int j=0; j<adjacencyList[i].length; ++j){
        if(links[adjacencyList[i][j]] != -1 ) adjacencyList[i][j] = links[adjacencyList[i][j]];
      }
    }
    System.out.println("done modifying redirection");
  }
  
  private int dfs(int[] links, int index) {
    if(links[index] == -1) return index;
    return links[index] = dfs(links, links[index]);
  }

  /**
   * This function computes the PageRank based on the internal graph generated
   * by the {@link prepare} function, and stores the PageRank to be used for
   * ranking.
   * 
   * Note that you will have to store the computed PageRank with each document
   * the same way you do the indexing for HW2. I.e., the PageRank information
   * becomes part of the index and can be used for ranking in serve mode. Thus,
   * you should store the whatever is needed inside the same directory as
   * specified by _indexPrefix inside {@link _options}.
   *
   * @throws IOException
   */
  @Override
  public void compute() throws IOException, ClassNotFoundException {
    float lambda = _options._lambda;   // TODO: Should be put in the options
    int iters = _options._numIter;   // TODO: Should be put in the options
    // Load from the file.
    String graphFile = _options._indexPrefix + "/graph.idx";
    System.out.println("Load corpus graph from: " + graphFile);
    ObjectInputStream reader =
        new ObjectInputStream(new FileInputStream(graphFile));
    int[][] adjacencyList = (int[][]) reader.readObject();
    // Initialize the page rank.
    float[] prevPageRank = new float[adjacencyList.length];
    for (int i = 0; i < prevPageRank.length; i++) { prevPageRank[i] = 1.0f / prevPageRank.length; }
    // Compute the update for page rank
    float[] pageRank = new float[prevPageRank.length];
    for (int i = 0; i < iters; i++) {
      for (int j = 0; j < pageRank.length; j++) { pageRank[j] = (1.0f - lambda) / pageRank.length; }
      for (int j = 0; j < pageRank.length; j++) {
        for (int k : adjacencyList[j]) {
          pageRank[k] += lambda * prevPageRank[j] / adjacencyList[j].length;
        }
      }
      // Copy the current page rank to the prevPageRank
      System.arraycopy(pageRank, 0, prevPageRank, 0, pageRank.length);
    }
    String pageRankFile = _options._indexPrefix + "/pagerank.idx";
    System.out.println("Store page rank to: " + pageRankFile);
    ObjectOutputStream writer =
        new ObjectOutputStream(new FileOutputStream(pageRankFile));
    writer.writeObject(pageRank);
    writer.close();
  }

  /**
   * During indexing mode, this function loads the PageRank values computed
   * during mining mode to be used by the indexer.
   *
   * @throws IOException
   */
  @Override
  public Object load() throws IOException, ClassNotFoundException {
    String pageRankFile = _options._indexPrefix + "/pagerank.idx";
    System.out.println("Load page rank from: " + pageRankFile);
    ObjectInputStream reader =
        new ObjectInputStream(new FileInputStream(pageRankFile));
    return reader.readObject();
  }
}
