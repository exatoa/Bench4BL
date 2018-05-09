/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.db.dao.DbUtil;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class GitCommitLogCollectorTest {
	
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
		double alpha = 0.2;
		double beta = 0.3;
		int pastDays = 15;
		
		Property prop = Property.loadInstance(Property.ZXING);

		prop.alpha = alpha;
		prop.beta = beta;
		prop.pastDays = pastDays;
		
		DbUtil dbUtil = new DbUtil();
		String dbName = Property.getInstance().productName;
		dbUtil.openConnetion(dbName);
		boolean commitDataIncluded = true;
		dbUtil.initializeAllData(commitDataIncluded);
		dbUtil.closeConnection();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void verifyCollectCommitLog() throws Exception {
		long startTime = System.currentTimeMillis();
		
//		String repoDir = Property.SWT_REPO_DIR;
//		String productName = Property.getInstance().getProductName();
//		Calendar since = new GregorianCalendar(2004, Calendar.OCTOBER, 1);
//		Calendar until = new GregorianCalendar(2010, Calendar.MAY, 1);
//		GitCommitLogCollector gitCommitLogCollector = new GitCommitLogCollector(productName, repoDir);
//		gitCommitLogCollector.collectCommitLog(since.getTime(), until.getTime(), true);
		
		// for AspectJ
		String repoDir = Property.getInstance().repoDir;
		String productName = Property.getInstance().productName;
		Calendar since = new GregorianCalendar(2002, Calendar.JULY, 1);
		Calendar until = new GregorianCalendar(2008, Calendar.DECEMBER, 15);
		GitCommitLogCollector gitCommitLogCollector = new GitCommitLogCollector(productName, repoDir);
		gitCommitLogCollector.collectCommitLog(since.getTime(), until.getTime(), true);
		
//		String repoDir = Property.getInstance().getRepoDir();
//		String productName = Property.getInstance().getProductName();
//		Calendar since = new GregorianCalendar(2004, Calendar.AUGUST, 1);
//		Calendar until = new GregorianCalendar(2011, Calendar.MARCH, 31);
//		GitCommitLogCollector gitCommitLogCollector = new GitCommitLogCollector(productName, repoDir);
//		gitCommitLogCollector.collectCommitLog(since.getTime(), until.getTime(), true);
		
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.printf("Elapsed time of collectCommitLog() for %s: %d.%d sec\n", productName, elapsedTime / 1000, elapsedTime % 1000);		

	}
}
