package org.buglocator.sourcecode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.TreeSet;

import org.buglocator.property.Property;

public class Indexer {
	private String workDir = Property.getInstance().WorkDir + Property.getInstance().Separator;
	private String lineSparator = Property.getInstance().LineSeparator;

	/**
	 * Entry 함수
	 * @throws IOException
	 */
	public void index() throws IOException {
		// countTable: count how many times a word occurs in all files
		Hashtable<String, Integer> countTable = countDoc();		//문서 전체에서 등장하는 word의 카운트
		Hashtable<String, Integer> idSet = new Hashtable<String, Integer>();	//각 word별 id  <word, id>
		int id = 0;
		int errorCount = 0;
		FileWriter writerWord = new FileWriter(workDir + "Wordlist.txt");		
		for (String key : countTable.keySet()) {
			idSet.put(key, id);
			writerWord.write(key + "\t" + id + lineSparator);
			writerWord.flush();
			id++;
		}
		writerWord.close();
		Property.getInstance().WordCount = id;
		

		// IDC.txt tells how many time a word occurs in all files
		FileWriter writerDoc = new FileWriter(workDir + "IDC.txt");
		for (String key : countTable.keySet()) {
			writerDoc.write(key + "\t" + countTable.get(key) + lineSparator);
			writerDoc.flush();
		}
		writerDoc.close();

		//
		FileWriter errorList = new FileWriter(workDir + "CodeTerm-NoTermList.txt");
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "CodeCorpus.txt"));
		String line = null;
		FileWriter writer = new FileWriter(workDir + "TermInfo.txt");
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");		//values[0] : 파일명, values[1] : word list splitted with a space.
			String[] words = values[1].split(" ");
			int totalCount = 0;		//한 파일 내에 등장하는 전체 word count; sum of each word count.

			// 한 파일 내에 등장하는 word 별 count  계산.
			Hashtable<Integer, Integer> termTable = new Hashtable<Integer, Integer>();
			for (String word : words) {
				if (word.trim().equals("")) continue;

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
			if (totalCount==0){
				//System.err.println("Warnnig::This file has no term: "+values[0]);
				errorCount++;
				errorList.write(values[0]+"\n");
				errorList.flush();
				continue;
			}
			StringBuffer output = new StringBuffer();
			output.append(values[0] + "\t" + totalCount + ";");
			TreeSet<Integer> tmp = new TreeSet<Integer>();
			for (String word : words) {
				if (word.trim().equals("")) continue;

				Integer termId = idSet.get(word);
				if (tmp.contains(termId)) continue;
				
				tmp.add(termId);
				int termCount = termTable.get(termId);
				// documentCount means how many times a word occurs in
				// all files
				int documentCount = countTable.get(word);
				output.append(termId + ":" + termCount + " " + documentCount + "\t");
			}
			writer.write(output.toString() + lineSparator);
			writer.flush();
		}
		reader.close();
		writer.close();
		errorList.close();
		
		if (errorCount>0) {
			System.err.println("Warnning:: This project has "+errorCount+" empty term files. Check the CodeTerm-NoTermList.txt!");
		}
	}

	public Hashtable<String, Integer> countDoc() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "CodeCorpus.txt"));
		String line = null;

		Hashtable<String, Integer> countTable = new Hashtable<String, Integer>();

		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			String[] words = values[1].split(" ");
			
			//한 code에서 생성된 모든 word set 얻기
			TreeSet<String> wordSet = new TreeSet<String>();
			for (String word : words) {
				if (!word.trim().equals("") && !wordSet.contains(word)) {
					wordSet.add(word);
				}
			}
			
			//Full word count 생성.
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
