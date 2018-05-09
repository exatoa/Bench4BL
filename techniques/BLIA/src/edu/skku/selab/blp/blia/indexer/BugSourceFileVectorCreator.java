/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.db.AnalysisValue;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugSourceFileVectorCreator {
	public void create(String version) throws Exception {
		BugDAO bugDAO = new BugDAO();
		HashMap<Integer, Bug> bugs = bugDAO.getBugs();
		Property property = Property.getInstance();
		String productName = property.productName;
		
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		HashMap<String, Integer> sourceFileTermMap = sourceFileDAO.getTermMap(productName);
		
		SourceFileVectorCreator sourceFileVectorCreator = new SourceFileVectorCreator(); 
		Hashtable<String, Integer> inverseDocCountTable = sourceFileVectorCreator.getInverseDocCountTable(version);
		int fileCount = sourceFileDAO.getSourceFileCount(productName, version);
		
		int bugID = 0;
		int totalTermCount = 0;
		int bugTermCount = 0;
		int inverseDocCount = 0;
		double tf = 0.0;
		double idf = 0.0;
		double termWeight = 0.0;
		String bugTerm = "";
		Iterator<Integer> bugsIter = bugs.keySet().iterator();
		while (bugsIter.hasNext()) {
			// calculate term count, IDC, TF and IDF
			bugID = bugsIter.next();
			
			// debug code
//			if (bugID.contains("99145")) {
//				System.out.println("BugSourceFileVectorCreator.create(): " + bugID);
//			}

			Bug bug = bugs.get(bugID);
			
			String bugCorpusContent = bug.getCorpusContent();
			
			// get term count
			String bugTermArray[] = bugCorpusContent.split(" ");
			Hashtable<String, Integer> bugTermTable = new Hashtable<String, Integer>();
			for (int i = 0; i < bugTermArray.length; i++) {
				bugTerm = bugTermArray[i];
				if (!bugTerm.trim().equals("")) {
					if (bugTermTable.containsKey(bugTerm)) {
						Integer count = (Integer) bugTermTable.get(bugTerm);
						count = Integer.valueOf(count.intValue() + 1);
						bugTermTable.remove(bugTerm);
						bugTermTable.put(bugTerm, count);
					} else {
						bugTermTable.put(bugTerm, Integer.valueOf(1));
					}
				}
			}
			
			totalTermCount = 0;
			// calculate totalTermCount
			Iterator<String> bugTermTableIter = bugTermTable.keySet().iterator();
			while (bugTermTableIter.hasNext()) {
				bugTerm = bugTermTableIter.next();
				bugTermCount = bugTermTable.get(bugTerm);
				
				if (sourceFileTermMap.containsKey(bugTerm)) {
					totalTermCount += bugTermCount;
				}
//						System.out.printf("Corpus: %s, termCount: %d\n", corpus, termCount);
			}
			
			bugDAO.updateTotalTermCount(productName, bugID, totalTermCount);
//				System.out.printf("totalTermCount: %d\n", totalTermCount);
			
			double corpusNorm = 0.0D;
			double summaryCorpusNorm = 0.0D;
			double descriptionCorpusNorm = 0.0D;

			HashSet<String> summaryTermSet = SourceFileVectorCreator.CorpusToSet(bug.getCorpus().getSummaryPart());
			HashSet<String> descriptionTermSet = SourceFileVectorCreator.CorpusToSet(bug.getCorpus().getDescriptionPart());

			bugTermTableIter = bugTermTable.keySet().iterator();
			while (bugTermTableIter.hasNext()) {
				bugTerm = bugTermTableIter.next();
				
				// test code
//				System.out.println("bugTerm:" + bugTerm);
				if (sourceFileTermMap.containsKey(bugTerm)) {
					bugTermCount = bugTermTable.get(bugTerm);
					inverseDocCount = inverseDocCountTable.get(bugTerm).intValue();
					
					// calculate TF, IDF, Vector
					tf = getTfValue(bugTermCount, totalTermCount);
					idf = getIdfValue(inverseDocCount, fileCount);
					termWeight = tf * idf;
					double termWeightSquare = termWeight * termWeight;
					corpusNorm += termWeightSquare;
					
					if (summaryTermSet.contains(bugTerm)) {
						summaryCorpusNorm += termWeightSquare;
					}

					if (descriptionTermSet.contains(bugTerm)) {
						descriptionCorpusNorm += termWeightSquare;
					}
					
					AnalysisValue bugSfTermWeight = new AnalysisValue(bugID, productName, bugTerm, bugTermCount, inverseDocCount, tf, idf);						
					bugDAO.insertBugSfTermWeight(bugSfTermWeight);
				}
			}

//				System.out.printf("word: %f\n", word);
			corpusNorm = Math.sqrt(corpusNorm);
			summaryCorpusNorm = Math.sqrt(summaryCorpusNorm);
			descriptionCorpusNorm = Math.sqrt(descriptionCorpusNorm);

			bugDAO.updateNormValues(productName, bugID, corpusNorm, summaryCorpusNorm, descriptionCorpusNorm);					
		}
	}
	
	/**
	 * Get term frequency value
	 * 
	 * @param freq
	 * @param totalTermCount
	 * @return
	 */
	private float getTfValue(int freq, int totalTermCount) {
		return (float) Math.log(freq) + 1.0F;
	}

	/**
	 * Get inverse document frequency value
	 * 
	 * @param docCount
	 * @param totalCount
	 * @return
	 */
	private float getIdfValue(double docCount, double totalCount) {
		return (float) Math.log(totalCount / docCount);
	}
}
