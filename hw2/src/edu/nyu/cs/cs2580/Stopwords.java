package edu.nyu.cs.cs2580;

import java.io.Serializable;
import java.lang.String;
import java.util.Vector;

public class Stopwords implements Serializable {

  private static final long serialVersionUID = -2614350730206565859L;
  
  public Vector<String> wordsList = new Vector<String>();

  public Stopwords() {
    wordsList.add("a");
    wordsList.add("able");
    wordsList.add("about");
    wordsList.add("above");
    wordsList.add("according");
    wordsList.add("accordingly");
    wordsList.add("across");
    wordsList.add("actually");
    wordsList.add("after");
    wordsList.add("afterwards");
    wordsList.add("again");
    wordsList.add("against");
    wordsList.add("all");
    wordsList.add("allow");
    wordsList.add("allows");
    wordsList.add("almost");
    wordsList.add("alone");
    wordsList.add("along");
    wordsList.add("already");
    wordsList.add("also");
    wordsList.add("although");
    wordsList.add("always");
    wordsList.add("am");
    wordsList.add("among");
    wordsList.add("amongst");
    wordsList.add("an");
    wordsList.add("and");
    wordsList.add("another");
    wordsList.add("any");
    wordsList.add("anybody");
    wordsList.add("anyhow");
    wordsList.add("anyone");
    wordsList.add("anything");
    wordsList.add("anyway");
    wordsList.add("anyways");
    wordsList.add("anywhere");
    wordsList.add("apart");
    wordsList.add("appear");
    wordsList.add("appreciate");
    wordsList.add("appropriate");
    wordsList.add("are");
    wordsList.add("around");
    wordsList.add("as");
    wordsList.add("aside");
    wordsList.add("ask");
    wordsList.add("asking");
    wordsList.add("associated");
    wordsList.add("at");
    wordsList.add("available");
    wordsList.add("away");
    wordsList.add("awfully");
    wordsList.add("b");
    wordsList.add("be");
    wordsList.add("became");
    wordsList.add("because");
    wordsList.add("become");
    wordsList.add("becomes");
    wordsList.add("becoming");
    wordsList.add("been");
    wordsList.add("before");
    wordsList.add("beforehand");
    wordsList.add("behind");
    wordsList.add("being");
    wordsList.add("believe");
    wordsList.add("below");
    wordsList.add("beside");
    wordsList.add("besides");
    wordsList.add("best");
    wordsList.add("better");
    wordsList.add("between");
    wordsList.add("beyond");
    wordsList.add("both");
    wordsList.add("brief");
    wordsList.add("but");
    wordsList.add("by");
    wordsList.add("c");
    wordsList.add("came");
    wordsList.add("can");
    wordsList.add("cannot");
    wordsList.add("cant");
    wordsList.add("cause");
    wordsList.add("causes");
    wordsList.add("certain");
    wordsList.add("certainly");
    wordsList.add("changes");
    wordsList.add("clearly");
    wordsList.add("co");
    wordsList.add("com");
    wordsList.add("come");
    wordsList.add("comes");
    wordsList.add("concerning");
    wordsList.add("consequently");
    wordsList.add("consider");
    wordsList.add("considering");
    wordsList.add("contain");
    wordsList.add("containing");
    wordsList.add("contains");
    wordsList.add("corresponding");
    wordsList.add("could");
    wordsList.add("course");
    wordsList.add("currently");
    wordsList.add("d");
    wordsList.add("definitely");
    wordsList.add("described");
    wordsList.add("despite");
    wordsList.add("did");
    wordsList.add("different");
    wordsList.add("do");
    wordsList.add("does");
    wordsList.add("doing");
    wordsList.add("done");
    wordsList.add("down");
    wordsList.add("downwards");
    wordsList.add("during");
    wordsList.add("e");
    wordsList.add("each");
    wordsList.add("edu");
    wordsList.add("eg");
    wordsList.add("eight");
    wordsList.add("either");
    wordsList.add("else");
    wordsList.add("elsewhere");
    wordsList.add("enough");
    wordsList.add("entirely");
    wordsList.add("especially");
    wordsList.add("et");
    wordsList.add("etc");
    wordsList.add("even");
    wordsList.add("ever");
    wordsList.add("every");
    wordsList.add("everybody");
    wordsList.add("everyone");
    wordsList.add("everything");
    wordsList.add("everywhere");
    wordsList.add("ex");
    wordsList.add("exactly");
    wordsList.add("example");
    wordsList.add("except");
    wordsList.add("f");
    wordsList.add("far");
    wordsList.add("few");
    wordsList.add("fifth");
    wordsList.add("first");
    wordsList.add("five");
    wordsList.add("followed");
    wordsList.add("following");
    wordsList.add("follows");
    wordsList.add("for");
    wordsList.add("former");
    wordsList.add("formerly");
    wordsList.add("forth");
    wordsList.add("four");
    wordsList.add("from");
    wordsList.add("further");
    wordsList.add("furthermore");
    wordsList.add("g");
    wordsList.add("get");
    wordsList.add("gets");
    wordsList.add("getting");
    wordsList.add("given");
    wordsList.add("gives");
    wordsList.add("go");
    wordsList.add("goes");
    wordsList.add("going");
    wordsList.add("gone");
    wordsList.add("got");
    wordsList.add("gotten");
    wordsList.add("greetings");
    wordsList.add("h");
    wordsList.add("had");
    wordsList.add("happens");
    wordsList.add("hardly");
    wordsList.add("has");
    wordsList.add("have");
    wordsList.add("having");
    wordsList.add("he");
    wordsList.add("hello");
    wordsList.add("help");
    wordsList.add("hence");
    wordsList.add("her");
    wordsList.add("here");
    wordsList.add("hereafter");
    wordsList.add("hereby");
    wordsList.add("herein");
    wordsList.add("hereupon");
    wordsList.add("hers");
    wordsList.add("herself");
    wordsList.add("hi");
    wordsList.add("him");
    wordsList.add("himself");
    wordsList.add("his");
    wordsList.add("hither");
    wordsList.add("hopefully");
    wordsList.add("how");
    wordsList.add("howbeit");
    wordsList.add("however");
    wordsList.add("i");
    wordsList.add("ie");
    wordsList.add("if");
    wordsList.add("ignored");
    wordsList.add("immediate");
    wordsList.add("in");
    wordsList.add("inasmuch");
    wordsList.add("inc");
    wordsList.add("indeed");
    wordsList.add("indicate");
    wordsList.add("indicated");
    wordsList.add("indicates");
    wordsList.add("inner");
    wordsList.add("insofar");
    wordsList.add("instead");
    wordsList.add("into");
    wordsList.add("inward");
    wordsList.add("is");
    wordsList.add("it");
    wordsList.add("its");
    wordsList.add("itself");
    wordsList.add("j");
    wordsList.add("just");
    wordsList.add("k");
    wordsList.add("keep");
    wordsList.add("keeps");
    wordsList.add("kept");
    wordsList.add("know");
    wordsList.add("knows");
    wordsList.add("known");
    wordsList.add("l");
    wordsList.add("last");
    wordsList.add("lately");
    wordsList.add("later");
    wordsList.add("latter");
    wordsList.add("latterly");
    wordsList.add("least");
    wordsList.add("less");
    wordsList.add("lest");
    wordsList.add("let");
    wordsList.add("like");
    wordsList.add("liked");
    wordsList.add("likely");
    wordsList.add("little");
    wordsList.add("ll"); //added to avoid words like you'll,I'll etc.
    wordsList.add("look");
    wordsList.add("looking");
    wordsList.add("looks");
    wordsList.add("ltd");
    wordsList.add("m");
    wordsList.add("mainly");
    wordsList.add("many");
    wordsList.add("may");
    wordsList.add("maybe");
    wordsList.add("me");
    wordsList.add("mean");
    wordsList.add("meanwhile");
    wordsList.add("merely");
    wordsList.add("might");
    wordsList.add("more");
    wordsList.add("moreover");
    wordsList.add("most");
    wordsList.add("mostly");
    wordsList.add("much");
    wordsList.add("must");
    wordsList.add("my");
    wordsList.add("myself");
    wordsList.add("n");
    wordsList.add("name");
    wordsList.add("namely");
    wordsList.add("nd");
    wordsList.add("near");
    wordsList.add("nearly");
    wordsList.add("necessary");
    wordsList.add("need");
    wordsList.add("needs");
    wordsList.add("neither");
    wordsList.add("never");
    wordsList.add("nevertheless");
    wordsList.add("next");
    wordsList.add("nine");
    wordsList.add("no");
    wordsList.add("nobody");
    wordsList.add("non");
    wordsList.add("none");
    wordsList.add("noone");
    wordsList.add("nor");
    wordsList.add("normally");
    wordsList.add("not");
    wordsList.add("nothing");
    wordsList.add("novel");
    wordsList.add("now");
    wordsList.add("nowhere");
    wordsList.add("o");
    wordsList.add("obviously");
    wordsList.add("of");
    wordsList.add("off");
    wordsList.add("often");
    wordsList.add("oh");
    wordsList.add("ok");
    wordsList.add("okay");
    wordsList.add("on");
    wordsList.add("once");
    wordsList.add("one");
    wordsList.add("ones");
    wordsList.add("only");
    wordsList.add("onto");
    wordsList.add("or");
    wordsList.add("other");
    wordsList.add("others");
    wordsList.add("otherwise");
    wordsList.add("ought");
    wordsList.add("our");
    wordsList.add("ours");
    wordsList.add("ourselves");
    wordsList.add("out");
    wordsList.add("outside");
    wordsList.add("over");
    wordsList.add("overall");
    wordsList.add("own");
    wordsList.add("p");
    wordsList.add("particular");
    wordsList.add("particularly");
    wordsList.add("per");
    wordsList.add("perhaps");
    wordsList.add("placed");
    wordsList.add("please");
    wordsList.add("plus");
    wordsList.add("possible");
    wordsList.add("presumably");
    wordsList.add("probably");
    wordsList.add("provides");
    wordsList.add("q");
    wordsList.add("que");
    wordsList.add("quite");
    wordsList.add("qv");
    wordsList.add("r");
    wordsList.add("rather");
    wordsList.add("rd");
    wordsList.add("re");
    wordsList.add("really");
    wordsList.add("reasonably");
    wordsList.add("regarding");
    wordsList.add("regardless");
    wordsList.add("regards");
    wordsList.add("relatively");
    wordsList.add("respectively");
    wordsList.add("right");
    wordsList.add("s");
    wordsList.add("said");
    wordsList.add("same");
    wordsList.add("saw");
    wordsList.add("say");
    wordsList.add("saying");
    wordsList.add("says");
    wordsList.add("second");
    wordsList.add("secondly");
    wordsList.add("see");
    wordsList.add("seeing");
    wordsList.add("seem");
    wordsList.add("seemed");
    wordsList.add("seeming");
    wordsList.add("seems");
    wordsList.add("seen");
    wordsList.add("self");
    wordsList.add("selves");
    wordsList.add("sensible");
    wordsList.add("sent");
    wordsList.add("serious");
    wordsList.add("seriously");
    wordsList.add("seven");
    wordsList.add("several");
    wordsList.add("shall");
    wordsList.add("she");
    wordsList.add("should");
    wordsList.add("since");
    wordsList.add("six");
    wordsList.add("so");
    wordsList.add("some");
    wordsList.add("somebody");
    wordsList.add("somehow");
    wordsList.add("someone");
    wordsList.add("something");
    wordsList.add("sometime");
    wordsList.add("sometimes");
    wordsList.add("somewhat");
    wordsList.add("somewhere");
    wordsList.add("soon");
    wordsList.add("sorry");
    wordsList.add("specified");
    wordsList.add("specify");
    wordsList.add("specifying");
    wordsList.add("still");
    wordsList.add("sub");
    wordsList.add("such");
    wordsList.add("sup");
    wordsList.add("sure");
    wordsList.add("t");
    wordsList.add("take");
    wordsList.add("taken");
    wordsList.add("tell");
    wordsList.add("tends");
    wordsList.add("th");
    wordsList.add("than");
    wordsList.add("thank");
    wordsList.add("thanks");
    wordsList.add("thanx");
    wordsList.add("that");
    wordsList.add("thats");
    wordsList.add("the");
    wordsList.add("their");
    wordsList.add("theirs");
    wordsList.add("them");
    wordsList.add("themselves");
    wordsList.add("then");
    wordsList.add("thence");
    wordsList.add("there");
    wordsList.add("thereafter");
    wordsList.add("thereby");
    wordsList.add("therefore");
    wordsList.add("therein");
    wordsList.add("theres");
    wordsList.add("thereupon");
    wordsList.add("these");
    wordsList.add("they");
    wordsList.add("think");
    wordsList.add("third");
    wordsList.add("this");
    wordsList.add("thorough");
    wordsList.add("thoroughly");
    wordsList.add("those");
    wordsList.add("though");
    wordsList.add("three");
    wordsList.add("through");
    wordsList.add("throughout");
    wordsList.add("thru");
    wordsList.add("thus");
    wordsList.add("to");
    wordsList.add("together");
    wordsList.add("too");
    wordsList.add("took");
    wordsList.add("toward");
    wordsList.add("towards");
    wordsList.add("tried");
    wordsList.add("tries");
    wordsList.add("truly");
    wordsList.add("try");
    wordsList.add("trying");
    wordsList.add("twice");
    wordsList.add("two");
    wordsList.add("u");
    wordsList.add("un");
    wordsList.add("under");
    wordsList.add("unfortunately");
    wordsList.add("unless");
    wordsList.add("unlikely");
    wordsList.add("until");
    wordsList.add("unto");
    wordsList.add("up");
    wordsList.add("upon");
    wordsList.add("us");
    wordsList.add("use");
    wordsList.add("used");
    wordsList.add("useful");
    wordsList.add("uses");
    wordsList.add("using");
    wordsList.add("usually");
    wordsList.add("uucp");
    wordsList.add("v");
    wordsList.add("value");
    wordsList.add("various");
    wordsList.add("ve"); //added to avoid words like I've,you've etc.
    wordsList.add("very");
    wordsList.add("via");
    wordsList.add("viz");
    wordsList.add("vs");
    wordsList.add("w");
    wordsList.add("want");
    wordsList.add("wants");
    wordsList.add("was");
    wordsList.add("way");
    wordsList.add("we");
    wordsList.add("welcome");
    wordsList.add("well");
    wordsList.add("went");
    wordsList.add("were");
    wordsList.add("what");
    wordsList.add("whatever");
    wordsList.add("when");
    wordsList.add("whence");
    wordsList.add("whenever");
    wordsList.add("where");
    wordsList.add("whereafter");
    wordsList.add("whereas");
    wordsList.add("whereby");
    wordsList.add("wherein");
    wordsList.add("whereupon");
    wordsList.add("wherever");
    wordsList.add("whether");
    wordsList.add("which");
    wordsList.add("while");
    wordsList.add("whither");
    wordsList.add("who");
    wordsList.add("whoever");
    wordsList.add("whole");
    wordsList.add("whom");
    wordsList.add("whose");
    wordsList.add("why");
    wordsList.add("will");
    wordsList.add("willing");
    wordsList.add("wish");
    wordsList.add("with");
    wordsList.add("within");
    wordsList.add("without");
    wordsList.add("wonder");
    wordsList.add("would");
    wordsList.add("would");
    wordsList.add("x");
    wordsList.add("y");
    wordsList.add("yes");
    wordsList.add("yet");
    wordsList.add("you");
    wordsList.add("your");
    wordsList.add("yours");
    wordsList.add("yourself");
    wordsList.add("yourselves");
    wordsList.add("z");
    wordsList.add("zero");
  }
}
