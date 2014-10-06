Web Search Engine HW1

1. Basic usage

In order to compile the search engine, move to src directory and use the following command:

$ javac edu/nyu/cs/cs2580/*.java

To run the server, enter use

$ java edu.nyu.cs.cs2580.SearchEngine 25813 ./../data/corpus.tsv

Then, to use the search engine, use curl command on a terminal

$ curl "<HOST>:25813/search?query=<QUERY>&ranker=<RANKER-TYPE>&format=<FORMAT-TYPE>"

or use a web browser and enter

http://<HOST>:<PORT>/search?query=<QUERY>&ranker=<RANKER-TYPE>&format=<FORMAT-TYPE>

2. Ranker type

- cosine

The score is based on cosine distance between query vector and document (body) vector, represented as L2-normalized tf-idf vectors. We used idf in the lecture slide, 1 + log2(# of documents / # of documents in which a term shows up).

- QL

The score is based on the natural logarithm of language model probability of the query.

- phrase

The score is based on the number of occurrences of query bigrams in the document. If the length of the query is one, the number of unigram occurences is used. No particular normalization is done.

- numviews

The score is based on the number of views. No particular normalization is done.

- linear

The score is based on the linear combination of the above scores. More precisely, score = 2 * (cosine) + 0.1 * (QL) + 0.005 * (phrase) + 0.000005 * (numviews).

3. Format type

If <FORMAT-TYPE>=text, the output is generated in a text format, and if <FORMAT-TYPE>=html, the output is generated in an html format. For html format, the results are clickable and they are linked to logging pages (and further redirected to the real contents pages in a real search engine).