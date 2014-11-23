package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

/**
 * 
 * @author Joshua
 *
 */
public class Bhattacharyya {
	public void computeQueries(String prf, String output) throws IOException{
		File prfFile = new File(prf);
		File oo = new File(output);
		PrintWriter out = new PrintWriter(oo);
		if(!oo.exists()) oo.createNewFile();
		Vector<String> queries = new Vector<String>(); 
		Vector<HashMap<String, Double>> scores = new Vector<HashMap<String, Double>>();
		readFile(prfFile, queries, scores);
		for(int i=0;i<scores.size();i++)
			for(int j=i+1;j<scores.size();j++)
				out.write(queries.get(i) + "\t" + queries.get(j) + "\t" + 
						compareProb(scores.get(i), scores.get(j)) + "\n");
		out.flush(); out.close();
	}
	
	// take two hashmaps and return the similarity
	public double compareProb(HashMap<String, Double> h1,
			HashMap<String, Double> h2) {
		double score = 0;
		Set<Entry<String, Double>> set = h1.entrySet();
	    Iterator<Entry<String, Double>> iterator = set.iterator();
	    while (iterator.hasNext()){
	    	Entry<String,Double> me = iterator.next();
	    	if(h2.containsKey(me.getKey())){
	    		score += Math.sqrt(me.getValue() * h2.get(me.getKey()));
	    	}
	    }
		return score;
	}

	// read a file, get all directories and read them
	public void readFile(File file, Vector<String> queries, 
			Vector<HashMap<String, Double>> scores) throws FileNotFoundException {
		Scanner sc = new Scanner(file);
		while(sc.hasNextLine()){
			String next = sc.nextLine();
			String nextquery = next.split(":")[0];
			queries.add(nextquery);
			String nextfile = next.split(":")[1];
			Scanner temp = new Scanner(new File(nextfile));
			HashMap<String, Double> hm = new HashMap<String,Double>();
			while(temp.hasNextLine()){
				String stat = temp.nextLine();
				hm.put(stat.split("\t")[0], Double.parseDouble(stat.split("\t")[1]));
			}
			temp.close();
			scores.add(hm);
		}
		sc.close();
	}
	
	public static void main(String args[]) throws IOException{
		Bhattacharyya bh = new Bhattacharyya();
		bh.computeQueries(args[0], args[1]);
		System.out.println("Results saved to " + args[1]);
	}
}
