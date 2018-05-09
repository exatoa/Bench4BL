package org.brtracer.sourcecode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.brtracer.property.Property;

public class CodeVectorCreator {
	private String workDir = Property.getInstance().WorkDir + Property.getInstance().Separator;
	private String lineSparator = Property.getInstance().LineSeparator;

	public int fileCount = Property.getInstance().FileCount;
	public int codeTermCount = Property.getInstance().WordCount;


	public void create() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "TermInfo.txt"));
		String line = null;
		FileWriter writer = new FileWriter(workDir + "CodeVector.txt");
		FileWriter errorWriter = new FileWriter(workDir + "CodeVector_noTerms.txt");
		int errorCount = 0;
		
		while ((line = reader.readLine()) != null) {
			String[] values = line.split(";");

			String name = values[0].substring(0, values[0].indexOf("\t"));
			if (values.length == 1) {
				errorWriter.write("Warnning:: This file has no terms: " + name);
				errorCount ++;
				continue;
			}
			Integer totalTermCount = Integer.parseInt(values[0].substring(values[0].indexOf("\t") + 1));
			String[] termInfos = values[1].split("\t");
			float[] vector = new float[codeTermCount];
			for (String str : termInfos) {
				String[] strs = str.split(":");
				Integer termId = Integer.parseInt(strs[0]);
				Integer termCount = Integer.parseInt(strs[1].substring(0, strs[1].indexOf(" ")));
				Integer documentCount = Integer.parseInt(strs[1].substring(strs[1].indexOf(" ") + 1));

				float tf = this.getTfValue(termCount, totalTermCount);
				float idf = this.getIdfValue(documentCount, fileCount);
				vector[termId] = tf * idf;
			}
			double norm = 0.0f;
			for (int i = 0; i < vector.length; i++) {
				norm += vector[i] * vector[i];
			}
			norm = Math.sqrt(norm);

			StringBuffer buf = new StringBuffer();
			buf.append(name + ";");
			for (int i = 0; i < vector.length; i++) {
				if (vector[i] != 0.0f) {
					vector[i] = vector[i] / (float) norm;
					buf.append(i + ":" + vector[i] + " ");
				}
			}
			writer.write(buf.toString() + lineSparator);
			writer.flush();
		}
		reader.close();
		writer.close();
		errorWriter.close();
		
		if (errorCount > 0)
			System.err.println("CodeVectorCreator :: " + errorCount +" files has no terms.");
	}

	private float getTfValue(int freq, int totalTermCount) {
		return (float) Math.log(freq) + 1;
	}

	private float getIdfValue(double docCount, double totalCount) {
		return (float) Math.log(totalCount / docCount);
	}
}
