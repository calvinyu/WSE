package edu.nyu.cs.cs2580;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @CS2580: implement this class for HW2 to handle phrase. If the raw query is
 * ["new york city"], the presence of the phrase "new york city" must be
 * recorded here and be used in indexing and ranking.
 */
public class QueryPhrase extends Query {

  public QueryPhrase(String query) {
    super(query);
  }

  @Override
  public void processQuery() {
    Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
    Matcher regexMatcher = regex.matcher(_query);
    while (regexMatcher.find()) {
      if (regexMatcher.group(1) != null) {
        // Add double-quoted string without the quotes
        String word = regexMatcher.group(1);
        _tokens.add(regexMatcher.group(1));
      } else if (regexMatcher.group(2) != null) {
        // Add single-quoted string without the quotes
        String word = regexMatcher.group(2);
        _tokens.add(regexMatcher.group(2));
      } else {
        // Add unquoted word
        String word = regexMatcher.group();
        _tokens.add(regexMatcher.group());
      }
    }
  }
}
