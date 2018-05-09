/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class AnalysisValue {
	private int ID;
	protected String name;
	protected String version;
	protected String productName;
	protected String term;
	protected int sourceFileVersionID;
	protected int termID;
	protected int termCount;
	protected int invDocCount;
	protected double tf;
	protected double idf;
	private double termWeight;
	
	final private int INIT_VALUE = -1;

	public AnalysisValue() {
		setID(0);
		name = "";
		version = "";
		productName = "";
		term = "";
		sourceFileVersionID = INIT_VALUE;
		setTermID(INIT_VALUE);
		termCount = INIT_VALUE;
		invDocCount = INIT_VALUE;
		tf = INIT_VALUE;
		idf = INIT_VALUE;
		termWeight = INIT_VALUE;
	}
	
	public AnalysisValue(String name, String productName, String term, double termWeight) {
		setID(0);
		setName(name);
		version = "";
		setProductName(productName);
		setTerm(term);
		setTermID(INIT_VALUE);
		termCount = INIT_VALUE;
		invDocCount = INIT_VALUE;
		tf = INIT_VALUE;
		idf = INIT_VALUE;
		setTermWeight(termWeight);
	}
	
	public AnalysisValue(int ID, String productName, String term, double termWeight) {
		setID(ID);
		setName("");
		version = "";
		setProductName(productName);
		setTerm(term);
		setTermID(INIT_VALUE);
		termCount = INIT_VALUE;
		invDocCount = INIT_VALUE;
		tf = INIT_VALUE;
		idf = INIT_VALUE;
		setTermWeight(termWeight);
	}
	
	public AnalysisValue(String name, String productName, String term, int termCount, int invDocCount, double tf, double idf) {
		setID(0);
		setName(name);
		version = "";
		setProductName(productName);
		setTerm(term);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(tf);
		setIdf(idf);
		setTermWeight(INIT_VALUE);
	}
	
	public AnalysisValue(int ID, String productName, String term, int termCount, int invDocCount, double tf, double idf) {
		setID(ID);
		setName("");
		version = "";
		setProductName(productName);
		setTerm(term);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(tf);
		setIdf(idf);
		setTermWeight(INIT_VALUE);
	}
	
	public AnalysisValue(String name, String productName, String term, int termCount, int invDocCount, double tf, double idf, double termWeight) {
		setID(0);
		setName(name);
		version = "";
		setProductName(productName);
		setTerm(term);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(tf);
		setIdf(idf);
		setTermWeight(termWeight);
	}
	
	public AnalysisValue(int ID, String productName, String term, int termCount, int invDocCount, double tf, double idf, double termWeight) {
		setID(ID);
		setName("");
		version = "";
		setProductName(productName);
		setTerm(term);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(tf);
		setIdf(idf);
		setTermWeight(termWeight);
	}	
	
	/**
	 * 
	 */
	public AnalysisValue(String name, String productName, String version, String term, int termCount, int invDocCount) {
		setID(0);
		setName(name);
		setVersion(version);
		setProductName(productName);
		setTerm(term);
		setSourceFileVersionID(INIT_VALUE);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(INIT_VALUE);
		setIdf(INIT_VALUE);
		setTermWeight(INIT_VALUE);
	}
	
	public AnalysisValue(int ID, String productName, String version, String term, int termCount, int invDocCount) {
		setID(ID);
		setName("");
		setVersion(version);
		setProductName(productName);
		setTerm(term);
		setSourceFileVersionID(INIT_VALUE);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(INIT_VALUE);
		setIdf(INIT_VALUE);
		setTermWeight(INIT_VALUE);
	}
	
	/**
	 * 
	 */
	public AnalysisValue(String name, String productName, String version, String term, int termCount, int invDocCount, double tf, double idf) {
		setID(0);
		setName(name);
		setVersion(version);
		setProductName(productName);
		setTerm(term);
		setSourceFileVersionID(INIT_VALUE);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(tf);
		setIdf(idf);
		setTermWeight(INIT_VALUE);
	}

	public AnalysisValue(int ID, String productName, String version, String term, int termCount, int invDocCount, double tf, double idf) {
		setID(ID);
		setName("");
		setVersion(version);
		setProductName(productName);
		setTerm(term);
		setSourceFileVersionID(INIT_VALUE);
		setTermID(INIT_VALUE);
		setTermCount(termCount);
		setInvDocCount(invDocCount);
		setTf(tf);
		setIdf(idf);
		setTermWeight(INIT_VALUE);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the termCount
	 */
	public int getTermCount() {
		return termCount;
	}

	/**
	 * @param termCount the termCount to set
	 */
	public void setTermCount(int termCount) {
		this.termCount = termCount;
	}

	/**
	 * @return the invDocCount
	 */
	public int getInvDocCount() {
		return invDocCount;
	}

	/**
	 * @param invDocCount the invDocCount to set
	 */
	public void setInvDocCount(int invDocCount) {
		this.invDocCount = invDocCount;
	}

	/**
	 * @return the tf
	 */
	public double getTf() {
		return tf;
	}

	/**
	 * @param tf the tf to set
	 */
	public void setTf(double tf) {
		this.tf = tf;
	}

	/**
	 * @return the idf
	 */
	public double getIdf() {
		return idf;
	}

	/**
	 * @param idf the idf to set
	 */
	public void setIdf(double idf) {
		this.idf = idf;
	}

	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}

	/**
	 * @return the sourceFileVersionID
	 */
	public int getSourceFileVersionID() {
		return sourceFileVersionID;
	}

	/**
	 * @param sourceFileVersionID the sourceFileVersionID to set
	 */
	public void setSourceFileVersionID(int sourceFileVersionID) {
		this.sourceFileVersionID = sourceFileVersionID;
	}

	/**
	 * @return the termID
	 */
	public int getTermID() {
		return termID;
	}

	/**
	 * @param termID the termID to set
	 */
	public void setTermID(int termID) {
		this.termID = termID;
	}

	/**
	 * @return the termWeight
	 */
	public double getTermWeight() {
		return termWeight;
	}

	/**
	 * @param termWeight the termWeight to set
	 */
	public void setTermWeight(double termWeight) {
		this.termWeight = termWeight;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}
}
