package utils;

import java.util.ArrayList;

public class Splitter {
	public static void main(String[] args) {
		String contents[]=splitSourceCode("Types.getDeclaredType(DeclaredType typeDecl, TypeMirror... typeParameters)");
		for(String content:contents){
			System.out.println(content);
		}
	}

	public static String[] splitNatureLanguage(String natureLanguage) {
		ArrayList<String> wordList = new ArrayList<String>();
		StringBuffer wordBuffer = new StringBuffer();
		for (char c : natureLanguage.toCharArray()) {
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || c == '\'') {
				wordBuffer.append(c);
			} else {
				String word = wordBuffer.toString();
				if (!word.equals("")) {
					wordList.add(word);
				}
				wordBuffer = new StringBuffer();
			}
		}

		if (wordBuffer.length() != 0) {
			String word = wordBuffer.toString();
			if (!word.equals("")) {
				wordList.add(word);
			}
			wordBuffer = new StringBuffer();
		}
		return wordList.toArray(new String[wordList.size()]);
	}

	public static String[] splitNatureLanguageWithUnderline(String natureLanguage) {
		ArrayList<String> wordList = new ArrayList<String>();
		StringBuffer wordBuffer = new StringBuffer();
		for (char c : natureLanguage.toCharArray()) {
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || c == '\'' || c == '_') {
				wordBuffer.append(c);
			} else {
				String word = wordBuffer.toString();
				if (!word.equals("")) {
					wordList.add(word);
				}
				wordBuffer = new StringBuffer();
			}
		}

		if (wordBuffer.length() != 0) {
			String word = wordBuffer.toString();
			if (!word.equals("")) {
				wordList.add(word);
			}
			wordBuffer = new StringBuffer();
		}
		return wordList.toArray(new String[wordList.size()]);
	}
	
	public static String[] splitSourceCode(String sourceCode) {
		StringBuffer contentBuf = new StringBuffer();
		StringBuffer wordBuf = new StringBuffer();
		sourceCode += "$";
		for (char c : sourceCode.toCharArray()) {
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
				wordBuf.append(c);
				continue;
			}
			int length = wordBuf.length();
			if (length != 0) {
				int k = 0;
				for (int i = 0, j = 1; i < length - 1; i++, j++) {
					char first = wordBuf.charAt(i);
					char second = wordBuf.charAt(j);
					if ((first >= 'A' && first <= 'Z')
							&& (second >= 'a' && second <= 'z')) {
						contentBuf.append(wordBuf.substring(k, i));
						contentBuf.append(' ');
						k = i;
						continue;
					}
					if ((first >= 'a' && first <= 'z')
							&& (second >= 'A' && second <= 'Z')) {
						contentBuf.append(wordBuf.substring(k, j));
						contentBuf.append(' ');
						k = j;
						continue;
					}
				}
				if (k < length) {
					contentBuf.append(wordBuf.substring(k));
					contentBuf.append(" ");
				}
				wordBuf = new StringBuffer();
			}
		}
		String[] words = contentBuf.toString().split(" ");
		contentBuf = new StringBuffer();
		for (int i = 0; i < words.length; i++) {
			if (!words[i].trim().equals("") && words[i].length() >= 2) {
				contentBuf.append(words[i] + " ");
			}
		}
		return contentBuf.toString().trim().split(" ");
	}
}
