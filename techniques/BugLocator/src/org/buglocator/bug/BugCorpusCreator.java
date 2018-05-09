package org.buglocator.bug;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.buglocator.property.Property;
import org.buglocator.utils.Splitter;
import org.buglocator.utils.Stem;
import org.buglocator.utils.Stopword;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
//import java.util.Comparator;

public class BugCorpusCreator {
	
	private final String workDir = Property.getInstance().WorkDir;
	private final String pathSeperator = Property.getInstance().Separator;
	private final String lineSeperator = Property.getInstance().LineSeparator;

	
//	class BugComparator implements Comparator<Bug>{
//		@Override
//		public int compare(Bug a, Bug b) {
//			if(a.getOpenDate().after(b.getOpenDate()))
//				return 1;
//			else if(a.getOpenDate().before(b.getOpenDate()))
//				return -1;
//			else 
//				return 0;
//			
//		}
//	}
	/**
	 * 시작함수
	 * @throws IOException
	 */
	public void create() throws IOException {		
		//Create Temp Directory 
		String dirPath = workDir + this.pathSeperator + "BugCorpus" + this.pathSeperator;
		File dirObj = new File(dirPath);
		if (!dirObj.exists())
			dirObj.mkdirs();

		//Create Corpus and Sort
		ArrayList<Bug> list = this.parseXML();
		//list.sort(new BugComparator());		// fixed date 로 정렬되어 있음
		
		//Corpus Store
		Property.getInstance().BugReportCount = list.size();
		for (Bug bug : list) {
			writeCorpus(bug, dirPath);
		}
		
		//summarize corpus information.
		FileWriter writer = new FileWriter(this.workDir + this.pathSeperator + "SortedId.txt");
		FileWriter writerFix = new FileWriter(this.workDir + this.pathSeperator + "FixLink.txt");

		for (Bug bug : list) {
			//XML의 bug리스트는 fixed_date로 정렬되어있어서 안해 도됨
			writer.write(bug.getBugId() + "\t" + bug.getFixDate() + this.lineSeperator);
			writer.flush();
			
			for (String fixName : bug.set) {
				writerFix.write(bug.getBugId() + "\t" + fixName + this.lineSeperator);
				writerFix.flush();
			}
		}
		writer.close();
		writerFix.close();
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
	
	
	/**
	 * 지정된 버그파일(XML)로 정보 로드.
	 * XML파일은 여러개의 버그리포트가 하나로 정리된 파일을 말함. 
	 * @return
	 */
	private ArrayList<Bug> parseXML() {
		ArrayList<Bug> list = new ArrayList<Bug>();
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			InputStream is = new FileInputStream(Property.getInstance().BugFilePath);
			Document doc = domBuilder.parse(is);
			Element root = doc.getDocumentElement();
			NodeList bugRepository = root.getChildNodes();
			if (bugRepository == null)
				return list;

			for (int i = 0; i < bugRepository.getLength(); i++) {
				Node bugNode = bugRepository.item(i);
				if (bugNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				String bugId = bugNode.getAttributes().getNamedItem("id").getNodeValue();
				String openDate = bugNode.getAttributes().getNamedItem("opendate").getNodeValue();
				String fixDate = bugNode.getAttributes().getNamedItem("fixdate").getNodeValue();
				
				
				Bug bug = new Bug();
				bug.setBugId(bugId);
				bug.setOpenDate(makeTime(openDate));
				bug.setFixDate(makeTime(fixDate));

				for (Node node = bugNode.getFirstChild(); node != null; node = node.getNextSibling()) {
					if (node.getNodeType() != Node.ELEMENT_NODE)
						continue;

					if (node.getNodeName().equals("buginformation")) {
						NodeList _l = node.getChildNodes();
						for (int j = 0; j < _l.getLength(); j++) {
							Node _n = _l.item(j);
							if (_n.getNodeName().equals("summary")) {
								String summary = _n.getTextContent();
								bug.setBugSummary(summary);
							}

							if (_n.getNodeName().equals("description")) {
								String description = _n.getTextContent();
								bug.setBugDescription(description);
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
				list.add(bug);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	/**
	 * 최종 corpus를 파일에 기록
	 * 
	 * @param bug
	 * @param storeDir
	 * @throws IOException
	 */
	private void writeCorpus(Bug bug, String storeDir) throws IOException {

		//split words from bug content (summary + description)
		String content = bug.getBugSummary() + " " + bug.getBugDescription();
		String[] splitWords = Splitter.splitNatureLanguage(content);
		
		// concatenate words in bug
		StringBuffer corpus = new StringBuffer();
		for (String word : splitWords) {
			word = Stem.stem(word.toLowerCase());
			if (!Stopword.isEnglishStopword(word)) {
				corpus.append(word + " ");
			}
		}
		
		//save corpus.
		FileWriter writer = new FileWriter(storeDir + bug.getBugId() + ".txt");
		writer.write(corpus.toString().trim());
		writer.flush();
		writer.close();

	}

}
