package org.amalgam.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class downloadRepository_tomcat {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		int upb = 100;
		HashMap<String, ArrayList<Integer>> buggy_related_revision = new HashMap(); 
		String svn = "http://svn.apache.org/repos/asf/tomcat/tc6.0.x/trunk";
		//String svn = "http://svn.apache.org/repos/asf/tomcat/trunk";
		//String outbat= "W:\\research\\mutation test\\data_1\\lucene\\download.bat";
		String projectRoot = "D:\\Shaowei\\research\\mutation test\\data\\tomcat_6.x\\";
		String projectDiff = projectRoot.substring(0,projectRoot.length()-1)+"diff";
		
		if(!new File(projectDiff).isDirectory()){
			new File(projectDiff).mkdirs();
		}
		String logFile = projectRoot+"log.txt";
		
		// pattern for tomcat
		Pattern p = Pattern.compile("cgi\\?id=[0-9]+");
		//pattern for lucene
		//Pattern p = Pattern.compile("LUCENE-[0-9]+:");
		// load content_revision_map
		HashMap<String,String> content_revision_map = load_content_revision_map(logFile);
		// load real bug 
		String realbugFile = projectRoot + "realbug.csv";
		String mapFile = projectRoot+"map.txt";
		ArrayList<String> bugs = loadRealBug(realbugFile);
		int index =0;
		int file_number = 0;
		try {
			HashMap<String,Integer> bugId_revisionId_map =new HashMap();
			for(String content : content_revision_map.keySet()){
				
				   Matcher m = p.matcher(content);
				   if(m.find()){
				        String b =  m.group();
				        //b  = b.replace(":", "");
				        b = b.split("=")[1];
				        String revision_str = content_revision_map.get(content);
				        System.out.println(b);
				        if(revision_str ==null)
				        	continue;
				        int revision = Integer.parseInt(revision_str);
				        if(bugs.contains(b)){
				        	if(!bugId_revisionId_map.containsKey(b)){
				        		bugId_revisionId_map.put(b, revision);
				        		ArrayList<Integer> revisions = new ArrayList(); 
				        		revisions.add(revision);
				        		buggy_related_revision.put(b, revisions);
				        	}else{
				        		if (revision < bugId_revisionId_map.get(b))
				        			bugId_revisionId_map.put(b, revision);
				        		if(!buggy_related_revision.get(b).contains(revision)){
				        			buggy_related_revision.get(b).add(revision);
				        		}
				        	}
				        }
				    
				   }
			
			}
			// sort the revision in increase order and output
			BufferedWriter map = new BufferedWriter(new FileWriter(mapFile)); 
			map.write("bugId,revision\n");
			for(String bug_id : buggy_related_revision.keySet()){
				ArrayList<Integer> revisions = buggy_related_revision.get(bug_id);
				Collections.sort(revisions);
				map.write(bug_id+",");
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i< revisions.size(); i++){
					sb.append(revisions.get(i)+",");
				}
				String str = sb.toString().substring(0,sb.toString().length()-1);
				map.write(str+"\n");
				
			}
			map.close();
			
			
			String outbat= projectRoot + "download"+file_number+".bat";
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(outbat));
			
			// output bug fix related revision
			
			
			
			
			// output download bat
			for(String bugId: buggy_related_revision.keySet()){
				index ++;
				// get the post-fix revision
				ArrayList<Integer> revisionlist= buggy_related_revision.get(bugId);
				int post_fix = revisionlist.get(revisionlist.size()-1);
				
	        	bw.write("svn checkout -r " + post_fix +" "+svn+" " +post_fix+"\n");
	        	if(index == upb){
	        		index=0;
	        		bw.close();
	        		file_number++;
	        		outbat= projectRoot + "download"+file_number+".bat";
	        		bw = new BufferedWriter(new FileWriter(outbat));
	        	}
	        	
	        	
			}
			//output diff
			
			String diffbat = projectRoot+"diff.bat";
			BufferedWriter bw_diffbat_whole = new BufferedWriter(new FileWriter(diffbat));
			
			for(String key: buggy_related_revision.keySet()){
				
				ArrayList<Integer> revisionlist= buggy_related_revision.get(key);
				int post_fix = revisionlist.get(revisionlist.size()-1);
				//mk post revision folder
				String path = projectDiff + "/" +post_fix;
				if (!new File(path).isDirectory())
					new File(path).mkdirs();
				BufferedWriter bw_diffbat = new BufferedWriter(new FileWriter(path+"/diff.bat"));
				
				for(int i = 0 ; i< revisionlist.size();i++){
					int post = revisionlist.get(i);
					int pre = post-1;
					String str = "svn diff -r "+ post +":" + pre + " " + svn+"> " + i +"\n";
					bw_diffbat.write(str);
				}
				String cmd = "type ";
				for(int i = 0 ; i< revisionlist.size();i++){
					cmd = cmd + i+" ";
				}
				cmd = cmd +">total.diff\n";
				bw_diffbat.write(cmd);
				bw_diffbat.close();
				bw_diffbat_whole.write("cd \""+path+"\"\n");
				bw_diffbat_whole.write("call \""+path+"/diff.bat\"\n");
			}
			bw_diffbat_whole.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private static ArrayList<String> loadRealBug(String realbugFile) {
		// TODO Auto-generated method stub
		ArrayList<String> bugs = new ArrayList();
		try {
			String content = readContent(realbugFile);
			String[] lines = content.split("\n");
			for(int i =1; i < lines.length; i++){
				String[] strs = lines[i].split(",");
				if(strs[1].equals("BUG")){
					bugs.add(strs[0]);
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bugs;
	}

	private static HashMap<String,String> load_content_revision_map(String logFile){
		HashMap<String,String> content_revision_map = new HashMap();
		
		// load content and revision map
		try {
			String content = readContent(logFile);
			String pattern = "------------------------------------------------------------------------\n";
			String[] revision_blocks = content.split(pattern);
			for(int i =0; i< revision_blocks.length; i++){
				if(revision_blocks[i].trim().isEmpty())
					continue;
				addToHash(revision_blocks[i],content_revision_map);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content_revision_map;
	}
	private static void addToHash(String block, HashMap<String,String> map) {
		// TODO Auto-generated method stub
		String[] lines = block.split("\n");
		String firstLine = lines[0];
		int index = firstLine.indexOf("|");
		String revision = firstLine.substring(1,index-1);
		map.put(block, revision);
	}


	public static String readContent(String file) throws IOException{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while((line = br.readLine()) != null){
			sb.append(line.replace("\r", "") + "\n");
			
		}
		return sb.toString();
	}
}
