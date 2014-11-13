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
    List<Integer> result = new ArrayList<Integer>();
    while(kb.hasNextInt()){
      int rank = kb.nextInt();
      result.add(rank);
      System.out.println(rank);
    }
    return result;
  }

  private static double calculateCorrelation(
          List<Integer> r1, List<Integer> r2){
    //initialization
    int n;
    double z;
    //check length
    if(r1.size() != r2.size() || r1.size() == 0){
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
