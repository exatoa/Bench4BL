package org.brtracer.bug;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.brtracer.property.Property;
import org.brtracer.utils.Splitter;
import org.brtracer.utils.Stem;
import org.brtracer.utils.Stopword;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BugCorpusCreator {

	private final String workDir = Property.getInstance().WorkDir;
	private final String pathSeperator = Property.getInstance().Separator;
	private final String lineSeperator = Property.getInstance().LineSeparator;

	public void create() throws IOException {

		ArrayList<Bug> list = new BugCorpusCreator().parseXML();

		String dirPath = workDir + this.pathSeperator + "BugCorpus" + this.pathSeperator;
		File file = new File(dirPath);
		Property.getInstance().BugReportCount = list.size();
		if (!file.exists())
			file.mkdirs();

		for (Bug bug : list) {
			writeCorpus(bug, dirPath);
		}
		FileWriter writer = new FileWriter(workDir + this.pathSeperator + "SortedId.txt");
		FileWriter writerFix = new FileWriter(workDir + this.pathSeperator + "FixLink.txt");
		FileWriter writerClassName = new FileWriter(workDir + this.pathSeperator + "DescriptionClassName.txt");

		for (Bug bug : list) {
			writer.write(bug.getBugId() + "\t" + bug.getFixDate() + this.lineSeperator);

			writer.flush();
			for (String fixName : bug.getSet()) {
				writerFix.write(bug.getBugId() + "\t" + fixName + this.lineSeperator);
				writerFix.flush();
			}
			String classnames = extractClassName(bug.getBugDescription());
			writerClassName.write(bug.getBugId() + "\t" + classnames + this.lineSeperator);
		}
		writerClassName.close();
		writer.close();
		writerFix.close();
	}

	/**
	 * 지정된 버그파일(XML)로 부터 버그ID-corpus 집합을 얻음 XML파일은 여러개의 버그리포트가 하나로 정리된 파일을 말함.
	 * 
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
				bug.setOpenDate(openDate);
				bug.setFixDate(fixDate);

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

		String content = bug.getBugSummary() + " " + bug.getBugDescription();
		String[] splitWords = Splitter.splitNatureLanguage(content);
		StringBuffer corpus = new StringBuffer();
		for (String word : splitWords) {
			word = Stem.stem(word.toLowerCase());
			if (!Stopword.isEnglishStopword(word)) {
				corpus.append(word + " ");
			}
		}
		FileWriter writer = new FileWriter(storeDir + bug.getBugId() + ".txt");
		writer.write(corpus.toString().trim());
		writer.flush();
		writer.close();
	}

	public String extractClassName(String content) {

		String pattern = "[a-zA-Z_][a-zA-Z0-9_\\-]*\\.java";
		StringBuffer res = new StringBuffer();

		// Create a Pattern object
		Pattern r = Pattern.compile(pattern);

		// Create matcher object.
		Matcher m = r.matcher(content);
		while (m.find()) {
			res.append(m.group(0) + " ");
		}
		return res.toString();
	}

}
