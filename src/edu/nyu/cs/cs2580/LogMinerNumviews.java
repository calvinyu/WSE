package edu.nyu.cs.cs2580;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import edu.nyu.cs.cs2580.SearchEngine.Options;

/**
 * @CS2580: Implement this class for HW3.
 */
public class LogMinerNumviews extends LogMiner {

  public LogMinerNumviews(Options options) {
    super(options);
  }

  /**
   * This function processes the logs within the log directory as specified by
   * the {@link _options}. The logs are obtained from Wikipedia dumps and have
   * the following format per line: [language]<space>[article]<space>[#views].
   * Those view information are to be extracted for documents in our corpus and
   * stored somewhere to be used during indexing.
   *
   * Note that the log contains view information for all articles in Wikipedia
   * and it is necessary to locate the information about articles within our
   * corpus.
   *
   * @throws IOException
   */
  @Override
  public void compute() throws IOException {
    File logDir = new File(_options._logPrefix);
    int[] numviews = new int[_options._docNames.size()];
    for (File file : logDir.listFiles()) {
      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line;
      while ((line = reader.readLine()) != null) {
        String[] lines = line.trim().split(" ");
        if (lines.length < 3 || !lines[2].matches("\\d+")) continue;
        String[] docList = lines[1].split("/");
        String basename;
        if (docList.length != 0) basename = docList[docList.length - 1];
        else basename = lines[1];
        if (_options._docNames.containsKey(basename)) {
          if(_options._docNames.containsKey(basename+".html"))
            numviews[_options._docNames.get(basename+".html")] += Integer.parseInt(lines[2]);
          else
            numviews[_options._docNames.get(basename)] += Integer.parseInt(lines[2]);
        }
      }
      reader.close();
    }

    String numviewsFile = _options._indexPrefix + "/numviews.idx";
    System.out.println("Store page rank to: " + numviewsFile);
    ObjectOutputStream writer =
        new ObjectOutputStream(new FileOutputStream(numviewsFile));
    writer.writeObject(numviews);
    writer.close();
  }

  /**
   * During indexing mode, this function loads the NumViews values computed
   * during mining mode to be used by the indexer.
   * 
   * @throws IOException
   */
  @Override
  @SuppressWarnings("unchecked")
  public Object load() throws IOException, ClassNotFoundException {
    String numviewsFile = _options._indexPrefix + "/numviews.idx";
    System.out.println("Load number of views from: " + numviewsFile);
    ObjectInputStream reader =
        new ObjectInputStream(new FileInputStream(numviewsFile));
    return reader.readObject();
  }
}
