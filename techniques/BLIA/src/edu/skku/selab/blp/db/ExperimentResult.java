/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class ExperimentResult {
	private int top1;
	private int top5;
	private int top10;
	private double top1Rate;
	private double top5Rate;
	private double top10Rate;
	private double MRR;
	private double MAP;
	private String productName;
	private String algorithmName;
	private String algorithmDescription;
	private double alpha;
	private double beta;
	private int pastDays;
	private Date experimentDate;
	private double candidateRate;
	
	/**
	 * 
	 */
	public ExperimentResult() {
		top1 = 0;
		top5 = 0;
		top10 = 0;
		top1Rate = 0.0;
		top5Rate = 0.0;
		top10Rate = 0.0;
		MRR = 0.0;
		MAP = 0.0;
		productName = "";
		algorithmName = "";
		algorithmDescription = "";
		alpha = 0.0;
		beta = 0.0;
		pastDays = 0;
		setExperimentDate(new Date(System.currentTimeMillis()));
		candidateRate = 0;
	}

	/**
	 * @return the top1
	 */
	public int getTop1() {
		return top1;
	}

	/**
	 * @param top1 the top1 to set
	 */
	public void setTop1(int top1) {
		this.top1 = top1;
	}

	/**
	 * @return the top5
	 */
	public int getTop5() {
		return top5;
	}

	/**
	 * @param top5 the top5 to set
	 */
	public void setTop5(int top5) {
		this.top5 = top5;
	}

	/**
	 * @return the top10
	 */
	public int getTop10() {
		return top10;
	}

	/**
	 * @param top10 the top10 to set
	 */
	public void setTop10(int top10) {
		this.top10 = top10;
	}

	/**
	 * @return the mRR
	 */
	public double getMRR() {
		return MRR;
	}

	/**
	 * @param MRR the MRR to set
	 */
	public void setMRR(double MRR) {
		this.MRR = MRR;
	}

	/**
	 * @return the MAP
	 */
	public double getMAP() {
		return MAP;
	}

	/**
	 * @param MAP the MAP to set
	 */
	public void setMAP(double MAP) {
		this.MAP = MAP;
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
	 * @return the algorithmName
	 */
	public String getAlgorithmName() {
		return algorithmName;
	}

	/**
	 * @param algorithmName the algorithmName to set
	 */
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	/**
	 * @return the algorithmDescription
	 */
	public String getAlgorithmDescription() {
		return algorithmDescription;
	}

	/**
	 * @param algorithmDescription the algorithmDescription to set
	 */
	public void setAlgorithmDescription(String algorithmDescription) {
		this.algorithmDescription = algorithmDescription;
	}

	/**
	 * @return the experimentDate
	 */
	public Date getExperimentDate() {
		return experimentDate;
	}

	/**
	 * @param experimentDate the experimentDate to set
	 */
	public void setExperimentDate(Date experimentDate) {
		this.experimentDate = experimentDate;
	}

	/**
	 * @return the experimentDateString
	 */
	public String getExperimentDateString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(experimentDate);
	}

	/**
	 * @param experimentDateString the experimentDateString to set
	 */
	public void setExperimentDate(String experimentDateString) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			this.experimentDate = simpleDateFormat.parse(experimentDateString);			
		} catch (Exception e) {
			this.experimentDate = null;
			e.printStackTrace();
		}		
	}

	/**
	 * @return the top1Rate
	 */
	public double getTop1Rate() {
		return top1Rate;
	}

	/**
	 * @param top1Rate the top1Rate to set
	 */
	public void setTop1Rate(double top1Rate) {
		this.top1Rate = top1Rate;
	}

	/**
	 * @return the top5Rate
	 */
	public double getTop5Rate() {
		return top5Rate;
	}

	/**
	 * @param top5Rate the top5Rate to set
	 */
	public void setTop5Rate(double top5Rate) {
		this.top5Rate = top5Rate;
	}

	/**
	 * @return the top10Rate
	 */
	public double getTop10Rate() {
		return top10Rate;
	}

	/**
	 * @param top10Rate the top10Rate to set
	 */
	public void setTop10Rate(double top10Rate) {
		this.top10Rate = top10Rate;
	}

	/**
	 * @return the alpha
	 */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha the alpha to set
	 */
	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/**
	 * @return the beta
	 */
	public double getBeta() {
		return beta;
	}

	/**
	 * @param beta the beta to set
	 */
	public void setBeta(double beta) {
		this.beta = beta;
	}

	/**
	 * @return the pastDays
	 */
	public int getPastDays() {
		return pastDays;
	}

	/**
	 * @param pastDays the pastDays to set
	 */
	public void setPastDays(int pastDays) {
		this.pastDays = pastDays;
	}

	/**
	 * @return the candidateRate
	 */
	public double getCandidateRate() {
		return candidateRate;
	}

	/**
	 * @param candidateRate the candidateRate to set
	 */
	public void setCandidateRate(double candidateRate) {
		this.candidateRate = candidateRate;
	}

}
