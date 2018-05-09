/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.common.SourceFileCorpus;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class IntegratedAnalysisDAOTest {

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
		DbUtil dbUtil = new DbUtil();
		dbUtil.openConnetion();
		dbUtil.initializeAllData();
		dbUtil.closeConnection();

		String fileName1 = "test_10.java";
		String fileName2 = "test_11.java";
		String productName = "BLIA";
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		
		sourceFileDAO.deleteAllSourceFiles();
		assertNotEquals("fileName1 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName1, productName));
		assertNotEquals("fileName2 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName2, productName));
		
		sourceFileDAO.deleteAllVersions();
		String version1 = "v0.1";
		String releaseDate1 = "2004-10-18 17:40:00";
		String version2 = "v0.2";
		String releaseDate2 = "2014-02-12 07:12:00";
		assertNotEquals("Version insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertVersion(version1, releaseDate1));
		assertNotEquals("Version insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertVersion(version2, releaseDate2));
		
		String corpusContent1 = "acc contain constant us defin access";
		String corpusContent2 = "element listen event event result";
		SourceFileCorpus corpus1 = new SourceFileCorpus();
		corpus1.setContent(corpusContent1);
		SourceFileCorpus corpus2 = new SourceFileCorpus();
		corpus2.setContent(corpusContent2);
		
		int totalCorpusCount1 = 5;
		int totalCorpusCount2 = 34;
		double lengthScore1 = 0.32;
		double lengthScore2 = 0.1238;
		int sourceFileID = sourceFileDAO.getSourceFileID(fileName1, productName);
		assertNotEquals("CorpusSet insertion failed!", BaseDAO.INVALID,
				sourceFileDAO.insertCorpusSet(sourceFileID, version1, corpus1, totalCorpusCount1, lengthScore1));
		assertNotEquals("CorpusSet insertion failed!", BaseDAO.INVALID,
				sourceFileDAO.insertCorpusSet(sourceFileID, version2, corpus2, totalCorpusCount2, lengthScore2));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void verifyGetAnalysisValues() throws Exception {
		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
		
		integratedAnalysisDAO.deleteAllIntegratedAnalysisInfos();
		int bugID1 = 101;
		String productName = "BLIA";
		String fileName1 = "test_10.java";
		double vsmScore = 0.321;
		double similarityScore = 0.6281;
		double bugLocatorScore = 0.5833;
		double stackTraceScore = 0.8321;
		double bliaScore = 0.7329;
		final double delta = 0.00001;
		String version1 = "v0.1";

		IntegratedAnalysisValue integratedAnalysisValue = new IntegratedAnalysisValue();
		integratedAnalysisValue.setBugID(bugID1);
		integratedAnalysisValue.setFileName(fileName1);
		integratedAnalysisValue.setVersion(version1);
		integratedAnalysisValue.setProductName(productName);
		integratedAnalysisValue.setVsmScore(vsmScore);
		integratedAnalysisValue.setSimilarityScore(similarityScore);
		integratedAnalysisValue.setBugLocatorScore(bugLocatorScore);
		integratedAnalysisValue.setStackTraceScore(stackTraceScore);
		integratedAnalysisValue.setBLIAScore(bliaScore);
		
		assertNotEquals("AnalysisVaule insertion failed!", BaseDAO.INVALID,
				integratedAnalysisDAO.insertAnalysisVaule(integratedAnalysisValue));
		
		HashMap<Integer, IntegratedAnalysisValue> analysisValues = integratedAnalysisDAO.getAnalysisValues(bugID1);
		assertEquals("analysisValues size is wrong.", 1, analysisValues.size());
		
		
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		int sourceFileVersionID = sourceFileDAO.getSourceFileVersionID(fileName1, productName, version1);
		IntegratedAnalysisValue analysisValue = analysisValues.get(sourceFileVersionID); 
		assertNotNull("analysisValue can't be found.", analysisValue);
		assertEquals("Bug ID is NOT same!", bugID1, analysisValue.getBugID());
		
		assertEquals("VSM Score is NOT same!", vsmScore, analysisValue.getVsmScore(), delta);
		assertEquals("similarityScore is NOT same!", similarityScore, analysisValue.getSimilarityScore(), delta);
		assertEquals("bugLocatorScore is NOT same!", bugLocatorScore, analysisValue.getBugLocatorScore(), delta);
		assertEquals("stackTraceScore is NOT same!", stackTraceScore, analysisValue.getStackTraceScore(), delta);
		assertEquals("bliaScore is NOT same!", bliaScore, analysisValue.getBLIAScore(), delta);
	}

}
