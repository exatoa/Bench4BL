/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.analysis;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.db.CommitInfo;
import edu.skku.selab.blp.db.dao.DbUtil;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class ScmRepoAnalyzerTest {
	
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
		Property prop = Property.loadInstance(Property.SWT);
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
	public void verifyGetDiffDays() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ScmRepoAnalyzer scmRepoAnalyzer = new ScmRepoAnalyzer();
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(2015, 2, 1);
		Date sourceDate = calendar.getTime();
		calendar.set(2015, 2, 12);
		Date targetDate = calendar.getTime();
		
		Method getDiffDaysMethod = ScmRepoAnalyzer.class.getDeclaredMethod("getDiffDays", Date.class, Date.class);
		getDiffDaysMethod.setAccessible(true);
		
		Double returnValue = (Double) getDiffDaysMethod.invoke(scmRepoAnalyzer, sourceDate, targetDate);
		assertEquals(11.0, returnValue.doubleValue(), 0.001);
	}
	
	@Test
	public void verifyFindCommitInfoWithinDays() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ScmRepoAnalyzer scmRepoAnalyzer = new ScmRepoAnalyzer();

		Method findCommitInfoWithinDaysMethod = ScmRepoAnalyzer.class.getDeclaredMethod("findCommitInfoWithinDays", ArrayList.class, Date.class, Integer.class);
		findCommitInfoWithinDaysMethod.setAccessible(true);
		
		CommitInfo commitInfo1 = new CommitInfo();
		commitInfo1.setCommitDate("2004-10-07 01:02:22");

		CommitInfo commitInfo2 = new CommitInfo();
		commitInfo2.setCommitDate("2004-10-15 04:28:35");
		
		CommitInfo commitInfo3 = new CommitInfo();
		commitInfo3.setCommitDate("2004-10-26 05:00:41");

		CommitInfo commitInfo4 = new CommitInfo();
		commitInfo4.setCommitDate("2005-01-21 12:33:02");

		ArrayList<CommitInfo> commitInfos = new ArrayList<CommitInfo>(); 
		commitInfos.add(commitInfo1);
		commitInfos.add(commitInfo2);
		commitInfos.add(commitInfo3);
		commitInfos.add(commitInfo4);
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(2004, Calendar.OCTOBER, 27, 9, 10, 22);
		Date openDate = calendar.getTime();
		
		@SuppressWarnings("unchecked")
		ArrayList<CommitInfo> foundCommitInfos = (ArrayList<CommitInfo>) findCommitInfoWithinDaysMethod.invoke(scmRepoAnalyzer, commitInfos, openDate, new Integer(15));
		assertEquals(2, foundCommitInfos.size());
		assertEquals(commitInfo2.getCommitDate(), foundCommitInfos.get(0).getCommitDate());
		assertEquals(commitInfo3.getCommitDate(), foundCommitInfos.get(1).getCommitDate());
	}
	
	@Test
	public void verifyCalculateCommitLogScore() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ScmRepoAnalyzer scmRepoAnalyzer = new ScmRepoAnalyzer();

		Method calculateCommitLogScoreMethod = ScmRepoAnalyzer.class.getDeclaredMethod("calculateCommitLogScore", Date.class, Date.class, Integer.class);
		calculateCommitLogScoreMethod.setAccessible(true);
		
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(2004, Calendar.OCTOBER, 7, 1, 2, 22);
		Date commitDate = calendar.getTime();
		calendar.set(2004, Calendar.OCTOBER, 12, 21, 53, 0);
		Date openDate = calendar.getTime();
		Integer pastDays = new Integer(15);

		Double commitLogScore = (Double) calculateCommitLogScoreMethod.invoke(scmRepoAnalyzer, commitDate, openDate, pastDays);
		assertEquals(commitLogScore.doubleValue(), 0.009, 0.0001);
	}
}
