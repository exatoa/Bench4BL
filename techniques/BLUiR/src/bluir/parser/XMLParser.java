package bluir.parser;

import bluir.entity.BugReport;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParser
{
	public HashMap<String, BugReport> createRepositoryMap(String XMLPath)
	{
		HashMap<String, BugReport> bugRepository = new HashMap();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		

		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			

			Document doc = db.parse(XMLPath);
			
			doc.getDocumentElement().normalize();
			
			NodeList bugList = doc.getElementsByTagName("bug");
			

			for (int bugNumber = 0; bugNumber < bugList.getLength(); bugNumber++) {
				BugReport bugReport = new BugReport();
				Node bug = bugList.item(bugNumber);
				String bugId = bug.getAttributes().getNamedItem("id").getFirstChild().getNodeValue();
				bugReport.setBugId(bugId);
				
				Element bugInformation = (Element)bug.getChildNodes().item(1);
				String summary = getTagValue("summary", bugInformation);
				bugReport.setSummary(summary);
				String description = getTagValue("description", bugInformation);
				bugReport.setDescription(description);
				
				Element fileNode = (Element)bug.getChildNodes().item(3);
				
				NodeList fileNodeList = fileNode.getElementsByTagName("file");
				
				Set<String> files = new HashSet();
				
				for (int j = 0; j < fileNodeList.getLength(); j++) {
					String fileName = fileNodeList.item(j).getChildNodes().item(0).getNodeValue();
					
					fileName = fileName.replaceAll("/", ".");
					

					files.add(fileName);
				}
				bugReport.setFixedFiles(files);
				bugRepository.put(bugId, bugReport);
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return bugRepository;
	}
	
	public List<BugReport> createRepositoryList(String XMLPath) {
		List<BugReport> bugRepository = new ArrayList();
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		

		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			

			Document doc = db.parse(XMLPath);
			
			doc.getDocumentElement().normalize();
			
			NodeList bugList = doc.getElementsByTagName("bug");
			

			for (int bugNumber = 0; bugNumber < bugList.getLength(); bugNumber++) {
				BugReport bugReport = new BugReport();
				Node bug = bugList.item(bugNumber);
				String bugId = bug.getAttributes().getNamedItem("id").getFirstChild().getNodeValue();
				bugReport.setBugId(bugId);
				
				Element bugInformation = (Element)bug.getChildNodes().item(1);
				String summary = getTagValue("summary", bugInformation);
				bugReport.setSummary(summary);
				String description = getTagValue("description", bugInformation);
				bugReport.setDescription(description);
				
				Element fileNode = (Element)bug.getChildNodes().item(3);
				
				NodeList fileNodeList = fileNode.getElementsByTagName("file");
				
				Set<String> files = new HashSet();
				
				for (int j = 0; j < fileNodeList.getLength(); j++) {
					String fileName = fileNodeList.item(j).getChildNodes().item(0).getNodeValue();
					fileName = fileName.replaceAll("/", ".");
					files.add(fileName);
				}
				bugReport.setFixedFiles(files);
				bugRepository.add(bugReport);
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return bugRepository;
	}
	
	public static void main(String[] args) throws IOException {
		XMLParser parser = new XMLParser();
		HashMap<String, BugReport> bugRepo = parser.createRepositoryMap("path/to/bug/repo/EclipseBugRepository.xml");
		
		int i = 1;
		
		FileWriter fw = new FileWriter("path/to/result/directoryqrels");
		BufferedWriter bw = new BufferedWriter(fw);
		
//		Iterator<String> it;
//		for (Iterator localIterator = bugRepo.entrySet().iterator(); localIterator.hasNext(); it.hasNext())
//		{
//			Map.Entry<String, BugReport> entry = (Map.Entry)localIterator.next();
//			String key = (String)entry.getKey();
//			BugReport bugReport = (BugReport)bugRepo.get(key);
//			Set<String> fixedFiles = bugReport.getFixedFiles();
//			
//			it = fixedFiles.iterator();
//			
//			continue;
//			bw.write(key + " 0 " + (String)it.next() + " 1");
//			bw.newLine();
//		}
		for(Entry<String, BugReport> entry : bugRepo.entrySet()) {
			String key = entry.getKey();
		    BugReport bugReport = entry.getValue();
		    Set<String> fixedFiles = bugReport.getFixedFiles();
		    
		    for (String file : fixedFiles){
			    bw.write(key + " 0 " + file + " 1");
				bw.newLine();
		    }
		}

		bw.close();
	}
	

	private static String getTagValue(String sTag, Element eElement)
	{
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		
		Node nValue = nlList.item(0);
		String str = "";
		if (nValue != null) {
			str = nValue.getNodeValue();
		}
		


		return str;
	}
}
