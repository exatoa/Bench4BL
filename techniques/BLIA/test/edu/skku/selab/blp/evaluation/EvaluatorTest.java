/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.evaluation;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.blia.analysis.BLIA;
import edu.skku.selab.blp.db.dao.DbUtil;
import edu.skku.selab.blp.db.dao.SourceFileDAO;
import edu.skku.selab.blp.evaluation.Evaluator;
import edu.skku.selab.blp.utils.Util;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class EvaluatorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	private void runBLIA(boolean useStrucrutedInfo, boolean prepareAnalysisData, boolean preAnalyze, boolean analyze,
			boolean includeStackTrace) throws Exception {
		Property prop = Property.getInstance();
				
		String version = SourceFileDAO.DEFAULT_VERSION_STRING;
		long startTime = System.currentTimeMillis();

		BLIA blia = new BLIA();

		if (prepareAnalysisData) {
			DbUtil dbUtil = new DbUtil();
			String dbName = Property.getInstance().productName;
			dbUtil.openConnetion(dbName);
			boolean commitDataIncluded = false;
			dbUtil.initializeAllData(commitDataIncluded);
			dbUtil.closeConnection();

			System.out.printf("[STARTED] BLIA prepareAnalysisData().\n");
			startTime = System.currentTimeMillis();
			blia.prepareAnalysisData(useStrucrutedInfo, prop.since.getTime(), prop.until.getTime());
			System.out.printf("[DONE] BLIA prepareAnalysisData().(Total %s sec)\n", Util.getElapsedTimeSting(startTime));
		}

		if (preAnalyze) {
			if (!prepareAnalysisData) {
				DbUtil dbUtil = new DbUtil();
				String dbName = prop.productName;
				dbUtil.openConnetion(dbName);
				dbUtil.initializeAnalysisData();
				dbUtil.closeConnection();
			}
			
			System.out.printf("[STARTED] BLIA pre-anlaysis.\n");
			blia.preAnalyze();
			System.out.printf("[DONE] BLIA pre-anlaysis.(Total %s sec)\n", Util.getElapsedTimeSting(startTime));
		}
		
		if (analyze) {
			System.out.printf("[STARTED] BLIA anlaysis.\n");
			startTime = System.currentTimeMillis();
			blia.analyze(version, includeStackTrace);
			System.out.printf("[DONE] BLIA anlaysis.(Total %s sec)\n", Util.getElapsedTimeSting(startTime));
		}
	}
	
	
	@Test
	public void verifyEvaluateBLIAOnce() throws Exception {
		Property prop = Property.loadInstance();

		boolean useStrucrutedInfo = true;
		
		boolean prepareAnalysisData = true;
		boolean preAnalyze = true;
		boolean analyze = true;
		boolean includeStackTrace = true;

		long totalStartTime = System.currentTimeMillis();
		System.out.printf("[STARTED] BLIA Evaluation once.\n");
		runBLIA(useStrucrutedInfo, prepareAnalysisData, preAnalyze, analyze, includeStackTrace);
		
		if (analyze) {
			String algorithmDescription = "[BLIA] alpha: " + prop.alpha +
					", beta: " + prop.beta + ", pastDays: " + prop.pastDays +
					", cadidateLimitRate: " + prop.candidateLimitRate;
			if (useStrucrutedInfo) {
				algorithmDescription += " with structured info";
			}
			if (!includeStackTrace) {
				algorithmDescription += " without Stack-Trace analysis";
			}
			
			Evaluator evaluator = new Evaluator(prop.productName, Evaluator.ALG_BLIA, algorithmDescription,
					prop.alpha, prop.beta, prop.pastDays, prop.candidateLimitRate);
			evaluator.evaluate();
		}
		
		System.out.printf("[DONE] BLIA Evaluation once(Total %s sec)\n", Util.getElapsedTimeSting(totalStartTime));
	}
	
	@Test
	public void verifyEvaluateBLIAWithChangingAlphaAndBeta() throws Exception {
		Property prop = Property.loadInstance();
		
		// Before this method running, verifyEvaluateBLIAOnce() should be called to create indexing DB
		boolean useStrucrutedInfo = true;
		
		boolean prepareAnalysisData = false;
		boolean preAnalyze = false;
		boolean analyze = true;
		boolean includeStackTrace = true;

		long startTime = System.currentTimeMillis();
		System.out.printf("[STARTED] BLIA Evaluation repeatedly.\n");
		
		for (double alpha = 0.0; alpha <= 0.9; alpha += 0.1) {
			for (double beta = 0.0; beta <= 0.9; beta += 0.1) {
				prop.alpha = alpha;
				prop.beta = beta;
				runBLIA(useStrucrutedInfo, prepareAnalysisData, preAnalyze, analyze, includeStackTrace);

				if (analyze) {
					String algorithmDescription = "[BLIA] alpha: " + prop.alpha +
							", beta: " + prop.beta + ", pastDays: " + prop.pastDays;
					if (useStrucrutedInfo) {
						algorithmDescription += " with structured info";
					}
					if (!includeStackTrace) {
						algorithmDescription += " without Stack-Trace analysis";
					}
					
					Evaluator evaluator = new Evaluator(prop.productName, Evaluator.ALG_BLIA, algorithmDescription,
							prop.alpha, prop.beta, prop.pastDays, prop.candidateLimitRate);
					evaluator.evaluate();
				}
			}
		}
		
		System.out.printf("[DONE] BLIA Evaluation repeatedly(Total %s sec)\n", Util.getElapsedTimeSting(startTime));
	}
	
	@Test
	public void verifyEvaluateBLIAWithChangingPastDays() throws Exception {
		Property prop = Property.loadInstance();
		
		// Before this method running, verifyEvaluateBLIAOnce() should be called to create indexing DB
		boolean useStrucrutedInfo = true;
		
		boolean prepareAnalysisData = false;
		boolean preAnalyze = true; // DO NOT change preAnalyze for this experiment, because changing pastDays needs pre-analysis. 
		boolean analyze = true;
		boolean includeStackTrace = true;
		
		long startTime = System.currentTimeMillis();
		System.out.printf("[STARTED] BLIA Evaluation repeatedly.\n");
		
		int[] pastDays = {15}; //, 30, 60, 90, 120};
		for (int i = 0; i < pastDays.length; i++) {
			prop.pastDays = pastDays[i];
			runBLIA(useStrucrutedInfo, prepareAnalysisData, preAnalyze, analyze, includeStackTrace);

			if (analyze) {
				String algorithmDescription = "[BLIA] alpha: " + prop.alpha +
						", beta: " + prop.beta + ", pastDays: " + prop.pastDays;
				if (useStrucrutedInfo) {
					algorithmDescription += " with structured info";
				}
				if (!includeStackTrace) {
					algorithmDescription += " without Stack-Trace analysis";
				}
				
				Evaluator evaluator = new Evaluator(prop.productName, Evaluator.ALG_BLIA, algorithmDescription,
						prop.alpha, prop.beta, prop.pastDays, prop.candidateLimitRate);
				evaluator.evaluate();
			}
		}
		
		System.out.printf("[DONE] BLIA Evaluation repeatedly(Total %s sec)\n", Util.getElapsedTimeSting(startTime));
	}
	
	@Test
	public void verifyEvaluateBLIAWithChangingCandidateLimitRate() throws Exception {
		Property prop = Property.loadInstance();
		
		// Before this method running, verifyEvaluateBLIAOnce() should be called to create indexing DB
		boolean useStrucrutedInfo = true;
		
		// DO NOT change prepareAnalysisData for this experiment, because changing candidateLimitSize needs prepareAnalysisData. 
		boolean prepareAnalysisData = true;
		boolean preAnalyze = true;
		boolean analyze = true;
		boolean includeStackTrace = true;
		
		long startTime = System.currentTimeMillis();
		System.out.printf("[STARTED] BLIA Evaluation repeatedly.\n");
		
		double[] candidateLimitRate = {1.0, 0.5, 0.2, 0.1, 0.05};
		
		for (int i = 0; i < candidateLimitRate.length; i++) {
			prop.candidateLimitRate = candidateLimitRate[i];
			runBLIA(useStrucrutedInfo, prepareAnalysisData, preAnalyze, analyze, includeStackTrace);

			if (analyze) {
				String algorithmDescription = "[BLIA] alpha: " + prop.alpha +
						", beta: " + prop.beta + ", pastDays: " + prop.pastDays;
				algorithmDescription += ", candidateLimitRate: " + candidateLimitRate[i];
				if (useStrucrutedInfo) {
					algorithmDescription += " with structured info";
				}
				if (!includeStackTrace) {
					algorithmDescription += " without Stack-Trace analysis";
				}
				
				Evaluator evaluator = new Evaluator(prop.productName, Evaluator.ALG_BLIA, algorithmDescription,
						prop.alpha, prop.beta, prop.pastDays, prop.candidateLimitRate);
				evaluator.evaluate();
			}
		}
		
		System.out.printf("[DONE] BLIA Evaluation repeatedly(Total %s sec)\n", Util.getElapsedTimeSting(startTime));
	}
}
