package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.io.*;
import java.util.*;
import java.lang.Math;
class Evaluator {
  static class PRPair {
    double recall;
    double precision;
    public PRPair(double precision, double recall) {
      this.recall = recall;
      this.precision = precision;
    }
  };

  public static void main(String[] args) throws IOException {
    HashMap < String , HashMap < Integer , Double > > relevance_judgments =
    new HashMap < String , HashMap < Integer , Double > >();
//Put all true score of  stdin in this array
    List<Double> result = new ArrayList<Double>();
    String response = "";
    if (args.length < 1){
      System.out.println("need to provide relevance_judgments");
      return;
    }
    String p = args[0];
// first read the relevance judgments into the HashMap
    readRelevanceJudgments(p,relevance_judgments);
// now evaluate the results from stdin
    response += parseStdin(relevance_judgments, result, System.in);
    response += getMetrics(result);
    System.out.println(response);
//generate evalutation files
    System.out.println("Evaluating...");
    //generateFiles(relevance_judgments);
  }
  public static void evaluateAll(Ranker ranker) {
    HashMap < String , HashMap < Integer , Double > > relevance_judgments =
    new HashMap < String , HashMap < Integer , Double > >();
    readRelevanceJudgments("../data/qrels.tsv", relevance_judgments);
    generateFiles(relevance_judgments, ranker);
  }
  public static void generateFiles(
    HashMap <String, HashMap<Integer, Double> > relevance_judgments,
    Ranker ranker) {
//read queries from data/queries
    ArrayList< String > queries = new ArrayList < String > ();
    readFromFile("./../data/queries.tsv", queries);
//rankers
    String[] ranker_types = {"cosine", "QL", "phrase", "numviews", "linear"};
    String[] ranker_types_for_file = {"vsm", "ql", "phrase", "numviews", "linear"};

    for( int i=0; i<ranker_types.length; ++i){
      String ranker_type = ranker_types[i];
//get results from Ranker
      String evaluations = "";
      for( String query : queries ) {
        List<Double> result = new ArrayList<Double>();
        evaluations += query;
        try {
          String searchResult = QueryHandler.ScoredDocumentToString(
            query, ranker.runquery(query, ranker_type));
          parseStdin(relevance_judgments, result, 
            new ByteArrayInputStream(searchResult.getBytes())); 
        }catch(Exception e){
          System.out.println("Error: " + e.getMessage());
        }
        evaluations += getMetrics(result) + "\n";
      }
//write to file
      writeToFile("./../results/hw1.3-" + ranker_types_for_file[i] + ".tsv", evaluations);

    }
  }

  public static void writeToFile(String filePath, String data) {
    File file = new File(filePath);
    FileWriter fr = null;
    try{
      fr = new FileWriter(file);
      fr.write(data);
    }catch(IOException e){
      System.out.println("Error: " + e.getMessage());
    }finally{
      try{
        fr.close();
      }catch(IOException e){
        System.out.println("Error: " + e.getMessage());
      }
    }
  }

  public static void readFromFile(String filePath, List< String > output ) {
    try {
      BufferedReader reader =new BufferedReader(new InputStreamReader(
        new FileInputStream(new File(filePath))));
      String line = null;
      while((line = reader.readLine()) != null) {
        output.add(line);
      }
    }catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  public static String getMetrics(List<Double> result) {  
    String response = "";
// Precision at 1, 5, 10
    response += '\t' + Double.toString(evaluatePrecision(result, 1));
    response += '\t' + Double.toString(evaluatePrecision(result, 5));
    response += '\t' + Double.toString(evaluatePrecision(result, 10));
// Recall at 1, 5, 10
    response += '\t' + Double.toString(evaluateRecall(result, 1));
    response += '\t' + Double.toString(evaluateRecall(result, 5));
    response += '\t' + Double.toString(evaluateRecall(result, 10));
// F(0.50) at 1, 5, 10
    response += '\t' + Double.toString(evaluateF(result, 0.5, 1));
    response += '\t' + Double.toString(evaluateF(result, 0.5, 5));
    response += '\t' + Double.toString(evaluateF(result, 0.5, 10));
// Precision at recall points {0.0, 0.1, 0.2,...,1.0}
// 1. Build the graph
    PRPair[] PRGraph = new PRPair[result.size() + 1];
    buildPR(result, PRGraph);
// 2. Calculate precision at points.
    for(double i = 0.0; i <= 1.0; i += 0.1) {
      response += '\t' + Double.toString(getPrecisionAtRecall(PRGraph, i));
    }
// Average precision
    response += '\t' + Double.toString(getAveragePrecision(result));
// nDCG at 1, 5, 10 (using the gain values presented in Lecture 2)  
    response += '\t' + Double.toString(evaluateNDCG(result, 1));
    response += '\t' + Double.toString(evaluateNDCG(result, 5));
    response += '\t' + Double.toString(evaluateNDCG(result, 10));
// Reciprocal rank
    response += '\t' + Double.toString(evaluateReciprocal(result));
    return response;
/* FORMAT 
* <QUERY><TAB><METRIC_0>...<TAB><METRIC_N>
*/
  }

  public static double evaluateNDCG(List <Double> result, int K) {
    double[] optimalRelevanceOrder = new double[K];
    double dcg = result.get(0);
    optimalRelevanceOrder[0] = result.get(0);
    for(int i=1; i<K; ++i){
      dcg += result.get(i) / (Math.log(1+i) / Math.log(2));
      optimalRelevanceOrder[i] = result.get(i);
    }
    Arrays.sort(optimalRelevanceOrder);
    double mdcg = optimalRelevanceOrder[K-1];
    for(int i=1; i<K; ++i) {
      mdcg += optimalRelevanceOrder[K-i-1] / (Math.log(i+1) / Math.log(2));
    }
    if(mdcg == 0 ) return 0;
    return dcg/mdcg;  
  }

  public static void readRelevanceJudgments(
    String p,HashMap < String , HashMap < Integer , Double > > relevance_judgments){
    try {
      BufferedReader reader = new BufferedReader(new FileReader(p));
      try {
        String line = null;
        while ((line = reader.readLine()) != null){
  // parse the query,did,relevance line
          Scanner s = new Scanner(line).useDelimiter("\t");
          String query = s.next();
          int did = Integer.parseInt(s.next());
          String grade = s.next();
          double rel = 0.0;
  // convert to binary relevance
          if(grade.equals("Perfect")) rel = 10.0;
          else if(grade.equals("Excellent")) rel = 7.0;
          else if(grade.equals("Good")) rel = 5.0;
          else if(grade.equals("Fair")) rel = 1.0;
          else rel = 1.0;
          if (relevance_judgments.containsKey(query) == false){
            HashMap < Integer , Double > qr = new HashMap < Integer , Double >();
            relevance_judgments.put(query,qr);
          }
          HashMap < Integer , Double > qr = relevance_judgments.get(query);
          qr.put(did,rel);
        }
      } finally {
        reader.close();
      }
    } catch (IOException ioe){
      System.err.println("Oops " + ioe.getMessage());
    }
  }



  public static double evaluateReciprocal(
    List < Double > result){
    double precision = 0.0;
    int RR = 0;
    for(int i = 0; i < result.size(); ++i) {
      double rel = result.get(i);
      if( rel >= 5.0 ){ 
        return (double)1/(i+1);
      }
    }
    return 0.0;
  } 

  public static double evaluatePrecision(
    List < Double > result, int K){
    int RR = 0;
    for(int i = 0; i < K; ++i) {
      double rel = result.get(i);
      if( rel >= 5.0 ){ 
        RR ++;
      }
    }
    if( K == 0) return 0;
    else return (double)RR / K;
  } 

  public static double getAveragePrecision(
    List < Double > result){
    double AP = 0.0;
    int RR = 0;
    for(int i = 0; i < result.size(); ++i) {
      double rel = result.get(i);
      if( rel >= 5.0 ){ 
        RR ++; 
        AP += (double)RR/(i+1);
      }
    }
    if( RR == 0 ) return 0; 
    return AP/RR;
  } 


  public static double evaluateRecall(
    List < Double > result, int K){
    int RR = 0;
    int R = 0;
    for(int i = 0; i < result.size(); ++i) {
      double rel = result.get(i); 
      if( rel >= 5.0 && i < K){ 
        RR ++;
      }
      if(rel >= 5.0) R++;
    }
    if( R == 0 ) return 0;
    else return (double)RR / R;
  } 

  public static double evaluateF(
    List < Double > result, double alpha, int K){
    double f = 0.0;
    double P = evaluatePrecision(result, K);
    double R = evaluateRecall(result, K);
    f = 1.0/(alpha/P + (1-alpha)/R);
    return f;
  } 

  public static void buildPR(
    List < Double > result, PRPair[] PRGraph) {
    for(int i = 0; i < PRGraph.length; ++i) {
      double p = evaluatePrecision(result, i);
      double r = evaluateRecall(result, i);
      PRGraph[i] = new PRPair(p, r);
    }
  }

  public static double getPrecisionAtRecall(PRPair[] PRGraph, double x) {
    if( x < 0 || x > 1) throw 
      new IllegalArgumentException("recall point should be between 0 and 1");
    //if( x == 0.0 ) return 1.0;
    for(int i=1; i<PRGraph.length; ++i) {
      if(PRGraph[i].recall == x) 
        return PRGraph[i].precision;
      if(PRGraph[i-1].recall < x && PRGraph[i].recall > x) {
        double rx = PRGraph[i-1].recall;
        double ry = PRGraph[i].recall;
        double px = PRGraph[i-1].precision;
        double py = PRGraph[i].precision;
        return ( px * (ry - x) + py * (x - rx) ) / (ry - rx);
      } 
    }
    return 0;
  }

  public static String parseStdin(
    HashMap < String , HashMap < Integer , Double > > relevance_judgments,
    List <Double> result, InputStream fin){
    String query = "";
  // only consider one query per call 
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

      String line = null;
      while ((line = reader.readLine()) != null){
        Scanner s = new Scanner(line).useDelimiter("\t");
        query = s.next();
        int did = Integer.parseInt(s.next());
        String title = s.next();
        double score = Double.parseDouble(s.next());
        if (relevance_judgments.containsKey(query) == false){
          throw new IOException("query not found");
        }
        HashMap < Integer , Double > qr = relevance_judgments.get(query);
        if (qr.containsKey(did) != false){
          result.add(qr.get(did));
        }
        else{
          result.add(0.0);
        }
      }
    } catch (Exception e){
      System.err.println("Error:" + e.getMessage());
    }
    return query;
  }
}
