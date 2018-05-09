package utils;

public class Stem {
	private static PorterStemmer stemmer = new PorterStemmer();
//	private static StanfordStemmer sstemmer = new StanfordStemmer();
	
	public static String stem(String word) {
		stemmer.reset();
		stemmer.stem(word);
		return stemmer.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(stem("return"));
	}
}
