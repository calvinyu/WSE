package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 
 * @author Joshua
 *
 */
class QueryHandler implements HttpHandler {

  private String resultFormat = "not_specified";
  private Ranker _ranker;
  
  //Constructor
  public QueryHandler(Ranker ranker) throws IOException{
    _ranker = ranker;
    System.out.println("Generating results ...");
    //create corresponding files
    File parent = new File("./../results");
    if(!parent.exists()){
      parent.mkdir();
    }
    File file1 = new File("./../results/hw1.1-vsm.tsv");
    if(!file1.exists()){
    	file1.createNewFile();
    }
    File file2 = new File("./../results/hw1.1-ql.tsv");
    if(!file2.exists()){
    	file2.createNewFile();
    }
    File file3 = new File("./../results/hw1.1-phrase.tsv");
    if(!file3.exists()){
    	file3.createNewFile();
    }
    File file4 = new File("./../results/hw1.1-numviews.tsv");
    if(!file4.exists()){
    	file4.createNewFile();
    }
    File file5 = new File("./../results/hw1.2-linear.tsv");
    if(!file5.exists()){
      file5.createNewFile();
    }

    PrintWriter out1 = new PrintWriter(file1);
    PrintWriter out2 = new PrintWriter(file2);
    PrintWriter out3 = new PrintWriter(file3);
    PrintWriter out4 = new PrintWriter(file4);
    PrintWriter out5 = new PrintWriter(file5);
    //read in queries from queries.tsv, run them and write to files
    Scanner sc = new Scanner(new File("./../data/queries.tsv"));
    int count_of_queries = 0;
    while(sc.hasNextLine()){
      count_of_queries++;
    	String que = sc.nextLine();
    	Vector < ScoredDocument > cos = _ranker.runquery(que,"cosine");
    	out1.print(ScoredDocumentToString(que, cos));
    	Vector < ScoredDocument > QL = _ranker.runquery(que,"QL");
    	out2.print(ScoredDocumentToString(que, QL));
    	Vector < ScoredDocument > phr = _ranker.runquery(que,"phrase");
    	out3.print(ScoredDocumentToString(que, phr));
    	Vector < ScoredDocument > numviews = _ranker.runquery(que,"numviews");
    	out4.print(ScoredDocumentToString(que, numviews));
      Vector < ScoredDocument > lin = _ranker.runquery(que, "lin");
      out5.print(ScoredDocumentToString(que, lin));
    }
	out1.close(); out2.close(); out3.close(); out4.close(); out5.close(); sc.close();
    System.out.println("Done generating results for " + count_of_queries + " queries...");
    System.out.println("Evaluating files ...");
    Evaluator.evaluateAll(_ranker);
    System.out.println("Done Evaluating Files!");
  }
  
  public static String ScoredDocumentToString(String query, Vector < ScoredDocument > sds){
	  String queryResponse = "";
	  Iterator < ScoredDocument > itr = sds.iterator();
      while (itr.hasNext()){
        ScoredDocument sd = itr.next();
        if (queryResponse.length() > 0){
          queryResponse = queryResponse + "\n";
        }
        // Transfer a ScoredDocument into a String
        queryResponse = queryResponse + query + "\t" + sd.asString();
      }
      if (queryResponse.length() > 0){
        queryResponse = queryResponse + "\n";
      }
      return queryResponse;
  }
  
  public static String ScoredDocumentToHTML(String query, Vector < ScoredDocument > sds){
	  String queryResponse = "";
	  Date dd = new Date();
	  String sessionid = "S-"+String.valueOf(dd.getTime()).substring(5);
	  Iterator < ScoredDocument > itr = sds.iterator();
      while (itr.hasNext()){
        ScoredDocument sd = itr.next();
        if (queryResponse.length() > 0){
          queryResponse = queryResponse + "\n";
        }
        // Transfer a ScoredDocument into a HTML element with hyperlink
        String temp = sd.asString();
        String docid = temp.substring(0, temp.indexOf("\t"));
        String hyperlink1 = "sessionid=" + sessionid + "&action=click&query=" + query + "&docid=" + docid;
        String hyperlink = "<a href=\"http://localhost:25813/log?" + hyperlink1
        		+ "\" target=\"_blank\">";
        queryResponse = queryResponse + hyperlink + query + "\t" + sd.asString() + "</a>";
        // Log each result
        try {
			Logger.log(sessionid, query, docid, "render");
		} catch (IOException e){
			e.printStackTrace();
		}
      }
      if (queryResponse.length() > 0){
        queryResponse = queryResponse + "\n";
      }
      return queryResponse;
  }

  // Store attributes(keys) and values in a hash map
  public static Map<String, String> getQueryMap(String query){
    String[] params = query.split("&");
    Map<String, String> map = new HashMap<String, String>();
    for (String param : params){
      String name = param.split("=")[0];
      String value = param.split("=")[1];
      map.put(name, value);
    }
    return map;
  }
  
  // Implement the actual handling part
  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    if (!requestMethod.equalsIgnoreCase("GET")){  // GET requests only.
      return;
    }

    // Print the user request header.
    Headers requestHeaders = exchange.getRequestHeaders();
    //System.out.print("Incoming request: ");
    //for (String key : requestHeaders.keySet()){
    //  System.out.print(key + ":" + requestHeaders.get(key) + "; ");
    //}
    System.out.println();
    
    // Get query and path in the RequestURI and decode them
    String queryResponse = "";
    String uriQuery = exchange.getRequestURI().getQuery();
    uriQuery = URLDecoder.decode(uriQuery, "UTF-8");
    String uriPath = exchange.getRequestURI().getPath();
    uriPath = URLDecoder.decode(uriPath, "UTF-8");

    if ((uriPath != null) && (uriQuery != null)){
      if (uriPath.equals("/search")){
        Map<String, String> query_map = getQueryMap(uriQuery);
        Set<String> keys = query_map.keySet();
        // Get the format attribute for future use
        if(keys.contains("format")){
        	resultFormat = query_map.get("format");
        }
        if (keys.contains("query")){
          if (keys.contains("ranker")){
            String ranker_type = query_map.get("ranker");
            Vector < ScoredDocument > sds = null;
            if (ranker_type.equals("cosine")){
                sds = _ranker.runquery(query_map.get("query"),"cosine");
            } else if (ranker_type.equals("QL")){
            	sds = _ranker.runquery(query_map.get("query"),"QL");
            } else if (ranker_type.equals("phrase")){
            	sds = _ranker.runquery(query_map.get("query"),"phrase");
            } else if (ranker_type.equals("linear")){
            	sds = _ranker.runquery(query_map.get("query"),"linear");
            } else {
              queryResponse = (ranker_type+" not implemented yet.");
            }
            // process the result from ranker and generate corresponding format
            if(resultFormat.equals("html")){
            	queryResponse = ScoredDocumentToHTML(query_map.get("query"), sds);
            } else queryResponse = ScoredDocumentToString(query_map.get("query"), sds);
          } else {
            // if no ranker type is specified, use Linear Model
            Vector < ScoredDocument > sds = _ranker.runquery(query_map.get("query"),"default");
            if(resultFormat.equals("html")){
            	queryResponse = ScoredDocumentToHTML(query_map.get("query"), sds);
            } else queryResponse = ScoredDocumentToString(query_map.get("query"), sds);
          }
        }
      }
      else if (uriPath.equals("/log")){
    	  Map<String, String> query_map = getQueryMap(uriQuery);
    	  String query = query_map.get("query");
    	  String docid = query_map.get("docid");
    	  String sessionid = query_map.get("sessionid");
    	  resultFormat = "text";
    	  Logger.log(sessionid, query, docid, "click");
    	  queryResponse = "Click logged!";
      }
    }
    
    // Construct a simple response.
    Headers responseHeaders = exchange.getResponseHeaders();
    OutputStream responseBody = exchange.getResponseBody();
    if(resultFormat.equals("text")||resultFormat.equals("not_specified")){
      responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0);  // arbitrary number of bytes
      responseBody.write(queryResponse.getBytes());
    } else if(resultFormat.equals("html")){
      responseHeaders.set("Content-Type", "text/html");
      exchange.sendResponseHeaders(200, 0);  // arbitrary number of bytes
      String htmlContent = "<head><script src=\"\"></script></head>"
      +"<p>Search result:<br></p>" + queryResponse.replace("\n", "<br>")
      + "<p id=\"backToSearch\">Back to Home</p>";
      responseBody.write(htmlContent.getBytes());
    }
    responseBody.close();
  }
}
