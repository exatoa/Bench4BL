package utils;

import generics.Bug;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import utils.FileToLines;
import utils.ReadFileToList;


public class ReadBugsFromXML {

	/**
	 * This method is used to read a list of Bugs from a file formated with XML
	 * @param loc
	 * @param pro
	 * @param version
	 * @return
	 */
	
	public static List<Bug> getFixedBugsFromXML(String bugFile) {
		List<Bug> fixedBugs = new ArrayList<Bug>();
		String repo = "";
		try {
			String content = ReadFileToList.readFiles(bugFile);
//			List<String> lines = FileToLines.fileToLines(proLoc);
//			for (String line : lines) {
//				repo += line + "\n";
//			}
			Document dom = DocumentHelper.parseText(content);
			Element rootElt = dom.getRootElement();
			List<Element> bugEle = rootElt.elements("bug");
			for (Element bug : bugEle) {
				int bid = Integer.parseInt(bug.attributeValue("id"));
				String openDate = bug.attributeValue("opendate");
				String fixDate = bug.attributeValue("fixdate");
				long time = 0;
				long time2 = 0;
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					time = Long.parseLong(openDate);
				} catch (NumberFormatException e){
					time = formatter.parse(openDate).getTime();
				}
				try {
					time2 = Long.parseLong(fixDate);
				} catch (NumberFormatException e) {
					time2 = formatter.parse(fixDate).getTime();
				}
				Element bugInfo = bug.element("buginformation");
				String summary = bugInfo.elementText("summary");
				String description = bugInfo.elementText("description");				
				Bug oneBug = new Bug(bid,summary,description,"Fixed","","","","","",time,time2);
				List<Element> files = bug.element("fixedFiles").elements("file");
				for (Element file : files) {
					String fileName = file.getStringValue();
//					if (fileName.contains("test") || fileName.contains("Test") || fileName.contains("/docs/")) continue;
					if (fileName.contains("/"))	fileName = fileName.replace("/", ".");
					oneBug.addFile(fileName);
				}
				
				fixedBugs.add(oneBug);
				//System.out.println(openDate.toString() + "\t" + fixDate.toString())
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return fixedBugs;
	}
	
}
