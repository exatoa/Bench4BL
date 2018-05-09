package org.brtracer.sourcecode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.TreeSet;

import org.brtracer.property.Property;

public class Indexer_OriginClass {
	private String workDir = Property.getInstance().WorkDir + Property.getInstance().Separator;
	private String lineSparator = Property.getInstance().LineSeparator;

	public static void main(String[] args) throws IOException {

	}

	public void index() throws IOException {
		Hashtable<String, Integer> countTable = countDoc();
		Hashtable<String, Integer> idSet = new Hashtable<String, Integer>();
		int id = 0;
		int wordCount = 0;
		for (String key : countTable.keySet()) {
			idSet.put(key, id);
			id++;
			wordCount++;
		}

		BufferedReader reader = new BufferedReader(new FileReader(workDir + "CodeCorpus_OriginClass.txt"));
		String line = null;
		FileWriter writer = new FileWriter(workDir + "TermInfo_OriginClass.txt");
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			String[] words = values[1].split(" ");
			int totalCount = 0;

			Hashtable<Integer, Integer> termTable = new Hashtable<Integer, Integer>();
			for (String word : words) {
				if (!word.trim().equals("")) {
					totalCount++;
					Integer termId = idSet.get(word);
					if (termTable.containsKey(termId)) {
						Integer count = termTable.get(termId);
						count++;
						termTable.remove(termId);
						termTable.put(termId, count);
					} else {
						termTable.put(termId, 1);
					}
				}
			}
			StringBuffer output = new StringBuffer();
			output.append(values[0] + "\t" + totalCount + ";");
			TreeSet<Integer> tmp = new TreeSet<Integer>();
			for (String word : words) {
				if (!word.trim().equals("")) {
					Integer termId = idSet.get(word);
					if (!tmp.contains(termId)) {
						tmp.add(termId);
						int termCount = termTable.get(termId);
						int documentCount = countTable.get(word);
						output.append(termId + ":" + termCount + " " + documentCount + "\t");
					}
				}
			}
			writer.write(output.toString() + lineSparator);
			writer.flush();
		}
		reader.close();
		writer.close();
	}

	public Hashtable<String, Integer> countDoc() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "CodeCorpus_OriginClass.txt"));
		String line;

		Hashtable<String, Integer> countTable = new Hashtable<String, Integer>();

		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			String[] words = values[1].split(" ");
			TreeSet<String> wordSet = new TreeSet<String>();
			for (String word : words) {
				if (!word.trim().equals("") && !wordSet.contains(word)) {
					wordSet.add(word);
				}
			}
			for (String word : wordSet) {
				if (countTable.containsKey(word)) {
					Integer count = countTable.get(word);
					count++;
					countTable.remove(word);
					countTable.put(word, count);
				} else {
					countTable.put(word, 1);
				}
			}
		}
		reader.close();

		return countTable;
	}
}
