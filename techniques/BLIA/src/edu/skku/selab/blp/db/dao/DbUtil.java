/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.skku.selab.blp.Property;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class DbUtil {
	protected PreparedStatement ps = null;
	protected ResultSet rs = null;
	
	public void openConnetion() throws Exception {
		String dbName = BaseDAO.DEFAULT_DB_NAME;
		BaseDAO.openConnection(dbName);
	}
	
	public void openConnetion(String dbName) throws Exception {
		BaseDAO.openConnection(dbName);
	}
	
	public void openEvaluationDbConnection() throws Exception {
		BaseDAO.openEvaluationDbConnection();
	}
	
	public void closeConnection() throws Exception {
		BaseDAO.closeConnection();
	}
	
	public int createAllAnalysisTables() throws Exception {
		String sql = "CREATE TABLE SF_INFO(SF_ID INT PRIMARY KEY HASH AUTO_INCREMENT, SF_NAME VARCHAR(255), CLS_NAME VARCHAR(255), PROD_NAME VARCHAR(31)); " +
				"CREATE UNIQUE HASH INDEX IDX_SF_INFO_NAME ON SF_INFO(SF_NAME); " +
				"CREATE UNIQUE HASH INDEX IDX_SF_INFO_CLS_NAME ON SF_INFO(CLS_NAME); " +
				"CREATE INDEX IDX_SF_PROD_NAME ON SF_INFO(PROD_NAME); " +
				
				"CREATE TABLE SF_VER_INFO (SF_VER_ID INT PRIMARY KEY HASH AUTO_INCREMENT, SF_ID INT, VER VARCHAR(15),"
				+ " COR VARCHAR, CLS_COR VARCHAR, MTH_COR VARCHAR, VAR_COR VARCHAR, CMT_COR VARCHAR, TOT_CNT INT, LEN_SCORE DOUBLE,"
				+ " COR_NORM DOUBLE, CLS_COR_NORM DOUBLE, MTH_COR_NORM DOUBLE, VAR_COR_NORM DOUBLE, CMT_COR_NORM DOUBLE); " +
				"CREATE INDEX COMP_IDX_SF_VER_ID ON SF_VER_INFO(SF_ID, VER); " +
				
				"CREATE TABLE SF_TERM_INFO (SF_TERM_ID INT PRIMARY KEY HASH AUTO_INCREMENT, TERM VARCHAR(255), PROD_NAME VARCHAR(31)); " + 
				"CREATE UNIQUE HASH INDEX IDX_SF_TERM ON SF_TERM_INFO(TERM); " +
				"CREATE INDEX IDX_SF_TERM_PROD ON SF_TERM_INFO(PROD_NAME); " +
				
				"CREATE TABLE SF_IMP_INFO (SF_VER_ID INT, IMP_CLASS VARCHAR(255)); " +
				"CREATE INDEX IDX_SF_IMP_INFO ON SF_IMP_INFO(SF_VER_ID); " +
				
				"CREATE TABLE SF_TERM_WGT (SF_VER_ID INT, SF_TERM_ID INT, TERM_CNT INT, INV_DOC_CNT INT, TF DOUBLE, IDF DOUBLE); " +
				"CREATE UNIQUE INDEX COMP_IDX_SF_TERM_WGT ON SF_TERM_WGT(SF_VER_ID, SF_TERM_ID); " +
				
//				"CREATE TABLE FUNC_INFO(FUNC_ID INT PRIMARY KEY HASH AUTO_INCREMENT, FUNC_NAME VARCHAR(255), PROD_NAME VARCHAR(31)); " +
//				"CREATE TABLE FUNC_VER_INFO (FUNC_VER_ID INT PRIMARY KEY HASH AUTO_INCREMENT, FUNC_ID INT, VER VARCHAR(15), COR_SET VARCHAR, TOT_CNT INT, LEN_SCORE DOUBLE); " +
//				"CREATE TABLE FUNC_COR_INFO (FUNC_COR_ID INT PRIMARY KEY HASH AUTO_INCREMENT, COR VARCHAR(255), PROD_NAME VARCHAR(31)); " +
//				"CREATE TABLE FUNC_ANALYSIS (FUNC_VER_ID INT, FUNC_COR_ID INT, TERM_CNT INT, INV_DOC_CNT INT, TF DOUBLE, IDF DOUBLE, VEC DOUBLE); " +

				"CREATE TABLE BUG_INFO(BUG_ID INT PRIMARY KEY HASH, PROD_NAME VARCHAR(31), OPEN_DATE DATETIME, FIXED_DATE DATETIME,"
				+ " COR VARCHAR, SMR_COR VARCHAR, DESC_COR VARCHAR, TOT_CNT INT,"
				+ " COR_NORM DOUBLE, SMR_COR_NORM DOUBLE, DESC_COR_NORM DOUBLE, VER VARCHAR(15)); " +
				"CREATE INDEX IDX_BUG_INFO_PROD ON BUG_INFO(PROD_NAME); " +
				"CREATE INDEX IDX_BUG_INFO_FDATE ON BUG_INFO(FIXED_DATE); " +
				
				"CREATE TABLE BUG_TERM_INFO(BUG_TERM_ID INT PRIMARY KEY HASH AUTO_INCREMENT, TERM VARCHAR(255), PROD_NAME VARCHAR(31)); " +
				"CREATE UNIQUE HASH INDEX IDX_BUG_TERM ON BUG_TERM_INFO(TERM); " +
				"CREATE INDEX IDX_BUG_TERM_PROD ON BUG_TERM_INFO(PROD_NAME); " +
				
				"CREATE TABLE BUG_STRACE_INFO (BUG_ID INT, STRACE_CLASS VARCHAR(255)); " +
				"CREATE INDEX IDX_BUG_STRACE_INFO ON BUG_STRACE_INFO(BUG_ID); " +
				
				"CREATE TABLE BUG_SF_TERM_WGT(BUG_ID INT, SF_TERM_ID INT, TERM_CNT INT, INV_DOC_CNT INT, TF DOUBLE, IDF DOUBLE); " +
				"CREATE UNIQUE INDEX COMP_IDX_BUG_SF_TERM_WGT ON BUG_SF_TERM_WGT(BUG_ID, SF_TERM_ID); " +

				"CREATE TABLE BUG_TERM_WGT(BUG_ID INT, BUG_TERM_ID INT, TW DOUBLE); " +
				"CREATE UNIQUE INDEX COMP_IDX_BUG_TERM_WGT ON BUG_TERM_WGT(BUG_ID, BUG_TERM_ID); " +
				
				"CREATE TABLE BUG_FIX_INFO(BUG_ID INT, FIXED_SF_VER_ID INT, FIXED_FUNC_VER_ID INT); " +
				"CREATE INDEX IDX_BUG_FIX_INFO ON BUG_FIX_INFO(BUG_ID); " +
				
				"CREATE TABLE SIMI_BUG_ANAYSIS(BUG_ID INT, SIMI_BUG_ID INT, SIMI_BUG_SCORE DOUBLE); " +
				"CREATE INDEX IDX_SIMI_BUG_ANAYSIS ON SIMI_BUG_ANAYSIS(BUG_ID); " +
				

				"CREATE TABLE COMM_INFO(COMM_ID VARCHAR(127) PRIMARY KEY HASH, PROD_NAME VARCHAR(31), COMM_DATE DATETIME, MSG VARCHAR, COMMITTER VARCHAR(63)); " +
				"CREATE INDEX IDX_COMM_PROD_NAME ON COMM_INFO(PROD_NAME); " +
				
				"CREATE TABLE COMM_FILE_INFO(COMM_ID VARCHAR(127), COMM_FILE VARCHAR(255), COMM_TYPE INT); " +
				"CREATE INDEX IDX_COMM_FILE_INFO ON COMM_FILE_INFO(COMM_ID); " +
				
				
				"CREATE TABLE VER_INFO(VER VARCHAR(15) PRIMARY KEY HASH, REL_DATE DATETIME); " +

				"CREATE MEMORY TABLE INT_ANALYSIS(BUG_ID INT, SF_VER_ID INT, VSM_SCORE DOUBLE, SIMI_SCORE DOUBLE, BL_SCORE DOUBLE, STRACE_SCORE DOUBLE, COMM_SCORE DOUBLE, BLIA_SCORE DOUBLE); " +
				"CREATE INDEX IDX_INT_ANAL_BLIA_SCORE ON INT_ANALYSIS(BLIA_SCORE DESC); " +
				"CREATE INDEX IDX_INT_ANAL_BUG_ID ON INT_ANALYSIS(BUG_ID); " +
				"CREATE UNIQUE INDEX COMP_IDX_INT_ANALYSIS ON INT_ANALYSIS(BUG_ID, SF_VER_ID); ";

		int returnValue = BaseDAO.INVALID;
		try {
			ps = BaseDAO.getAnalysisDbConnection().prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int createEvaluationTable() throws Exception {
		String sql = "CREATE TABLE EXP_INFO(TOP1 INT, TOP5 INT, TOP10 INT, TOP1_RATE DOUBLE, TOP5_RATE DOUBLE, TOP10_RATE DOUBLE, MAP DOUBLE, MRR DOUBLE, PROD_NAME VARCHAR(31),"
					+ " ALG_NAME VARCHAR(31), ALG_DESC VARCHAR(255), ALPHA DOUBLE, BETA DOUBLE, PAST_DAYS INT, EXP_DATE DATETIME); " +
				"CREATE INDEX IDX_EXP_INFO_PROD ON EXP_INFO(PROD_NAME); " +
				"CREATE INDEX IDX_EXP_INFO_ALG ON EXP_INFO(ALG_NAME); ";
				
		int returnValue = BaseDAO.INVALID;
		try {
			ps = BaseDAO.getEvaluationDbConnection().prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int dropAllAnalysisTables() {//throws Exception {
		String sql = "DROP TABLE SF_INFO; " +
				"DROP TABLE SF_VER_INFO; " +
				"DROP TABLE SF_TERM_INFO; " +
				"DROP TABLE SF_IMP_INFO; " +
				"DROP TABLE SF_TERM_WGT; " +

//				"DROP TABLE FUNC_INFO; " +
//				"DROP TABLE FUNC_VER_INFO; " +
//				"DROP TABLE FUNC_COR_INFO; " +
//				"DROP TABLE FUNC_ANALYSIS; " +

				"DROP TABLE BUG_INFO; " +
				"DROP TABLE BUG_TERM_INFO; " +
				"DROP TABLE BUG_STRACE_INFO; " +
				"DROP TABLE BUG_SF_TERM_WGT; " +
				"DROP TABLE BUG_TERM_WGT; " +
				"DROP TABLE BUG_FIX_INFO; " +
				"DROP TABLE SIMI_BUG_ANAYSIS; " +
				"DROP TABLE INT_ANALYSIS; " +

				"DROP TABLE COMM_INFO; " +
				"DROP TABLE COMM_FILE_INFO; " +
				"DROP TABLE VER_INFO; ";
		
		int returnValue = BaseDAO.INVALID;
		try {
			ps = BaseDAO.getAnalysisDbConnection().prepareStatement(sql);
			returnValue = ps.executeUpdate();
			
		} catch (SQLException e) {
			if (e.getErrorCode()==42102)
				returnValue = 1;
			else {
				e.printStackTrace();
			}
		}
		
		return returnValue;		
	}
	
	public int dropEvaluationTable() throws Exception {
		String sql = "DROP TABLE EXP_INFO; ";

		int returnValue = BaseDAO.INVALID;
		try {
			ps = BaseDAO.getEvaluationDbConnection().prepareStatement(sql);
			returnValue = ps.executeUpdate();
		} catch (SQLException e) {
			if (e.getErrorCode()==42102)
				returnValue = 1;
			else {
				e.printStackTrace();
			}
		}
		
		return returnValue;		
	}
	
	public void initializeAllData() throws Exception {
		boolean commitDataIncluded = true;
		initializeAllData(commitDataIncluded);
	}
	
	public void initializeAllData(boolean commitDataIncluded) throws Exception {
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		sourceFileDAO.deleteAllSourceFiles();
		sourceFileDAO.deleteAllVersions();
		sourceFileDAO.deleteAllCorpuses();
		sourceFileDAO.deleteAllTerms();
		sourceFileDAO.deleteAllImportedClasses();
		sourceFileDAO.deleteAllTermWeights();
		
		BugDAO bugDAO = new BugDAO();
		bugDAO.deleteAllBugs();
		bugDAO.deleteAllTerms();
		bugDAO.deleteAllStackTraceClasses();
		bugDAO.deleteAllBugSfTermWeights();
		bugDAO.deleteAllBugTermWeights();
		bugDAO.deleteAllBugFixedInfo();
		bugDAO.deleteAllSimilarBugInfo();
		
		if (commitDataIncluded) {
			CommitDAO commitDAO = new CommitDAO();
			commitDAO.deleteAllCommitInfo();
			commitDAO.deleteAllCommitFileInfo();
		}
		
		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
		integratedAnalysisDAO.deleteAllIntegratedAnalysisInfos();
	}
	
	public void initializeAnalysisData() throws Exception {
		IntegratedAnalysisDAO integratedAnalysisDAO = new IntegratedAnalysisDAO();
		integratedAnalysisDAO.deleteAllIntegratedAnalysisInfos();
	}

	public void initializeExperimentResultData() throws Exception {
		ExperimentResultDAO experimentDAO = new ExperimentResultDAO();
		experimentDAO.deleteAllExperimentResults();
	}


}
