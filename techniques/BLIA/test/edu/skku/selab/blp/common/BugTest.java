/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.common;

import static org.junit.Assert.*;

import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import edu.skku.selab.blp.common.Bug;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugTest {

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
	public void verifyConstructor() {
		int ID = 29769;
		String productName = "BLIA";
		String openDate = "2003-01-19 11:42:00";
		String fixDate = "2003-01-24 21:17:00";
		String summary = "Ajde does not support new AspectJ 1.1 compiler options";
		String description = "The org.aspectj.ajde.BuildOptionsAdapter interface does not yet support the new AspectJ 1.1 compiler options. These need to be added to the interface, any old or renamed options deprecated, and then the correct processing needs to happen within Ajde to pass these options to the compiler. This enhancement is needed by the various IDE projects for there AspectJ 1.1 support.";
		String version = "v1.0";
		String fixedFile1 = "org.aspectj/modules/ajde/testdata/examples/figures-coverage/figures/Figure.java";
		String fixedFile2 = "org.aspectj/modules/ajde/testsrc/org/aspectj/ajde/AjdeTests.java";
		TreeSet<String> fixedFiles = new TreeSet<String>();
		fixedFiles.add(fixedFile1);
		fixedFiles.add(fixedFile2);
		
		Bug bug = new Bug(ID, productName, openDate, fixDate, summary, description, version, fixedFiles);
		
		assertEquals("ID is NOT equal.", ID, bug.getID());
		assertEquals("openDate is NOT equal.", openDate, bug.getOpenDateString());
		assertEquals("fixDate is NOT equal.", fixDate, bug.getFixedDateString());
		assertEquals("summary is NOT equal.", summary, bug.getSummary());
		assertEquals("description is NOT equal.", description, bug.getDescription());
		assertEquals("version is NOT equal.", version, bug.getVersion());
		assertEquals("fixedFiles is NOT equal.", fixedFiles, bug.getFixedFiles());
	}

}
