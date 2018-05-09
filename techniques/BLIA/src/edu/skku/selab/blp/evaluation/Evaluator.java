/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.evaluation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.SourceFile;
import edu.skku.selab.blp.db.ExperimentResult;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.ExperimentResultDAO;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;
import edu.skku.selab.blp.utils.Util;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class Evaluator {
	public final static String ALG_BUG_LOCATOR = "BugLocator";
	public final static String ALG_BLIA = "BLIA";
	
	private ExperimentResult experimentResult;
	private ArrayList<Bug> bugs = null;
	private HashMap<Integer, String> sourceIDFileMap = null;
	private HashMap<Integer, HashSet<SourceFile>> realFixedFilesMap = null;;
	private HashMap<Integer, ArrayList<IntegratedAnalysisValue>> rankedValuesMap = null;
	private FileWriter writer = null; 
	
	private Integer syncLock = 0;
	private int top1 = 0;
	private int top5 = 0;
	private int top10 = 0;
	
	private Double sumOfRRank = 0.0;
	private Double MAP = 0.0;
	
	/**
	 * 
	 */
	public Evaluator(String productName, String algorithmName, String algorithmDescription, double alpha, double beta, int pastDays) {
		experimentResult = new ExperimentResult();
		experimentResult.setProductName(productName);
		experimentResult.setAlgorithmName(algorithmName);
		experimentResult.setAlgorithmDescription(algorithmDescription);
		experimentResult.setAlpha(alpha);
		experimentResult.setBeta(beta);
		experimentResult.setPastDays(pastDays);
		bugs = null;
		realFixedFilesMap = null;
	}
	
	/**
	 * 
	 */
	public Evaluator(String productName, String algorithmName, String algorithmDescription, double alpha, double beta, int pastDays, double candidateRate) {
		this(productName, algorithmName, algorithmDescription, alpha, beta, pastDays);
		experimentResult.setCandidateRate(candidateRate);
	}
	
	/**
	 * Evaluate 함수
	 * @throws Exception
	 */
	public void evaluate() throws Exception {
		long startTime = System.currentTimeMillis();
		System.out.printf("[STARTED] Evaluator.evaluate().\n");
		
		String productName = experimentResult.getProductName();
		BugDAO bugDAO = new BugDAO();
		bugs = bugDAO.getAllBugs(productName, true);
		
		//소스파일 정보 로드
		sourceIDFileMap = new HashMap<Integer, String>();
		
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		HashMap<String, Integer> sourceFileMap = sourceFileDAO.getSourceFileVersionIDs(productName, SourceFileDAO.DEFAULT_VERSION_STRING);		
		for (Entry<String, Integer> entry : sourceFileMap.entrySet()) {
			sourceIDFileMap.put(entry.getValue(), entry.getKey());
		}
		
		realFixedFilesMap = new HashMap<Integer, HashSet<SourceFile>>();
		rankedValuesMap = new HashMap<Integer, ArrayList<IntegratedAnalysisValue>>();
		for (int i = 0; i < bugs.size(); i++) {
			int bugID = bugs.get(i).getID();
			HashSet<SourceFile> fixedFiles = bugDAO.getFixedFiles(bugID);
			realFixedFilesMap.put(bugID, fixedFiles);
			rankedValuesMap.put(bugID, getRankedValues(bugID, 0));
		}

		calculateMetrics();
		
		experimentResult.setExperimentDate(new Date(System.currentTimeMillis()));
		ExperimentResultDAO experimentResultDAO = new ExperimentResultDAO();
		experimentResultDAO.insertExperimentResult(experimentResult);
		
		System.out.printf("[DONE] Evaluator.evaluate().(Total %s sec)\n", Util.getElapsedTimeSting(startTime));
	}
	
	private ArrayList<IntegratedAnalysisValue> getRankedValues(int bugID, int limit) throws Exception {
		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
		ArrayList<IntegratedAnalysisValue> rankedValues = null;
		if (experimentResult.getAlgorithmName().equalsIgnoreCase(Evaluator.ALG_BUG_LOCATOR)) {
			rankedValues = integratedAnalysisDAO.getBugLocatorRankedValues(bugID, limit);
		} else if (experimentResult.getAlgorithmName().equalsIgnoreCase(Evaluator.ALG_BLIA)) {
			rankedValues = integratedAnalysisDAO.getBLIARankedValues(bugID, limit);
		}
		
		return rankedValues;
	}
	
	private void calculate(int bugID, ArrayList<IntegratedAnalysisValue> rankedValues, HashSet<SourceFile> fixedFiles) throws Exception {
		HashSet<Integer> AnswerFileIDs = new HashSet<Integer>();
		HashMap<Integer, String> AnswerFileNameMap = new HashMap<Integer, String>();

		Iterator<SourceFile> fixedFilesIter = fixedFiles.iterator();
		while (fixedFilesIter.hasNext()) {
			SourceFile fixedFile = fixedFilesIter.next();
			AnswerFileIDs.add(fixedFile.getSourceFileVersionID());
			AnswerFileNameMap.put(fixedFile.getSourceFileVersionID(), fixedFile.getName());
		}
		
		int numberOfFixedFiles = 0;
		int numberOfPositiveInstances = 0;
		for (int j = 0; j < rankedValues.size(); j ++) {
			int sourceFileVersionID = rankedValues.get(j).getSourceFileVersionID();
			if (AnswerFileIDs.contains(sourceFileVersionID)) {
				numberOfPositiveInstances++;
			}
		}
		
		double sumOfAP = 0.0;
		double precision = 0.0;
		
		int checkCount = 0;
		int checkTopAnswer = 0;
		int p_top1 = 0;
		int p_top5 = 0;
		int p_top10 = 0;
		for (int j = 0; j < rankedValues.size(); j++) {
			int sourceFileID = rankedValues.get(j).getSourceFileVersionID();
			
			if (AnswerFileIDs.contains(sourceFileID)) {
				double score = rankedValues.get(j).getBLIAScore();
				String fileName = AnswerFileNameMap.get(sourceFileID);						
				String log = bugID + "\t" + fileName + "\t" + j + "\t" + score +"\n";
				writer.write(log);
				
				if (j < 1) { p_top1++; } 
				if (j < 5) { p_top5++; } 
				if (j < 10){ p_top10++; }
				checkCount++;
				
				//MAP 계산.
				numberOfFixedFiles++;
				precision = ((double) numberOfFixedFiles) / (j + 1);
				sumOfAP += (precision / numberOfPositiveInstances);
				
				//MRR 계산.
				if (checkTopAnswer==0){
					sumOfRRank += (1.0 / (j + 1));
					checkTopAnswer = 1;
				}
			}
		}

		if (p_top1 > 0) {	top1++; } 
		if (p_top5 > 0) {	top5++; } 
		if (p_top10 > 0){	top10++;}
		MAP += sumOfAP;
		
		if (checkCount==0)
			System.out.println(bugID + "\t" + "failed to find answers");
    }
	
	private void printAllResults(String _path, int _bugID, ArrayList<IntegratedAnalysisValue> _rankedValues) throws IOException {
		FileWriter fullwriter = new FileWriter(_path + Property.getInstance().separator + _bugID + ".txt", false);
		
		for (int rank = 0; rank < _rankedValues.size(); rank++) {
			//int sourceFileID = _rankedValues.get(rank).getSourceFileVersionID();
			double score = _rankedValues.get(rank).getBLIAScore();
			int versionID = _rankedValues.get(rank).getSourceFileVersionID();
			String fileName = sourceIDFileMap.get(versionID);
			String line = rank + "\t" + score + "\t" + fileName +"\n";
			fullwriter.write(line);
		}
		fullwriter.close();
	}
	
	private void calculateMetrics() throws IOException{
		String recommendedPath = Property.getInstance().WORK_DIR + Property.getInstance().separator + "recommended";
		File resultDir = new File(recommendedPath);
		if (!resultDir.exists()) 
			resultDir.mkdirs();
		
		//result output file
		String outputFileName = Property.getInstance().OUTPUT_FILE;
		writer = new FileWriter(outputFileName, false); 
		
		//calculation
		for (int i = 0; i < bugs.size(); i++) {
			int bugID = bugs.get(i).getID();
			try {
    			HashSet<SourceFile> answerFiles = realFixedFilesMap.get(bugID);
    			// Exception handling
    			if (null == answerFiles) {
    				System.err.println("[Error] " + bugID + "has no fixed files.");
    				continue;
    			}
    			
    			ArrayList<IntegratedAnalysisValue> rankedValues = rankedValuesMap.get(bugID);
    			if (rankedValues == null) {
    				System.out.printf("[ERROR] Bug ID: %d\n", bugID);
    				continue;
    			}
    			
    			printAllResults(recommendedPath, bugID, rankedValues);
    			
        		calculate(bugID, rankedValues, answerFiles);
        		
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
		}
		
		experimentResult.setTop1(top1);
		experimentResult.setTop5(top5);
		experimentResult.setTop10(top10);
		
		int bugCount = bugs.size();
		experimentResult.setTop1Rate((double) top1 / bugCount);
		experimentResult.setTop5Rate((double) top5 / bugCount);
		experimentResult.setTop10Rate((double) top10 / bugCount);

		System.out.printf("Top1: %d, Top5: %d, Top10: %d\nTop1Rate: %f, Top5Rate: %f, Top10Rate: %f\n",
				experimentResult.getTop1(), experimentResult.getTop5(), experimentResult.getTop10(),
				experimentResult.getTop1Rate(), experimentResult.getTop5Rate(), experimentResult.getTop10Rate());
		String log = "Top1: " + experimentResult.getTop1() + ", " +
				"Top5: " + experimentResult.getTop5() + ", " +
				"Top10: " + experimentResult.getTop10() + ", " +
				"Top1Rate: " + experimentResult.getTop1Rate() + ", " +
				"Top5Rate: " + experimentResult.getTop5Rate() + ", " +
				"Top10Rate: " + experimentResult.getTop10Rate() + "\n";
		//writer.write(log);
		
////////////////////////////////////////////////////////////////////////////
		double MRR = sumOfRRank / bugs.size();
		experimentResult.setMRR(MRR);
		
		System.out.printf("MRR: %f\n", experimentResult.getMRR());
		log = "MRR: " + experimentResult.getMRR() + "\n";
		//writer.write(log);

////////////////////////////////////////////////////////////////////////////
		MAP = MAP / bugs.size();
		experimentResult.setMAP(MAP);
		
		System.out.printf("MAP: %f\n", experimentResult.getMAP());
		log = "MAP: " + experimentResult.getMAP() + "\n";
		//writer.write(log);
		
		writer.flush();
		writer.close();
		
	}

}
