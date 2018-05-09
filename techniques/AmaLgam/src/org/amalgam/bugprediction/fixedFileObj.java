package org.amalgam.bugprediction;

import java.util.Date;
import java.util.HashMap;

/**
 * fixedFile의 시간 정보를 담는 객체
 */
public class fixedFileObj {
	private String name;
	private HashMap<String, Date> bugId_CommitDate;
	
	public fixedFileObj(String name){
		this.name = name;
		this.bugId_CommitDate = new HashMap();
	}

	public void addCommit(String bugId, Date bugFixedDate) {
		// TODO Auto-generated method stub
		this.bugId_CommitDate.put(bugId,bugFixedDate);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Date> getBugId_CommitDate() {
		return bugId_CommitDate;
	}

	public void setBugId_CommitDate(HashMap<String, Date> bugId_CommitDate) {
		this.bugId_CommitDate = bugId_CommitDate;
	}
	
}
