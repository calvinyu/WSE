package edu.nyu.cs.cs2580; 
/**
 * @author Calvin Yu
 */ 
import java.util.*;
import java.io.*;
class Spearman {
  static List<Integer> pageRankList;
  static List<Integer> numViewList;
  static Scanner kb;
  //expecting two arguments <PATH-TO-PAGERANKS> <PATH-TO-NUMVIEWS>
  public static void main(String[] args){
    if(args.length != 2){
      System.out.println("Wrong number of arguments were given!");
      System.out.println("expecting two arguments <PATH-TO-PAGERANKS> <PATH-TO-NUMVIEWS>");
      return;
    }
    //read
    pageRankList = readFromFile(args[0]);
    numViewList = readFromFile(args[1]); 
    if(pageRankList == null || numViewList == null) return;
    //calculate
    double result = calculateCorrelation(pageRankList, numViewList);
    System.out.println("Correction is " + result);
  }

  //Assume the input contains only Integers
  private static List<Integer> readFromFile(String path){
    List<Double> result = new ArrayList<Double>();
    try{
      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
      float [] b = (float[]) ois.readObject();
      for(float f:b) result.add((double)f);
    }
    catch(Exception e){};
    return processScore(result);
    /*
    //Open File
    try{
      kb = new Scanner(new File(path));
    }
    catch(IOException e){
      System.out.println("Can't read file: " + path);
      return null;
    }
    System.out.println("Content of " + path  + ":");
    //Storing and printing contents
    List<Double> result = new ArrayList<Double>();
    while(kb.hasNextDouble()){
      Double rank = kb.nextDouble();
      result.add(rank);
      System.out.println(rank);
    }
    System.out.println("End of content");
    return processScore(result);
    */
  }

  private static List<Integer> processScore(List<Double> score){
    List<Double> sorted = new ArrayList<Double>();
    for(Double i:score) sorted.add(i);
    Collections.sort(sorted);
    List<Integer> result = new ArrayList<Integer>();
    for(Double i:score) result.add(bs(sorted, i));
    return result;
  }

  private static int bs(List<Double> list, Double target){
    int low = 0;
    int high = list.size() - 1;
    while(low<=high){
      int m = (high+low)/2;
      if(list.get(m) >= target) high = m - 1;
      else if(list.get(m) < target) low = m + 1;
    }
    return low;
  }
  private static double calculateCorrelation(
          List<Integer> r1, List<Integer> r2){
    //initialization
    int n;
    double z;
    //check length
    if(r1.size() != r2.size() || r1.size() == 0){
      System.out.println("Length 1: " + r1.size() + ", Length 2: " + r2.size());
      System.out.println("Length doesn't match or equals zero");
      return -1;
    }
    //length
    n = r1.size();
    //calculate z
    z = 0;
    for(int rank: r1) z+= rank;
    z /= n;
    System.out.printf("Length = %d, z = %f\n", n, z);
    //calculating cor
    double inner = 0;
    double x2 = 0;
    double y2 = 0;
    for(int i=0; i<n; ++i){
        double a = r1.get(i) - z;
        double b = r2.get(i) - z;
        inner += a*b;
        x2 += a*a;
        y2 += b*b;
    }
    return inner/(x2*y2);
  }
}
