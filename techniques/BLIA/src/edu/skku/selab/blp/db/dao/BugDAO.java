/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcSQLException;

import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.BugCorpus;
import edu.skku.selab.blp.common.SourceFile;
import edu.skku.selab.blp.db.AnalysisValue;
import edu.skku.selab.blp.db.SimilarBugInfo;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugDAO extends BaseDAO {

	/**
	 * @throws Exception
	 */
	public BugDAO() throws Exception {
		super();
	}
	
	public int insertBug(Bug bug) {
		String sql = "INSERT INTO BUG_INFO (BUG_ID, PROD_NAME, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, VER) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int returnValue = INVALID;
		
		// releaseDate format : "2004-10-18 17:40:00"
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bug.getID());
			ps.setString(2, bug.getProductName());
			ps.setString(3, bug.getOpenDateString());
			ps.setString(4, bug.getFixedDateString());
			BugCorpus bugCorpus = bug.getCorpus();
			ps.setString(5, bugCorpus.getContent());
			ps.setString(6, bugCorpus.getSummaryPart());
			ps.setString(7, bugCorpus.getDescriptionPart());
			ps.setInt(8, bug.getTotalCorpusCount());
			ps.setString(9, bug.getVersion());
			
			returnValue = ps.executeUpdate();
			
			ArrayList<String> stackTraceClasses = bug.getStackTraceClasses();
			if (null != stackTraceClasses) {
				for (int i = 0; i < stackTraceClasses.size(); i++) {
					insertStackTraceClass(bug.getID(), stackTraceClasses.get(i));				
				}
			}
		} catch (JdbcSQLException e) {
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return returnValue;
	}
	
	public int insertStructuredBug(Bug bug) {
		String sql = "INSERT INTO BUG_INFO (BUG_ID, PROD_NAME, OPEN_DATE, FIXED_DATE, SMR_COR, DESC_COR, TOT_CNT, VER) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		int returnValue = INVALID;
		
		// releaseDate format : "2004-10-18 17:40:00"
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bug.getID());
			ps.setString(2, bug.getProductName());
			ps.setString(3, bug.getOpenDateString());
			ps.setString(4, bug.getFixedDateString());
			BugCorpus bugCorpus = bug.getCorpus();
			ps.setString(5, bugCorpus.getSummaryPart());
			ps.setString(6, bugCorpus.getDescriptionPart());
			ps.setInt(7, bug.getTotalCorpusCount());
			ps.setString(8, bug.getVersion());
			
			returnValue = ps.executeUpdate();
			
			ArrayList<String> stackTraceClasses = bug.getStackTraceClasses();
			if (null != stackTraceClasses) {
				for (int i = 0; i < stackTraceClasses.size(); i++) {
					insertStackTraceClass(bug.getID(), stackTraceClasses.get(i));				
				}
			}
		} catch (JdbcSQLException e) {
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return returnValue;
	}
	
	public int deleteAllBugs() {
		String sql = "DELETE FROM BUG_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<Integer, Bug> getBugs() {
		HashMap<Integer, Bug> bugs = new HashMap<Integer, Bug>();
		
		String sql = "SELECT BUG_ID, PROD_NAME, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			Bug bug = null;
			rs = ps.executeQuery();
			while (rs.next()) {
				bug = new Bug();
				bug.setID(rs.getInt("BUG_ID"));
				bug.setProductName(rs.getString("PROD_NAME"));
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));
				
				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setContent(rs.getString("COR"));
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bugCorpus.setContentNorm(rs.getDouble("COR_NORM"));
				bugCorpus.setSummaryCorpusNorm(rs.getDouble("SMR_COR_NORM"));
				bugCorpus.setDecriptionCorpusNorm(rs.getDouble("DESC_COR_NORM"));
				bug.setCorpus(bugCorpus);
				
				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
				bugs.put(bug.getID(), bug);
			}
			
			Iterator<Integer> bugsIter = bugs.keySet().iterator();
			while (bugsIter.hasNext()) {
				int bugID = bugsIter.next();
				bug = bugs.get(bugID);
				bug.setStackTraceClasses(getStackTraceClasses(bugID));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugs;	
	}
	
	
	public ArrayList<Bug> getAllBugs(String productName, boolean orderedByFixedDate) {
		ArrayList<Bug> bugs = new ArrayList<Bug>();
		
		String sql = "SELECT BUG_ID, PROD_NAME, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO " +
				"WHERE PROD_NAME = ? ";
		
		if (orderedByFixedDate) {
			sql += "ORDER BY FIXED_DATE";
		}
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			
			Bug bug = null;
			rs = ps.executeQuery();
			while (rs.next()) {
				bug = new Bug();
				bug.setID(rs.getInt("BUG_ID"));
				bug.setProductName(rs.getString("PROD_NAME"));
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));

				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setContent(rs.getString("COR"));
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bugCorpus.setContentNorm(rs.getDouble("COR_NORM"));
				bugCorpus.setSummaryCorpusNorm(rs.getDouble("SMR_COR_NORM"));
				bugCorpus.setDecriptionCorpusNorm(rs.getDouble("DESC_COR_NORM"));
				bug.setCorpus(bugCorpus);

				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
				bugs.add(bug);
			}
			
			for (int i = 0; i < bugs.size(); i++) {
				bug = bugs.get(i);
				bug.setStackTraceClasses(getStackTraceClasses(bug.getID()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugs;	
	}

	public int getBugCountWithFixedDate(String productName, String fixedDateString) {
		String sql = "SELECT COUNT(BUG_ID) FROM BUG_INFO " +
				"WHERE PROD_NAME = ? AND FIXED_DATE = ?";
		
		int count = 0;
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, fixedDateString);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1); 
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;	
	}
	
	public ArrayList<Bug> getPreviousFixedBugs(String productName, String fixedDateString, int exceptedBugID) {
		ArrayList<Bug> bugs = new ArrayList<Bug>();
		
		String sql = "SELECT BUG_ID, PROD_NAME, OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, COR_NORM, SMR_COR_NORM, DESC_COR_NORM, VER FROM BUG_INFO " +
				"WHERE PROD_NAME = ? AND FIXED_DATE <= ? AND BUG_ID != ? ORDER BY FIXED_DATE";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, fixedDateString);
			ps.setInt(3, exceptedBugID);
			
			Bug bug = null;
			rs = ps.executeQuery();
			while (rs.next()) {
				bug = new Bug();
				bug.setID(rs.getInt("BUG_ID"));
				bug.setProductName(rs.getString("PROD_NAME"));
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));

				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setContent(rs.getString("COR"));
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bugCorpus.setContentNorm(rs.getDouble("COR_NORM"));
				bugCorpus.setSummaryCorpusNorm(rs.getDouble("SMR_COR_NORM"));
				bugCorpus.setDecriptionCorpusNorm(rs.getDouble("DESC_COR_NORM"));
				bug.setCorpus(bugCorpus);

				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
				bugs.add(bug);
			}
			
			for (int i = 0; i < bugs.size(); i++) {
				bug = bugs.get(i);
				bug.setStackTraceClasses(getStackTraceClasses(bug.getID()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugs;	
	}
	
	public Bug getBug(int bugID, String productName) {
		String sql = "SELECT OPEN_DATE, FIXED_DATE, COR, SMR_COR, DESC_COR, TOT_CNT, VER FROM BUG_INFO WHERE BUG_ID = ? AND PROD_NAME = ?";
		Bug bug = null;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setString(2, productName);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				bug = new Bug();
				bug.setID(bugID);
				bug.setProductName(productName);
				bug.setOpenDate(rs.getTimestamp("OPEN_DATE"));
				bug.setFixedDate(rs.getTimestamp("FIXED_DATE"));

				BugCorpus bugCorpus = new BugCorpus();
				bugCorpus.setContent(rs.getString("COR"));
				bugCorpus.setSummaryPart(rs.getString("SMR_COR"));
				bugCorpus.setDescriptionPart(rs.getString("DESC_COR"));
				bug.setCorpus(bugCorpus);

				bug.setTotalCorpusCount(rs.getInt("TOT_CNT"));
				bug.setVersion(rs.getString("VER"));
			}
			
			bug.setStackTraceClasses(getStackTraceClasses(bugID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bug;	
	}

	
	public int insertBugTerm(String term, String productName) {
		String sql = "INSERT INTO BUG_TERM_INFO (TERM, PROD_NAME) VALUES (?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, term);
			ps.setString(2, productName);
			
			returnValue = ps.executeUpdate();
			
			sql = "SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM = ? AND PROD_NAME = ?";
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, term);
			ps.setString(2, productName);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt("BUG_TERM_ID");	
			}
		} catch (JdbcSQLException e) {
			e.printStackTrace();
			
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllTerms() {
		String sql = "DELETE FROM BUG_TERM_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	/**
	 * Get <Source file name, Corpus sets> with product name and version
	 * 
	 * @param productName	Product name
	 * @return HashMap<Integer, String>	<Source file name, Corpus sets>
	 */
	public HashMap<Integer, String> getCorpusMap(String productName) {
		HashMap<Integer, String> corpusMap = new HashMap<Integer, String>();
		
		String sql = "SELECT BUG_ID, COR " +
					"FROM BUG_INFO " +
					"WHERE PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				corpusMap.put(rs.getInt("BUG_ID"), rs.getString("COR"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corpusMap;
	}
	
	/**
	 * Get norm value with bug ID
	 * 
	 * @param bugID		Bug ID
	 * @return double 	corpus norm value
	 */
	public double getNormValue(int bugID) {
		double norm = 0;
		
		String sql = "SELECT COR_NORM " +
					"FROM BUG_INFO " +
					"WHERE BUG_ID = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				norm = rs.getDouble("COR_NORM");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return norm;
	}


	
	public HashMap<String, Integer> getTermMap(String productName) {
		HashMap<String, Integer> termMap = new HashMap<String, Integer>();
		
		String sql = "SELECT TERM, BUG_TERM_ID FROM BUG_TERM_INFO WHERE PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1,  productName);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				termMap.put(rs.getString("TERM"), rs.getInt("BUG_TERM_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return termMap;
	}
	
	public int getAllTermCount(String productName) {
		String sql = "SELECT COUNT(BUG_TERM_ID) FROM BUG_TERM_INFO WHERE PROD_NAME = ?";
		
		int allTermCount = 0;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1,  productName);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				allTermCount = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allTermCount;
	}
	
	public int getBugCount(String productName) {
		String sql = "SELECT COUNT(BUG_ID) FROM BUG_INFO WHERE PROD_NAME = ?";
		
		int bugCount = 0;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1,  productName);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				bugCount = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bugCount;
	}
	
	public int getSfTermID(String term, String productName) {
		int returnValue = INVALID;
		String sql = "SELECT SF_TERM_ID FROM SF_TERM_INFO WHERE TERM = ? AND PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, term);
			ps.setString(2, productName);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt("SF_TERM_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public HashMap<String, AnalysisValue> getSfTermMap(int bugID) {
		String sql = "SELECT A.TF, A.IDF, B.TERM FROM BUG_SF_TERM_WGT A, SF_TERM_INFO B WHERE A.BUG_ID = ? AND A.SF_TERM_ID = B.SF_TERM_ID";
		
		HashMap<String, AnalysisValue> sourceFileTermMap = null;
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				if (null == sourceFileTermMap) {
					sourceFileTermMap = new HashMap<String, AnalysisValue>();
				}
				
				AnalysisValue sourceFileTermWeight = new AnalysisValue();
				sourceFileTermWeight.setTf(rs.getDouble("TF"));
				sourceFileTermWeight.setIdf(rs.getDouble("IDF"));
				
				String term = rs.getString("TERM");
				sourceFileTermMap.put(term, sourceFileTermWeight);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileTermMap;	
	}
	
	public int insertStackTraceClass(int bugID, String className) {
		String sql = "INSERT INTO BUG_STRACE_INFO (BUG_ID, STRACE_CLASS) VALUES (?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setString(2, className);
			
			returnValue = ps.executeUpdate();
		} catch (JdbcSQLException e) {
			e.printStackTrace();
			
			if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllStackTraceClasses() {
		String sql = "DELETE FROM BUG_STRACE_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}

	public ArrayList<String> getStackTraceClasses(int bugID) {
		ArrayList<String> stackTraceClasses = null;

		String sql = "SELECT STRACE_CLASS "+
				"FROM BUG_STRACE_INFO " +
				"WHERE BUG_ID = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == stackTraceClasses) {
					stackTraceClasses = new ArrayList<String>();
				}
				
				stackTraceClasses.add(rs.getString("STRACE_CLASS"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stackTraceClasses;
	}
	
	public int insertBugSfTermWeight(AnalysisValue bugSfTermWeight) {
		int termID = getSfTermID(bugSfTermWeight.getTerm(), bugSfTermWeight.getProductName());
		
		String sql = "INSERT INTO BUG_SF_TERM_WGT (BUG_ID, SF_TERM_ID, TERM_CNT, INV_DOC_CNT, TF, IDF) " +
				"VALUES (?, ?, ?, ?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugSfTermWeight.getID());
			ps.setInt(2, termID);
			ps.setInt(3, bugSfTermWeight.getTermCount());
			ps.setInt(4, bugSfTermWeight.getInvDocCount());
			ps.setDouble(5, bugSfTermWeight.getTf());
			ps.setDouble(6, bugSfTermWeight.getIdf());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllBugSfTermWeights() {
		String sql = "DELETE FROM BUG_SF_TERM_WGT";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public AnalysisValue getBugSfTermWeight(int bugID, String productName, String term) {
		AnalysisValue termWeight = null;

		String sql = "SELECT C.TERM_CNT, C.INV_DOC_CNT, C.TF, C.IDF "+
				"FROM BUG_INFO A, SF_TERM_INFO B, BUG_SF_TERM_WGT C " +
				"WHERE A.BUG_ID = ? AND A.PROD_NAME = ? AND " +
				"B.TERM = ? AND " +
				"B.PROD_NAME = ? AND B.SF_TERM_ID = C.SF_TERM_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setString(2, productName);
			ps.setString(3, term);
			ps.setString(4, productName);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				termWeight = new AnalysisValue();
				
				termWeight.setID(bugID);
				termWeight.setProductName(productName);
				termWeight.setTerm(term);
				termWeight.setTermCount(rs.getInt("TERM_CNT"));
				termWeight.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				termWeight.setTf(rs.getDouble("TF"));
				termWeight.setIdf(rs.getDouble("IDF"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return termWeight;
	}
	
	public int getBugTermID(String term, String productName) {
		int returnValue = INVALID;
		String sql = "SELECT BUG_TERM_ID FROM BUG_TERM_INFO WHERE TERM = ? AND PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, term);
			ps.setString(2, productName);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt("BUG_TERM_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public int insertBugTermWeight(AnalysisValue analysisValue) {
		int termID = analysisValue.getTermID();
		if (INVALID == termID) {
			termID = getBugTermID(analysisValue.getTerm(), analysisValue.getProductName());
		}
		
		String sql = "INSERT INTO BUG_TERM_WGT (BUG_ID, BUG_TERM_ID, TW) " +
				"VALUES (?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, analysisValue.getID());
			ps.setInt(2, termID);
			ps.setDouble(3, analysisValue.getTermWeight());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllBugTermWeights() {
		String sql = "DELETE FROM BUG_TERM_WGT";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public AnalysisValue getBugTermWeight(int bugID, String productName, String term) {
		AnalysisValue returnValue = null;

		String sql = "SELECT C.TW "+
				"FROM BUG_INFO A, BUG_TERM_INFO B, BUG_TERM_WGT C " +
				"WHERE A.BUG_ID = ? AND A.PROD_NAME = ? AND "+
				"A.BUG_ID = C.BUG_ID AND B.TERM = ? AND B.BUG_TERM_ID = C.BUG_TERM_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setString(2, productName);
			ps.setString(3, term);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = new AnalysisValue();
				
				returnValue.setID(bugID);
				returnValue.setProductName(productName);
				returnValue.setTerm(term);
				returnValue.setTermWeight(rs.getDouble("TW"));				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public ArrayList<AnalysisValue> getBugTermWeightList(int bugID) {
		ArrayList<AnalysisValue> bugAnalysisValues = null;
		AnalysisValue bugTermWeight = null;

		String sql = "SELECT B.TERM, C.BUG_TERM_ID, C.TW "+
				"FROM BUG_TERM_INFO B, BUG_TERM_WGT C " +
				"WHERE C.BUG_ID = ? AND B.BUG_TERM_ID = C.BUG_TERM_ID ORDER BY C.BUG_TERM_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == bugAnalysisValues) {
					bugAnalysisValues = new ArrayList<AnalysisValue>();
				}
				
				bugTermWeight = new AnalysisValue();
				bugTermWeight.setID(bugID);
				bugTermWeight.setTerm(rs.getString("TERM"));
				bugTermWeight.setTermID(rs.getInt("BUG_TERM_ID"));
				bugTermWeight.setTermWeight(rs.getDouble("TW"));
				
				bugAnalysisValues.add(bugTermWeight);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bugAnalysisValues;
	}
	
//	public int insertBugFixInfo(int bugID, String fileName, String functionName, String version, String productName) {
//		String sql = "INSERT INTO BUG_FIX_INFO (BUG_ID, FIXED_SF_VER_ID, FIXED_FUNC_VER_ID) VALUES (?, ?, ?)";
//		int returnValue = INVALID;
//		
//		try {
//			SourceFileDAO sourceFileDAO = new SourceFileDAO();
//			int fixedSourceFileID = sourceFileDAO.getSourceFileVersionID(fileName, productName, version);
//			
//			// TODO: implement functionDAO.getFunctionVersionID() later
//			int fixedFunctionID = INVALID;
//
//			ps = analysisDbConnection.prepareStatement(sql);
//			ps.setInt(1, bugID);
//			ps.setInt(2, fixedSourceFileID);
//			ps.setInt(3, fixedFunctionID);
//			
//			returnValue = ps.executeUpdate();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return returnValue;
//	}
	
	public int insertBugFixedFileInfo(int bugID, String fileName, String version, String productName) {
		String sql = "INSERT INTO BUG_FIX_INFO (BUG_ID, FIXED_SF_VER_ID, FIXED_FUNC_VER_ID) VALUES (?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			SourceFileDAO sourceFileDAO = new SourceFileDAO();
			int fixedSourceFileID = sourceFileDAO.getSourceFileVersionID(fileName, productName, version);
			int fixedFunctionID = INVALID;

			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setInt(2, fixedSourceFileID);
			ps.setInt(3, fixedFunctionID);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}

	public int insertBugFixedFuncInfo(int bugID, String functionName, String version, String productName) {
		String sql = "INSERT INTO BUG_FIX_INFO (BUG_ID, FIXED_SF_VER_ID, FIXED_FUNC_VER_ID) VALUES (?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			int fixedSourceFileID = INVALID;
					
			// TODO: implement functionDAO.getFunctionVersionID() later
			int fixedFunctionID = INVALID;

			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setInt(2, fixedSourceFileID);
			ps.setInt(3, fixedFunctionID);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}

	
	public int deleteAllBugFixedInfo() {
		String sql = "DELETE FROM BUG_FIX_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashSet<SourceFile> getFixedFiles(int bugID) {
		HashSet<SourceFile> fixedFiles = null;
		
		String sql = "SELECT A.SF_NAME, B.VER, C.FIXED_SF_VER_ID FROM SF_INFO A, SF_VER_INFO B, BUG_FIX_INFO C " + 
				"WHERE C.BUG_ID = ? AND C.FIXED_SF_VER_ID = B.SF_VER_ID AND A.SF_ID = B.SF_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == fixedFiles) {
					fixedFiles = new HashSet<SourceFile>();
				}

				SourceFile sourceFile = new SourceFile();
				sourceFile.setName(rs.getString("SF_NAME"));
				sourceFile.setVersion(rs.getString("VER"));
				sourceFile.setSourceFileVersionID(rs.getInt("FIXED_SF_VER_ID"));

				fixedFiles.add(sourceFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fixedFiles;
	}
	
//	/**
//	 * 
//	 * @param productName
//	 * @return <BugID, SourceFileVersionID>
//	 */
//	public HashMap<String, Integer> getAllFixedFiles(String productName) {
//		HashMap<String, Integer> fixedFiles = null;
//		
//		String sql = "SELECT B.BUG_ID, B.FIXED_SF_VER_ID FROM BUG_INFO A, BUG_FIX_INFO B " + 
//				"WHERE A.BUG_ID = B.BUG_ID AND A.PROD_NAME = ?";
//		
//		try {
//			ps = analysisDbConnection.prepareStatement(sql);
//			ps.setString(1, productName);
//			
//			rs = ps.executeQuery();
//			
//			while (rs.next()) {
//				if (null == fixedFiles) {
//					fixedFiles = new HashMap<String, Integer>();
//				}
//
//				fixedFiles.put(rs.getString("BUG_ID"), rs.getInt("FIXED_SF_VER_ID"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return fixedFiles;
//	}
	
	public int insertSimilarBugInfo(int bugID, int similarBugID, double similarityScore) {
		String sql = "INSERT INTO SIMI_BUG_ANAYSIS (BUG_ID, SIMI_BUG_ID, SIMI_BUG_SCORE) VALUES (?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			ps.setInt(2, similarBugID);
			ps.setDouble(3, similarityScore);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllSimilarBugInfo() {
		String sql = "DELETE FROM SIMI_BUG_ANAYSIS";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashSet<SimilarBugInfo> getSimilarBugInfos(int bugID) {
		HashSet<SimilarBugInfo> similarBugInfos = null;

		String sql = "SELECT SIMI_BUG_ID, SIMI_BUG_SCORE FROM SIMI_BUG_ANAYSIS " + 
				"WHERE BUG_ID = ? AND SIMI_BUG_SCORE != 0.0";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == similarBugInfos) {
					similarBugInfos = new HashSet<SimilarBugInfo>();
				}

				SimilarBugInfo similarBugInfo = new SimilarBugInfo();
				similarBugInfo.setSimilarBugID(rs.getInt("SIMI_BUG_ID"));
				similarBugInfo.setSimilarityScore(rs.getDouble("SIMI_BUG_SCORE"));

				similarBugInfos.add(similarBugInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return similarBugInfos;
	}
	
	public int updateTotalTermCount(String productName, int bugID, int totalTermCount) {
		String sql = "UPDATE BUG_INFO SET TOT_CNT = ? " +
				"WHERE BUG_ID = ? AND PROD_NAME = ?";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, totalTermCount);
			ps.setInt(2, bugID);
			ps.setString(3, productName);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateNormValues(String productName, int bugID, double corpusNorm, double summaryCorpusNorm, double descriptionCorpusNorm) {
		String sql = "UPDATE BUG_INFO SET COR_NORM = ?, SMR_COR_NORM = ?, DESC_COR_NORM = ? " +
				"WHERE BUG_ID = ? AND PROD_NAME = ?";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, corpusNorm);
			ps.setDouble(2, summaryCorpusNorm);
			ps.setDouble(3, descriptionCorpusNorm);
			
			ps.setInt(4, bugID);
			ps.setString(5, productName);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
}
