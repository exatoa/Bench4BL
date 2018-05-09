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
	 * 생성자
	 */
	public CodeRepository(){}
	
	/**
	 * git repository로부터 commit들을 로드.
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
	 * 생성된 로그파일을 파싱
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
	 * commit log 파일로 부터 한 개의 commit item을 읽어옴.
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
				
			//예외처리
			line = line.trim();
			if (line.equals("")) continue;						//공백인 경우 다음라인으로.
			if (line.compareTo(commit_split)==0) break;	//분리자 처리 (등장하면 다음으로 넘어감)
			
			
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
		
		//죄종 종료 체크.
		if (endFlag==true && item.hash == "")	return null;
		
		//버그가 아닌 경우에 무시하도록 함.
		if (!isBug(item.comment))	item.hash = "";
		
		return item;
	}

	
	/**
	 * 입력된 comment가 bug수정에 대한 comment인지 확인 
	 * @param comment : 입력 comment
	 * @param regex : 버그를 판단할 정규식.
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
	 * 입력된 파일의 FullClassName추출
	 * 파일 내에 지정된 packageName + FileName으로 생성
	 * (파일에 한개의 클래스만 있어야 되겟는데....)
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
	 * 가공된 git 로그 파일에서 정보를 추출하여
	 * 각 파일 클래스 별로 수정된 날짜 정보 수집
	 * (한개의 logs파일에 대해서 처리가능하도록 수정. by Zeck)
	 * @param logFilePath 로드할 데이터를 담고 있는 로그파일
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
				
				if (!fileHistories.containsKey(fullClassName)) {
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
	 * Log정보로부터 commit Date 정보들 얻음.  (Hash, Date)
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
	 * commit log 정보를 분석하여 bugObjs에 commit date를 입력
	 * @param bugObjs : 대상 bugs
	 */
	public void getCommitDateOfBugObj(HashMap<String, Bug> bugObjs) 
	{
		// 모든 커밋에 대해서
		for (CommitItem item : commits)	
		{	
			// check if message contain the bugid
			int count = 0;
			int curIndex = 10000;
			String currentBugid = null;
			
			//모든 버그들을 대상으로 확인. (comment 정보에 bugID가 포함되어있는지 확인)
			for (Bug bug : bugObjs.values()) 	//버그아이디들을 대상으로
			{
				//한 커밋에 여러 버그 아이디가 존재하는 경우, 가장 앞에 등장한 버그 아이디를 커밋의 버그아이디로 결정
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