/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcSQLException;

import edu.skku.selab.blp.common.SourceFileCorpus;
import edu.skku.selab.blp.db.AnalysisValue;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class SourceFileDAO extends BaseDAO {
	final static public String DEFAULT_VERSION_STRING = "v1.0";
	final static public double INIT_LENGTH_SCORE = 0.0;
	final static public int INIT_TOTAL_COUPUS_COUNT = 0;
	
	
	/**
	 * @throws Exception
	 */
	public SourceFileDAO() throws Exception {
		super();
	}
	
	public int insertSourceFile(String fileName, String productName) {
		String sql = "INSERT INTO SF_INFO (SF_NAME, CLS_NAME, PROD_NAME) VALUES (?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, fileName);
			ps.setString(3, productName);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileID(fileName, productName);
		} 

		return returnValue;
	}
	
	public int insertSourceFile(String fileName, String className, String productName) {
		String sql = "INSERT INTO SF_INFO (SF_NAME, CLS_NAME, PROD_NAME) VALUES (?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, className);
			ps.setString(3, productName);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileID(fileName, className, productName);
		} 

		return returnValue;
	}
	
	public int deleteAllSourceFiles() {
		String sql = "DELETE FROM SF_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, Integer> getSourceFiles(String productName) {
		HashMap<String, Integer> fileInfo = new HashMap<String, Integer>();
		
		String sql = "SELECT SF_NAME, SF_ID FROM SF_INFO WHERE PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				fileInfo.put(rs.getString("SF_NAME"), rs.getInt("SF_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	public int getSourceFileCount(String productName, String version) {
		String sql = "SELECT COUNT(SF_VER_ID) FROM SF_INFO A, SF_VER_INFO B WHERE A.PROD_NAME = ? AND A.SF_ID = B.SF_ID AND B.VER = ?";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				returnValue = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;
	}

	public int insertVersion(String version, String releaseDate) {
		String sql = "INSERT INTO VER_INFO (VER, REL_DATE) VALUES (?, ?)";
		int returnValue = INVALID;
		
		// releaseDate format : "2004-10-18 17:40:00"
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, version);
			ps.setString(2, releaseDate);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllVersions() {
		String sql = "DELETE FROM VER_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, Date> getVersions() {
		HashMap<String, Date> versions = new HashMap<String, Date>();
		
		String sql = "SELECT VER, REL_DATE FROM VER_INFO";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				versions.put(rs.getString("VER"), rs.getTimestamp("REL_DATE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versions;	
	}
	
	public String getSourceFilePath(String fileName, String productName) {
		String sourceFilePath = null;
		String sql = "SELECT SF_PATH FROM SF_INFO " +
				"WHERE SF_NAME = ? AND PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, productName);
			
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				sourceFilePath = rs.getString("SF_PATH");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFilePath;	
	}
	
	public int getSourceFileID(String fileName, String productName) {
		int returnValue = INVALID;
		String sql = "SELECT SF_ID FROM SF_INFO " +
				"WHERE SF_NAME = ? AND PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, productName);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public int getSourceFileID(String fileName, String className, String productName) {
		int returnValue = INVALID;
		String sql = "SELECT SF_ID FROM SF_INFO " +
				"WHERE SF_NAME = ? AND CLS_NAME = ? AND PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, className);
			ps.setString(3, productName);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public int getSourceFileVersionID(int sourceFileID, String version) {
		int returnValue = INVALID;
		String sql = "SELECT SF_VER_ID FROM SF_VER_INFO B " +
				"WHERE SF_ID = ? AND VER = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileID);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_VER_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}

	public int getSourceFileVersionID(String fileName, String productName, String version) {
		int returnValue = INVALID;
		String sql = "SELECT B.SF_VER_ID FROM SF_INFO A, SF_VER_INFO B " +
				"WHERE A.SF_NAME = ? AND A.PROD_NAME = ? AND B.VER = ? AND A.SF_ID = B.SF_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, productName);
			ps.setString(3, version);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = rs.getInt("SF_VER_ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnValue;	
	}
	
	public HashSet<String> getSourceFileNames(String productName, String version) {
		HashSet<String> sourceFileNames = null;
		String sql = "SELECT A.SF_NAME FROM SF_INFO A, SF_VER_INFO B " +
				"WHERE A.PROD_NAME = ? AND B.VER = ? AND A.SF_ID = B.SF_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == sourceFileNames) {
					sourceFileNames = new HashSet<String>();
				}
				sourceFileNames.add(rs.getString("SF_NAME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileNames;	
	}
	
//	public HashSet<String> getClassNames(String productName, String version) {
//		HashSet<String> sourceFileNames = null;
//		String sql = "SELECT A.CLS_NAME FROM SF_INFO A, SF_VER_INFO B " +
//				"WHERE A.PROD_NAME = ? AND B.VER = ? AND A.SF_ID = B.SF_ID";
//		
//		try {
//			ps = analysisDbConnection.prepareStatement(sql);
//			ps.setString(1, productName);
//			ps.setString(2, version);
//			
//			rs = ps.executeQuery();
//			
//			while (rs.next()) {
//				if (null == sourceFileNames) {
//					sourceFileNames = new HashSet<String>();
//				}
//				
//				String fileName = rs.getString("CLS_NAME");
//				String className = fileName.substring(0, fileName.lastIndexOf("."));
//				sourceFileNames.add(className);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return sourceFileNames;	
//	}
	
	public HashMap<String, String> getClassNames(String productName, String version) {
		HashMap<String, String> sourceFileNames = null;
		String sql = "SELECT A.CLS_NAME, A.SF_NAME FROM SF_INFO A, SF_VER_INFO B " +
				"WHERE A.PROD_NAME = ? AND B.VER = ? AND A.SF_ID = B.SF_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == sourceFileNames) {
					sourceFileNames = new HashMap<String, String>();
				}
				
				String classNameWithExtension = rs.getString("CLS_NAME");
				String className = classNameWithExtension.substring(0, classNameWithExtension.lastIndexOf("."));
				String fileName = rs.getString("SF_NAME");
				sourceFileNames.put(className, fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileNames;	
	}
	
	public int insertStructuredCorpusSet(int sourceFileID, String version, SourceFileCorpus corpus, int totalCorpusCount, double lengthScore) {
		String sql = "INSERT INTO SF_VER_INFO (SF_ID, VER, CLS_COR, MTH_COR, VAR_COR, CMT_COR, TOT_CNT, LEN_SCORE) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileID);
			ps.setString(2, version);
			ps.setString(3, corpus.getClassPart());
			ps.setString(4, corpus.getMethodPart());
			ps.setString(5, corpus.getVariablePart());
			ps.setString(6, corpus.getCommentPart());
			ps.setInt(7, totalCorpusCount);
			ps.setDouble(8, lengthScore);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileVersionID(sourceFileID, version);
		} 

		return returnValue;
	}

	public int insertCorpusSet(int sourceFileID, String version, SourceFileCorpus corpus, int totalCorpusCount, double lengthScore) {
		String sql = "INSERT INTO SF_VER_INFO (SF_ID, VER, COR, CLS_COR, MTH_COR, VAR_COR, CMT_COR, TOT_CNT, LEN_SCORE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileID);
			ps.setString(2, version);
			ps.setString(3, corpus.getContent());
			ps.setString(4, corpus.getClassPart());
			ps.setString(5, corpus.getMethodPart());
			ps.setString(6, corpus.getVariablePart());
			ps.setString(7, corpus.getCommentPart());
			ps.setInt(8, totalCorpusCount);
			ps.setDouble(9, lengthScore);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (INVALID != returnValue) {
			returnValue = getSourceFileVersionID(sourceFileID, version);
		} 

		return returnValue;
	}
	
	public int deleteAllCorpuses() {
		String sql = "DELETE FROM SF_VER_INFO";
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
	 * Get <Source file name, CorpusMap> with product name and version
	 * 
	 * @param productName	Product name
	 * @param version		Version
	 * @return HashMap<String, SourceFileCorpus>	<Source file name, Source file corpus>
	 */
	public HashMap<String, SourceFileCorpus> getCorpusMap(String productName, String version) {
		HashMap<String, SourceFileCorpus> corpusSets = new HashMap<String, SourceFileCorpus>();
		
		String sql = "SELECT A.SF_NAME, B.COR, B.CLS_COR, B.MTH_COR, B.VAR_COR, B.CMT_COR " +
					"FROM SF_INFO A, SF_VER_INFO B " +
					"WHERE A.SF_ID = B.SF_ID AND " +
					"A.PROD_NAME = ? AND B.VER = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				SourceFileCorpus corpus = new SourceFileCorpus();
				corpus.setContent(rs.getString("COR"));
				corpus.setClassPart(rs.getString("CLS_COR"));
				corpus.setMethodPart(rs.getString("MTH_COR"));
				corpus.setVariablePart(rs.getString("VAR_COR"));
				corpus.setCommentPart(rs.getString("CMT_COR"));
				corpusSets.put(rs.getString("SF_NAME"), corpus);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return corpusSets;
	}
	
	/**
	 * Get SourceFileCorpus with source file version ID
	 * 
	 * @param sourceFileVersionID	Source file version ID
	 * @return SourceFileCorpus		Source file corpus
	 */
	public SourceFileCorpus getCorpus(int sourceFileVersionID) {
		String sql = "SELECT COR, CLS_COR, MTH_COR, VAR_COR, CMT_COR, COR_NORM, CLS_COR_NORM, MTH_COR_NORM, VAR_COR_NORM, CMT_COR_NORM  " +
					"FROM SF_VER_INFO B " +
					"WHERE SF_VER_ID = ?";
		
		SourceFileCorpus corpus = null;
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				corpus = new SourceFileCorpus();
				corpus.setContent(rs.getString("COR"));
				corpus.setClassPart(rs.getString("CLS_COR"));
				corpus.setMethodPart(rs.getString("MTH_COR"));
				corpus.setVariablePart(rs.getString("VAR_COR"));
				corpus.setCommentPart(rs.getString("CMT_COR"));
				corpus.setContentNorm(rs.getDouble("COR_NORM"));
				corpus.setClassCorpusNorm(rs.getDouble("CLS_COR_NORM"));
				corpus.setMethodCorpusNorm(rs.getDouble("MTH_COR_NORM"));
				corpus.setVariableCorpusNorm(rs.getDouble("VAR_COR_NORM"));
				corpus.setCommentCorpusNorm(rs.getDouble("CMT_COR_NORM"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return corpus;
	}
	
	public double getNormValue(int sourceFileVersionID) {
		String sql = "SELECT COR_NORM " +
					"FROM SF_VER_INFO B " +
					"WHERE SF_VER_ID = ?";
		
		double norm = 0;
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				norm = rs.getDouble("COR_NORM");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return norm;
	}
	
	/**
	 * Get SourceFileCorpus with source file version ID
	 * 
	 * @param sourceFileVersionID	Source file version ID
	 * @return SourceFileCorpus		Source file corpus
	 */
	public SourceFileCorpus getNormValues(int sourceFileVersionID) {
		String sql = "SELECT COR_NORM, CLS_COR_NORM, MTH_COR_NORM, VAR_COR_NORM, CMT_COR_NORM " +
					"FROM SF_VER_INFO B " +
					"WHERE SF_VER_ID = ?";
		
		SourceFileCorpus corpus = null;
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				corpus = new SourceFileCorpus();
				corpus.setContentNorm(rs.getDouble("COR_NORM"));
				corpus.setClassCorpusNorm(rs.getDouble("CLS_COR_NORM"));
				corpus.setMethodCorpusNorm(rs.getDouble("MTH_COR_NORM"));
				corpus.setVariableCorpusNorm(rs.getDouble("VAR_COR_NORM"));
				corpus.setCommentCorpusNorm(rs.getDouble("CMT_COR_NORM"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return corpus;
	}
	
	public HashMap<String, Integer> getSourceFileVersionIDs(String productName, String version) {
		HashMap<String, Integer> sourceFileVersionIDs = new HashMap<String, Integer>();
		
		String sql = "SELECT A.SF_NAME, B.SF_VER_ID " +
					"FROM SF_INFO A, SF_VER_INFO B " +
					"WHERE A.SF_ID = B.SF_ID AND " +
					"A.PROD_NAME = ? AND B.VER = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				sourceFileVersionIDs.put(rs.getString("SF_NAME"), rs.getInt("SF_VER_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sourceFileVersionIDs;
	}
	
	public HashMap<String, Integer> getTotalCorpusLengths(String productName, String version) {
		HashMap<String, Integer> totalCorpusLengths = new HashMap<String, Integer>();
		
		String sql = "SELECT A.SF_NAME, B.TOT_CNT " +
					"FROM SF_INFO A, SF_VER_INFO B " +
					"WHERE A.SF_ID = B.SF_ID AND " +
					"A.PROD_NAME = ? AND B.VER = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				totalCorpusLengths.put(rs.getString("SF_NAME"), rs.getInt("TOT_CNT"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalCorpusLengths;
	}
	
	public int updateLengthScore(String productName, String fileName, String version, double lengthScore) {
		String sql = "UPDATE SF_VER_INFO SET LEN_SCORE = ? " +
				"WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE  A.SF_ID = B.SF_ID AND A.PROD_NAME = ? " +
				"AND A.SF_NAME = ? AND B.VER = ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, lengthScore);
			ps.setString(2, productName);
			ps.setString(3, fileName);
			ps.setString(4, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateTotalCoupusCount(String productName, String fileName, String version, int totalCorpusCount) {
		String sql = "UPDATE SF_VER_INFO SET TOT_CNT = ? " +
				"WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE A.SF_ID = B.SF_ID AND A.PROD_NAME = ? " +
				"AND A.SF_NAME = ? AND B.VER = ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, totalCorpusCount);
			ps.setString(2, productName);
			ps.setString(3, fileName);
			ps.setString(4, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateNormValue(String productName, String fileName, String version, double corpusNorm) {
		String sql = "UPDATE SF_VER_INFO SET COR_NORM = ? " +
				"WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE A.SF_ID = B.SF_ID AND A.PROD_NAME = ? " +
				"AND A.SF_NAME = ? AND B.VER = ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, corpusNorm);
			
			ps.setString(2, productName);
			ps.setString(3, fileName);
			ps.setString(4, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateNormValues(String productName, String fileName, String version,
			double corpusNorm, double classNorm, double methodNorm, double variableNorm, double commentNorm) {
		String sql = "UPDATE SF_VER_INFO SET COR_NORM = ?, CLS_COR_NORM = ?, MTH_COR_NORM = ?, VAR_COR_NORM = ?, CMT_COR_NORM = ? " +
				"WHERE SF_ID IN (SELECT A.SF_ID FROM SF_INFO A, SF_VER_INFO B WHERE A.SF_ID = B.SF_ID AND A.PROD_NAME = ? " +
				"AND A.SF_NAME = ? AND B.VER = ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, corpusNorm);
			ps.setDouble(2, classNorm);
			ps.setDouble(3, methodNorm);
			ps.setDouble(4, variableNorm);
			ps.setDouble(5, commentNorm);
			
			ps.setString(6, productName);
			ps.setString(7, fileName);
			ps.setString(8, version);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, Double> getLengthScores(String productName, String version) {
		HashMap<String, Double> lengthScores = new HashMap<String, Double>();
		
		String sql = "SELECT A.SF_NAME, B.LEN_SCORE " +
					"FROM SF_INFO A, SF_VER_INFO B " +
					"WHERE A.SF_ID = B.SF_ID AND " +
					"A.PROD_NAME = ? AND B.VER = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lengthScores.put(rs.getString("SF_NAME"), rs.getDouble("LEN_SCORE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lengthScores;
	}
	
	public double getLengthScore(int sourceFileVersionID) {
		double lengthScore = INIT_LENGTH_SCORE;

		String sql = "SELECT LEN_SCORE " +
					"FROM SF_VER_INFO " +
					"WHERE SF_VER_ID = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				lengthScore = rs.getDouble("LEN_SCORE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lengthScore;
	}
	
	public int insertTerm(String term, String productName) {
		String sql = "INSERT INTO SF_TERM_INFO (TERM, PROD_NAME) VALUES (?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, term);
			ps.setString(2, productName);
			
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
	
	public int deleteAllTerms() {
		String sql = "DELETE FROM SF_TERM_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, Integer> getTermMap(String productName) {
		HashMap<String, Integer> fileInfo = new HashMap<String, Integer>();
		
		String sql = "SELECT TERM, SF_TERM_ID FROM SF_TERM_INFO WHERE PROD_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1,  productName);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				fileInfo.put(rs.getString("TERM"), rs.getInt("SF_TERM_ID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	public int getTermID(String term, String productName) {
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
	
	public int insertImportedClasses(int sourceFileVersionID, ArrayList<String> importedClasses) {
		String sql = "INSERT INTO SF_IMP_INFO (SF_VER_ID, IMP_CLASS) VALUES (?, ?)";
		int returnValue = INVALID;
		
		for (int i = 0; i < importedClasses.size(); i++) {
			try {
				String importedClass = importedClasses.get(i);
				ps = analysisDbConnection.prepareStatement(sql);
				ps.setInt(1, sourceFileVersionID);
				ps.setString(2, importedClass);
				
				returnValue = ps.executeUpdate();
			} catch (JdbcSQLException e) {
				e.printStackTrace();
				
				if (ErrorCode.DUPLICATE_KEY_1 != e.getErrorCode()) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (INVALID == returnValue) {
				break;
			}
		}
		
		return returnValue;
	}
	
	public int deleteAllImportedClasses() {
		String sql = "DELETE FROM SF_IMP_INFO";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, ArrayList<String>> getAllImportedClasses(String productName, String version) {
		HashMap<String, ArrayList<String>> importedClassesMap = new HashMap<String, ArrayList<String>>();
		
		String sql = "SELECT A.SF_NAME, C.IMP_CLASS " +
					"FROM SF_INFO A, SF_VER_INFO B, SF_IMP_INFO C " +
					"WHERE A.SF_ID = B.SF_ID AND " +
					"B.SF_VER_ID = C.SF_VER_ID AND " +
					"A.PROD_NAME = ? AND B.VER = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				String sourceFilename = rs.getString("SF_NAME");
				if (importedClassesMap.containsKey(sourceFilename)) {
					ArrayList<String> importedClasses = importedClassesMap.get(sourceFilename);
					importedClasses.add(rs.getString("IMP_CLASS"));
				} else {
					ArrayList<String> importedClasses = new ArrayList<String>();
					importedClasses.add(rs.getString("IMP_CLASS"));
					
					importedClassesMap.put(sourceFilename, importedClasses);	
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return importedClassesMap;
	}
	
	public ArrayList<String> getImportedClasses(String productName, String version, String fileName) {
		ArrayList<String> importedClasses = null;
		
		String sql = "SELECT C.IMP_CLASS " +
					"FROM SF_INFO A, SF_VER_INFO B, SF_IMP_INFO C " +
					"WHERE A.SF_ID = B.SF_ID AND " +
					"B.SF_VER_ID = C.SF_VER_ID AND " +
					"A.PROD_NAME = ? AND B.VER = ? AND A.SF_NAME = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, productName);
			ps.setString(2, version);
			ps.setString(3, fileName);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				if (null == importedClasses) {
					importedClasses = new ArrayList<String>();
				}
				
				importedClasses.add(rs.getString("IMP_CLASS"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return importedClasses;
	}
	
	public int insertTermWeight(AnalysisValue termWeight) {
		
		int fileVersionID = getSourceFileVersionID(termWeight.getName(),
				termWeight.getProductName(), termWeight.getVersion());
		int termID = getTermID(termWeight.getTerm(), termWeight.getProductName());
		
		String sql = "INSERT INTO SF_TERM_WGT (SF_VER_ID, SF_TERM_ID, TERM_CNT, INV_DOC_CNT, TF, IDF) " +
				"VALUES (?, ?, ?, ?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, fileVersionID);
			ps.setInt(2, termID);
			ps.setInt(3, termWeight.getTermCount());
			ps.setInt(4, termWeight.getInvDocCount());
			ps.setDouble(5, termWeight.getTf());
			ps.setDouble(6, termWeight.getIdf());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateTermWeight(AnalysisValue termWeight) {
		String sql = "UPDATE SF_TERM_WGT SET TERM_CNT = ?, INV_DOC_CNT = ?, TF = ?, IDF = ?" +
				"WHERE SF_VER_ID = ? AND SF_TERM_ID = ?";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, termWeight.getTermCount());
			ps.setInt(2, termWeight.getInvDocCount());
			ps.setDouble(3, termWeight.getTf());
			ps.setDouble(4, termWeight.getIdf());
			ps.setInt(5, termWeight.getSourceFileVersionID());
			ps.setInt(6, termWeight.getTermID());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int deleteAllTermWeights() {
		String sql = "DELETE FROM SF_TERM_WGT";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public AnalysisValue getTermWeight(String fileName, String productName, String version, String term) {
		AnalysisValue returnValue = null;

		String sql = "SELECT D.TERM_CNT, D.INV_DOC_CNT, D.TF, D.IDF "+
				"FROM SF_INFO A, SF_VER_INFO B, SF_TERM_INFO C, SF_TERM_WGT D " +
				"WHERE A.SF_NAME = ? AND A.PROD_NAME = ? AND A.SF_ID = B.SF_ID AND " +
				"B.VER = ? AND B.SF_VER_ID = D.SF_VER_ID AND C.TERM = ? AND " +
				"C.PROD_NAME = ? AND C.SF_TERM_ID = D.SF_TERM_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, productName);
			ps.setString(3, version);
			ps.setString(4, term);
			ps.setString(5, productName);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				returnValue = new AnalysisValue();
				
				returnValue.setName(fileName);
				returnValue.setProductName(productName);
				returnValue.setVersion(version);
				returnValue.setTerm(term);
				returnValue.setTermCount(rs.getInt("TERM_CNT"));
				returnValue.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				returnValue.setTf(rs.getDouble("TF"));
				returnValue.setIdf(rs.getDouble("IDF"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<String, AnalysisValue> getTermMap(String productName, String fileName, String version) {
		HashMap<String, AnalysisValue> termMap = null;

		String sql = "SELECT C.TERM, D.SF_VER_ID, D.SF_TERM_ID, D.TERM_CNT, D.INV_DOC_CNT, D.TF, D.IDF "+
				"FROM SF_INFO A, SF_VER_INFO B, SF_TERM_INFO C, SF_TERM_WGT D " +
				"WHERE A.SF_NAME = ? AND A.PROD_NAME = ? AND A.SF_ID = B.SF_ID AND " +
				"B.VER = ? AND B.SF_VER_ID = D.SF_VER_ID AND " +
				"C.PROD_NAME = ? AND C.SF_TERM_ID = D.SF_TERM_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setString(1, fileName);
			ps.setString(2, productName);
			ps.setString(3, version);
			ps.setString(4, productName);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == termMap) {
					termMap = new HashMap<String, AnalysisValue>();
				}
				AnalysisValue termWeight = new AnalysisValue();
				
				String term = rs.getString("TERM");
				termWeight.setName(fileName);
				termWeight.setProductName(productName);
				termWeight.setVersion(version);
				termWeight.setTerm(term);
				termWeight.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				termWeight.setTermID(rs.getInt("SF_TERM_ID"));
				termWeight.setTermCount(rs.getInt("TERM_CNT"));
				termWeight.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				termWeight.setTf(rs.getDouble("TF"));
				termWeight.setIdf(rs.getDouble("IDF"));
				
				termMap.put(term, termWeight);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return termMap;
	}
	
	public HashMap<String, AnalysisValue> getTermMap(int sourceFileVersionID) {
		HashMap<String, AnalysisValue> termMap = null;

		String sql = "SELECT C.TERM, D.SF_VER_ID, D.SF_TERM_ID, D.TERM_CNT, D.INV_DOC_CNT, D.TF, D.IDF "+
				"FROM SF_TERM_INFO C, SF_TERM_WGT D " +
				"WHERE D.SF_VER_ID = ? AND C.SF_TERM_ID = D.SF_TERM_ID";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, sourceFileVersionID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == termMap) {
					termMap = new HashMap<String, AnalysisValue>();
				}
				AnalysisValue analysisValue = new AnalysisValue();
				
				String term = rs.getString("TERM");
				analysisValue.setTerm(term);
				analysisValue.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				analysisValue.setTermID(rs.getInt("SF_TERM_ID"));
				analysisValue.setTermCount(rs.getInt("TERM_CNT"));
				analysisValue.setInvDocCount(rs.getInt("INV_DOC_CNT"));
				analysisValue.setTf(rs.getDouble("TF"));
				analysisValue.setIdf(rs.getDouble("IDF"));
				
				termMap.put(term, analysisValue);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return termMap;
	}
}
