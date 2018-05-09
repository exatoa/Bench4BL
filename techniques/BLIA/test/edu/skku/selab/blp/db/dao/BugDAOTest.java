/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.SourceFileCorpus;
import edu.skku.selab.blp.common.BugCorpus;
import edu.skku.selab.blp.common.SourceFile;
import edu.skku.selab.blp.db.AnalysisValue;
import edu.skku.selab.blp.db.SimilarBugInfo;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugDAOTest {
	private int bugID1 = 101;
	private int bugID2 = 102;
	private int bugID3 = 103;
	private String productName = "BLIA";
	private String fixedDateString1 = "2004-12-01 17:40:00";
	private String fixedDateString2 = "2014-03-27 07:12:00";
	private String fixedDateString3 = "2014-03-27 07:12:00";
	private String corpusContent1 = "acc contain constant us defin access";
	private String corpusContent2 = "element listen event event result";
	private String corpusContent3 = "event blia result";
	private String summaryContent1 = "acc contain";
	private String summaryContent2 = "element";
	private String summaryContent3 = "event";
	private String descriptionContent1 = "constant us defin access";
	private String descriptionContent2 = "listen event event result";
	private String descriptionContent3 = "blia result";

	private String stackTrace1 = "edu.skku.selab.blia";
	private String stackTrace2 = "edu.skku.selab.blp";
	private String stackTrace3 = "org.blia";
	private String stackTrace4 = "org.blp";
	private String term1 = "acc";
	private String term2 = "element";
	private String version = "v1.0";
	
	private int termCount = 10;
	private int idc = 32;
	private double tf = 0.53;
	private double idf = 0.259;
	private double delta = 0.00005;
	
	private double termWeight = 0.0482;
	
	private String fileName1 = "test_10.java";
	private String fileName2 = "test_11.java";
	
	private double similarityScore1 = 0.82;
	private double similarityScore2 = 0.24;
	
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
		
		Bug bug1 = new Bug();
		bug1.setID(bugID1);
		bug1.setProductName(productName);
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date fixedDate1 = simpleDateFormat.parse(fixedDateString1);
		bug1.setFixedDate(fixedDate1);
		BugCorpus bugCorpus1 = new BugCorpus();
		bugCorpus1.setContent(corpusContent1);
		bugCorpus1.setSummaryPart(summaryContent1);
		bugCorpus1.setDescriptionPart(descriptionContent1);
		bug1.setCorpus(bugCorpus1);
		ArrayList<String> stackTraces1 = new ArrayList<String>();
		stackTraces1.add(stackTrace1);
		stackTraces1.add(stackTrace2);
		bug1.setStackTraceClasses(stackTraces1);
		bug1.setVersion(version);
		
		Bug bug2 = new Bug();
		bug2.setID(bugID2);
		bug2.setProductName(productName);
		bug2.setFixedDate(fixedDateString2);
		BugCorpus bugCorpus2 = new BugCorpus();
		bugCorpus2.setContent(corpusContent2);
		bugCorpus2.setSummaryPart(summaryContent2);
		bugCorpus2.setDescriptionPart(descriptionContent2);
		bug2.setCorpus(bugCorpus2);
		ArrayList<String> stackTraces2 = new ArrayList<String>();
		stackTraces2.add(stackTrace3);
		stackTraces2.add(stackTrace4);
		bug2.setStackTraceClasses(stackTraces2);
		bug2.setVersion(version);
		
		Bug bug3 = new Bug();
		bug3.setID(bugID3);
		bug3.setProductName(productName);
		bug3.setFixedDate(fixedDateString3);
		BugCorpus bugCorpus3 = new BugCorpus();
		bugCorpus3.setContent(corpusContent3);
		bugCorpus3.setSummaryPart(summaryContent3);
		bugCorpus3.setDescriptionPart(descriptionContent3);
		bug3.setCorpus(bugCorpus3);
		bug3.setVersion(version);
		
		BugDAO bugDAO = new BugDAO();
		
		bugDAO.deleteAllBugs();
		assertNotEquals("Bug insertion failed!", BaseDAO.INVALID, bugDAO.insertBug(bug1));
		assertNotEquals("Bug insertion failed!", BaseDAO.INVALID, bugDAO.insertBug(bug2));
		assertNotEquals("Bug insertion failed!", BaseDAO.INVALID, bugDAO.insertBug(bug3));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void verifyGetBug() throws Exception {
		BugDAO bugDAO = new BugDAO();
		HashMap<Integer, Bug> bugs = bugDAO.getBugs();
		
		Bug foundBug1 = bugs.get(bugID1);
		Bug foundBug2 = bugs.get(bugID2);
		assertEquals("bugID1 is wrong.", bugID1, foundBug1.getID());
		assertEquals("productName is wrong.", productName, foundBug1.getProductName());
		assertEquals("fixedDateString1 is wrong.", fixedDateString1, foundBug1.getFixedDateString());
		BugCorpus bugCorpus = foundBug1.getCorpus();
		assertEquals("corpusContent1 is wrong.", corpusContent1, bugCorpus.getContent());
		assertEquals("summaryContent1 is wrong.", summaryContent1, bugCorpus.getSummaryPart());
		assertEquals("descriptionContent1 is wrong.", descriptionContent1, bugCorpus.getDescriptionPart());
		assertEquals("stackTraces1 is wrong.", stackTrace1, foundBug1.getStackTraceClasses().get(0));
		assertEquals("stackTraces2 is wrong.", stackTrace2, foundBug1.getStackTraceClasses().get(1));
		assertEquals("version is wrong.", version, foundBug1.getVersion());

		assertEquals("bugID2 is wrong.", bugID2, foundBug2.getID());
		assertEquals("productName is wrong.", productName, foundBug2.getProductName());
		assertEquals("fixedDateString2 is wrong.", fixedDateString2, foundBug2.getFixedDateString());
		bugCorpus = foundBug2.getCorpus();
		assertEquals("corpusContent2 is wrong.", corpusContent2, bugCorpus.getContent());
		assertEquals("summaryContent2 is wrong.", summaryContent2, bugCorpus.getSummaryPart());
		assertEquals("descriptionContent2 is wrong.", descriptionContent2, bugCorpus.getDescriptionPart());
		assertEquals("stackTrace3 is wrong.", stackTrace3, foundBug2.getStackTraceClasses().get(0));
		assertEquals("stackTrace4 is wrong.", stackTrace4, foundBug2.getStackTraceClasses().get(1));
		assertEquals("version is wrong.", version, foundBug2.getVersion());
		
		Bug foundBug = bugDAO.getBug(bugID1, productName);
		assertEquals("bugID1 is wrong.", bugID1, foundBug.getID());
		assertEquals("productName is wrong.", productName, foundBug.getProductName());
		assertEquals("fixedDateString1 is wrong.", fixedDateString1, foundBug.getFixedDateString());
		bugCorpus = foundBug.getCorpus();
		assertEquals("corpusContent1 is wrong.", corpusContent1, bugCorpus.getContent());
		assertEquals("summaryContent1 is wrong.", summaryContent1, bugCorpus.getSummaryPart());
		assertEquals("descriptionContent1 is wrong.", descriptionContent1, bugCorpus.getDescriptionPart());
		assertEquals("stackTraces1 is wrong.", stackTrace1, foundBug.getStackTraceClasses().get(0));
		assertEquals("stackTraces2 is wrong.", stackTrace2, foundBug.getStackTraceClasses().get(1));
		assertEquals("version is wrong.", version, foundBug.getVersion());
		
		assertTrue(2 == bugDAO.getBugCountWithFixedDate(productName, fixedDateString3));
		ArrayList<Bug> bugList = bugDAO.getPreviousFixedBugs(productName, fixedDateString3, bugID3);
		assertTrue(2 == bugList.size());
		
		foundBug1 = bugList.get(0);
		assertEquals("bugID1 is wrong.", bugID1, foundBug1.getID());
		assertEquals("productName is wrong.", productName, foundBug1.getProductName());
		assertEquals("fixedDateString1 is wrong.", fixedDateString1, foundBug1.getFixedDateString());
		bugCorpus = foundBug1.getCorpus();
		assertEquals("corpusContent1 is wrong.", corpusContent1, bugCorpus.getContent());
		assertEquals("summaryContent1 is wrong.", summaryContent1, bugCorpus.getSummaryPart());
		assertEquals("descriptionContent1 is wrong.", descriptionContent1, bugCorpus.getDescriptionPart());
		assertEquals("stackTraces1 is wrong.", stackTrace1, foundBug1.getStackTraceClasses().get(0));
		assertEquals("stackTraces2 is wrong.", stackTrace2, foundBug1.getStackTraceClasses().get(1));
		assertEquals("version is wrong.", version, foundBug1.getVersion());
		
		foundBug2 = bugList.get(1);
		assertEquals("bugID2 is wrong.", bugID2, foundBug2.getID());
		assertEquals("productName is wrong.", productName, foundBug2.getProductName());
		assertEquals("fixedDateString2 is wrong.", fixedDateString2, foundBug2.getFixedDateString());
		bugCorpus = foundBug2.getCorpus();
		assertEquals("corpusContent2 is wrong.", corpusContent2, bugCorpus.getContent());
		assertEquals("summaryContent2 is wrong.", summaryContent2, bugCorpus.getSummaryPart());
		assertEquals("descriptionContent2 is wrong.", descriptionContent2, bugCorpus.getDescriptionPart());
		assertEquals("stackTrace3 is wrong.", stackTrace3, foundBug2.getStackTraceClasses().get(0));
		assertEquals("stackTrace4 is wrong.", stackTrace4, foundBug2.getStackTraceClasses().get(1));
		assertEquals("version is wrong.", version, foundBug2.getVersion());
	}

	@Test
	public void verifyGetBugSfTermVector() throws Exception {
		BugDAO bugDAO = new BugDAO();

		bugDAO.deleteAllTerms();
		assertNotEquals("Term insertion failed!", BaseDAO.INVALID, bugDAO.insertBugTerm(term1, productName));
		assertNotEquals("Term insertion failed!", BaseDAO.INVALID, bugDAO.insertBugTerm(term2, productName));
		
		HashMap<String, Integer> wordMap = bugDAO.getTermMap(productName);
		assertNotNull("Can't find corpus1.", wordMap.get(term1));
		assertNotNull("Can't find corpus2.", wordMap.get(term2));
		
		// preparation phase
		bugDAO.deleteAllBugSfTermWeights();
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		sourceFileDAO.deleteAllTerms();
		sourceFileDAO.insertTerm(term1, productName);
		sourceFileDAO.insertTerm(term2, productName);
		
		AnalysisValue analysisValue = new AnalysisValue(bugID1, productName, term1, termCount, idc, tf, idf);
		assertNotEquals("BugSfAnalysisValue insertion failed!", BaseDAO.INVALID, bugDAO.insertBugSfTermWeight(analysisValue));
		
		AnalysisValue termWeight = bugDAO.getBugSfTermWeight(bugID1, productName, term1);
		assertEquals("Bug ID of AnalysisValue is wrong.", bugID1, termWeight.getID());
		assertEquals("productName of AnalysisValue is wrong.", productName, termWeight.getProductName());
		assertEquals("term1 of AnalysisValue is wrong.", term1, termWeight.getTerm());
		assertEquals("termCount of AnalysisValue is wrong.", termCount, termWeight.getTermCount());
		assertEquals("idc of AnalysisValue is wrong.", idc, termWeight.getInvDocCount());
		assertEquals("tf of AnalysisValue is wrong.", tf, termWeight.getTf(), delta);
		assertEquals("idf of AnalysisValue is wrong.", idf, termWeight.getIdf(), delta);
	}

	@Test
	public void verifyGetBugTermWeight() throws Exception {
		BugDAO bugDAO = new BugDAO();
		
		bugDAO.deleteAllTerms();
		assertNotEquals("Term insertion failed!", BaseDAO.INVALID, bugDAO.insertBugTerm(term1, productName));
		
		bugDAO.deleteAllBugTermWeights();
		AnalysisValue analysisValue = new AnalysisValue(bugID1, productName, term1, termWeight);
		assertNotEquals("BugAnalysisValue insertion failed!", BaseDAO.INVALID, bugDAO.insertBugTermWeight(analysisValue));
		
		AnalysisValue returnValue = bugDAO.getBugTermWeight(bugID1, productName, term1);
		assertEquals("Bug ID of AnalysisValue is wrong.", bugID1, returnValue.getID());
		assertEquals("productName of AnalysisValue is wrong.", productName, returnValue.getProductName());
		assertEquals("term1 of AnalysisValue is wrong.", term1, returnValue.getTerm());
		assertEquals("termWeight of AnalysisValue is wrong.", termWeight, returnValue.getTermWeight(), delta);
	}

	@Test
	public void verifyGetFixedFiles() throws Exception {
		BugDAO bugDAO = new BugDAO();
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		
		// preparation phase
		sourceFileDAO.deleteAllSourceFiles();
		assertNotEquals("fileName1 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName1, productName));
		assertNotEquals("fileName2 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName2, productName));

		sourceFileDAO.deleteAllVersions();
		String version1 = "v0.1";
		String releaseDate1 = "2004-10-18 17:40:00";
		String version2 = "v0.2";
		String releaseDate2 = "2014-02-12 07:12:00";
		assertNotEquals("version1 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertVersion(version1, releaseDate1));
		assertNotEquals("version2 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertVersion(version2, releaseDate2));
		
		int totalCorpusCount1 = 5;
		int totalCorpusCount2 = 34;
		double lengthScore1 = 0.32;
		double lengthScore2 = 0.1238;
		SourceFileCorpus corpus1 = new SourceFileCorpus();
		corpus1.setContent(corpusContent1);
		SourceFileCorpus corpus2 = new SourceFileCorpus();
		corpus2.setContent(corpusContent2);
		int sourceFileID = sourceFileDAO.getSourceFileID(fileName1, productName);				
		assertNotEquals("fileName1's corpus insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertCorpusSet(sourceFileID, version1, corpus1, totalCorpusCount1, lengthScore1));
		sourceFileID = sourceFileDAO.getSourceFileID(fileName2, productName);
		assertNotEquals("fileName2's corpus insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertCorpusSet(sourceFileID, version1, corpus2, totalCorpusCount2, lengthScore2));
		
		assertNotEquals("BugFixedFileInfo insertion failed!", BaseDAO.INVALID, bugDAO.insertBugFixedFileInfo(bugID1, fileName1, version1, productName));
		assertNotEquals("BugFixedFileInfo insertion failed!", BaseDAO.INVALID, bugDAO.insertBugFixedFileInfo(bugID1, fileName2, version1, productName));
		
		HashSet<SourceFile> fixedFiles = bugDAO.getFixedFiles(bugID1);
		assertEquals("Fixedfiles count is wrong.", 2, fixedFiles.size());
		Iterator<SourceFile> iter1 = fixedFiles.iterator();
		
		SourceFile sourceFile = iter1.next();
		if (fileName1.equalsIgnoreCase(sourceFile.getName())) {
			assertEquals("version1 is wrong.", version1, sourceFile.getVersion());
		} else if (fileName2.equalsIgnoreCase(sourceFile.getName())) {
			assertEquals("version1 is wrong.", version1, sourceFile.getVersion());
		} else {
			fail("SourceFile is wrong.");
		}
		
		sourceFile = iter1.next();
		if (fileName1.equalsIgnoreCase(sourceFile.getName())) {
			assertEquals("version1 is wrong.", version1, sourceFile.getVersion());
		} else if (fileName2.equalsIgnoreCase(sourceFile.getName())) {
			assertEquals("version1 is wrong.", version1, sourceFile.getVersion());
		} else {
			fail("SourceFile is wrong.");
		}
	}
	
	@Test
	public void verifyGetSimilarBugInfos() throws Exception {
		BugDAO bugDAO = new BugDAO();
		
		bugDAO.deleteAllSimilarBugInfo();
		assertNotEquals("BugFixedFileInfo insertion failed!", BaseDAO.INVALID, bugDAO.insertSimilarBugInfo(bugID1, bugID2, similarityScore1));
		assertNotEquals("BugFixedFileInfo insertion failed!", BaseDAO.INVALID, bugDAO.insertSimilarBugInfo(bugID1, bugID3, similarityScore2));
		HashSet<SimilarBugInfo> similarBugInfos = bugDAO.getSimilarBugInfos(bugID1);
		assertEquals("SimilarBugInfos count is wrong.", 2, similarBugInfos.size());
		Iterator<SimilarBugInfo> iter2 = similarBugInfos.iterator();
		
		SimilarBugInfo similarBugInfo = iter2.next();
		if (bugID2 == similarBugInfo.getSimilarBugID()) {
			assertEquals("similarityScore1 is wrong.", similarityScore1, similarBugInfo.getSimilarityScore(), delta);			
		} else if (bugID3 == similarBugInfo.getSimilarBugID()) {
			assertEquals("similarityScore2 is wrong.", similarityScore2, similarBugInfo.getSimilarityScore(), delta);
		} else {
			fail("SimilarBugInfo is wrong.");
		}
		
		similarBugInfo = iter2.next();
		if (bugID2 == similarBugInfo.getSimilarBugID()) {
			assertEquals("similarityScore1 is wrong.", similarityScore1, similarBugInfo.getSimilarityScore(), delta);			
		} else if (bugID3 == similarBugInfo.getSimilarBugID()) {
			assertEquals("similarityScore2 is wrong.", similarityScore2, similarBugInfo.getSimilarityScore(), delta);
		} else {
			fail("SimilarBugInfo is wrong.");
		}
	}
	
	@Test
	public void verifyGetStackTraceClasses() throws Exception {
		BugDAO bugDAO = new BugDAO();

		bugDAO.deleteAllStackTraceClasses();
		String className1 = "edu.skku.blp.blia.className1";
		String className2 = "edu.skku.blp.blia.className2";
		assertNotEquals("StackTraceClass insertion failed!", BaseDAO.INVALID, bugDAO.insertStackTraceClass(bugID3, className1));
		assertNotEquals("StackTraceClass insertion failed!", BaseDAO.INVALID, bugDAO.insertStackTraceClass(bugID3, className2));
		
		ArrayList<String> stackTraceClasses = bugDAO.getStackTraceClasses(bugID3);
		assertEquals("stackTraceClasses count is wrong.", 2, stackTraceClasses.size());
		
		assertEquals("className1 is wrong.", className1, stackTraceClasses.get(0));
		assertEquals("className2 is wrong.", className2, stackTraceClasses.get(1));		
	}

}
