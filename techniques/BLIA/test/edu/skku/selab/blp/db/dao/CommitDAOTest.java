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
import java.util.Date;
import java.util.Iterator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.db.CommitInfo;
import edu.skku.selab.blp.db.dao.CommitDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class CommitDAOTest {
	private String productName = "BLIA";
	private String fileName1 = "test_10.java";
	private String fileName2 = "test_11.java";
	private String fileName3 = "test_20.java";
	private String fileName4 = "test_21.java";
	private String fileName5 = "test_22.java";

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
		
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		
		// preparation phase
		sourceFileDAO.deleteAllSourceFiles();
		assertNotEquals("fileName1 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName1, productName));
		assertNotEquals("fileName2 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName2, productName));
		assertNotEquals("fileName3 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName3, productName));
		assertNotEquals("fileName4 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName4, productName));
		assertNotEquals("fileName5 insertion failed!", BaseDAO.INVALID, sourceFileDAO.insertSourceFile(fileName5, productName));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void verifyGetCommitInfo() throws Exception {
		String commitID1 = "COMMIT-001";
		String commitDateString1 = "2015-01-15 15:19:00";
		String message1 = "[1] Commited by Klaus for BLIA testing";

		String commitID2 = "COMMIT-002";
		String commitDateString2 = "2015-01-31 15:19:00";
		String message2 = "[2] Commited by Klaus for BLIA testing";
		
		CommitInfo commitInfo1 = new CommitInfo();
		commitInfo1.setCommitID(commitID1);
		commitInfo1.setProductName(productName);
		commitInfo1.setCommitDate(commitDateString1);
		commitInfo1.setMessage(message1);
		commitInfo1.addCommitFile(CommitInfo.MODIFY_COMMIT, fileName1);
		commitInfo1.addCommitFile(CommitInfo.MODIFY_COMMIT, fileName2);
		
		CommitInfo commitInfo2 = new CommitInfo();
		commitInfo2.setCommitID(commitID2);
		commitInfo2.setProductName(productName);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date commitDate = simpleDateFormat.parse(commitDateString2);
		commitInfo2.setCommitDate(commitDate);
		commitInfo2.setMessage(message2);
		commitInfo2.addCommitFile(CommitInfo.MODIFY_COMMIT, fileName3);
		commitInfo2.addCommitFile(CommitInfo.MODIFY_COMMIT, fileName4);
		commitInfo2.addCommitFile(CommitInfo.DELETE_COMMIT, fileName5);

		CommitDAO commitDAO = new CommitDAO();		
		commitDAO.deleteAllCommitInfo();
		assertNotEquals("CommitInfo insertion failed!", BaseDAO.INVALID, commitDAO.insertCommitInfo(commitInfo1));
		assertNotEquals("CommitInfo insertion failed!", BaseDAO.INVALID, commitDAO.insertCommitInfo(commitInfo2));
		assertEquals("CommitInfoCount is wrong.", 2, commitDAO.getCommitInfoCount(productName));
		
		CommitInfo returnedCommitInfo = commitDAO.getCommitInfo(commitID1, productName);
		assertEquals("commitID1 is wrong.", commitID1, returnedCommitInfo.getCommitID());
		assertEquals("productName is wrong.", productName, returnedCommitInfo.getProductName());
		assertEquals("commitDateString1 is wrong.", commitDateString1, returnedCommitInfo.getCommitDateString());
		assertEquals("message1 is wrong.", message1, returnedCommitInfo.getMessage());
		
		assertEquals("CommitFiles count is wrong.", 2, returnedCommitInfo.getCommitFiles(CommitInfo.MODIFY_COMMIT).size());
		Iterator<String> iter = returnedCommitInfo.getCommitFiles(CommitInfo.MODIFY_COMMIT).iterator();
		String commitFile = iter.next();
		if ( (!commitFile.equalsIgnoreCase(fileName1)) && (!commitFile.equalsIgnoreCase(fileName2))) {
			fail("commitFiles are wrong.");
		}
		
		commitFile = iter.next();
		if ( (!commitFile.equalsIgnoreCase(fileName1)) && (!commitFile.equalsIgnoreCase(fileName2))) {
			fail("commitFiles are wrong.");
		}
		
		returnedCommitInfo = commitDAO.getCommitInfo(commitID2, productName);
		assertEquals("commitID2 is wrong.", commitID2, returnedCommitInfo.getCommitID());
		assertEquals("productName is wrong.", productName, returnedCommitInfo.getProductName());
		assertEquals("commitDateString2 is wrong.", commitDateString2, returnedCommitInfo.getCommitDateString());
		assertEquals("message2 is wrong.", message2, returnedCommitInfo.getMessage());

		assertEquals("CommitFiles count is wrong.", 2, returnedCommitInfo.getCommitFiles(CommitInfo.MODIFY_COMMIT).size());
		iter = returnedCommitInfo.getCommitFiles(CommitInfo.MODIFY_COMMIT).iterator();
		commitFile = iter.next();
		if ((!commitFile.equalsIgnoreCase(fileName3)) && (!commitFile.equalsIgnoreCase(fileName4))) {
			fail("commitFiles are wrong.");
		}

		commitFile = iter.next();
		if ((!commitFile.equalsIgnoreCase(fileName3)) && (!commitFile.equalsIgnoreCase(fileName4))) {
			fail("commitFiles are wrong.");
		}
		
		assertEquals("CommitFiles count is wrong.", 1, returnedCommitInfo.getCommitFiles(CommitInfo.DELETE_COMMIT).size());
		iter = returnedCommitInfo.getCommitFiles(CommitInfo.DELETE_COMMIT).iterator();
		commitFile = iter.next();
		if (!commitFile.equalsIgnoreCase(fileName5)) {
			fail("commitFiles are wrong.");
		}
	}

}
