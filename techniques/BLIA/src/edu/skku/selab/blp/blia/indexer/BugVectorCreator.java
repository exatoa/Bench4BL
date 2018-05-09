/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;

import edu.udo.cs.wvtool.config.WVTConfigException;
import edu.udo.cs.wvtool.config.WVTConfiguration;
import edu.udo.cs.wvtool.config.WVTConfigurationFact;
import edu.udo.cs.wvtool.config.WVTConfigurationRule;
import edu.udo.cs.wvtool.generic.output.WordVectorWriter;
import edu.udo.cs.wvtool.generic.stemmer.LovinsStemmerWrapper;
import edu.udo.cs.wvtool.generic.stemmer.PorterStemmerWrapper;
import edu.udo.cs.wvtool.generic.stemmer.WVTStemmer;
import edu.udo.cs.wvtool.generic.vectorcreation.TFIDF;
import edu.udo.cs.wvtool.main.WVTDocumentInfo;
import edu.udo.cs.wvtool.main.WVTFileInputList;
import edu.udo.cs.wvtool.main.WVTool;
import edu.udo.cs.wvtool.wordlist.WVTWordList;
import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.db.AnalysisValue;
import edu.skku.selab.blp.db.dao.BugDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugVectorCreator {
	private static final String HOME_FOLDER = (new StringBuilder(String.valueOf(Property.getInstance().WORK_DIR))).append(Property.getInstance().separator).toString();
    private static final String BUG_CORPUS_FOLDER = (new StringBuilder("BugCorpus")).append(Property.getInstance().separator).toString();
	
	
	public void create() throws Exception {
		Property property = Property.getInstance();
		
		WVTool wvt = new WVTool(false);
		WVTConfiguration config = new WVTConfiguration();
		final WVTStemmer porterStemmer = new PorterStemmerWrapper();
		config.setConfigurationRule("stemmer", new WVTConfigurationRule() {
			public Object getMatchingComponent(WVTDocumentInfo d)
					throws WVTConfigException {
				return porterStemmer;
			}
		});
		WVTStemmer stemmer = new LovinsStemmerWrapper();
		config.setConfigurationRule("stemmer", new WVTConfigurationFact(stemmer));
		WVTFileInputList list = new WVTFileInputList(1);
		
		// Can't change file interface easily.
		list.addEntry(new WVTDocumentInfo((new StringBuilder(String.valueOf(HOME_FOLDER))).append(BUG_CORPUS_FOLDER).toString(), "txt", "", "english", 0));
		WVTWordList wordList = wvt.createWordList(list, config);
		wordList.pruneByFrequency(1, 0x7fffffff);
		
		int termCount = wordList.getNumWords();
		property.bugTermCount = termCount;
		
		String bugTermListFile = (new StringBuilder(String.valueOf(HOME_FOLDER))).append("BugTermList.txt").toString();
		wordList.storePlain(new FileWriter(bugTermListFile));
//		wordList.storePlain(new BufferedWriter(new OutputStreamWriter(System.out)));
		
		String productName = property.productName;
		BugDAO bugDAO = new BugDAO();
		
		HashMap<Integer, Integer> bugWordIndexMap = new HashMap<Integer, Integer>(); 
		BufferedReader reader = new BufferedReader(new FileReader(bugTermListFile));
		String line = null;
		int index = 0;
		while ((line = reader.readLine()) != null) {
			String term = line;
			int bugTermID = bugDAO.insertBugTerm(term, productName);
			bugWordIndexMap.put(index++, bugTermID);
		}
		reader.close();

		FileWriter outFile = new FileWriter((new StringBuilder(String.valueOf(HOME_FOLDER))).append("BugVector.txt").toString());
		WordVectorWriter wvw = new WordVectorWriter(outFile, true);
		config.setConfigurationRule("output", new WVTConfigurationFact(wvw));
		config.setConfigurationRule("vectorcreation", new WVTConfigurationFact(new TFIDF()));
		wvt.createVectors(list, config, wordList);
		wvw.close();
		outFile.close();
		
		// read "BugVector.txt" then insert vector value to DB
		String workDir = (new StringBuilder(String.valueOf(property.WORK_DIR))).append(property.separator).toString();
		reader = new BufferedReader(new FileReader((new StringBuilder(String.valueOf(workDir))).append("BugVector.txt").toString()));
		while ((line = reader.readLine()) != null) {
			String values[] = line.split(";");
			int bugID = Integer.parseInt(values[0].split("\\.")[0]);
			
			if (values.length != 1) {
				HashMap<Integer, Double> bugTermVectors = getVectors(values[1].trim(), bugWordIndexMap);
				Iterator<Integer> corpusVectorsIter = bugTermVectors.keySet().iterator();
				
				while (corpusVectorsIter.hasNext()) {
					int bugTermID =  corpusVectorsIter.next();
					AnalysisValue analysisValue = new AnalysisValue();
					analysisValue.setID(bugID);
					analysisValue.setTermID(bugTermID);
					analysisValue.setTermWeight(bugTermVectors.get(bugTermID).doubleValue());
					bugDAO.insertBugTermWeight(analysisValue);
				}
			}
		}
		
		reader.close();
	}
	
	/**
	 * 
	 * @param vecStr
	 * @return <BugWordID, Vector>
	 */
	private HashMap<Integer, Double> getVectors(String vecStr, HashMap<Integer, Integer> bugWordIndexMap) {
		String values[] = vecStr.split(" ");
		String as[];
		HashMap<Integer, Double> bugWordVectors = new HashMap<Integer, Double>();
		int j = (as = values).length;
		for (int i = 0; i < j; i++) {
			String str = as[i];
			int index = Integer.valueOf(Integer.parseInt(str.substring(0,str.indexOf(":"))));
			Integer bugWordID = bugWordIndexMap.get(index);
			Double vector = Double.parseDouble(str.substring(str.indexOf(":") + 1));
			
			bugWordVectors.put(bugWordID, vector);
		}

		return bugWordVectors;
	}
}
