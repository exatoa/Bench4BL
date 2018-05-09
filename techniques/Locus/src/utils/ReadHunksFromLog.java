package utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import utils.FileToLines;
import generics.Commit;
import generics.Hunk;
import generics.Patch;

public class ReadHunksFromLog {
	
	public static Commit readOneCommitWithHunkGit(String file) {
		Commit commit = null;
		
		//Variables for commit
		String changeSet = "";
		String authorName = "";
		String authorEmail = "";
		Date date = null;
		String description = "";
		String line;
		
		// Variables for patch
		String preFile = null;
		String postFile = null;
		String command = null;
		Patch patch = null;
		List<String> content = new ArrayList<String>();
//		System.out.println(file);
        List<String> rawCommit = FileToLines.fileToLines(file);
		int index = 0;
		try {
			boolean isContent = false;
			line = rawCommit.get(index++);
			while (!line.startsWith("commit"))
				line = rawCommit.get(index++);
			String[] split = line.split(":");
			changeSet = split[split.length - 1].trim();
			
			line = rawCommit.get(index++);
			while (!line.startsWith("Author"))
				line = rawCommit.get(index++);
			
		
			int nameStart = line.indexOf(":") + 1;
			int nameEnd = line.indexOf("<");
			int emailEnd = line.indexOf(">");
			if (nameEnd == -1)
				nameEnd = line.length();
			if (emailEnd == -1)
				emailEnd = line.length();
			
			authorName = line.substring(nameStart, nameEnd).trim();
			authorEmail = line.substring(nameEnd == line.length() ? nameEnd : nameEnd + 1, emailEnd);
			
			line = rawCommit.get(index++);
			
			while (!line.startsWith("Date"))
				line = rawCommit.get(index++);
			
			date = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH).parse(line.substring(line.indexOf(":") + 1).trim());
			

			description = "";
			while (index < rawCommit.size() && line != null && !line.startsWith("diff")) {
				line = rawCommit.get(index);
				if (!line.startsWith("diff"))
					description += "\n" + line;
				index++;
				if (index >= rawCommit.size()) break;
			}
			description.trim();
			commit = new Commit(changeSet,authorName,authorEmail,date,description);
			while (line != null) {
				if (line.startsWith("diff")) {
					if ( patch != null) {
						patch.addContent(content);
						commit.addPatch(patch);
						content.clear();
						isContent = false;
					}
					command = line;
				} else if (line.startsWith("---")) {
//					System.out.println(line);
					try {
						preFile = line.substring(4);
						if (preFile.contains("\t"))
							preFile = preFile.split("\t")[0].trim();
					} catch (IndexOutOfBoundsException e) {
						//System.out.println("Exception\t" + line);
					}
				} else if (line.startsWith("+++")) {
					try {
						postFile = line.substring(4);
						if (postFile.contains("\t"))
							postFile = postFile.split("\t")[0].trim();
					} catch (IndexOutOfBoundsException e) {
						//System.out.println("Exception\t" + line);
					}
					patch = new Patch(command,preFile,postFile);
//					System.out.println(patch);
					isContent = true;
				} else if (line.startsWith("index")) {
					String[] tmp = line.split(" ");
//					System.out.println(tmp[1]);
//					if (tmp[1].contains("..")) System.out.println(true);
					String[] tmp2 = tmp[1].split("\\.\\.");
//					System.out.println(tmp2[1]);
					String preIndex = tmp2[0];
					String postIndex = tmp2[1];
					tmp = command.split(" ");
					command = "diff -r " + preIndex + " -r " + postIndex + " " + tmp[2];	
				}
				else {
					if (isContent) content.add(line);
				}
				if (index >= rawCommit.size()) break;
				line = rawCommit.get(index++);
//				System.out.println(size + "\t" + index);
			}
			if (command != null) {
				patch = new Patch(command,preFile,postFile);
				patch.addContent(content);
				commit.addPatch(patch);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return commit;
	}
	
	public static void main(String[] args) {
		Commit commit = readOneCommitWithHunkGit("fff70558a434b6bceb64b4669f5657cf58e69d73.txt");
		List<Hunk> hunks = commit.getAllHunks();
		System.out.println(hunks.size());
		for (Hunk hunk : hunks) {
			System.out.println(hunk.preChangeSet + "\t" + hunk.postChangeSet);
		}
	}
}
