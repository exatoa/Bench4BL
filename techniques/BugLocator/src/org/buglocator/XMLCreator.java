package org.buglocator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XMLCreator {
	public static void main(String[] args) throws Exception {
		
		
		String bugPath = "D:\\works\\Bug\\_data_src\\swt_bugreports";
		String fixedFilePath = "F:\\swt\\FixLink.txt";  //?????
		String outputPath = "D:\\works\\Bug\\_data_sec\\SWTBugRepository.xml";
		
		XMLCreator creator = new XMLCreator();
		creator.buildXML(bugPath, fixedFilePath, outputPath);
	}
	
	public void buildXML(String _bugPath, String _fixedFilePath, String _outputPath) throws Exception {
		Hashtable<String, TreeSet<String>> fixTable = this.getFixFileSet(_fixedFilePath);
		Bug[] bugs = this.loadBugInfo(_bugPath, fixTable);
		Document doc = this.concatenateBugs(bugs);
		this.writeXML(doc, _outputPath);	
	}

	/**
	 * 
	 * @param _fixedFile
	 * @return
	 * @throws IOException
	 */
	public Hashtable<String, TreeSet<String>> getFixFileSet(String _fixedFile) throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(_fixedFile));
		String line = null;
		Hashtable<String, TreeSet<String>> fixTable = new Hashtable<String, TreeSet<String>>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			if (fixTable.containsKey(values[0])) {
				fixTable.get(values[0]).add(values[1]);
			} else {
				TreeSet<String> set = new TreeSet<String>();
				set.add(values[1]);
				fixTable.put(values[0], set);
			}
		}
		reader.close();
		return fixTable;
	}
	
	

	/**
	 * 지정된 폴더에서 버그리포트 정보를 추출 (xml로 저장된 원번 버그리포트 파일)
	 * 버그질라 버그리포트만 가능.
	 * @return
	 * @throws Exception
	 */
	public Bug[] loadBugInfo(String _bugPath, Hashtable<String, TreeSet<String>> _fixedTable) throws Exception {
		File file = new File(_bugPath);
		File[] files = file.listFiles();
		Bug[] list = new Bug[files.length];
		int bugCount = 0;
		
		BufferedReader reader = null;
		for (File f : files) {
			reader = new BufferedReader(new FileReader(f));
			Bug bug = new Bug();

			String idLine = reader.readLine();
			idLine = idLine.substring(idLine.indexOf("\t")).trim();
			bug.bugId = idLine;

			String openDateLine = reader.readLine();
			openDateLine = openDateLine.substring(openDateLine.indexOf("\t")).trim();
			bug.openDate = openDateLine.trim();

			String fixDateLine = reader.readLine();
			fixDateLine = fixDateLine.substring(fixDateLine.indexOf("\t"));
			bug.fixDate = fixDateLine.trim();

			String bugSummaryLine = reader.readLine();
			bugSummaryLine = bugSummaryLine.substring(bugSummaryLine.indexOf("\t")).trim();
			bug.bugSummary = bugSummaryLine;

			String bugDescriptionLine = reader.readLine();
			bugDescriptionLine = bugDescriptionLine.substring(bugDescriptionLine.indexOf("\t")).trim();
			bug.bugDescription = bugDescriptionLine;

			bug.set = _fixedTable.get(idLine);
			if (bug.set != null)
				list[bugCount++] = bug;
		}
		System.out.println(bugCount);
		reader.close();
		
		sortbyDate(list);
		return list;
	}
	

	
	/**
	 * 버그리포트 정보를 기반으로 XML document 생성.
	 * @param bugs
	 * @return
	 */
	public Document concatenateBugs(Bug[] bugs){
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("bugrepository");
		
		for (Bug bug : bugs) {
			Element bugElement = root.addElement("bug");
			bugElement.addAttribute("id", bug.bugId);
			bugElement.addAttribute("opendate", bug.openDate.replace("/", "-"));
			bugElement.addAttribute("fixdate", bug.fixDate.replace("/", "-"));

			Element bugInfoElement = bugElement.addElement("buginformation");
			Element summary = bugInfoElement.addElement("summary");
			summary.setText(bug.bugSummary);
			Element description = bugInfoElement.addElement("description");
			description.setText(bug.bugDescription);
			Element bugFixElement = bugElement.addElement("fixedFiles");
			for (String name : bug.set) {
				Element fileElement = bugFixElement.addElement("file");
				fileElement.setText(name);
			}
		}
		return document;
	}

	/**
	 * 생성된 XML데이터를 파일에 기록
	 * @param _xml
	 * @param _outputPath
	 */
	public void writeXML(Document _xml, String _outputPath)
	{
		//XML Write
		XMLWriter output;
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("ISO-8859-1");
		try {
			output = new XMLWriter(new FileWriter(_outputPath), format);
			output.write(_xml);
			output.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	

	/**
	 * Bug list를 date를 기중으로 quick sort함. (오름차순 정렬)
	 * @param list
	 * @throws Exception
	 */
	private void sortbyDate(Bug[] list) throws Exception {
		this.quickSort(list, 0, list.length - 1);
	}

	private void quickSort(Bug[] list, int p, int r) throws Exception {
		if (p < r) {
			int m = partition(list, p, r);
			quickSort(list, p, m - 1);
			quickSort(list, m + 1, r);
		}
	}

	private int partition(Bug[] list, int p, int r) throws ParseException {
		Bug pivot = list[r];
		Date pivotDate = getDateFromString(pivot.fixDate);
		int i = p - 1;
		for (int j = p; j < r; j++) {
			Date compareDate = getDateFromString(list[j].fixDate);
			if (compareDate.compareTo(pivotDate) < 0) {
				i++;
				swap(list, i, j);
			}
		}
		swap(list, i + 1, r);
		return i + 1;
	}

	private void swap(Bug[] list, int i, int j) {
		Bug tmp = list[i];
		list[i] = list[j];
		list[j] = tmp;

	}

	private Date getDateFromString(String dateString) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date splitDate = dateFormat.parse(dateString.replace("/", "-"));
		return splitDate;
	}

	private Date getDateFromLongType(String timeSpan) {
		long span = Long.parseLong(timeSpan);
		Date date = new Date(span);
		return date;
	}


}