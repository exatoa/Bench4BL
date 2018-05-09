package utils;


public class HgHelp {	
	
	public static String getCommitByRevision(String revision, String workingPath) 
		throws Exception {
		ExecCommand executor = new ExecCommand();
		String result = executor.exec("hg log -v -p -r " + revision, workingPath);
		return result;
	}
	
		
}
