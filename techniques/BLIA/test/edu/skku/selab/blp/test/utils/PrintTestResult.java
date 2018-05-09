package edu.skku.selab.blp.test.utils;

import static org.junit.Assert.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.BugCorpus;
import edu.skku.selab.blp.common.SourceFile;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAO;
import edu.skku.selab.blp.utils.Stem;

/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class PrintTestResult {

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
	
	@Test
	public void testStem() {
		String word = "i'll";
		assertEquals(word, Stem.stem(word));
		
		String[] results = StringUtils.splitByCharacterTypeCamelCase(word);
		assertEquals("i", results[0]);
		assertEquals("'", results[1]);
		assertEquals("ll", results[2]);
		
		word = "'s";
		assertEquals("", word.substring(0, word.lastIndexOf("\'s")));
		
		word = "tree's";
		assertEquals("tree", word.substring(0, word.lastIndexOf("\'s")));
		
		word = "tree's's";
		assertEquals("tree's", word.substring(0, word.lastIndexOf("\'s")));
	}

	@Test
	public void printResult() throws Exception {
		BugDAO bugDAO = new BugDAO();
		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
		
		HashMap<Integer, Bug> allBugs = bugDAO.getBugs();
		FileWriter resultWriter = new FileWriter("ExperimentResult.txt");
		
		Iterator<Bug> bugIter = allBugs.values().iterator();
		int limit = 20;
		while (bugIter.hasNext()) {
			Bug bug = bugIter.next();
			
			ArrayList<IntegratedAnalysisValue> resultList = integratedAnalysisDAO.getBLIARankedValues(bug.getID(), limit);
			
			for (int i = 0; i < resultList.size(); i++) {
				IntegratedAnalysisValue result = resultList.get(i); 
				resultWriter.write(result.getBugID() + "|" +
						(i + 1) + "|" +
						result.getFileName() + "|" +
						result.getVsmScore() + "|" +
						result.getSimilarityScore() + "|" +
						result.getBugLocatorScore() + "|" +
						result.getStackTraceScore() + "|" +
						result.getCommitLogScore() + "|" +
						result.getBLIAScore() + "\n");				
			}
		}
		resultWriter.close();
		
		FileWriter bugInfotWriter = new FileWriter("BugInfo.txt");
		bugIter = allBugs.values().iterator();
		while (bugIter.hasNext()) {
			Bug bug = bugIter.next();
			
			bugInfotWriter.write(bug.getID() + "|");
			
			Iterator<SourceFile> fixedFilesIter = bugDAO.getFixedFiles(bug.getID()).iterator();
			while (fixedFilesIter.hasNext()) {
				SourceFile fixedFile = fixedFilesIter.next();
				
				bugInfotWriter.write(fixedFile.getName() + "&");
			}
			bugInfotWriter.write("|");
			
			BugCorpus bugCorpus = bug.getCorpus();
			bugInfotWriter.write(bugCorpus.getContent() + "|" +
					bugCorpus.getSummaryPart() + "|" +
					bugCorpus.getDescriptionPart() + "|" +
					bug.getOpenDateString() + "|" +
					bug.getFixedDateString() + "\n");
		}		
		bugInfotWriter.close();
	}
	
	@Test
	public void arrayTest() {
		double arr[] = new double[3];
		System.out.printf("1: %f, 2: %f, 3: %f\n", arr[0], arr[1], arr[2]);
	}

}
