/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.common;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class SourceFile {
	private String name;
	private String producName;
	private String version;
	private String corpusSet;
	private double lenghthScore;
	private int sourceFileVersionID;

	/**
	 * 
	 */
	public SourceFile() {
		this.name = "";
		this.producName = "";
		this.version = "";
		this.corpusSet = "";
		this.lenghthScore = 0.0;
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
	 * @return the producName
	 */
	public String getProducName() {
		return producName;
	}

	/**
	 * @param producName the producName to set
	 */
	public void setProducName(String producName) {
		this.producName = producName;
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
	 * @return the corpusSet
	 */
	public String getCorpusSet() {
		return corpusSet;
	}

	/**
	 * @param corpusSet the corpusSet to set
	 */
	public void setCorpusSet(String corpusSet) {
		this.corpusSet = corpusSet;
	}

	/**
	 * @return the lenghthScore
	 */
	public double getLenghthScore() {
		return lenghthScore;
	}

	/**
	 * @param lenghthScore the lenghthScore to set
	 */
	public void setLenghthScore(double lenghthScore) {
		this.lenghthScore = lenghthScore;
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

}
