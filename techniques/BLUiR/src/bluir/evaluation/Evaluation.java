package bluir.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import bluir.core.Property;

public class Evaluation {
	private final String bugFilePath = Property.getInstance().BugFilePath;
	private final String outputFilePath = Property.getInstance().OutputFile;
	private final String lineSparator = Property.getInstance().LineSeparator;
	private final String indriQueryResult = Property.getInstance().WorkDir + "indriQueryResult";
	private String recommendedPath =  Property.getInstance().WorkDir + Property.getInstance().Separator+ "recommended" +Property.getInstance().Separator;

	private Hashtable<Integer, TreeSet<String>> fixedTable;
	private Hashtable<String, Integer> idTable;
	private Hashtable<Integer, String> nameTable;

	public Evaluation()
	{
		fixedTable = getFixedFileTable();
		
		idTable = new Hashtable<String, Integer>();
		nameTable = new Hashtable<Integer, String>();
	}

	/**
	 * query 결과파일을 로드하여 정답과 대조
	 * @return
	 * @throws IOException
	 */
	public boolean evaluate() throws IOException
	{
		//결과파일 로드
		Hashtable<Integer, Hashtable<Integer, Rank>> results = getResultTable();
	
		//출력파일 준비
		FileWriter outputWriter = new FileWriter(this.outputFilePath);
		File resultDir = new File(recommendedPath);
		if (!resultDir.exists()) 
			resultDir.mkdirs();
		
		//각 버그리포트에 대해서,....
		Set<Integer> bugIDS = results.keySet();
		for (Integer bugID : bugIDS)
		{ 
			//추천결과 정보 로드
			Hashtable<Integer, Rank> recommends = results.get(bugID);
			
			ArrayList<Rank> recommendsList = new ArrayList<Rank>(recommends.values());
			recommendsList.sort((Rank o1, Rank o2)->o1.rank-o2.rank);	// order of rank in ASC
			
			//추천결과 출력
			FileWriter writer = new FileWriter(recommendedPath + bugID + ".txt");
			for (Rank rank : recommendsList) {
				if(nameTable.containsKey(rank.fileID)) {
					writer.write(rank.rank  + "\t" +rank.score + "\t" + nameTable.get(rank.fileID) + this.lineSparator);
				}
			}
			writer.close();
			
			//정답파일이 존재하는지 확인.
			TreeSet<String> fileSet = fixedTable.get(bugID);
			for(String fileName : fileSet)
			{
				if (!idTable.containsKey(fileName)) continue;
				int fileID = idTable.get(fileName);
				
				if (!recommends.containsKey(fileID)) continue;
				Rank rank = recommends.get(fileID);
				
				
				outputWriter.write(bugID + "\t" + fileName + "\t" + rank.rank + "\t" + rank.score + this.lineSparator);
				outputWriter.flush();
			}
		}
		outputWriter.close();
		
		return true;
	}

	
	/**
	 * Indri에서 추천된 결과를 로드.
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	private Hashtable<Integer, Hashtable<Integer, Rank>> getResultTable() throws NumberFormatException, IOException {
		String line = null;
		int fileIndex = 0;
				
		Hashtable<Integer, Hashtable<Integer, Rank>> table = new Hashtable<Integer, Hashtable<Integer, Rank>>();

		long count=0;
		BufferedReader reader = new BufferedReader(new FileReader(this.indriQueryResult));
		while ((line = reader.readLine()) != null) {
			count++;
			if (line.matches("[0-9]+ Q0 [$a-zA-Z./]+.*")==false) {
				System.err.println("Line-"+count+": "+line);
				continue;
			}
			
			//75739 Q0 org.eclipse.swt.ole.win32.Variant.java 1 0.930746 indri
			String[] values = line.split(" ");
			String filename = values[2].trim();
			
			//find File ID
			int fid = 0;
			if (!idTable.containsKey(filename)){
				fid = fileIndex++;
				idTable.put(filename, fid);				
				nameTable.put(fid, filename);
			}
			else
				fid = idTable.get(filename);
			
			Rank item = new Rank();			
			item.bugID = Integer.parseInt(values[0]);
			item.fileID = fid;
			item.rank = Integer.parseInt(values[3])-1;
			item.score = Double.parseDouble(values[4]);
			
			if (!table.containsKey(item.bugID)){
				table.put(item.bugID, new Hashtable<Integer, Rank>());
			}
			table.get(item.bugID).put(item.fileID, item);			
		}
		reader.close();

		return table;
		
	}
	
	/**
	 * 지정된 버그파일(XML)에서 fixed File list정보를 얻음
	 * XML파일은 여러개의 버그리포트가 하나로 정리된 파일을 말함. 
	 * @return
	 */
	private Hashtable<Integer, TreeSet<String>> getFixedFileTable() {
		
		Hashtable<Integer, TreeSet<String>> fixTable = new Hashtable<Integer, TreeSet<String>>();

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
			InputStream is = new FileInputStream(bugFilePath);
			Document doc = domBuilder.parse(is);
			Element root = doc.getDocumentElement();
			NodeList bugRepository = root.getChildNodes();
			if (bugRepository == null)
				return null;

			for (int i = 0; i < bugRepository.getLength(); i++) {
				Node bugNode = bugRepository.item(i);
				if (bugNode.getNodeType() != Node.ELEMENT_NODE)
					continue;

				//get bugID
				String strBugID = bugNode.getAttributes().getNamedItem("id").getNodeValue();
				Integer bugID = Integer.parseInt(strBugID);

				for (Node node = bugNode.getFirstChild(); node != null; node = node.getNextSibling()) {
					if (node.getNodeType() != Node.ELEMENT_NODE) continue;
					if (!node.getNodeName().equals("fixedFiles")) continue;
				
					NodeList fileNodeList = node.getChildNodes();					
					for (int j = 0; j < fileNodeList.getLength(); j++) {
						Node _n = fileNodeList.item(j);
						if (!_n.getNodeName().equals("file")) continue;
					
						String fileName = _n.getTextContent();
						
						//append fixTable
						if (!fixTable.containsKey(bugID))
							fixTable.put(bugID, new TreeSet<String>());
						fixTable.get(bugID).add(fileName);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return fixTable;
	}
	
}
