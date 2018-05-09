/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.analysis;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.BugCorpus;
import edu.skku.selab.blp.common.SourceFileCorpus;
import edu.skku.selab.blp.db.AnalysisValue;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class SourceFileAnalyzer {
	private ArrayList<Bug> bugs;
	private HashMap<String, Integer> sourceFileVersionIDs;
	private HashMap<Integer, HashMap<String, AnalysisValue>> sourceFileAllTermMaps;
	private HashMap<Integer, SourceFileCorpus> sourceFileCorpusMap;
	private HashMap<Integer, Double> sourceFileLengthScoreMap;
	
	public SourceFileAnalyzer() {
		bugs = null;
		sourceFileVersionIDs = null;
		sourceFileAllTermMaps = null;
		sourceFileCorpusMap = null;
		sourceFileLengthScoreMap = null;
	}
	
    public SourceFileAnalyzer(ArrayList<Bug> bugs) {
    	this.bugs = bugs;
    	sourceFileVersionIDs = null;
    	sourceFileAllTermMaps = null;
    	sourceFileCorpusMap = null;
    	sourceFileLengthScoreMap = null;
    }
    
	/**
	 * Calculate VSM score between source files and each bug report 
	 * 
	 * @see edu.skku.selab.blp.analysis.IAnalyzer#analyze()
	 */
	public void analyze(String version, boolean useStructuredInfo) throws Exception {
		Property property = Property.getInstance();
		String productName = property.productName;

		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		sourceFileVersionIDs = sourceFileDAO.getSourceFileVersionIDs(productName, version);
		sourceFileAllTermMaps = new HashMap<Integer, HashMap<String, AnalysisValue>>();
		sourceFileCorpusMap = new HashMap<Integer, SourceFileCorpus>();
		sourceFileLengthScoreMap = new HashMap<Integer, Double>();
		
		String filename = Property.getInstance().WORK_DIR;
    	if (filename.endsWith(Property.getInstance().separator) == false)
    		filename = filename + Property.getInstance().separator;
    	filename = filename + "SourceFileAnalyzer_wrongFile.txt";
    	
    	int errorCount = 0;
    	FileWriter errorWriter = null;
    	try {
    		errorWriter = new FileWriter(filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Iterator<String> sourceFileVersionIDIter = sourceFileVersionIDs.keySet().iterator();
		while(sourceFileVersionIDIter.hasNext()) {
			int sourceFileVersionID = sourceFileVersionIDs.get(sourceFileVersionIDIter.next());
			
			HashMap<String, AnalysisValue> sourceFileTermMap = sourceFileDAO.getTermMap(sourceFileVersionID);
			if (sourceFileTermMap == null) {
				errorWriter.write("Wrong source file version ID: " + sourceFileVersionID + "\n");
				errorCount ++;
				//System.err.printf();
			}
			sourceFileAllTermMaps.put(sourceFileVersionID, sourceFileTermMap);
			
			SourceFileCorpus corpus = sourceFileDAO.getCorpus(sourceFileVersionID);
			sourceFileCorpusMap.put(sourceFileVersionID, corpus);
			
			double lengthScore = sourceFileDAO.getLengthScore(sourceFileVersionID);
			sourceFileLengthScoreMap.put(sourceFileVersionID, lengthScore);
		}
		errorWriter.close();
		
		if (errorCount > 0)
			System.err.printf("There are %d wrong source file versions. please check SourceFileAnalyzer_wrongFile.txt\n", errorCount);
		
		ExecutorService executor = Executors.newFixedThreadPool(Property.THREAD_COUNT);

		for (int i = 0; i < bugs.size(); i++) {
			// calculate term count, IDC, TF and IDF
			Runnable worker = new WorkerThread(bugs.get(i), version, useStructuredInfo);
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
	}
	
    private class WorkerThread implements Runnable {
    	private Bug bug;
    	private String version;
    	private boolean useStructuredInfo;
    	
        public WorkerThread(Bug bug, String version, boolean useStructuredInfo){
            this.bug = bug;
            this.version = version;
            this.useStructuredInfo = useStructuredInfo;
        }
     
        @Override
        public void run() {
			// Compute similarity between Bug report & source files
        	
        	try {
    			if (useStructuredInfo) {
    				computeSimilarityWithStructuredInfo(bug, version);
    			} else {
    				computeSimilarity(bug, version);
    			}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        
    	private void computeSimilarity(Bug bug, String version) throws Exception {
    		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
    		SourceFileDAO sourceFileDAO = new SourceFileDAO();
    		
    		BugDAO bugDAO = new BugDAO();
    		HashMap<String, AnalysisValue> bugSfTermMap = bugDAO.getSfTermMap(bug.getID());
    		
    		Iterator<String> sourceFileVersionIDIter = sourceFileVersionIDs.keySet().iterator();
    		while(sourceFileVersionIDIter.hasNext()) {
    			int sourceFileVersionID = sourceFileVersionIDs.get(sourceFileVersionIDIter.next());

    			// corpus, analysisValue
    			HashMap<String, AnalysisValue> sourceFileTermMap = sourceFileAllTermMaps.get(sourceFileVersionID);
    			
    			double vsmScore = 0.0;
    			Iterator<String> sourceFileTermIter = sourceFileTermMap.keySet().iterator();
    			while (sourceFileTermIter.hasNext()) {
    				String sourceFileTerm = sourceFileTermIter.next();
    				double sourceFileTermWeight = sourceFileTermMap.get(sourceFileTerm).getTf() * sourceFileTermMap.get(sourceFileTerm).getIdf();
    				
    				double bugTermWeight = 0;
    				AnalysisValue bugTermValue = bugSfTermMap.get(sourceFileTerm);
    				if (null != bugTermValue) {
    					bugTermWeight = bugTermValue.getTf() * bugTermValue.getIdf();
    					
    					// ASSERT code: IDF values must be same!
//    					if (bugTermValue.getIdf() != sourceFileTermMap.get(sourceFileTerm).getIdf()) {
//    						System.out.printf("Bug IDF: %f, Source IDF: %f\n", bugTermValue.getIdf(), sourceFileTermMap.get(sourceFileTerm).getIdf());
//    					}
    				} 
    				
    				vsmScore += (bugTermWeight * sourceFileTermWeight);
    			}

    			double sourceFileNorm = sourceFileDAO.getNormValue(sourceFileVersionID);
    			double bugNorm = bugDAO.getNormValue(bug.getID());
    			vsmScore = (vsmScore / (sourceFileNorm * bugNorm));
    			vsmScore = vsmScore * sourceFileLengthScoreMap.get(sourceFileVersionID);
    			
    			IntegratedAnalysisValue integratedAnalysisValue = new IntegratedAnalysisValue();
    			integratedAnalysisValue.setBugID(bug.getID());
    			integratedAnalysisValue.setSourceFileVersionID(sourceFileVersionID);
    			integratedAnalysisValue.setVsmScore(vsmScore);
    			integratedAnalysisDAO.insertAnalysisVaule(integratedAnalysisValue);
    		}
    	}
    	
    	private void computeSimilarityWithStructuredInfo(Bug bug, String version) throws Exception {
    		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
    		BugDAO bugDAO = new BugDAO();
    		HashMap<String, AnalysisValue> bugSfTermMap = bugDAO.getSfTermMap(bug.getID());
    		
    		TreeSet<Double> vsmScoreSet = new TreeSet<Double>();
    		LinkedList<IntegratedAnalysisValue> integratedAnalysisValueList = new LinkedList<IntegratedAnalysisValue>();
    		Iterator<String> sourceFileVersionIDIter = sourceFileVersionIDs.keySet().iterator();
    		while(sourceFileVersionIDIter.hasNext()) {
    			String sourceFileName = sourceFileVersionIDIter.next();
    			int sourceFileVersionID = sourceFileVersionIDs.get(sourceFileName);

    			double vsmScore = 0.0;
    			// corpus, analysisValue
    			HashMap<String, AnalysisValue> sourceFileTermMap = sourceFileAllTermMaps.get(sourceFileVersionID);
    			
    			SourceFileCorpus corpus = sourceFileCorpusMap.get(sourceFileVersionID);
    			String[] sourceFileCorpusSet = new String[4];
    			sourceFileCorpusSet[0] = corpus.getClassPart();
    			sourceFileCorpusSet[1] = corpus.getMethodPart();
    			sourceFileCorpusSet[2] = corpus.getVariablePart();
    			sourceFileCorpusSet[3] = corpus.getCommentPart();
    			double[] sourceFileNormSet = new double[4];
    			sourceFileNormSet[0] = corpus.getClassCorpusNorm();
    			sourceFileNormSet[1] = corpus.getMethodCorpusNorm();
    			sourceFileNormSet[2] = corpus.getVariableCorpusNorm();
    			sourceFileNormSet[3] = corpus.getCommentCorpusNorm();
    			
    			BugCorpus bugCorpus = bug.getCorpus();
    			String[] bugCorpusParts = new String[2];
    			bugCorpusParts[0] = bugCorpus.getSummaryPart();
    			bugCorpusParts[1] = bugCorpus.getDescriptionPart();
    			double[] bugNormSet = new double[2];
    			bugNormSet[0] = bugCorpus.getSummaryCorpusNorm();
    			bugNormSet[1] = bugCorpus.getDecriptionCorpusNorm();
    					
    			for (int i = 0; i < sourceFileCorpusSet.length; i++) {
    				for (int j = 0; j < bugCorpusParts.length; j++) {
    					if (sourceFileCorpusSet[i] == "" || bugCorpusParts[j] == "") {
    						continue;
    					}
    					
    					String[] sourceFileTerms = sourceFileCorpusSet[i].split(" ");
    					String[] bugTerms = bugCorpusParts[j].split(" ");
    					HashSet<String> bugTermSet = new HashSet<String>();
    					for (int k = 0; k < bugTerms.length; k++) {
    						bugTermSet.add(bugTerms[k]);
    					}
    					
    					double cosineSimilarityScore = 0.0;
    					for (int k = 0; k < sourceFileTerms.length; k++) {
    						if (bugTermSet.contains(sourceFileTerms[k])) {
    							if (null == sourceFileTermMap.get(sourceFileTerms[k])) {
    								System.out.printf("Exception occurred term: %s\n", sourceFileTerms[k]);
    								continue;
    							}
    							
    							AnalysisValue sourceFileTermValue = sourceFileTermMap.get(sourceFileTerms[k]);
    							double sourceFileTermWeight = sourceFileTermValue.getIdf() * sourceFileTermValue.getIdf();
    							
    							double bugTermWeight = 0;
    							AnalysisValue bugTermValue = bugSfTermMap.get(sourceFileTerms[k]);
    							if (null != bugTermValue) {
    								bugTermWeight = bugTermValue.getTf() * bugTermValue.getIdf();
    							} 
    							
    							cosineSimilarityScore += (bugTermWeight * sourceFileTermWeight);
    						}
    					}
    					
    					if (cosineSimilarityScore != 0 && sourceFileNormSet[i] != 0 && bugNormSet[j] != 0) {
    						
    						// debug code
//    						if (bug.getID().contains("59895")) {
//    							if (sourceFileName.contains("org.aspectj.ajdt.internal.core.builder.AjState.java") ||
//    									sourceFileName.contains("org.aspectj.ajdt.internal.core.builder.AjBuildManager.java") ||
//    									sourceFileName.contains("org.aspectj.ajdt.internal.core.builder.AjBuildConfig.java")) {
//    								System.out.printf("source: %s, cosineScore: %f, sourceNormSet[%d]: %f, bugNormSet[%d]: %f, vsmScore: %f\n",
//    										sourceFileName, cosineSimilarityScore, i, sourceFileNormSet[i], j, bugNormSet[j],
//    										(cosineSimilarityScore / (sourceFileNormSet[i] * bugNormSet[j])));
//    							}
//    						}
    						
    						double weight = 1;
    						if (i == 3) {
    							weight = 0.5;	// weight 0.3~0.5 is best for AspectJ
    						}
    						vsmScore += (cosineSimilarityScore / (sourceFileNormSet[i] * bugNormSet[j])) * weight;
    					}
    				}
    			}

    			// debug code
//    			if (bug.getID().contains("59895")) {
//    				if (sourceFileName.contains("org.aspectj.ajdt.internal.core.builder.AjState.java") ||
//    						sourceFileName.contains("org.aspectj.ajdt.internal.core.builder.AjBuildManager.java") ||
//    						sourceFileName.contains("org.aspectj.ajdt.internal.core.builder.AjBuildConfig.java")) {
//    					System.out.printf("source: %s, vsmScore: %f, LengthScore: %f, finalVsmScore: %f\n",
//    							sourceFileName, vsmScore, sourceFileDAO.getLengthScore(sourceFileVersionID),
//    							vsmScore * sourceFileDAO.getLengthScore(sourceFileVersionID));
//    				}
//    			}
    			
    			vsmScore = vsmScore * sourceFileLengthScoreMap.get(sourceFileVersionID);
    			
    			if (vsmScore > 0) {
	    			IntegratedAnalysisValue integratedAnalysisValue = new IntegratedAnalysisValue();
	    			integratedAnalysisValue.setBugID(bug.getID());
	    			integratedAnalysisValue.setSourceFileVersionID(sourceFileVersionID);
	    			integratedAnalysisValue.setVsmScore(vsmScore);
	    			
	    			vsmScoreSet.add(vsmScore);
	    			integratedAnalysisValueList.add(integratedAnalysisValue);
    			}
    		}
    		
    		double limitVsmScore = 0;
    		int candidateLimitSize = Integer.MAX_VALUE;
    		
    		if (Property.getInstance().candidateLimitRate != 1.0) {
    			candidateLimitSize = (int) (Property.getInstance().fileCount * Property.getInstance().candidateLimitRate);
    		}

    		if (vsmScoreSet.size() > candidateLimitSize) {
//    			System.out.printf(">>> candidateLimitSize: %d\n", candidateLimitSize);
    			limitVsmScore = (Double) (vsmScoreSet.descendingSet().toArray()[candidateLimitSize -1]);
    		}
    		
//    		System.out.printf(">>> limitVsmScore: %f\n", limitVsmScore);
//    		int count = 0; // for test
    		for (IntegratedAnalysisValue integratedAnalysisValue:integratedAnalysisValueList) {
				if (integratedAnalysisValue.getVsmScore() >= limitVsmScore) {
					integratedAnalysisDAO.insertAnalysisVaule(integratedAnalysisValue);
//					count++;
				}
    		}
//    		System.out.printf(">>> vsmScoreSet.size(): %d, Count: %d\n", vsmScoreSet.size(), count);
    	}
    }
}
