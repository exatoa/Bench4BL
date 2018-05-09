/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.utils.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.skku.selab.blp.common.Bug;
import edu.skku.selab.blp.db.dao.SourceFileDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class BugRepositoryUtil {
	final static private String ORIGINAL_BUG_REPO_XML_OF_BRTRACER = ".\\data\\AspectJBugRepository.xml";
	final static private String FIXED_BUG_REPO_XML_OF_BRTRACER = ".\\data\\FixedAspectJBugRepository.xml";
	final static private String ORIGINAL_BUG_REPO_XML_OF_IBUGS = ".\\data\\repository.xml";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BugRepositoryUtil bugRepositoryUtil = new BugRepositoryUtil();

		ArrayList<Bug> bugsOfBRTracer = bugRepositoryUtil.parseXMLOfBRTracer();
		ArrayList<Bug> bugsOfIbugs = bugRepositoryUtil.parseXMLOfIbugs();
		HashMap<Integer, Bug> bugIDs = new HashMap<Integer, Bug>();
		
		for (int i = 0; i < bugsOfIbugs.size(); i++) {
			bugIDs.put(bugsOfIbugs.get(i).getID(), bugsOfIbugs.get(i));
		}
		
		for (int i = 0; i < bugsOfBRTracer.size(); i++) {
			int bugID = bugsOfBRTracer.get(i).getID();
			Bug foundBug = bugIDs.get(bugID); 
			if (null != foundBug) {
//				String oldDescription = bugsOfBRTracer.get(i).getDescription(); 
				bugsOfBRTracer.get(i).setDescription(foundBug.getDescription());
				
//				System.out.printf("Old: %s\n", oldDescription);
//				System.out.printf("New: %s\n", bugsOfBRTracer.get(i).getDescription());
			}
		}
		
		bugRepositoryUtil.writeXML(bugsOfBRTracer);
	}
	
	private ArrayList<Bug> parseXMLOfBRTracer() {
		ArrayList<Bug> list = new ArrayList<Bug>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			InputStream is = new FileInputStream(ORIGINAL_BUG_REPO_XML_OF_BRTRACER);
			Document doc = domBuilder.parse(is);
			Element root = doc.getDocumentElement();
			NodeList bugRepository = root.getChildNodes();
			if (null != bugRepository) {
				for (int i = 0; i < bugRepository.getLength(); i++) {
					Node bugNode = bugRepository.item(i);
					if (bugNode.getNodeType() == 1) {
						int bugId = Integer.parseInt(bugNode.getAttributes().getNamedItem("id").getNodeValue());
						String openDateString = bugNode.getAttributes().getNamedItem("opendate").getNodeValue();
						String fixDateString = bugNode.getAttributes().getNamedItem("fixdate").getNodeValue();
						Bug bug = new Bug();
						bug.setID(bugId);
						bug.setOpenDate(openDateString);
						bug.setFixedDateStringNotModified(fixDateString);
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
											String description = _n.getTextContent();
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
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}
	
	private ArrayList<Bug> parseXMLOfIbugs() {
		ArrayList<Bug> list = new ArrayList<Bug>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			InputStream is = new FileInputStream(ORIGINAL_BUG_REPO_XML_OF_IBUGS);
			Document doc = domBuilder.parse(is);
			Element root = doc.getDocumentElement();
			NodeList bugRepository = root.getChildNodes();
			if (null != bugRepository) {
				for (int i = 0; i < bugRepository.getLength(); i++) {
					Node bugNode = bugRepository.item(i);
					if (bugNode.getNodeType() == 1) {
						int bugId = Integer.parseInt(bugNode.getAttributes().getNamedItem("id").getNodeValue());

						Bug bug = new Bug();
						bug.setID(bugId);
						for (Node node = bugNode.getFirstChild(); node != null; node = node.getNextSibling()) {
							if (node.getNodeType() == 1) {
								if (node.getNodeName().equals("bugreport")) {
									String description = node.getTextContent();
									bug.setDescription(description);
								}
							}
						}
						
						list.add(bug);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	private void writeXML(ArrayList<Bug> bugs) {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			Document doc = domBuilder.newDocument();
			
			Element root = doc.createElement("bugrepository");
			root.setAttribute("name", "AspectJ");
			doc.appendChild(root);

			
			for (int i = 0; i < bugs.size(); i++) {
				Bug bug = bugs.get(i);
				Element bugElement = doc.createElement("bug");
				bugElement.setAttribute("id", Integer.toString(bug.getID()));
				bugElement.setAttribute("opendate", bug.getOpenDateString());
				bugElement.setAttribute("fixdate", bug.getFixedDateStringNotModified());
				{
					Element bugInfoElement = doc.createElement("buginformation");
					bugElement.appendChild(bugInfoElement);
					
					Element summaryElement = doc.createElement("summary");
					summaryElement.appendChild(doc.createTextNode(bug.getSummary()));
					bugInfoElement.appendChild(summaryElement);

					Element descriptionElement = doc.createElement("description");
					descriptionElement.appendChild(doc.createTextNode(bug.getDescription()));
					bugInfoElement.appendChild(descriptionElement);
				}
				{
					Element fixedFilesElement = doc.createElement("fixedFiles");
					bugElement.appendChild(fixedFilesElement);

					Iterator<String> fixedFiles = bug.getFixedFiles().iterator();
					while (fixedFiles.hasNext()) {
						String fixedFile = fixedFiles.next();
						Element fileElement = doc.createElement("file");
						fileElement.appendChild(doc.createTextNode(fixedFile));
						fixedFilesElement.appendChild(fileElement);
					}
				}
				root.appendChild(bugElement);
			}
			
			doc.setXmlStandalone(true);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(FIXED_BUG_REPO_XML_OF_BRTRACER));
			// Output to console for testing
//			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
