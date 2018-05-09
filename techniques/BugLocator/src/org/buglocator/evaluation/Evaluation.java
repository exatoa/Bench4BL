package org.buglocator.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import org.buglocator.property.Property;

public class Evaluation {
	private String workDir = Property.getInstance().WorkDir + Property.getInstance().Separator;
	private String outputFile = Property.getInstance().OutputFile;
	private int fileCount = Property.getInstance().FileCount;
	private int bugCount = Property.getInstance().BugReportCount;
	private float alpha = Property.getInstance().Alpha;
	private String lineSparator = Property.getInstance().LineSeparator;
	private String recommandedPath =  Property.getInstance().WorkDir + Property.getInstance().Separator+ "recommended" +Property.getInstance().Separator;

	Hashtable<String, Integer> idTable = null;
	Hashtable<Integer, String> nameTable = null;
	Hashtable<Integer, TreeSet<String>> fixTable = null;
	Hashtable<String, Double> lenTable = null;
	
	FileWriter outputWriter;

	public Evaluation() {
		try {
			this.idTable = getFileId();
			this.fixTable = getFixLinkTable();
			this.lenTable = getLenScore();
			this.nameTable = getClassNames();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileWriter errorWriter = null;
	public void evaluate() throws IOException {
		BufferedReader VSMReader = new BufferedReader(new FileReader(this.workDir + "VSMScore.txt"));
		BufferedReader GraphReader = new BufferedReader(new FileReader(this.workDir + "SimiScore.txt"));
		errorWriter = new FileWriter(this.workDir + "Evaluator-NoMatch.txt");
		outputWriter = new FileWriter(this.outputFile);
		File resultDir = new File(recommandedPath);
		if (!resultDir.exists()) 
			resultDir.mkdirs();
		
		int ErrorCount = 0;
		int ErrorBugCount=0;
		for (int count = 0; count < bugCount; count++)
		{
			// Craete VSM vector
			String vsmLine = VSMReader.readLine();
			String vsmIdStr = vsmLine.substring(0, vsmLine.indexOf(";"));
			Integer bugID = Integer.parseInt(vsmIdStr);
			String vsmVectorStr = vsmLine.substring(vsmLine.indexOf(";") + 1);
			float[] vsmVector = getVector(vsmVectorStr);

			// multiply length values
			for (String key : lenTable.keySet()) {
				Integer id = idTable.get(key);
				Double score = lenTable.get(key);
				vsmVector[id] *= score.floatValue();
			}
			vsmVector = normalize(vsmVector);

			// Create Simi vector (VSM과 id순서가 같음)
			String graphLine = GraphReader.readLine();
			// String graphIdStr = graphLine.substring(0,
			// graphLine.indexOf(";"));
			// Integer graphId = Integer.parseInt(graphIdStr); //VSM과 id순서가 같아서
			// 사용하지 않음
			String graphVectorStr = graphLine.substring(graphLine.indexOf(";") + 1); // id부분
																						// 제거
			float[] graphVector = getVector(graphVectorStr);
			graphVector = normalize(graphVector);

			// Calculate final Ranking  (This is recommendation process)
			float[] finalR = combine(vsmVector, graphVector, this.alpha);
			
			//Print Evaluation Result 
			
			int ret = this.printEvaluationResult(bugID, finalR);
			if (ret != 0){
				ErrorCount+= ret;
				ErrorBugCount++;
			}
		}
		VSMReader.close();
		GraphReader.close();
		errorWriter.close();
		outputWriter.close();
		
		
		if (ErrorCount!=0)
			System.err.println("There are "+ErrorCount+" no match files in "+ErrorBugCount + " Bug Reports");
		
	}
	
	public int printEvaluationResult(Integer _bugID, float[] _finalscore) throws IOException
	{
		//Score에 따라 파일이 정렬된 결과를 가져옴
		Rank[] sortedRank = getSortedRank(_finalscore);
		
		int ErrorCount = 0;
		
		//Evaluation Part-------------------------------------------------
		// 버그리포트에서 수정되었던 파일목록을 불러옴. (실제 정답 셋)
		TreeSet<String> fileSet = fixTable.get(_bugID);
		Iterator<String> fileIt = fileSet.iterator();
		Hashtable<Integer, String> answerIdTable = new Hashtable<Integer, String>();
		while (fileIt.hasNext()) {
			String fileName = fileIt.next();
			Integer fileId = idTable.get(fileName);
			//버그리포트에서 수정된 파일이 실제 코드에서는 없다면 에러가 발생. (버전이 맞지 않는 경우 종종 생김)
			if (fileId==null){
				errorWriter.write(_bugID + ": This version of source code has no "+ fileName +".... Please check it!\n" );
				errorWriter.flush();
				ErrorCount++;
				continue;
			}					
			answerIdTable.put(fileId, fileName);
		}
		
		//정답셋에 있는 파일들이 몇번째에 랭크되었는지 결과를 보여줌. (writer는 추천된 결과 전체를 보여줌)
		FileWriter writer = new FileWriter(recommandedPath + _bugID + ".txt");
		for (int i = 0; i < sortedRank.length; i++) {
			Rank rank = sortedRank[i];
			if(nameTable.containsKey(rank.id)) {
				writer.write(i + "\t" + rank.rank + "\t" + nameTable.get(rank.id) + this.lineSparator);
			}
			if ((!answerIdTable.isEmpty()) && (answerIdTable.containsKey(rank.id))) {
				outputWriter.write(_bugID + "\t" + answerIdTable.get(rank.id) + "\t" + i + "\t" + rank.rank + this.lineSparator);
				outputWriter.flush();
			}
		}
		writer.close();
		
		return ErrorCount;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Hashtable<String, Integer> getFileId() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(this.workDir + "ClassName.txt"));
		String line = null;
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			Integer idInteger = Integer.valueOf(Integer.parseInt(values[0]));
			String nameString = values[1].trim();
			table.put(nameString, idInteger);
		}
		reader.close();
		return table;
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Hashtable<Integer, String> getClassNames() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(this.workDir + "ClassName.txt"));
		String line = null;
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			Integer idInteger = Integer.valueOf(Integer.parseInt(values[0]));
			String nameString = values[1].trim();
			table.put(idInteger, nameString);
		}
		reader.close();
		return table;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public Hashtable<Integer, String> getFile() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(this.workDir + "ClassName.txt"));
		String line = null;
		Hashtable<Integer, String> table = new Hashtable<Integer, String>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			Integer idInteger = Integer.valueOf(Integer.parseInt(values[0]));
			String nameString = values[1].trim();
			table.put(idInteger, nameString);
		}
		reader.close();
		return table;
	}

	public Hashtable<Integer, TreeSet<String>> getFixLinkTable() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(this.workDir + "FixLink.txt"));
		String line = null;
		Hashtable<Integer, TreeSet<String>> table = new Hashtable<Integer, TreeSet<String>>();
		while ((line = reader.readLine()) != null) {
			String[] valueStrings = line.split("\t");
			Integer id = Integer.parseInt(valueStrings[0]);
			String fileName = valueStrings[1].trim();
			if (!table.containsKey(id)) {
				table.put(id, new TreeSet<String>());
			}
			table.get(id).add(fileName);
		}
		reader.close();
		return table;
	}

	private Rank[] getSortedRank(float[] finalR) {
		Rank[] R = new Rank[finalR.length];
		for (int i = 0; i < R.length; i++) {
			R[i] = new Rank();
			R[i].rank = finalR[i];
			R[i].id = i;
		}
		R = insertionSort(R);
		return R;
	}

	private Rank[] insertionSort(Rank[] R) {
		for (int i = 0; i < R.length; i++) {
			int maxIndex = i;
			for (int j = i; j < R.length; j++) {
				if (R[j].rank > R[maxIndex].rank) {
					maxIndex = j;
				}
			}
			Rank tmpRank = R[i];
			R[i] = R[maxIndex];
			R[maxIndex] = tmpRank;
		}
		return R;
	}

	public float[] combine(float[] vsmVector, float[] graphVector, float f) {
		float[] results = new float[this.fileCount];
		for (int i = 0; i < this.fileCount; i++) {
			results[i] = (vsmVector[i] * (1.0F - f) + graphVector[i] * f);
		}
		return results;
	}

	private float[] normalize(float[] array) {
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		for (int i = 0; i < array.length; i++) {
			if (max < array[i])
				max = array[i];
			if (min > array[i])
				min = array[i];
		}
		float span = max - min;
		for (int i = 0; i < array.length; i++) {
			array[i] = ((array[i] - min) / span);
		}
		return array;
	}

	private float[] getVector(String vectorStr) {
		float[] vector = new float[this.fileCount];
		String[] values = vectorStr.split(" ");

		for (String value : values) {
			String[] singleValues = value.split(":");
			if (singleValues.length == 2) {
				int index = Integer.parseInt(singleValues[0]);

				float sim = Float.parseFloat(singleValues[1]);
				vector[index] = sim;
			}
		}
		return vector;
	}

	private Hashtable<String, Double> getLenScore() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(this.workDir + "LengthScore.txt"));
		String line = null;
		Hashtable<String, Double> lenTable = new Hashtable<String, Double>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			String name = values[0];
			Double score = Double.valueOf(Double.parseDouble(values[1]));
			lenTable.put(name, score);
		}
		reader.close();
		return lenTable;
	}
}