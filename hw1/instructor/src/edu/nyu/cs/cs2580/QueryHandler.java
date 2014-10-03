package edu.nyu.cs.cs2580;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Iterator;
import java.util.Vector;
import java.net.URLDecoder;

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
    File file1 = new File("data/hw1.1-vsm.tsv");
    if(!file1.exists()){
    	file1.createNewFile();
    }
    File file2 = new File("data/hw1.1-ql.tsv");
    if(!file2.exists()){
    	file2.createNewFile();
    }
    File file3 = new File("data/hw1.1-phrase.tsv");
    if(!file3.exists()){
    	file3.createNewFile();
    }
    File file4 = new File("data/hw1.1-numviews.tsv");
    if(!file4.exists()){
    	file4.createNewFile();
    }
    PrintWriter out1 = new PrintWriter(file1);
    PrintWriter out2 = new PrintWriter(file2);
    PrintWriter out3 = new PrintWriter(file3);
    PrintWriter out4 = new PrintWriter(file4);
    //read in queries from queries.tsv, run them and write to files
    Scanner sc = new Scanner(new File("data/queries.tsv"));
    sc.useDelimiter("\n");
    int count_of_queries = 0;
    while(sc.hasNext()){
    	count_of_queries++;
    	String que = sc.next();
    	que = que.substring(0, que.length()-1);
    	Vector < ScoredDocument > cos = _ranker.runquery(que,"cosine");
    	out1.print(ScoredDocumentToString(que, cos));
    	Vector < ScoredDocument > QL = _ranker.runquery(que,"QL");
    	out2.print(ScoredDocumentToString(que, QL));
    	Vector < ScoredDocument > phr = _ranker.runquery(que,"phrase");
    	out3.print(ScoredDocumentToString(que, phr));
    	Vector < ScoredDocument > lin = _ranker.runquery(que,"linear");
    	out4.print(ScoredDocumentToString(que, lin));
    }
	out1.close(); out2.close(); out3.close(); out4.close(); sc.close();
    System.out.println("Done generating results for " + count_of_queries + " queries...");
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
    System.out.print("Incoming request: ");
    for (String key : requestHeaders.keySet()){
      System.out.print(key + ":" + requestHeaders.get(key) + "; ");
    }
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
            // process the result from ranker and generate String
            queryResponse = ScoredDocumentToString(query_map.get("query"), sds);
          } else {
            // if no ranker type is specified, use Linear Model
            Vector < ScoredDocument > sds = _ranker.runquery(query_map.get("query"),"default");
            queryResponse = ScoredDocumentToString(query_map.get("query"), sds);
          }
        }
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
      String htmlContent = "<head><link rel=\"stylesheet\" href=\"css/style.css\"></head>"
      +"<p>Search result:<br></p>" + queryResponse.replace("\n", "<br>")
      + "<p id=\"backToSearch\">Back to Home</p>";
      responseBody.write(htmlContent.getBytes());
    }
    responseBody.close();
  }
}
