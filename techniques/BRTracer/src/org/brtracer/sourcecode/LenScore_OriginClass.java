package org.brtracer.sourcecode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;

import org.brtracer.property.Property;

public class LenScore_OriginClass {
	private String workDir = Property.getInstance().WorkDir + Property.getInstance().Separator;
	private int fileCount = Property.getInstance().OriginFileCount;
	public static int B = 50;

	public static void main(String[] args) {
		LenScore_OriginClass score = new LenScore_OriginClass();
		try {
			score.computeLenScore();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
		System.out.println("finished");
	}

	public void computeLenScore() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(workDir + "TermInfo_OriginClass.txt"));
		String line = null;
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		Integer[] lens = new Integer[fileCount];
		int i = 0;
		Hashtable<String, Integer> lensTable = new Hashtable<String, Integer>();
		int count = 0;
		while ((line = reader.readLine()) != null) {
			String[] values = line.split(";");
			String name = values[0].substring(0, values[0].indexOf("\t"));
			Integer len = Integer.parseInt(values[0].substring(values[0].indexOf("\t") + 1));
			lensTable.put(name, len);
			lens[i++] = len;
			if (len != 0)
				count++;
			if (len > max) {
				max = len;
			}
			if (len < min) {
				min = len;
			}
		}
		double low = min;
		double high = max;
		Collections.sort(Arrays.asList(lens));
		double median;
		if (lens.length % 2 == 0) {
			median = (lens[lens.length / 2] + lens[lens.length / 2 + 1]) / 2;
		} else {
			median = lens[lens.length / 2 + 1];
		}

		int n = 0;
		FileWriter writer = new FileWriter(workDir + "LengthScore.txt");
		for (String key : lensTable.keySet()) {
			double len = (double) lensTable.get(key);
			double score = 0.0;
			double nor = this.getNormValue(len, max, min, median);
			if (len != 0) {
				if (len >= low && len <= high) {

					score = this.getLenScore(nor);
					n++;
				} else if (len < low) {
					score = 0.5;
				} else {
					score = 1.0;
				}
			} else {
				score = 0.0;
			}
			writer.write(key + "\t" + score + "\r\n");
			writer.flush();
		}
		writer.close();
	}

	public Double getNormValue(Double x, Double max, Double min, Double median) {
		return B * (x - median) / (max - min);
	}

	public double getLenScore(double len) {
		return (Math.exp(len) / (1 + Math.exp(len)));
	}

}
