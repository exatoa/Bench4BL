/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */

package edu.skku.selab.blp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;


public class Property {
	public static  String SETTING_FILE = "blp.properties";
	
	public final static  String ASPECTJ = "aspectj";
	public final static String ECLIPSE = "eclipse";
	public final static String SWT = "swt";
	public final static String ZXING = "zxing";
	
	public static Property p = null;
	
	public static int THREAD_COUNT = 0; //Integer.parseInt(Property.readProperty("THREAD_COUNT"));
	public static String WORK_DIR = ""; //Property.readProperty("WORK_DIR");
	public static String OUTPUT_FILE ="";// Property.readProperty("OUTPUT_FILE");	

	/**
	 * Property Entry :: Input setteing file
	 * @param settingFileName
	 * @return
	 * @throws Exception
	 */
	public static Property loadInstance(String _settingFileName) throws Exception {
		if (null == p) {
			Property.SETTING_FILE = _settingFileName;
			p = new Property(); //Do nothing
		}
		return p;
	}	

	public static Property getInstance() {
		return p;
	}	
	

	/************************************************************************8
	 * Normal method Area
	 * @throws ParseException 
	 */
	private Properties settings = null;
	
	public String targetProduct;
	public String bugFilePath;
	public String sourceCodeDir;
	public String[] sourceCodeDirList;

	public String separator = System.getProperty("file.separator");
	public String lineSeparator = System.getProperty("line.separator");
	
	public int fileCount;
	public int wordCount;
	public int bugReportCount;
	public int bugTermCount;
	public double alpha;
	public double beta;	
	public String productName;
	public String versionName;
	public int pastDays;
	public Calendar since = null;
	public Calendar until = null;
	public String repoDir;
	public double candidateLimitRate = 1.0;
	
	
	private Property() throws ParseException {
		settings = new Properties();
		try {
			settings.load(new FileInputStream(SETTING_FILE));
		} catch (IOException e) {
			System.err.println("Cannot read the file : "+SETTING_FILE);
			return;
		}
		
		//Read target prduct property
		String targetProduct = settings.getProperty("TARGET_PRODUCT");
		//targetProduct = targetProduct.toUpperCase();
		
		this.productName = settings.getProperty("PRODUCT");
		this.versionName = settings.getProperty("VERSION");
		this.sourceCodeDir = settings.getProperty("SOURCE_DIR");
		this.alpha = Double.parseDouble(settings.getProperty("ALPHA"));
		this.beta = Double.parseDouble(settings.getProperty("BETA"));
		this.pastDays = Integer.parseInt(settings.getProperty("PAST_DAYS"));
		this.repoDir = settings.getProperty("REPO_DIR");
		this.bugFilePath = settings.getProperty("BUG_REPO_FILE");
		this.candidateLimitRate = Double.parseDouble(settings.getProperty("CANDIDATE_LIMIT_RATE"));
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date sinceDate = dateFormat.parse(settings.getProperty("COMMIT_SINCE"));
		this.since = new GregorianCalendar();
		this.since.setTime(sinceDate);
		Date untilDate = dateFormat.parse(settings.getProperty("COMMIT_UNTIL"));
		this.until = new GregorianCalendar();
		this.until.setTime(untilDate);
		
		this.sourceCodeDirList = new String[1];
		this.sourceCodeDirList[0] = this.sourceCodeDir;
		
		this.sourceCodeDir = this.sourceCodeDir.replace("\\", "/");
		this.sourceCodeDir = this.sourceCodeDir.replace("//", "/");
		this.repoDir = this.repoDir.replace("\\", "/");
		this.repoDir = this.repoDir.replace("//", "/");
		this.bugFilePath = this.bugFilePath.replace("\\", "/");
		this.bugFilePath = this.bugFilePath.replace("//", "/");
		

		if (!this.sourceCodeDir.endsWith("/"))
			this.sourceCodeDir = this.sourceCodeDir + "/";
		//common variable load
		String originalWorkingPath = settings.getProperty("WORK_DIR");
		originalWorkingPath = originalWorkingPath.replace("\\", "/");
		originalWorkingPath = originalWorkingPath.replace("//", "/");
		if (!originalWorkingPath.endsWith("/"))
			originalWorkingPath += "/";
		
		THREAD_COUNT = Integer.parseInt(settings.getProperty("THREAD_COUNT"));		
		WORK_DIR =  originalWorkingPath + "BLIA_"+targetProduct + "_" + this.versionName + "/";
		OUTPUT_FILE = originalWorkingPath + "BLIA_"+targetProduct + "_" + this.versionName + "_output.txt";
		//Property.readProperty("OUTPUT_FILE") + "_"+ targetProduct + ".txt";
		
		//post processing
		OUTPUT_FILE = OUTPUT_FILE.replace("//", "/");
		WORK_DIR = WORK_DIR.replace("//", "/");
		this.sourceCodeDir = this.sourceCodeDir.replace("//", "/");
		this.repoDir = this.repoDir.replace("//", "/");
		
		
	}	
	
	/**
	 * 설정된 속성 출력
	 */
	public void printValues() {
		System.out.printf("WORK_DIR: %s\n", Property.WORK_DIR);
		System.out.printf("THREAD_COUNT: %d\n", Property.THREAD_COUNT);
		System.out.printf("OUTPUT_FILE: %s\n\n", Property.OUTPUT_FILE);		
		System.out.printf("Product name: %s\n", this.productName);
		System.out.printf("Version name: %s\n", this.versionName);
		System.out.printf("Source code dir: %s\n", this.sourceCodeDir);
		System.out.printf("Alpha: %f\n", this.alpha);
		System.out.printf("Beta: %f\n", this.beta);
		System.out.printf("Past days: %s\n", this.pastDays);
		System.out.printf("Repo dir: %s\n", this.repoDir);
		System.out.printf("Bug file path: %s\n", this.bugFilePath);
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		System.out.printf("Since: %s\n", dateFormat.format(this.since.getTime()));
		System.out.printf("Until: %s\n", dateFormat.format(this.until.getTime()));
		System.out.printf("candidateLimitRate: %f\n\n", this.candidateLimitRate);
	}
	

}
