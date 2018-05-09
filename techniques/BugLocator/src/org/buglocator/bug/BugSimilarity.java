package org.buglocator.bug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import org.buglocator.property.Property;

public class BugSimilarity {
	private int wordCount = Property.getInstance().BugTermCount;
	private int bugReportCount = Property.getInstance().BugReportCount;
	private String workDir = Property.getInstance().WorkDir + Property.getInstance().Separator;


	public void computeSimilarity() throws IOException {
		//Sorted ID 목록 로드
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "SortedId.txt"));
		String line = null;
		int[] idArr = new int[bugReportCount];
		int index = 0;
		while ((line = reader.readLine()) != null) {
			String idStr = line.substring(0, line.indexOf("\t"));
			idArr[index++] = Integer.parseInt(idStr);
		}
		reader.close();

		//Bug Vector로드 
		Hashtable<Integer, float[]> vectors = this.getVector();
		FileWriter writer = new FileWriter(workDir + "BugSimilarity.txt");

		//compare with cosine similarity
		for (int i = 0; i < bugReportCount; i++) {
			int firstId = idArr[i];
			float[] firstVector = vectors.get(firstId);
			String output = firstId + ";";
			for (int j = 0; j < i; j++) {
				int secondId = idArr[j];
				float[] secondVector = vectors.get(secondId);
				float similarity = this.getCosineValue(firstVector, secondVector);
				output += secondId + ":" + similarity + " ";
			}
			writer.write(output.trim() + Property.getInstance().LineSeparator);
			// System.out.println(i);
			writer.flush();
		}
		writer.close();
	}

	private float getCosineValue(float[] firstVector, float[] secondVector) {
		float len1 = 0;
		float len2 = 0;
		float product = 0;
		for (int i = 0; i < wordCount; i++) {
			len1 += firstVector[i] * firstVector[i];
			len2 += secondVector[i] * secondVector[i];
			product += firstVector[i] * secondVector[i];
		}
		return (float) (product / (Math.sqrt(len1) * Math.sqrt(len2)));
	}

	public Hashtable<Integer, float[]> getVector() throws IOException {

		Hashtable<Integer, float[]> vectors = new Hashtable<Integer, float[]>();

		BufferedReader reader = new BufferedReader(new FileReader(workDir + "BugVector.txt"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String idStr = line.substring(0, line.indexOf("."));
			String vectorStr = line.substring(line.indexOf(";") + 1).trim();
			Integer id = Integer.parseInt(idStr);
			float[] vector = this.getVector(vectorStr);
			vectors.put(id, vector);
		}
		reader.close();
		return vectors;
	}

	private float[] getVector(String vectorStr) {
		float[] vector = new float[wordCount];
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

}
