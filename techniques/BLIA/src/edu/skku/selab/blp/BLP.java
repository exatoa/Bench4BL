/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp;

import edu.skku.selab.blp.blia.analysis.BLIA;
import edu.skku.selab.blp.db.dao.BaseDAO;
import edu.skku.selab.blp.db.dao.DbUtil;
import edu.skku.selab.blp.evaluation.Evaluator;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BLP {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String Setting_File = "";
		if (args.length == 1){
			System.out.println("parameter:"+args[0]);
			Setting_File = args[0];
		}
		else if (args.length > 1){
			System.out.println("This is wrong usages.");
			showHelp();
			return;
		}
			
		
		// Load properties data to run BLIA
		Property prop = Property.loadInstance(Setting_File);
		//prop.printValues();
		
		//preparing BLIA working folder
		BLIA blia = new BLIA();
		
		// initialize DB and create all tables.
		initializeDB();
		
		// Run BLIA algorithm
		blia.run();

		String algorithmDescription = "[BLIA] alpha: " + prop.alpha
				+ ", beta: " + prop.beta + ", pastDays: "
				+ prop.pastDays + ", cadidateLimitRate: "
				+ prop.candidateLimitRate;

		// Evaluate the accuracy result of BLIA
		Evaluator evaluator = new Evaluator(prop.productName,
				Evaluator.ALG_BLIA, algorithmDescription, prop.alpha,
				prop.beta, prop.pastDays,
				prop.candidateLimitRate);
		evaluator.evaluate();
	}
	
	
	private static void showHelp() {
		String usage = "Usage:java -jar BLIA [settings file path]\n"
				+ "  \tThis program will make temp directory : BLIA_{working name}\\\n"
				+ "  \t                and final result file : BLIA_{working name}_output.txt";
		System.out.println(usage);
	}
	

	private static void initializeDB() throws Exception {
		Property prop = Property.getInstance();
		
		DbUtil dbUtil = new DbUtil();

		
		dbUtil.openConnetion(prop.productName);
		if (dbUtil.dropAllAnalysisTables()==BaseDAO.INVALID){
			System.err.println("Error occurs in initializing Data DB!");
			throw new Exception();
		}			
		dbUtil.createAllAnalysisTables();
		dbUtil.initializeAllData();
		dbUtil.closeConnection();
		
		
		dbUtil.openEvaluationDbConnection();
		if (dbUtil.dropEvaluationTable()==BaseDAO.INVALID){
			System.err.println("Error occurs in initializing EvaluationDB!");
			throw new Exception();
		}			
		dbUtil.createEvaluationTable();		
		dbUtil.initializeExperimentResultData();
		dbUtil.closeConnection();
	}

}
