package org.brtracer.utils;

public class Stem {
	private static PorterStemmer stemmer = new PorterStemmer();
	
	public static String stem(String word) {
		stemmer.reset();
		stemmer.stem(word);
		return stemmer.toString();
	}
}

