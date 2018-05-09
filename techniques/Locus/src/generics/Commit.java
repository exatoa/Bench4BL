package generics;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Commit {

	public String changeSet;
	public String userName;
	public String userEmail;
	public String description;
	public Date commitTime;
	public boolean isFiltered;
	public List<String> files;
	public List<Integer> fixedBugs;
	public List<Patch> patches;
	
	
	public Commit(String changeSet, String userName, String userEmail, Date commitTime, String description) {
		this.changeSet = changeSet;
		this.userName = userName;
		this.userEmail = (String) userEmail;
		this.commitTime = commitTime;
		this.description = description;
		patches = new ArrayList<Patch>();
		files = new ArrayList<String>();
		fixedBugs = new ArrayList<Integer>();
		isFiltered = false;
	}
	
	public void addFile(String file) {
		files.add(file);
//		System.out.println(files.size());
	}
	
	public boolean containsBug(String pro, int bid) {
		if ((description.contains("back") && description.contains("out")) || 
				(description.contains("Back") && description.contains("out"))) return false;
		String bugId = new Integer(bid).toString();
		if (pro.equals("Flink")) {
			if (description.contains("FLINK-" + bid)) {
//				System.out.println(description);
				return true;
			}
			else return false;
		}
		else if (description.contains(bugId)) {
			int index = description.indexOf(bugId);
			if (index - 1 >= 0 && Character.isDigit(description.charAt(index - 1))) return false;
			if (index + bugId.length() < description.length() && Character.isDigit(description.charAt(index + bugId.length()))) return false;
			fixedBugs.add(bid);
			return true;
		}
		else return false;
	}
	
	public void addBug(int bug) {
		fixedBugs.add(bug);
	}
	
	private boolean isValid(String type) {
//		for (int i = 0; i < validType.length; i++) 
//			if (type.equals(validType[i])) return true;
//		System.out.println(type); 
//		System.out.println(type.equals(".java"));
		if (type.equals(".java")) return true;
		return false;
	}

	/**
	 * 
	 * @return return related source files
	 */
	
	public List<String> getSourceFiles() {
//		if (!isFiltered) {
//			filterSourceFile();
//			isFiltered = true;
//		}
		return files;
	}
	
	
	/**
	 * This function is used to filter out source code related to testing and those invalid file types  
	 * 
	 */
	public void filterSourceFile() {
//		System.out.println(files.size());
		List<String> filteredFiles = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++) {
			// 2018-01-19 :: removed for including test files.
			//if (!files.get(i).contains("test")) {
				if (files.get(i).lastIndexOf(".") > 0) {
					String type = files.get(i).substring(files.get(i).lastIndexOf("."));
//					System.out.println(type);
					if (isValid(type)) 
						filteredFiles.add(files.get(i));
				}
			//}
		}
		files.clear();
		files = filteredFiles;
	}
	
	public void addPatch(Patch patch) {
		patches.add(patch);
	}
	
	public void extractHunks() {
		for (int i = 0; i < patches.size(); i++)
			patches.get(i).extractHunks();
	}
	
	public List<Hunk> getAllHunks() {
		extractHunks();
		List<Hunk> hunks = new ArrayList<Hunk>();
		for (Patch patch : patches) {
			hunks.addAll(patch.hunks);
		}
		return hunks;
	}
	
	public List<String> getFileTypes() {
		List<String> types = new ArrayList<String>();
		for (int i = 0; i < files.size(); i++)
			if (files.get(i).lastIndexOf(".") > 0) {
				String type = files.get(i).substring(files.get(i).lastIndexOf("."));
				if (isValid(type)) 
					types.add(type);
			}
		return types;
	}
	
	public String toString() {
		String line = changeSet + "\n";
		line += commitTime + "\n";
		for (int i = 0; i < files.size(); i++)
			line += files.get(i) + "\n";
		line += description + "\n";
		return line;
	}
	
	public static void main(String[] args) {
		Commit commit = new Commit(" "," "," ",null,"git-svn-id: https://svn.apache.org/repos/asf/tomcat/trunk@57752 1");
		System.out.println(commit.containsBug("Tomcat",57752));
	}
	
}
