/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeSet;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class Bug {

    private int ID;
    private String productName;
	private String fixedDateStringNotModified;
	private Date openDate;
    private Date fixedDate;
    private String summary;
    private String description;
    private TreeSet<String> fixedFiles;
    private BugCorpus bugCorpus;
    private int	totalCorpusCount;
    private String version;
    private ArrayList<String> stackTraceClasses;
    
    public Bug() {
    	this.ID = 0;
    	this.productName = "";
    	this.openDate = new Date(System.currentTimeMillis());
    	this.fixedDate = new Date(System.currentTimeMillis());
    	this.summary = "";
    	this.description = "";
    	this.bugCorpus = new BugCorpus();
    	this.totalCorpusCount = -1;
    	this.version = "";
    	this.fixedFiles = new TreeSet<String>();
    	this.stackTraceClasses = new ArrayList<String>();
    }
    
    public Bug(int ID, String productName, String openDateString, String fixedDateString, String summary, String description, String version, TreeSet<String> fixedFiles) {
    	this.ID = ID;
    	this.productName = productName;
    	setOpenDate(openDateString);
    	setFixedDate(fixedDateString);
    	this.summary = summary;
    	this.description = description;
    	this.version = version;
    	this.bugCorpus = new BugCorpus();
    	this.totalCorpusCount = -1;
    	this.fixedFiles = fixedFiles;
    	this.stackTraceClasses = new ArrayList<String>();
    }

    public int getID() {
		return ID;
	}
    
	public void setID(int ID) {
		this.ID = ID;
	}

	public Date getOpenDate() {
		return openDate;
	}
	
	public String getOpenDateString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(openDate);
	}
	
	public void setOpenDate(String openDateString) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			this.openDate = simpleDateFormat.parse(openDateString);			
		} catch (Exception e) {
			this.fixedDate = null;
			e.printStackTrace();
		}
	}
	
	public String getFixedDateString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(fixedDate);
	}
	
	public String getFixedDateStringNotModified() {
		return fixedDateStringNotModified;
	}
	
	public void setFixedDateStringNotModified(String fixedDateStringNotModified) {
		this.fixedDateStringNotModified = fixedDateStringNotModified;
	}
	
	public void setFixedDate(String fixDateString) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			this.fixedDate = simpleDateFormat.parse(fixDateString);			
		} catch (Exception e) {
			this.fixedDate = null;
			e.printStackTrace();
		}
	}
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public TreeSet<String> getFixedFiles() {
		return fixedFiles;
	}
	
	public void setFixedFiles(TreeSet<String> fixedFiles) {
		this.fixedFiles = fixedFiles;
	}    

	public void addFixedFile(String fixedFile) {
		this.fixedFiles.add(fixedFile);
	}

	/**
	 * @return the corpus
	 */
	public String getCorpusContent() {
//		return bugCorpus.getContent();
		return bugCorpus.getSummaryPart() + " " + bugCorpus.getDescriptionPart();
	}

	/**
	 * @param corpusContent the corpus to set
	 */
	public void setCorpusContent(String corpusContent) {
		bugCorpus.setContent(corpusContent);
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	/**
	 * @param fixedDate the fixedDate to set
	 */
	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}

	/**
	 * @param fixedDate the fixedDate to set
	 */
	public void setFixedDate(Date fixedDate) {
		this.fixedDate = fixedDate;
	}

	/**
	 * @return the fixedDate
	 */
	public Date getFixedDate() {
		return fixedDate;
	}

	/**
	 * @return the totalCorpusCount
	 */
	public int getTotalCorpusCount() {
		return totalCorpusCount;
	}

	/**
	 * @param totalCorpusCount the totalCorpusCount to set
	 */
	public void setTotalCorpusCount(int totalCorpusCount) {
		this.totalCorpusCount = totalCorpusCount;
	}

	/**
	 * @return the stackTraceClasses
	 */
	public ArrayList<String> getStackTraceClasses() {
		return stackTraceClasses;
	}

	/**
	 * @param stackTraceClasses the stackTraceClasses to set
	 */
	public void setStackTraceClasses(ArrayList<String> stackTraceClasses) {
		this.stackTraceClasses = stackTraceClasses;
	}
	
	public void addStackTraceClass(String stackTraceClass) {
		stackTraceClasses.add(stackTraceClass);
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the bugCorpus
	 */
	public BugCorpus getCorpus() {
		return bugCorpus;
	}

	/**
	 * @param bugCorpus the bugCorpus to set
	 */
	public void setCorpus(BugCorpus bugCorpus) {
		this.bugCorpus = bugCorpus;
	}
}
