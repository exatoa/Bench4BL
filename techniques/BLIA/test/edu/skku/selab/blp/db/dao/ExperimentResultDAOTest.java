/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import edu.skku.selab.blp.db.ExperimentResult;
import edu.skku.selab.blp.db.dao.ExperimentResultDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class ExperimentResultDAOTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void verify() {
		// empty
	}
	
	/**
	 * Ignored because all experimental results can be deleted. 
	 * 
	 * @throws Exception
	 */
	@Ignore
//	@Test
	public void verifyGetExperimentResult() throws Exception {
		ExperimentResultDAO experimentResultDAO = new ExperimentResultDAO();
		experimentResultDAO.deleteAllExperimentResults();

		int top1 = 1;
		int top5 = 5;
		int top10 = 10;
		int totalBugReportCount = 23;
		double top1Rate = (double) top1 / totalBugReportCount;
		double top5Rate = (double) top5 / totalBugReportCount;
		double top10Rate = (double) top10 / totalBugReportCount;
		double MRR = 0.324;
		double MAP = 0.432;
		String productName = "BLIA";
		String algorithmName = "BLIA";
		String algorithmDescription = "[TEST] Bug Localization with Integrated Analysis";
		double alpha = 0.2;
		double beta = 0.3;
		int pastDays = 15;
		Date experimentDate = new Date(System.currentTimeMillis());
		final double delta = 0.00001;
		
		ExperimentResult experimentResult = new ExperimentResult();
		experimentResult.setTop1(top1);
		experimentResult.setTop5(top5);
		experimentResult.setTop10(top10);
		experimentResult.setTop1Rate(top1Rate);
		experimentResult.setTop5Rate(top5Rate);
		experimentResult.setTop10Rate(top10Rate);
		experimentResult.setMRR(MRR);
		experimentResult.setMAP(MAP);
		experimentResult.setProductName(productName);
		experimentResult.setAlgorithmName(algorithmName);
		experimentResult.setAlgorithmDescription(algorithmDescription);
		experimentResult.setAlpha(alpha);
		experimentResult.setBeta(beta);
		experimentResult.setPastDays(pastDays);
		experimentResult.setExperimentDate(experimentDate);		
		
		experimentResultDAO.insertExperimentResult(experimentResult);
		ExperimentResult returnValue = experimentResultDAO.getExperimentResult(productName, algorithmName);
		assertEquals(top1, returnValue.getTop1());
		assertEquals(top5, returnValue.getTop5());
		assertEquals(top10, returnValue.getTop10());
		assertEquals(top1Rate, returnValue.getTop1Rate(), delta);
		assertEquals(top5Rate, returnValue.getTop5Rate(), delta);
		assertEquals(top10Rate, returnValue.getTop10Rate(), delta);
		assertEquals(MRR, returnValue.getMRR(), delta);
		assertEquals(MAP, returnValue.getMAP(), delta);
		assertEquals(productName, returnValue.getProductName());
		assertEquals(algorithmName, returnValue.getAlgorithmName());
		assertEquals(algorithmDescription, returnValue.getAlgorithmDescription());
		assertEquals(alpha, returnValue.getAlpha(), delta);
		assertEquals(beta, returnValue.getBeta(), delta);
		assertEquals(pastDays, returnValue.getPastDays());
		assertTrue((experimentDate.getTime() / 1000) == (returnValue.getExperimentDate().getTime() / 1000));
	}

}
