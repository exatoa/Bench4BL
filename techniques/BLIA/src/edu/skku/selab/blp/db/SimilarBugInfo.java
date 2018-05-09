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
public class SimilarBugInfo {
	private int similarBugID;
	private double similarityScore;

	/**
	 * 
	 */
	public SimilarBugInfo() {
		setSimilarBugID(0);
		setSimilarityScore(0.0);
	}

	/**
	 * @return the bugID
	 */
	public int getSimilarBugID() {
		return similarBugID;
	}

	/**
	 * @param bugID the bugID to set
	 */
	public void setSimilarBugID(int bugID) {
		this.similarBugID = bugID;
	}

	/**
	 * @return the similarityScore
	 */
	public double getSimilarityScore() {
		return similarityScore;
	}

	/**
	 * @param similarityScore the similarityScore to set
	 */
	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

}
