/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.blia.indexer.BugCorpusCreator;
import edu.skku.selab.blp.blia.indexer.BugVectorCreator;
import edu.skku.selab.blp.db.dao.DbUtil;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugVectorCreatorTest {
	
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
		Property prop = Property.loadInstance(Property.ZXING);
		prop.alpha = 0.2f;
		prop.beta = 0.3f;
		prop.pastDays = 15;
		
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
	public void verifyCreate() throws Exception {
		BugCorpusCreator bugCorpusCreator = new BugCorpusCreator();
		boolean stackTraceAnalysis = false;
		bugCorpusCreator.create(stackTraceAnalysis);
		
		BugVectorCreator bugVectorCreator = new BugVectorCreator();
		bugVectorCreator.create();
	}
	
}
