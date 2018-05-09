/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.analysis;

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
public class BliaTest {
	
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
		String targetProduct = Property.ZXING;
		Property prop = Property.loadInstance(targetProduct);
		double alpha = 0.41;
		double beta = 0.13;
		int pastDays = 60;
		prop.alpha = alpha;
		prop.beta = beta;
		prop.pastDays = pastDays;

		DbUtil dbUtil = new DbUtil();
		String dbName = Property.getInstance().productName;
		dbUtil.openConnetion(dbName);
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
	public void verifyBLIA() throws Exception {
		long startTime = System.currentTimeMillis();
		
		// Run BLIA algorithm
		BLIA blia = new BLIA();
		blia.run();

		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.printf("Elapsed time of BLIA: %d.%d sec\n", elapsedTime / 1000, elapsedTime % 1000);
	}
}
