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

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.db.IntegratedAnalysisValue;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class IntegratedAnalysisDAO extends BaseDAO {
	
	public final static int INVALID_SCORE = -1;

	/**
	 * @throws Exception
	 */
	public IntegratedAnalysisDAO() throws Exception {
		super();
	}
	
	public int insertAnalysisVaule(IntegratedAnalysisValue integratedAnalysisValue) {
		String sql = "INSERT INTO INT_ANALYSIS (BUG_ID, SF_VER_ID, VSM_SCORE, SIMI_SCORE, BL_SCORE, STRACE_SCORE, COMM_SCORE, BLIA_SCORE) "+
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		int returnValue = INVALID;
		
		try {
			SourceFileDAO sourceFileDAO = new SourceFileDAO();
			int sourceFileVersionID = integratedAnalysisValue.getSourceFileVersionID();
			
			if (INVALID == sourceFileVersionID) {
				sourceFileVersionID = sourceFileDAO.getSourceFileVersionID(integratedAnalysisValue.getFileName(), integratedAnalysisValue.getProductName(), integratedAnalysisValue.getVersion());
			}
			
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, integratedAnalysisValue.getBugID());
			ps.setInt(2, sourceFileVersionID);
			ps.setDouble(3, integratedAnalysisValue.getVsmScore());
			ps.setDouble(4, integratedAnalysisValue.getSimilarityScore());
			ps.setDouble(5, integratedAnalysisValue.getBugLocatorScore());
			ps.setDouble(6, integratedAnalysisValue.getStackTraceScore());
			ps.setDouble(7, integratedAnalysisValue.getCommitLogScore());
			ps.setDouble(8, integratedAnalysisValue.getBLIAScore());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateSimilarScore(IntegratedAnalysisValue integratedAnalysisValue) {
		String sql = "UPDATE INT_ANALYSIS SET SIMI_SCORE = ? WHERE BUG_ID = ? AND SF_VER_ID = ?";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, integratedAnalysisValue.getSimilarityScore());
			ps.setInt(2, integratedAnalysisValue.getBugID());
			ps.setInt(3, integratedAnalysisValue.getSourceFileVersionID());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public int updateBugLocatorScore(IntegratedAnalysisValue integratedAnalysisValue) {
		String sql = "UPDATE INT_ANALYSIS SET BL_SCORE = ? WHERE BUG_ID = ? AND SF_VER_ID = ?";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, integratedAnalysisValue.getBugLocatorScore());
			ps.setInt(2, integratedAnalysisValue.getBugID());
			ps.setInt(3, integratedAnalysisValue.getSourceFileVersionID());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;		
	}
	
	public int updateBLIAScore(IntegratedAnalysisValue integratedAnalysisValue) {
		String sql = "UPDATE INT_ANALYSIS SET BLIA_SCORE = ?, BL_SCORE = ? WHERE BUG_ID = ? AND SF_VER_ID = ?";
		int returnValue = INVALID;
		
//		System.out.printf("Bug ID: %d, SourceFileVerID: %d\n", integratedAnalysisValue.getBugID(), integratedAnalysisValue.getSourceFileVersionID());
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, integratedAnalysisValue.getBLIAScore());
			ps.setDouble(2, integratedAnalysisValue.getBugLocatorScore());
			ps.setInt(3, integratedAnalysisValue.getBugID());
			ps.setInt(4, integratedAnalysisValue.getSourceFileVersionID());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;		
	}
	
	public int updateStackTraceScore(IntegratedAnalysisValue integratedAnalysisValue) {
		String sql = "UPDATE INT_ANALYSIS SET STRACE_SCORE = ? WHERE BUG_ID = ? AND SF_VER_ID = ?";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, integratedAnalysisValue.getStackTraceScore());
			ps.setInt(2, integratedAnalysisValue.getBugID());
			ps.setInt(3, integratedAnalysisValue.getSourceFileVersionID());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;		
	}
	
	public int updateCommitLogScore(IntegratedAnalysisValue integratedAnalysisValue) throws Exception {
		String sql = "UPDATE INT_ANALYSIS SET COMM_SCORE = ? WHERE BUG_ID = ? AND SF_VER_ID = ?";
		int returnValue = INVALID;
		
		int sourceFileVersionID = integratedAnalysisValue.getSourceFileVersionID();
		if (INVALID == sourceFileVersionID) {
			String productName = Property.getInstance().productName;
			String fileName = integratedAnalysisValue.getFileName();
			if (fileName.contains(".java")) {
				fileName = fixFileName(fileName);
				integratedAnalysisValue.setFileName(fileName);
			}
			String version = integratedAnalysisValue.getVersion();
			
			SourceFileDAO sourceFileDAO = new SourceFileDAO();
			sourceFileVersionID = sourceFileDAO.getSourceFileVersionID(fileName, productName, version);
			
			if (INVALID == sourceFileVersionID) {
				return INVALID;
			} else {
				integratedAnalysisValue.setSourceFileVersionID(sourceFileVersionID);
			}
		}
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setDouble(1, integratedAnalysisValue.getCommitLogScore());
			ps.setInt(2, integratedAnalysisValue.getBugID());
			ps.setInt(3, integratedAnalysisValue.getSourceFileVersionID());
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;		
	}
	
	private String fixFileName(String javaFileName) {
		String productName = Property.getInstance().productName;
		
		String fixedFileName = javaFileName;
		fixedFileName = fixedFileName.replace('/', '.');

		switch (productName) {
		case Property.ASPECTJ:
			fixedFileName = javaFileName;
			break;
		case Property.ECLIPSE:
			if (-1 != fixedFileName.lastIndexOf("org.eclipse")) {
				fixedFileName = fixedFileName.substring(fixedFileName.lastIndexOf("org.eclipse"), fixedFileName.length());
			} else if (-1 != fixedFileName.lastIndexOf("org.osgi")) {
				fixedFileName = fixedFileName.substring(fixedFileName.lastIndexOf("org.osgi"), fixedFileName.length());
			} else if (-1 != fixedFileName.lastIndexOf("org.apache")) {
				fixedFileName = fixedFileName.substring(fixedFileName.lastIndexOf("org.osgi"), fixedFileName.length());
			} else {
				System.err.printf("Wrong fixed file that is not source file: %s\n", fixedFileName);
			}
			break;
		case Property.SWT:
			if (-1 != fixedFileName.lastIndexOf("org.eclipse.swt")) {
				fixedFileName = fixedFileName.substring(fixedFileName.lastIndexOf("org.eclipse.swt"), fixedFileName.length());
			} else {
				System.err.printf("Wrong fixed file that is not source file: %s\n", fixedFileName);
			}
			break;
		case Property.ZXING:
			if (-1 != fixedFileName.lastIndexOf("com.google.zxing")) {
				fixedFileName = fixedFileName.substring(fixedFileName.lastIndexOf("com.google.zxing"), fixedFileName.length());
			} else {
				System.err.printf("Wrong fixed file that is not source file: %s\n", fixedFileName);
			}
			break;
		default:
			break;
		}
		
		return fixedFileName;
	}
	
	public int deleteAllIntegratedAnalysisInfos() {
		String sql = "DELETE FROM INT_ANALYSIS";
		int returnValue = INVALID;
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			
			returnValue = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnValue;
	}
	
	public HashMap<Integer, IntegratedAnalysisValue> getAnalysisValues(int bugID) {
		HashMap<Integer, IntegratedAnalysisValue> integratedAnalysisValues = null;
		IntegratedAnalysisValue resultValue = null;

//		String sql = "SELECT C.SF_NAME, B.VER, C.PROD_NAME, A.SF_VER_ID, A.VSM_SCORE, A.SIMI_SCORE, A.BL_SCORE, A.STRACE_SCORE, A.COMM_SCORE, A.BLIA_SCORE "+
//				"FROM INT_ANALYSIS A, SF_VER_INFO B, SF_INFO C " +
//				"WHERE A.BUG_ID = ? AND A.SF_VER_ID = B.SF_VER_ID AND B.SF_ID = C.SF_ID";

		String sql = "SELECT A.SF_VER_ID, A.VSM_SCORE, A.SIMI_SCORE, A.BL_SCORE, A.STRACE_SCORE, A.COMM_SCORE, A.BLIA_SCORE "+
				"FROM INT_ANALYSIS A " +
				"WHERE A.BUG_ID = ?";
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == integratedAnalysisValues) {
					integratedAnalysisValues = new HashMap<Integer, IntegratedAnalysisValue>();
				}
				
				resultValue = new IntegratedAnalysisValue();
				resultValue.setBugID(bugID);
//				resultValue.setFileName(rs.getString("SF_NAME"));
//				resultValue.setProductName(rs.getString("PROD_NAME"));
				resultValue.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				resultValue.setVsmScore(rs.getDouble("VSM_SCORE"));
				resultValue.setSimilarityScore(rs.getDouble("SIMI_SCORE"));
				resultValue.setBugLocatorScore(rs.getDouble("BL_SCORE"));
				resultValue.setStackTraceScore(rs.getDouble("STRACE_SCORE"));
				resultValue.setCommitLogScore(rs.getDouble("COMM_SCORE"));
				resultValue.setBLIAScore(rs.getDouble("BLIA_SCORE"));
				
				integratedAnalysisValues.put(resultValue.getSourceFileVersionID(), resultValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return integratedAnalysisValues;
	}
	
	public ArrayList<IntegratedAnalysisValue> getBugLocatorRankedValues(int bugID, int limit) {
		ArrayList<IntegratedAnalysisValue> bugLocatorRankedValues = null;
		IntegratedAnalysisValue resultValue = null;

		String sql = "SELECT C.SF_NAME, B.VER, C.PROD_NAME, A.SF_VER_ID, A.VSM_SCORE, A.SIMI_SCORE, A.BL_SCORE, A.STRACE_SCORE, A.BLIA_SCORE "+
				"FROM INT_ANALYSIS A, SF_VER_INFO B, SF_INFO C " +
				"WHERE A.BUG_ID = ? AND A.SF_VER_ID = B.SF_VER_ID AND B.SF_ID = C.SF_ID AND A.BL_SCORE != 0" +
				"ORDER BY A.BL_SCORE DESC ";
		
		if (limit != 0) {
			sql += "LIMIT " + limit;
		}
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == bugLocatorRankedValues) {
					bugLocatorRankedValues = new ArrayList<IntegratedAnalysisValue>();
				}
				
				resultValue = new IntegratedAnalysisValue();
				resultValue.setBugID(bugID);
				resultValue.setFileName(rs.getString("SF_NAME"));
				resultValue.setProductName(rs.getString("PROD_NAME"));
				resultValue.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				resultValue.setVsmScore(rs.getDouble("VSM_SCORE"));
				resultValue.setSimilarityScore(rs.getDouble("SIMI_SCORE"));
				resultValue.setBugLocatorScore(rs.getDouble("BL_SCORE"));
				resultValue.setStackTraceScore(rs.getDouble("STRACE_SCORE"));
				resultValue.setBLIAScore(rs.getDouble("BLIA_SCORE"));
				
				bugLocatorRankedValues.add(resultValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bugLocatorRankedValues;
	}
	
	public ArrayList<IntegratedAnalysisValue> getBLIARankedValues(int bugID, int limit) {
		ArrayList<IntegratedAnalysisValue> bliaRankedValues = null;
		IntegratedAnalysisValue resultValue = null;

//		String sql = "SELECT C.SF_NAME, B.VER, C.PROD_NAME, A.SF_VER_ID, A.VSM_SCORE, A.SIMI_SCORE, A.BL_SCORE, A.STRACE_SCORE, A.COMM_SCORE, A.BLIA_SCORE "+
//				"FROM INT_ANALYSIS A, SF_VER_INFO B, SF_INFO C " +
//				"WHERE A.BUG_ID = ? AND A.SF_VER_ID = B.SF_VER_ID AND B.SF_ID = C.SF_ID AND A.BLIA_SCORE != 0" +
//				"ORDER BY A.BLIA_SCORE DESC ";

		String sql = "SELECT A.SF_VER_ID, A.BLIA_SCORE "+
				"FROM INT_ANALYSIS A " +
				"WHERE A.BUG_ID = ? AND A.BLIA_SCORE != 0 " +
				"ORDER BY A.BLIA_SCORE DESC ";

		
		if (limit != 0) {
			sql += "LIMIT " + limit;
		}
		
		try {
			ps = analysisDbConnection.prepareStatement(sql);
			ps.setInt(1, bugID);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				if (null == bliaRankedValues) {
					bliaRankedValues = new ArrayList<IntegratedAnalysisValue>();
				}
				
				resultValue = new IntegratedAnalysisValue();
				resultValue.setBugID(bugID);
				resultValue.setSourceFileVersionID(rs.getInt("SF_VER_ID"));
				resultValue.setBLIAScore(rs.getDouble("BLIA_SCORE"));
				
				bliaRankedValues.add(resultValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bliaRankedValues;
	}
}
