package miningChanges;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import utils.ChangeLocator;
import utils.ExtractCodeElementsFromSourceFile;
import utils.FileListUnderDirectory;
import utils.FileToLines;
import utils.Splitter;
import utils.Stopword;
import utils.WriteLinesToFile;

public class ExtractCodeLikeTerms {
	
	public HashMap<String,Integer> cltMaps;
	
	public List<String> extractCLTFromCodeSnippet(final String content) {
//		List<String> clts = new ArrayList<String>();
//		
//		return clts;
		return extractCLTFromNaturalLanguage(content);
	}
	
	private List<Integer> getAllIndexesOf(char c, String content) {
		List<Integer> indexes = new ArrayList<Integer>();
		String tmp = content;
		int index = tmp.indexOf(c);
		while (index >= 0) {
		    indexes.add(index);
		    index = tmp.indexOf(c, index + 1);
		}
		return indexes;
	}
	
	private boolean isLetter(char c) {
		return Character.isLetter(c);
	}
	
	private boolean isLetterOrDigit(char c) {
		return Character.isLetter(c) || Character.isDigit(c);
	}
	
	public List<String> extractCLTFromNaturalLanguage(final String content) {
		List<String> clts = new ArrayList<String>();
		HashSet<String> rawTerms = new HashSet<String>();
		String[] words = Splitter.splitNatureLanguageWithUnderline(content);
//		for (String word : words)
//			System.out.println(word);
		/*
		 * Detecting code elements with Camel Naming conventions
		 * */
		// detect code elements with Camel Naming convention and starting with caps (e.g. class names)
		String regex1 = "[A-Z][a-z]*([A-Z0-9][a-z0-9]*)*";
		// detect code elements with Camel Naming convention and starting with lowerCase (e.g functions name)
		String regex2 = "[a-z][A-Z0-9a-z]*[A-Z][A-Z0-9a-z]*";
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
//			System.out.println(word);
			if (word.matches(regex2) || word.matches(regex1) || word.contains("_")) {
				word = word.toLowerCase();
//				System.out.println(word);
//				if (CLTMaps.containsKey(word)) {
					clts.add(word);
					rawTerms.add(words[i]);
//				}
			}
		}
//		System.out.println(clts.toString());
		/*
		 * Detecting word after word "class" or word "method"
		 * 
		 * */
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			word = word.toLowerCase();
			if (word.equals("class") || word.equals("method")) {
				if (i < words.length - 1) {
					String tmp = words[i+1].toLowerCase();
//					if (CLTMaps.containsKey(tmp)) {
						if (!rawTerms.contains(words[i+1])) {
							rawTerms.add(words[i+1]);
							clts.add(tmp);
						}
					}
//				}
			}
		}
//		System.out.println(clts.toString());
		/*
		 * Detecting code elements before parenthesis or after dot (e.g. functions)
		 * */
		List<Integer> indexes = getAllIndexesOf('.',content);
//		System.out.println(indexes);
		for (int index : indexes) {
			if (index < content.length() - 1 && isLetter(content.charAt(index+1))) {
				// detecting the following word after the dot
				int tmp = index + 1;
				while (tmp < content.length() && isLetterOrDigit(content.charAt(tmp))) tmp++;
				String word = content.substring(index + 1, tmp);
//				System.out.println(word);
				if (!rawTerms.contains(word)) {
					rawTerms.add(word);
					clts.add(word.toLowerCase());
				}
				
			}
		}
//		System.out.println(clts.toString());
		indexes = getAllIndexesOf('(',content);
		for (int index : indexes) {
			String sub = content.substring(index);
			int right = sub.indexOf(")");
			if (right > index) {
				words = Splitter.splitNatureLanguageWithUnderline(content.substring(index,right));
				for (String word : words) {
					if (!rawTerms.contains(word)) {
						rawTerms.add(word);
						clts.add(word.toLowerCase());
					}
 				}
 			}
			if (index > 0 && isLetter(content.charAt(index - 1))) {
				// detecting the previous word before the "("
				int tmp = index - 1;
				while (tmp > 0 && isLetterOrDigit(content.charAt(tmp))) tmp--;
				String word = content.substring(tmp + 1, index);
//				System.out.println(word);
				if (!rawTerms.contains(word)) {
					rawTerms.add(word);
					clts.add(word.toLowerCase());
				}
			}
		}
//		System.out.println(clts.toString());
		return clts;
	}
	
	public HashMap<String,Integer> extractCodeLikeTerms() {
		String filename = main.Main.settings.get("workingLoc") + File.separator + "codeLikeTerms.txt";
		File file = new File(filename);
		cltMaps = new HashMap<String,Integer>();
		if (!file.exists()) {
			cltMaps = createCodeLikeTerms();
			List<String> lines = new ArrayList<String>();
			for (String key : cltMaps.keySet())
				lines.add(key + "\t" + cltMaps.get(key));
			WriteLinesToFile.writeLinesToFile(lines, filename);
		} else {
			List<String> lines = FileToLines.fileToLines(filename);
			for (String line : lines) {
				cltMaps.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
			}
		}
		return cltMaps;
	}
		
	private boolean isValid(String term) {
		boolean flag = true;
		if (term.length() < 5 && !term.contains("_")) flag = false;
		if (Stopword.isEnglishStopword(term) || Stopword.isKeyword(term)) flag = false;
		return flag;
	}
	
	private HashMap<String,Integer> createCodeLikeTerms() {
		HashMap<String,Integer> CLTMaps = new HashMap<String,Integer>();
		HashSet<String> cltCandidates = new HashSet<String>();
		List<String> lines = FileListUnderDirectory.getFileListUnder(main.Main.sourceDir, ".java");
		HashSet<String> codeLikeTermCorpus = new HashSet<String>();
		for (String line : lines) {
			// 2018-01-19 :: removed for including test files.
			//if (line.contains("test") || line.contains("Test")) continue;
			String[] tmp = line.split("/");
			tmp = tmp[1].split("/");
			for (String term : tmp) {
				if (!term.contains(".")) {
					String tmp2 = term.toLowerCase();
					cltCandidates.add(tmp2);
				}
				else {
					String tmp2 = term.substring(0,term.indexOf(".")).toLowerCase();
					cltCandidates.add(tmp2);

				}
			}
			HashSet<String> codeElements = ExtractCodeElementsFromSourceFile.extractCodeElements(line);
			for (String term : codeElements) {
				term = term.toLowerCase();
				cltCandidates.add(term);
			}
		}
		
		for (String clt : cltCandidates) {
			if (!isValid(clt)) continue;
 			CLTMaps.put(clt, codeLikeTermCorpus.size());
			codeLikeTermCorpus.add(clt);
		}
		return CLTMaps;
	} 
	
	public static void entry() {
		ExtractCodeLikeTerms eclt = new ExtractCodeLikeTerms();
		eclt.extractCodeLikeTerms();
	}
}
