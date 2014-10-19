package edu.nyu.cs.cs2580;

public class Remove {
	public static String remove(String ori){
		String temp = ori.replace("(", "").replace(")","").replace(",",
			"").replace(".", "").replace("\'", " ").replace("\"", "").replace("?",
			"").replace("!", "").replace(":", "").replace("[", "").replace("]", 
			"").replace("{", "").replace("}", "").replace("*", "").replace("#", 
			"").replace("%", "").replace("^", "").replace("/", "").replace("\\", 
			"").replace("&","").replace("$", "");
		return temp;
	}
}
