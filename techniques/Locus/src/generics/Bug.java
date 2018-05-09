package generics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Bug {
	
	public int id;
	public String status;
	public String product;
	public String version;
	public String summary;
	public String description;
	public String platform;
	public String component;
	public String crashSignature;
	public List<String> comments;
	public long reportTime;
	public long modifyTime;
	public List<String> buggyFiles;
	
	
	public Bug(File file) {
		comments = new ArrayList<String>();
		buggyFiles = new ArrayList<String>();
		
		BufferedReader br;
		String dateFormat = "EEE MMM dd HH:mm:ss z yyyy";
		try {
			br = new BufferedReader(new FileReader(file));
			String line = file.getName();
			String[] segs;
			this.id = Integer.parseInt(line.substring(0,line.indexOf(".")));
			line = br.readLine();
			segs = line.split("\t");
			reportTime = new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(segs[1]).getTime();
			modifyTime = new SimpleDateFormat(dateFormat, Locale.ENGLISH).parse(segs[2]).getTime();
			line = br.readLine();
			segs = line.split("\t");
			description = segs[1];
			line = br.readLine();
			segs = line.split("\t");
			status = segs[1];
			line = br.readLine();
			segs = line.split("\t");
			product = segs[1];
			line = br.readLine();
			segs = line.split("\t");
			version = segs[1];
			line = br.readLine();
			segs = line.split("\t");
			platform = segs[1];
			line = br.readLine();
			segs = line.split("\t");
			component = segs[1];
			line = br.readLine();
			segs = line.split("\t");
			if (segs.length > 1) crashSignature = segs[1];
			int num = Integer.parseInt(br.readLine());
//			System.out.println(num);			
			for (int i = 0; i < num; i++) {
				addComment(br.readLine());
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Bug(int id, String summary,String description,String status, String product, String version, String platform, String component, String crashSignature, long reportTime, long modifyTime) {
		this.id = id;
		this.status = status;
		this.summary = summary;
		this.product = product;
		this.version = version;
		this.component = component;
		this.platform = platform;
		this.crashSignature = crashSignature;
		this.reportTime = reportTime;
		this.modifyTime = modifyTime;
		this.description = description;
		comments = new ArrayList<String>();
		buggyFiles = new ArrayList<String>();
	}
	
	public Bug(int id) {
		this.id = id;
		comments = new ArrayList<String>();
		buggyFiles = new ArrayList<String>();
	}
	
	public void addComment(String comment) {
		comments.add(comment);
	}
	
	public void addFiles(List<String> files) {
		for (int i = 0; i < files.size(); i++)
			addFile(files.get(i));
	}
	
	public void addFile(String file) {

		if (file.endsWith(".java")) 
			buggyFiles.add(file);
	}
	
	public void removeFile(String file) {
		buggyFiles.remove(file);
	}
//	public boolean isFixed() {
//		for (int i = 0; i < resolvedTags.length; i++)
//			if (status.equals(resolvedTags[i])) return true;
//		return false;
//	}
	
	public void modifyStatus(String s) {
		status = s;
	}
	
	public void setFiles(List<String> files) {
		buggyFiles.clear();
		for (String s : files) {
			buggyFiles.add(s);
		}
	}
	
	public boolean containsMultipleFiles() {
		return (buggyFiles.size() > 1);
	}
	

	
	public String toStringSimple() {
		String line;
		//Date d1 = new Date(reportTime);
		//Date d2 = new Date(modifyTime);
		line = id + "\t" + (modifyTime/1000 - reportTime/1000);
		return line;
	}
	
	
	public String containsKeywords(String[] keywords) {
		for (String comment : comments) {
			boolean flag = true;
			for (String keyword : keywords) {
				if (!comment.contains(keyword)) {
					flag = false;
					break;
				} 
			}
			if (flag) return comment;
		}
		return null;
	}
	
	public String toStringFull() {
		String line;
		Date d1 = new Date(reportTime);
		Date d2 = new Date(modifyTime);
		line = id + "\t" + d1 + "\t" + d2 + "\n";
		line += "description:\t" + description + "\n";
		line += "status:\t" + status + "\n";
		line += "product:\t" + product + "\n";
		line += "version:\t" + version + "\n";
		line += "platform:\t" + platform + "\n";
		line += "component:\t" + component + "\n";
		line += "crashSignature:\t" + crashSignature + "\n";
		line += comments.size() + "\n";
		for (int i = 0; i < comments.size(); i++) {
			line += comments.get(i) + "\n";
		}
		return line;
	}
	
	public String toString() {
		String content = this.summary + "\n" + this.description;
		return content;
	}
}

