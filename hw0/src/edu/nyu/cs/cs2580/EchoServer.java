package edu.nyu.cs.cs2580;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EchoServer {

  // Our group number is 13.
  private static int port = 25813;
  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    // Create the server.
    InetSocketAddress addr = new InetSocketAddress(port);
    HttpServer server = HttpServer.create(addr, -1);

    // Attach specific paths to their handlers.
    server.createContext("/search", new EchoHandler());
    server.setExecutor(Executors.newCachedThreadPool());
    server.start();
    System.out.println("Listening on port: " + Integer.toString(port));
  }
}

/**
 * Modified instructor's code.
 */
class EchoHandler implements HttpHandler {
  public void handle(HttpExchange exchange) throws IOException {
    String requestMethod = exchange.getRequestMethod();
    if (!requestMethod.equalsIgnoreCase("GET")) { // GET requests only.
      return;
    }
    // Extract CGI arguments.
    HashMap<String, String> cgiMap = new HashMap<String, String>();
    String query = exchange.getRequestURI().getQuery();
    String[] cgiArgs = query.split("&");
    for (String cgiArg : cgiArgs) {
      String[] nameValPair = cgiArg.split("=");
      if (nameValPair.length >= 2) {
        cgiMap.put(nameValPair[0], nameValPair[1]);
      }
    }
    // Generate output.
    String output = "";
    String queryArg = cgiMap.get("query");
    if (queryArg != null) output = queryArg.replace("+", " ");
    output += "\n";
    // Create HTTP response.
    Headers responseHeaders = exchange.getResponseHeaders();
    responseHeaders.set("Content-Type", "text/plain");
    exchange.sendResponseHeaders(200, 0); // Arbitrary number of bytes.
    OutputStream responseBody = exchange.getResponseBody();
    responseBody.write(output.getBytes());
    responseBody.close();
  }
}
