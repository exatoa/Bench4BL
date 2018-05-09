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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class CommitInfo {
	public final static int ADD_COMMIT = 1;
	public final static int COPY_COMMIT = 2;
	public final static int DELETE_COMMIT = 3;
	public final static int MODIFY_COMMIT = 4;
	public final static int RENAME_COMMIT = 5;
	
	private String commitID;
	private String productName;
    private Date commitDate;
    private String message;
    private String committer;
    private HashMap<Integer, HashSet<String>> commitFilesMap;

	/**
	 * 
	 */
	public CommitInfo() {
		commitID = "";
		commitDate = new Date(System.currentTimeMillis());
		message = "";
		committer = "";
		commitFilesMap = new HashMap<Integer, HashSet<String>>();
	}

	/**
	 * @return the commitID
	 */
	public String getCommitID() {
		return commitID;
	}

	/**
	 * @param commitID the commitID to set
	 */
	public void setCommitID(String commitID) {
		this.commitID = commitID;
	}

	/**
	 * @return the commitDateString
	 */
	public String getCommitDateString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(commitDate);
	}

	/**
	 * @param commitDateString the commitDateString to set
	 */
	public void setCommitDate(String commitDateString) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			this.commitDate = simpleDateFormat.parse(commitDateString);			
		} catch (Exception e) {
			this.commitDate = null;
			e.printStackTrace();
		}		
	}

	/**
	 * @return the checkedInDate
	 */
	public Date getCommitDate() {
		return commitDate;
	}

	/**
	 * @param commitDate the commitDate to set
	 */
	public void setCommitDate(Date commitDate) {
		this.commitDate = commitDate;
	}

	/**
	 * @return the description
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the description to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the sourceFileName
	 */
	public HashSet<String> getCommitFiles(int commitType) {
		return commitFilesMap.get(commitType);
	}

	/**
	 * 
	 * @return
	 */
	public HashMap<Integer, HashSet<String>> getAllCommitFiles() {
		return commitFilesMap;
	}
	
	public HashSet<String> getAllCommitFilesWithoutCommitType() {
		HashSet<String> allCommitFiles = new HashSet<String>();
		
		Iterator<Integer> iter = commitFilesMap.keySet().iterator();
		while (iter.hasNext()) {
			HashSet<String> commitFiles = commitFilesMap.get(iter.next());
			
			Iterator<String> iterFileName = commitFiles.iterator();
			while (iterFileName.hasNext()) {
				String fileName = iterFileName.next();
				allCommitFiles.add(fileName);
			}
		}
		return allCommitFiles;
	}

	/**
	 * @param commitFiles the commitFiles to set
	 */
	public void setCommitFiles(Integer commitType, HashSet<String> commitFiles) {
		this.commitFilesMap.put(commitType, commitFiles);
	}
	
	/**
	 * @param commitFiles the commitFiles to set
	 */
	public void setCommitFiles(HashMap<Integer, HashSet<String>> allCommitFiles) {
		this.commitFilesMap = allCommitFiles;
	}
	
	public void addCommitFile(Integer commitType, String fileName) {
		HashSet<String> commitFiles = this.commitFilesMap.get(commitType);
		if (null == commitFiles) {
			commitFiles = new HashSet<String>();
			this.commitFilesMap.put(commitType, commitFiles);
		}
		commitFiles.add(fileName);
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
	 * @return the committer
	 */
	public String getCommitter() {
		return committer;
	}

	/**
	 * @param committer the committer to set
	 */
	public void setCommitter(String committer) {
		this.committer = committer;
	}

}
