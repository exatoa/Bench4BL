/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.common.BugCorpus;
import edu.skku.selab.blp.db.dao.BugDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;
import edu.skku.selab.blp.utils.Splitter;
import edu.skku.selab.blp.utils.Stem;
import edu.skku.selab.blp.utils.Stopword;

/**
 * Create each bug corpus from each bug report.  
 * 
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugCorpusCreator {
	private String stemContent(String content[]) {
		StringBuffer contentBuf = new StringBuffer();
		for (int i = 0; i < content.length; i++) {
			String word = content[i].toLowerCase();
			if (word.length() > 0) {
				String stemWord = Stem.stem(word);
				
				// debug code
//					System.out.printf("%d stemWord: %s\n", i, stemWord);
//					if (stemWord.contains("keys")) {
//						System.out.println("stemWord: " + stemWord);
//					}
				
				// Do NOT user Stopword.isKeyword() for BugCorpusCreator.
				// Because bug report is not source code.
				if (!Stopword.isEnglishStopword(stemWord) && !Stopword.isProjectKeyword(stemWord)) {
					contentBuf.append(stemWord);
					contentBuf.append(" ");
				}
			}
		}
		return contentBuf.toString();
	}
	
	/* (non-Javadoc)
	 * @see edu.skku.selab.blia.indexer.ICorpus#create()
	 */
	public void create(boolean stackTraceAnalysis) throws Exception {
		Property property = Property.getInstance();
		String productName = property.productName;
		ArrayList<Bug> list = parseXML(stackTraceAnalysis);
		property.bugReportCount = list.size();
		
		// To write bug corpus to file for compatibility
		String dirPath = (new StringBuilder(String.valueOf(property.WORK_DIR)))
				.append(property.separator)
				.append("BugCorpus")
				.append(property.separator).toString();
		File file = new File(dirPath);
		if (!file.exists())
			file.mkdirs();

		BugDAO bugDAO = new BugDAO();
		Bug bug;
		Iterator<Bug> bugIter = list.iterator();
		
		while (bugIter.hasNext()) {
			bug = (Bug) bugIter.next();
			bug.setProductName(productName);
			
			// test code
//			if (bug.getID().contains("92241")) {
//				System.out.println("BugID: " + bug.getID());
//			}
			
			BugCorpus bugCorpus = new BugCorpus();
			
			String content = (new StringBuilder(String.valueOf(bug.getSummary())))
					.append(" ").append(bug.getDescription()).toString();
//			String splitWords[] = Splitter.splitNatureLanguage(content);
			String splitWords[] = Splitter.splitNatureLanguageEx(content);
			String contentCorpus = stemContent(splitWords);
			bugCorpus.setContent(contentCorpus);
			// debug code
//			System.out.println("contentCorpus: " + contentCorpus);
			
//			String summaryPart = stemContent(Splitter.splitNatureLanguage(bug.getSummary()));
			String summaryPart = stemContent(Splitter.splitNatureLanguageEx(bug.getSummary()));
			bugCorpus.setSummaryPart(summaryPart);
			// debug code
//			System.out.println("summaryPart: " + summaryPart);
			
//			String descriptionPart = stemContent(Splitter.splitNatureLanguage(bug.getDescription()));
			String descriptionPart = stemContent(Splitter.splitNatureLanguageEx(bug.getDescription()));
			bugCorpus.setDescriptionPart(descriptionPart);
			// debug code
//			System.out.println("descriptionPart: " + descriptionPart);

			bug.setCorpus(bugCorpus);
			
			// To write bug corpus to file for compatibility
			FileWriter writer = new FileWriter((new StringBuilder(
					String.valueOf(dirPath))).append(bug.getID())
					.append(".txt").toString());
			writer.write(bugCorpus.getContent().trim());
			writer.flush();
			writer.close();

			// DO NOT insert corpus here~!
			// Creating BugCorpus willl be done at BugVectorCreator
//			String[] corpusArray = corpuses.toString().split(" ");
//			for (int i = 0; i < corpusArray.length; i++) {
//				bugDAO.insertCorpus(corpusArray[i], productName);
//			}
			
//			bugDAO.insertBug(bug);
			bugDAO.insertStructuredBug(bug);
			
			TreeSet<String> fixedFiles = bug.getFixedFiles();
			Iterator<String> fixedFilesIter = fixedFiles.iterator();
			while (fixedFilesIter.hasNext()) {
				String fixedFileName = (String) fixedFilesIter.next();
				bugDAO.insertBugFixedFileInfo(bug.getID(), fixedFileName, SourceFileDAO.DEFAULT_VERSION_STRING, productName);
			}
		}
	}
	
    public ArrayList<String> extractClassName(String content, int bugID) {
//        String pattern = "(([a-zA-Z0-9_\\-$]*\\.)*[a-zA-Z_<][a-zA-Z0-9_\\-$>]*\\(([a-zA-Z_][a-zA-Z0-9_\\-]*\\.java:[0-9]*|(?i)native method|(?i)unknown source)\\))";
        String pattern = "(([a-zA-Z0-9_\\-$]*\\.)*[a-zA-Z_<][a-zA-Z0-9_\\-$>]*" +
        		"[a-zA-Z_<(][a-zA-Z0-9_\\-$>);/\\[]*" +
        		"\\(([a-zA-Z_][a-zA-Z0-9_\\-]*\\.java:[0-9]*|[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java\\((?i)inlined compiled code\\)|[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java\\((?i)compiled code\\)|(?i)native method|(?i)unknown source)\\))";
        
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(content);
        ArrayList<String> stackTraceClasses = new ArrayList<String>();
        while (m.find()) {
        	String foundLine = m.group();
        	String methodName = foundLine.split("\\(")[0];
        	
        	String fileName = "";
        	if (methodName.contains("$")) {
        		fileName = methodName.substring(0, methodName.lastIndexOf("$"));
        	} else {
        		if (-1 == methodName.lastIndexOf(".")) {
        			System.err.printf("[BugCorpusCreator.extractClassName()] BugID: %d, Wrong stack trace: %s\n", bugID, foundLine);
        		} else {
        			fileName = methodName.substring(0, methodName.lastIndexOf("."));
        		}
        	}
        	
        	stackTraceClasses.add(fileName);
        }
        return stackTraceClasses;
    }
    
    public Date makeTime(String time){
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
    
	private ArrayList<Bug> parseXML(boolean stackTraceAnalysis) {
		ArrayList<Bug> list = new ArrayList<Bug>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		Property property = Property.getInstance();
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			InputStream is = new FileInputStream(property.bugFilePath);
			Document doc = domBuilder.parse(is);
			Element root = doc.getDocumentElement();
			NodeList bugRepository = root.getChildNodes();
			if (null != bugRepository) {
				for (int i = 0; i < bugRepository.getLength(); i++) {
					Node bugNode = bugRepository.item(i);
					if (bugNode.getNodeType() != 1) continue;
					
					int bugId = Integer.parseInt(bugNode.getAttributes().getNamedItem("id").getNodeValue());
					
					// debug code
//						if (bugId.contains("80830")) {
//							System.out.println("parseXML()> BugID: " + bugId);
//						}
					
					String openDateString = bugNode.getAttributes().getNamedItem("opendate").getNodeValue();
					String fixDateString = bugNode.getAttributes().getNamedItem("fixdate").getNodeValue();
					Bug bug = new Bug();
					bug.setID(bugId);
					bug.setOpenDate(makeTime(openDateString));//simpleDateFormat.parse(openDateString));
					bug.setFixedDate(makeTime(fixDateString));//simpleDateFormat.parse(fixDateString));
					for (Node node = bugNode.getFirstChild(); node != null; node = node.getNextSibling()) {
						if (node.getNodeType() == 1) {
							if (node.getNodeName().equals("buginformation")) {
								NodeList _l = node.getChildNodes();
								for (int j = 0; j < _l.getLength(); j++) {
									Node _n = _l.item(j);
									if (_n.getNodeName().equals("summary")) {
										String summary = _n.getTextContent();
										bug.setSummary(summary);
									}
									if (_n.getNodeName().equals("description")) {
										String content = _n.getTextContent();
										
										content = content.replace("&amp;", "&");
										content = content.replace("&quot;", "\"");
										content = content.replace("&lt;", "<");
										content = content.replace("&gt;", ">");
										
										// Extract class name before removement of HTML tag
										if (stackTraceAnalysis) {
											bug.setStackTraceClasses(extractClassName(content, bug.getID()));
										}
										
										// to remove HTML tag
			                        	String[] words = content.split("(?i)\\<[^\\>]*\\>");
			                        	String description = "";
			                        	for (int k = 0; k < words.length; k++) {
			                        		if (words[k].length() > 0) {
			                        			description += words[k];
			                        		}
			                        	}
			                        	description = description.trim();
										bug.setDescription(description);
									}
								}
							}
							if (node.getNodeName().equals("fixedFiles")) {
								NodeList _l = node.getChildNodes();
								for (int j = 0; j < _l.getLength(); j++) {
									Node _n = _l.item(j);
									if (_n.getNodeName().equals("file")) {
										String fileName = _n.getTextContent();
										
										String checkingString = "org.aspectj/modules/"; 
										if (fileName.contains(checkingString)) {
											fileName = fileName.substring(checkingString.length(), fileName.length());

											// debug code
//												System.out.printf("[BugCorpusCreator.parseXML()] BugID: %d, Fixed file name: %s\n", bug.getID(), fileName);
										}
										bug.addFixedFile(fileName);
									}
								}
							}
						}
					}
					
					// TODO: set version with default version because there is not affected version for the bug.
					bug.setVersion(SourceFileDAO.DEFAULT_VERSION_STRING);
					
					list.add(bug);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}
}
