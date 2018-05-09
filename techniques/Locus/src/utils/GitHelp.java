package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import generics.Commit;

public class GitHelp {
	public static String getAllCommitOneLine(String repo) 
		throws Exception{
		ExecCommand executor = new ExecCommand();
		String result = executor.exec("git log --pretty=format:%h%x09%an%x09%ad%x09%s",repo);
		return result;
	} 
	
	public static String getAllCommitWithFullDescription(String repo) 
		throws Exception {
		ExecCommand executor = new ExecCommand();
		String result = executor.exec("git log", repo);
		return result;
	}
	
	public static String getAllCommitWithChangedFiles(String repo) 
		throws Exception {
		ExecCommand executor = new ExecCommand();
		String result = executor.exec("git log --name-only --oneline", repo);
		return result;
	}
	
	public static String gitShow(String hash, String repo) 
			throws Exception{
		ExecCommand executor = new ExecCommand();
		String result = executor.execOneThread("git show " + hash, repo);
		return result;
	}
	
	public static String gitAnnotate(String file, String rev, String repo) 
			throws Exception {
		ExecCommand executor = new ExecCommand();
//		System.out.println("git annotate " + file + " " + rev + "^");
//		file = file.replace(" ", "\\ ");
		String[] annotate = {"git","annotate",file,rev + "^"};
		
//		System.out.println("git annotate " + file + " " + rev + "^");
//		String result = executor.execOneThread("git annotate " + file + " " + rev + "^", repo);
		String result = executor.execOneThread(annotate, repo);
		return result;
	}
	
	public static String gitAnnotateDeletedFile(String file, String rev, String repo) 
			throws Exception {
		
		ExecCommand executor = new ExecCommand();
		ExecCommand executor1 = new ExecCommand();
		ExecCommand executor2 = new ExecCommand();
		// create the file
		String path = file.substring(0, file.lastIndexOf("/"));
		String[] touches = {"touch",file};
		String[] annotate = {"git","annotate",file,rev + "^"};
		String[] removes = {"rm",file};
		String[] mkdirs = {"mkdir","-p",path};
//		System.out.println("touch " + file);
//		System.out.println("git annotate " + file + " " + rev + "^");
//		System.out.println("rm " + file);
		
//		file = file.replace(" ", "\\ ");
		
		ExecCommand mkdir = new ExecCommand();
		mkdir.execOneThread(mkdirs, repo);
//		mkdir.execOneThread("mkdir -p " + path, repo);
//		String status = executor1.execOneThread("touch " + file, repo);
		String status = executor1.execOneThread(touches, repo);
		if (status == null) {
			System.err.println("The specified path is not exits!");
			return null;
		}
		// annotate the source file
//		String result = executor.execOneThread("git annotate " + file + " " + rev + "^", repo);
		String result = executor.execOneThread(annotate, repo);
		// delete the file
//		executor2.execOneThread("rm " + file, repo);
		executor2.execOneThread(removes, repo);
		return result;
	}
	
	public static List<Commit> readFromTextGIT(String filename) {
		String hashId;
		String authorName;
		String authorEmail;
		Date date;
		String description;
		String line;
		List<Commit> commits = new ArrayList<Commit>();
		try {
			BufferedReader bw = new BufferedReader(new FileReader(new File(filename)));
			line = bw.readLine();
			while ( line != null) {
				hashId = line.substring(7);
				line = bw.readLine();
				if (!line.startsWith("Author")) line = bw.readLine();	
				int nameStart = line.indexOf(":") + 1;
				int nameEnd = line.indexOf("<");
				int emailEnd = line.indexOf(">");
				authorName = line.substring(nameStart, nameEnd).trim();
				authorEmail = line.substring(nameEnd + 1,emailEnd);
				line = bw.readLine();
				date = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH).parse(line.substring(line.indexOf(":") + 1).trim());
				line = bw.readLine();
				description = line;
				while (line != null && !line.startsWith("commit")) {
					description += "\n" + line;
					line = bw.readLine();
				}
				description.trim();
				Commit commit = new Commit(hashId,authorName,authorEmail,date,description);
				commits.add(commit);
			}
//			System.out.println(commits.size());
//			System.out.println(commits.get(0));
			bw.close();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return commits;
		
	}
}
