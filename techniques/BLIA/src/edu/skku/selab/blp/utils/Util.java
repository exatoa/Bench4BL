/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.utils;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class Util {	
	public static String getElapsedTimeSting(long startTime) {
		long elapsedTime = System.currentTimeMillis() - startTime;
		String elpsedTimeString = (elapsedTime / 1000) + "." + (elapsedTime % 1000);
		return elpsedTimeString;
	}
}
