package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Logger {
	public static void log(String sessionid, String query, String docid, String action) throws IOException{
    Date dd = new Date();
    String time = dd.toString();

  	File file = new File("./../results/hw1.4-log.tsv");
    if(!file.exists()){
      file.createNewFile();
    }
    FileWriter out = new FileWriter(file, true);
    out.write(sessionid + "\t" + query + "\t" + docid + "\t" + action + "\t" + time + "\n");
    out.flush(); out.close();
	}

}
