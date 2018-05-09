package org.amalgam.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.amalgam.bugprediction.fixedFileObj;
import org.amalgam.common.Property;
import org.amalgam.common.Utils;
import org.amalgam.models.Bug;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BugReportParser {
	private final static String bugPATH = Property.getInstance().BugFilePath;
	
	public static Date makeTime(String time){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
		Date date = null;
		try{
			date = formatter.parse(time);
		}
		catch(Exception e){
			long ltime = Long.parseLong(time);
			date = new Date(ltime);
		}

	    return date;
	}
	
	/**
	 * bug report가 저장된 XML파일에서 FixedFile 목록 가져오기.
	 * 버그리포트 별로 FixedFile을 추출
	 * @param xmlFile
	 * @return
	 */
	public static HashMap<String, Bug> loadFixedFileFromXML(HashMap<String, Bug> bugObjs) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(bugPATH));
			NodeList nodeList = document.getElementsByTagName("bug");
			
			//for each bug 
			for (int x = 0, size = nodeList.getLength(); x < size; x++) 
			{
				Element node = (Element) nodeList.item(x);
				String bugID = node.getAttributes().getNamedItem("id").getNodeValue();
				String bugFixeDate_str = node.getAttributes().getNamedItem("fixdate").getNodeValue();
				Date bugFixedDate = makeTime(bugFixeDate_str);
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//				Date bugFixedDate = formatter.parse(bugFixeDate_str);
				bugObjs.get(bugID).commitDate = bugFixedDate;

				// get fixed location
				NodeList fixedFiles = node.getElementsByTagName("fixedFiles");

				for (int j = 0; j < fixedFiles.getLength(); j++) 
				{
					NodeList fixFiles = ((Element) fixedFiles.item(j)).getElementsByTagName("file");
					
					for (int i = 0; i < fixFiles.getLength(); i++) 
					{
						Element el = (Element) fixFiles.item(i);
						if (el == null) continue;
						
						String filePath = el.getTextContent();

						if (filePath.endsWith(".java")) {
							String fixedFile = Utils.getUniqueClassName(filePath);
							if(bugObjs.containsKey(bugID)){
								bugObjs.get(bugID).addLink(fixedFile);
							}else{
								Bug b = new Bug(bugID);
								b.addLink(fixedFile);
								bugObjs.put(bugID, b);
							}
						}//if (filePath.endsWith(".java"))

					} //for i
				}//for j // each fixedfiles

			}// for each bug

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bugObjs;
	}
	
	/**
	 * FixedLink.txt로 부터 버그리포트 정보를 가져옴.  
	 * @param bugobjs
	 */
	static public void loadFixedFiles(HashMap<String, Bug> bugobjs){
		try {
			BufferedReader br = new BufferedReader(new FileReader(bugPATH));
			String line = null;
			while((line = br.readLine())!=null){
				if(line.trim().equals(""))
					continue;
				String[] spart = line.split("\t");
				String bugId = spart[0];
				String fixedFile = spart[1];
				fixedFile = Utils.getUniqueClassName(fixedFile);
				if(bugobjs.containsKey(bugId)){
					bugobjs.get(bugId).addLink(fixedFile);
				}else{
					Bug b = new Bug(bugId);
					b.addLink(fixedFile);
					bugobjs.put(bugId, b);
				}
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 */
	public static HashMap<String, Bug> loadBugReports() {
		HashMap<String, Bug> bugObjs = new HashMap<String, Bug>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new File(bugPATH));
			NodeList nodeList = document.getElementsByTagName("bug");
			
			//for each bug 
			for (int x = 0, size = nodeList.getLength(); x < size; x++) 
			{
				Element node = (Element) nodeList.item(x);
				String bugID = node.getAttributes().getNamedItem("id").getNodeValue();
				String bugFixeDate_str = node.getAttributes().getNamedItem("fixdate").getNodeValue();
				
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//				Date bugFixedDate = formatter.parse(bugFixeDate_str);
				Date bugFixedDate = makeTime(bugFixeDate_str);
				
				
				Bug item = new Bug(bugID);
				item.commitDate = bugFixedDate;

				// get fixed location
				NodeList fixedFiles = node.getElementsByTagName("fixedFiles");

				for (int j = 0; j < fixedFiles.getLength(); j++) 
				{
					NodeList fixFiles = ((Element) fixedFiles.item(j)).getElementsByTagName("file");
					
					for (int i = 0; i < fixFiles.getLength(); i++) 
					{
						Element el = (Element) fixFiles.item(i);
						if (el == null) continue;
						
						String filePath = el.getTextContent();

						if (filePath.endsWith(".java")) {
							String fixedFile = Utils.getUniqueClassName(filePath);
							item.addLink(fixedFile);
						}//if (filePath.endsWith(".java"))

					} //for i
				}//for j // each fixedfiles
				
				bugObjs.put(bugID, item);
			}// for each bug

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bugObjs;
	}
	

}
