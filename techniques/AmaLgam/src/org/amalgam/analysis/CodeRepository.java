package org.amalgam.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.amalgam.common.Property;
import org.amalgam.common.Utils;
import org.amalgam.models.Bug;
import org.amalgam.models.CommitItem;
import org.amalgam.models.FileObjs;
import org.apache.commons.io.FileUtils;

public class CodeRepository {
	private final String srcReporitory = Property.getInstance().SourceCodeRepo;
	private final String separator = Property.getInstance().Separator;
	private final String commit_split = "---------------------"; //\n";
	private final String regexStr = "(.*bug.*)|(.*fix.*)";

	
	public static ArrayList<CommitItem> commits = null;
	
	/**
	 * ������
	 */
	public CodeRepository(){}
	
	/**
	 * git repository�κ��� commit���� �ε�.
	 */
	public boolean loadCommits(){
	
		
		String output = Property.getInstance().WorkDir + this.separator + "log.txt";
		
//		File logFile = new File(output);
//		if (!logFile.exists()){
		// this is for Windows OS
//		String command = "cmd /c git log --name-status --pretty=format:\"---------------------%nhash:%h%nauthor:%an%ncommit_date:%ci%nmessage:%s%n\" > \""+ output +"\""

		String[] command = {
			"/bin/sh", 
			"-c",
			"git log --name-status --pretty=format:\"---------------------%nhash:%h%nauthor:%an%ncommit_date:%ci%nmessage:%s%n\" > \""+ output +"\""
		};
		
		if (!Utils.execute(command, srcReporitory))
			return false;
//		}
		
		if (!this.parseGitLogFile(output))
			return false;
		return true;	
	}
	
	/**
	 * ������ �α������� �Ľ�
	 * @param logFilePath
	 * @return
	 */
	public boolean parseGitLogFile(String logFilePath)
	{
		boolean flag = true;
		
		commits = new ArrayList<CommitItem>();		
		
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(logFilePath));
			while (true)
			{
				CommitItem item = this.getOneCommitLog(reader);
				if (item == null)	break;
				if (item.hash =="") continue;
				commits.add(item);
			}
			reader.close();
		}
		catch (IOException | ParseException e){
			e.printStackTrace();
			flag = false;
		}
		finally{
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}
		}

		return flag;
	}


	/**
	 * commit log ���Ϸ� ���� �� ���� commit item�� �о��.
	 * @param reader
	 * @param repo_path
	 * @return
	 * @throws IOException
	 * @throws ParseException 
	 */
	private CommitItem getOneCommitLog(BufferedReader reader) throws IOException, ParseException
	{		
		boolean endFlag = false;
		
		CommitItem item = new CommitItem();

		String line = null;
		while (true) 
		{			
			if((line = reader.readLine()) == null){
				endFlag = true;
				break;
			}
				
			//����ó��
			line = line.trim();
			if (line.equals("")) continue;						//������ ��� ������������.
			if (line.compareTo(commit_split)==0) break;	//�и��� ó�� (�����ϸ� �������� �Ѿ)
			
			
			// get commit date
			if (line.startsWith("commit_date:")) {
				String dateStr = line.replace("commit_date:", "").replace(" +0000", "");
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				item.commitDate = formatter.parse(dateStr);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(item.commitDate);
				calendar.add(Calendar.DAY_OF_YEAR, -1);
				item.commitDate = calendar.getTime();
				continue;
			}
			
			//hash
			if (line.startsWith("hash:")) {
				item.hash = line.replace("hash:", "");
				continue;
			}

			//comment
			if (line.startsWith("message:")) {
				item.comment = line.replace("message:", "");
				continue;
			}
			
			// get committed fixed
			if (line.startsWith("M\t") && line.endsWith(".java")) {
				String path = srcReporitory + line.replace("M\t", "");
				item.files.add(path);
				continue;
			}
		}		
		
		//���� ���� üũ.
		if (endFlag==true && item.hash == "")	return null;
		
		//���װ� �ƴ� ��쿡 �����ϵ��� ��.
		if (!isBug(item.comment))	item.hash = "";
		
		return item;
	}

	
	/**
	 * �Էµ� comment�� bug������ ���� comment���� Ȯ�� 
	 * @param comment : �Է� comment
	 * @param regex : ���׸� �Ǵ��� ���Խ�.
	 * @return
	 */
	private boolean isBug(String comment) {
		// TODO Auto-generated method stub
		if (comment.matches(regexStr)) {
			return true;
		}
		return false;
	}

	
	/**
	 * �Էµ� ������ FullClassName����
	 * ���� ���� ������ packageName + FileName���� ����
	 * (���Ͽ� �Ѱ��� Ŭ������ �־�� �ǰٴµ�....)
	 * Deprecated
	 * @param path
	 * @return
	 */		
	private static String getFullClassName(String path) {
		// TODO Auto-generated method stub
		try {
			if (new File(path).exists()) {
				String content = FileUtils.readFileToString(new File(path));
				String[] lines = content.split("\n");
				String packageName = null;
				for (int i = 0; i < lines.length; i++) {
					String line = lines[i].trim();
					if (line.startsWith("package ") && line.endsWith(";")) {
						packageName = line.replace("package ", "").replace(";", "").trim();
						break;
					}
				}
				int index = path.lastIndexOf("/");
				String className = path.substring(index + 1);
				String fullClassName = packageName + "." + className;
				return fullClassName;
			} else {
				int index_org = path.lastIndexOf("/org/");
				String fullClassName = path.substring(index_org + 1).replace("/", ".");
				return fullClassName;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	


	
	/**
	 * ������ git �α� ���Ͽ��� ������ �����Ͽ�
	 * �� ���� Ŭ���� ���� ������ ��¥ ���� ����
	 * (�Ѱ��� logs���Ͽ� ���ؼ� ó�������ϵ��� ����. by Zeck)
	 * @param logFilePath �ε��� �����͸� ��� �ִ� �α�����
	 * @param bugs
	 * @return
	 * @throws IOException 
	 */
	public HashMap<Integer, HashSet<CommitItem>> loadFileCommitHistory() {
		
		HashMap<Integer, HashSet<CommitItem>> fileHistories = new HashMap<Integer, HashSet<CommitItem>>();
		
		for (CommitItem item : commits){
		
			String fullClassName;
			
			// get full class name of java file
			for (int j = 0; j < item.files.size(); j++) {
				
				//get Full Class Name
				fullClassName = Utils.getUniqueClassName(item.files.get(j));
				int fid = FileObjs.put(fullClassName);
				
				if (!fileHistories.containsKey(fid)) {
					HashSet<CommitItem> histories = new HashSet<CommitItem>();
					histories.add(item);
					
					fileHistories.put(fid, histories);
				} else {
					fileHistories.get(fid).add(item); //.addCommit(item.hash, item.commitDate);
				}
			}
		}
		return fileHistories;
	}
	
	/**
	 * Log�����κ��� commit Date ������ ����.  (Hash, Date)
	 * @return
	 */
	public HashMap<String, Date> getCommitDates() {
		HashMap<String, Date> dates = new HashMap<String, Date>();		
		for (CommitItem item : commits){
			dates.put(item.hash, item.commitDate);
		}
		return dates;
	}

	/**
	 * commit log ������ �м��Ͽ� bugObjs�� commit date�� �Է�
	 * @param bugObjs : ��� bugs
	 */
	public void getCommitDateOfBugObj(HashMap<String, Bug> bugObjs) 
	{
		// ��� Ŀ�Կ� ���ؼ�
		for (CommitItem item : commits)	
		{	
			// check if message contain the bugid
			int count = 0;
			int curIndex = 10000;
			String currentBugid = null;
			
			//��� ���׵��� ������� Ȯ��. (comment ������ bugID�� ���ԵǾ��ִ��� Ȯ��)
			for (Bug bug : bugObjs.values()) 	//���׾��̵���� �������
			{
				//�� Ŀ�Կ� ���� ���� ���̵� �����ϴ� ���, ���� �տ� ������ ���� ���̵� Ŀ���� ���׾��̵�� ����
				if (item.comment.contains(bug.ID + " ")) {
					int index = item.comment.indexOf(bug.ID);
					if (index < curIndex) {	
						currentBugid = bug.ID;
						curIndex = index;
					}
					count++;
				}
			}
			if (currentBugid != null) {
				bugObjs.get(currentBugid).commitDate = item.commitDate;
			}
			if (count > 1) {
				System.out.println(currentBugid + "is related with "+ count + " other bugs.");
			}

		}
	}//method
	
	

}