package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * Handles each incoming query, students do not need to change this class except
 * to provide more query time CGI arguments and the HTML output.
 * 
 * N.B. This class is not thread-safe. 
 * 
 * @author congyu
 * @author fdiaz
 */
class QueryHandler implements HttpHandler {

  /**
   * CGI arguments provided by the user through the URL. This will determine
   * which Ranker to use and what output format to adopt. For simplicity, all
   * arguments are publicly accessible.
   */
  public static class CgiArguments {
    // The raw user query
    public String _query = "";
    // How many results to return
    private int _numResults = 10;
    
    private int _numDocs = 0;
    
    private int _numTerms = 0;
    
    private int _docid = 0;
    
    private String _sessionid = "";
    
    private String _action = "";
    
    // The type of the ranker we will be using.
    public enum RankerType {
      NONE,
      FULLSCAN,
      CONJUNCTIVE,
      FAVORITE,
      COSINE,
      PHRASE,
      QL,
      LINEAR,
      COMPREHENSIVE,
    }
    public RankerType _rankerType = RankerType.NONE;
    
    // The type of suggestions we will be using
    public enum SuggestionType {
    	NONE,
        TERM,
        PHRASE,
    }
    public SuggestionType _suggestionType = SuggestionType.NONE;
    
    // The output format.
    public enum OutputFormat {
      TEXT,
      HTML,
    }
    public OutputFormat _outputFormat = OutputFormat.TEXT;

    public CgiArguments(String uriQuery) {
      String[] params = uriQuery.split("&");
      for (String param : params) {
        String[] keyval = param.split("=", 2);
        if (keyval.length < 2) {
          continue;
        }
        String key = keyval[0].toLowerCase();
        String val = keyval[1];
        if (key.equals("query")) {
          _query = val;
        } else if (key.equals("num")) {
          try {
            _numResults = Integer.parseInt(val);
          } catch (NumberFormatException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("numdocs")) {
        	try {
        	_numDocs = Integer.parseInt(val);
          } catch (NumberFormatException e){
        	  //ignored
          }
        } else if (key.equals("numterms")) {
        	try {
        	_numTerms = Integer.parseInt(val);
          } catch (NumberFormatException e){
        	  //ignored
          }
        } else if (key.equals("docid")) {
        	try {
        	_docid = Integer.parseInt(val);
          } catch (NumberFormatException e){
        	  //ignored
          }
        } else if (key.equals("sessionid")) {
        	try {
        	_sessionid = val;
          } catch (NumberFormatException e){
        	  //ignored
          }
        } else if (key.equals("action")) {
        	try {
        	_action = val;
          } catch (NumberFormatException e){
        	  //ignored
          }
        } else if (key.equals("ranker")) {
          try {
            _rankerType = RankerType.valueOf(val.toUpperCase());
          } catch (IllegalArgumentException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("suggestion")) {
          try {
              _suggestionType = SuggestionType.valueOf(val.toUpperCase());
          } catch (IllegalArgumentException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        } else if (key.equals("format")) {
          try {
            _outputFormat = OutputFormat.valueOf(val.toUpperCase());
          } catch (IllegalArgumentException e) {
            // Ignored, search engine should never fail upon invalid user input.
          }
        }
      }  // End of iterating over params
    }
  }

  // For accessing the underlying documents to be used by the Ranker. Since 
  // we are not worried about thread-safety here, the Indexer class must take
  // care of thread-safety.
  private Indexer _indexer;
  private Options _options;
  
  public QueryHandler(Options options, Indexer indexer) {
    _indexer = indexer;
    _options = options;
  }

  private void respondWithMsg(HttpExchange exchange, final String message)
      throws IOException {
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    responseHeaders.set("Access-Control-Allow-Origin", "*");
    exchange.sendResponseHeaders(200, 0); // arbitrary number of bytes
    OutputStream responseBody = exchange.getResponseBody();
    responseBody.write(message.getBytes());
    responseBody.close();
  }

  private void constructTextOutput(
      final Vector<ScoredDocument> docs, StringBuffer response) {
    for (ScoredDocument doc : docs) {
      response.append(response.length() > 0 ? "\n" : "");
      response.append(doc.asTextResult());
    }
    response.append(response.length() > 0 ? "\n" : "");
  }
  
  private void constructHTMLOutput(final Vector<ScoredDocument> docs,
		  String query, StringBuffer response) {
	    for (ScoredDocument doc : docs) {
	      response.append(response.length() > 0 ? "<br>" : "");
	      response.append(doc.asHtmlResult(query));
	    }
	    response.append(response.length() > 0 ? "<br>" : "");
	  }

  public void handle(HttpExchange exchange) {
    try {
      String requestMethod = exchange.getRequestMethod();
      if (!requestMethod.equalsIgnoreCase("GET")) { // GET requests only.
        return;
      }

      // Print the user request header.
      Headers requestHeaders = exchange.getRequestHeaders();
      System.out.print("Incoming request: ");
      for (String key : requestHeaders.keySet()) {
        System.out.print(key + ":" + requestHeaders.get(key) + "; ");
      }
      System.out.println();

      // Validate the incoming request.
      String uriQuery = exchange.getRequestURI().getQuery();
      String uriPath = exchange.getRequestURI().getPath();
      System.out.println(uriQuery);
      System.out.println(uriPath);
      if (uriPath == null || uriQuery == null) {
        respondWithMsg(exchange, "Something wrong with the URI!");
      }
      if (uriPath.equals("/suggest")) {
        CgiArguments cgiArgs = new CgiArguments(uriQuery);
        Ranker ranker = Ranker.Factory.getRankerByArguments(
            cgiArgs, SearchEngine.OPTIONS, _indexer);
        List<String> temp = null;
        switch (cgiArgs._suggestionType) {
          case TERM:
            temp = ((RankerFavorite) ranker).suggestUnigram(
                new Query(cgiArgs._query), 5);
            break;
          case PHRASE:
            temp = ((RankerFavorite) ranker).suggestNgrams(
                new Query(cgiArgs._query), 5);
            break;
          default:
        }
        String result = "";
        for (String s : temp) {
          result += s + "\n";
        }
        System.out.println("aaaaaaaa : " + result);
        respondWithMsg(exchange, result);
      } else if (uriPath.equals("/prf")) {
        // should write response here
        CgiArguments cgiArgs = new CgiArguments(uriQuery);
        Ranker ranker = Ranker.Factory.getRankerByArguments(
            cgiArgs, SearchEngine.OPTIONS, _indexer);
        // Processing the query.
        Query processedQuery = new Query(cgiArgs._query);
        processedQuery.processQuery();
        Vector<ScoredDocument> scoredDocs =
            ranker.runQuery(processedQuery, cgiArgs._numResults);
        // compute and write to file
        String result = ranker.expandQuery(scoredDocs,
            cgiArgs._query, cgiArgs._numDocs, cgiArgs._numTerms);
        respondWithMsg(exchange, result);
      } else if (uriPath.equals("/log")) {
        CgiArguments cgiArgs = new CgiArguments(uriQuery);
        Logger.log(cgiArgs._sessionid, cgiArgs._query,
            cgiArgs._docid, cgiArgs._action);
        respondWithMsg(exchange, "logged!");
      } else {
        System.out.println("Query: " + uriQuery);

        // Process the CGI arguments.
        CgiArguments cgiArgs = new CgiArguments(uriQuery);
        if (cgiArgs._query.isEmpty()) {
          respondWithMsg(exchange, "No query is given!");
        }

        // Create the ranker.
        Ranker ranker = Ranker.Factory.getRankerByArguments(
            cgiArgs, SearchEngine.OPTIONS, _indexer);
        if (ranker == null) {
          respondWithMsg(exchange,
              "Ranker " + cgiArgs._rankerType.toString() + " is not valid!");
        }

        // Processing the query.
        Query processedQuery = new Query(cgiArgs._query);
        processedQuery.processQuery();

        // Ranking.
        Vector<ScoredDocument> scoredDocs =
            ranker.runQuery(processedQuery, cgiArgs._numResults);
        StringBuffer response = new StringBuffer();
        switch (cgiArgs._outputFormat) {
          case TEXT:
            constructTextOutput(scoredDocs, response);
            break;
          case HTML:
            constructHTMLOutput(scoredDocs, cgiArgs._query, response);
            break;
          default:
            // nothing
        }
        respondWithMsg(exchange, response.toString());
        System.out.println("Finished query: " + cgiArgs._query);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

