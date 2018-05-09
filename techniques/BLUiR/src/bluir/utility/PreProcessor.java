package bluir.utility;

import java.io.PrintStream;

public class PreProcessor {
	public static String process(String query) { String processedQuery = "";
		query = query.replaceAll("[^\\p{L}\\p{N}]", " ");
		String[] words = query.split(" ");
		
		for (int i = 0; i < words.length; i++) {
			processedQuery = processedQuery.trim() + " " + transform(words[i]);
		}
		
		return processedQuery;
	}
	
	public static String process1(String query) {
		String processedQuery = "";
		query = query.replaceAll("[^\\p{L}\\p{N}]", " ");
		String[] words = query.split(" ");
		
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() > 2) {
				processedQuery = processedQuery.trim() + " " + words[i];
			}
		}
		return processedQuery;
	}
	
	public static String transform(String name)
	{
		String[] processedName = name.replaceAll(
			String.format("%s|%s|%s", new Object[] {
			"(?<=[A-Z])(?=[A-Z][a-z])", 
			"(?<=[^A-Z])(?=[A-Z])", 
			"(?<=[A-Za-z])(?=[^A-Za-z])" }), 
			
			" ")
			.toLowerCase().split(" ");
		
		String transformedString = "";
		String[] arrayOfString1;
		int j = (arrayOfString1 = processedName).length; for (int i = 0; i < j; i++) { String word = arrayOfString1[i];
			if (word.length() > 2) {
				transformedString = transformedString.trim() + " " + word.trim();
			}
		}
		return transformedString;
	}
	



	public static void main(String[] args)
	{
		String str = "CLabel";
		System.out.println(transform(str));
	}
}

