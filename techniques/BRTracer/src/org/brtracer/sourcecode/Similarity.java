package org.brtracer.sourcecode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

import org.brtracer.property.Property;

public class Similarity {
	Hashtable<String, Integer> fileIdTable = null;
	private String workDir = Property.getInstance().WorkDir + Property.getInstance().Separator;
	private String lineSparator = Property.getInstance().LineSeparator;

	public int fileCount = Property.getInstance().FileCount;
	public int codeTermCount = Property.getInstance().WordCount;

	public Similarity() throws IOException {

	}

	public void compute() throws IOException {
		fileIdTable = getFileId();
		Hashtable<String, Integer> wordIdTable = getWordId();
		Hashtable<String, Integer> idcTable = getIDCTable();

		FileWriter writer = new FileWriter(workDir + "VSMScore.txt");
		BufferedReader readerId = new BufferedReader(new FileReader(workDir + "SortedId.txt"));
		String idLine = null;
		while ((idLine = readerId.readLine()) != null) {
			Integer bugId = Integer.parseInt(idLine.substring(0, idLine.indexOf("\t")));
			BufferedReader readerBug = new BufferedReader(
					new FileReader(workDir + "BugCorpus" + Property.getInstance().Separator + bugId + ".txt"));
			String line = readerBug.readLine();
			String[] words = line.split(" ");

			Hashtable<String, Integer> wordTable = new Hashtable<String, Integer>();
			for (String word : words) {
				if (!word.trim().equals("")) {
					if (wordTable.containsKey(word)) {
						Integer count = wordTable.get(word);
						count++;
						wordTable.remove(word);
						wordTable.put(word, count);
					} else {
						wordTable.put(word, 1);
					}
				}
			}
			int totalTermCount = 0;
			for (String word : wordTable.keySet()) {
				Integer id = wordIdTable.get(word);
				if (id != null) {
					totalTermCount += wordTable.get(word);
				}
			}
			float[] bugVector = new float[codeTermCount];

			for (String word : wordTable.keySet()) {
				Integer id = wordIdTable.get(word);
				if (id != null) {
					Integer idc = idcTable.get(word);
					Integer count = wordTable.get(word);
					float tf = getTfValue(count, totalTermCount);
					float idf = getIdfValue(idc, fileCount);
					bugVector[id] = tf * idf;
				}
			}
			double norm = 0.0f;
			for (int i = 0; i < bugVector.length; i++) {
				norm += bugVector[i] * bugVector[i];
			}
			norm = Math.sqrt(norm);
			for (int i = 0; i < bugVector.length; i++) {
				bugVector[i] = bugVector[i] / (float) norm;
			}

			float[] simValues = computeSimilarity(bugVector);

			StringBuffer buf = new StringBuffer();
			buf.append(bugId + ";");
			for (int i = 0; i < simValues.length; i++) {
				if (simValues[i] != 0.0f)
					buf.append(i + ":" + simValues[i] + " ");
			}
			writer.write(buf.toString().trim() + lineSparator);
			writer.flush();

		}
		writer.close();
	}

	private float[] computeSimilarity(float[] bugVector) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "CodeVector.txt"));
		String line = null;
		float[] simValues = new float[fileCount];
		while ((line = reader.readLine()) != null) {
			String[] values = line.split(";");
			String name = values[0];
			Integer fileId = fileIdTable.get(name);
			if (fileId == null) {
				System.out.println(name);
			}
			float[] codeVector = null;
			if (values.length != 1)
				codeVector = this.getVector(values[1]);
			else
				codeVector = this.getVector(null);
			float sim = 0.0f;
			for (int i = 0; i < codeVector.length; i++) {
				sim += bugVector[i] * codeVector[i];
			}
			simValues[fileId] = sim;
		}
		return simValues;
	}

	private float[] getVector(String vecStr) {
		float[] vector = new float[codeTermCount];
		if (vecStr == null)
			return vector;
		String[] values = vecStr.split(" ");
		for (String str : values) {
			Integer id = Integer.parseInt(str.substring(0, str.indexOf(":")));
			float w = Float.parseFloat(str.substring(str.indexOf(":") + 1));
			vector[id] = w;
		}
		return vector;
	}

	private float getTfValue(int freq, int totalTermCount) {
		return (float) Math.log(freq) + 1;
	}

	private float getIdfValue(double docCount, double totalCount) {
		return (float) Math.log(totalCount / docCount);
	}

	private Hashtable<String, Integer> getWordId() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "Wordlist.txt"));
		String line = null;
		Hashtable<String, Integer> wordIdTable = new Hashtable<String, Integer>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			wordIdTable.put(values[0], Integer.parseInt(values[1]));
		}
		reader.close();
		return wordIdTable;
	}

	private Hashtable<String, Integer> getIDCTable() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "IDC.txt"));
		String line;
		Hashtable<String, Integer> idcTable = new Hashtable<String, Integer>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			idcTable.put(values[0], Integer.parseInt(values[1]));
		}
		reader.close();
		return idcTable;
	}

	private Hashtable<String, Integer> getFileId() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "MethodName.txt"));
		String line;
		Hashtable<String, Integer> table = new Hashtable<String, Integer>();
		while ((line = reader.readLine()) != null) {
			String[] values = line.split("\t");
			Integer idInteger = Integer.parseInt(values[0]);
			String nameString = values[1].trim();
			table.put(nameString, idInteger);
		}
		return table;
	}
}
