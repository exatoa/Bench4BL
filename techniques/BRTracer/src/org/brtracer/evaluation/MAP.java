package org.brtracer.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import org.brtracer.property.Property;

public class MAP {
	private String home_folder = Property.getInstance().WorkDir; //"/home/ben/Projects/BRTracer/tmp/";
	private String metrics_folder = "MAP/";
	private int bugReportCount = 286;
	private String[][] result = null;

	public static void main(String[] args) {
		MAP map = new MAP();
		try {
			map.statistics();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public File[] getResultFile() {
		File dir = new File(home_folder + metrics_folder);
		return dir.listFiles();
	}

	public void statistics() throws Exception, IOException {
		File[] files = this.getResultFile();
		result = new String[files.length + 1][2];
		result[0][0] = "factor";
		result[0][1] = "MAP";
		int i = 1;
		for (File file : files) 
		{
			//File Read
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			float ap = 0.0f;
			Hashtable<Integer, ArrayList<Integer>> rankTable = new Hashtable<Integer, ArrayList<Integer>>();
			while ((line = reader.readLine()) != null) {
				String[] values = line.split("\t");
				Integer id = Integer.parseInt(values[0]);
				int rank = Integer.parseInt(values[2]);
				if (!rankTable.containsKey(id)) {
					rankTable.put(id, new ArrayList<Integer>());
				}
				rankTable.get(id).add(rank);
			}
			reader.close();
			
			Iterator<Integer> rankTableIt = rankTable.keySet().iterator();
			TreeSet<Integer> filterSet = new TreeSet<Integer>();
			while (rankTableIt.hasNext()) {
				Integer id = rankTableIt.next();
				ArrayList<Integer> rankList = rankTable.get(id);
				Integer[] ranks = this.sort(rankList);
				if (!filterSet.contains(id)) {
					
					filterSet.add(id);
				}
				float p = 0.0f;
				for (int j = 0; j < ranks.length; j++) {
					p += (float) (j + 1.0f) / (ranks[j] + 1.0f);
				}
				float sap = p / ranks.length;
				ap += sap;
				System.out.println(id + "," + sap);

			}
			float map = ap / bugReportCount;
			String factor = file.getName().replace(".csv", "")
					.replace("result_", "");
			result[i][0] = factor;
			result[i][1] = map + "";
			i++;
		}

		FileWriter writer = new FileWriter(home_folder + metrics_folder	+ "map.csv");
		for (int k = 0; k < result.length; k++) {
			String output = "";
			for (int j = 0; j < result[k].length; j++) {
				output += result[k][j] + ",";
			}
			writer.write(output + "\r\n");
			writer.flush();
		}
		writer.close();
	}

	public Integer[] sort(ArrayList<Integer> list) {
		Integer[] ranks = list.toArray(new Integer[list.size()]);
		for (int i = 0; i < ranks.length; i++) {
			int minIndex = i;
			for (int j = i + 1; j < ranks.length; j++) {
				if (ranks[j] < ranks[minIndex]) {
					minIndex = j;
				}
			}
			int tmp = ranks[i];
			ranks[i] = ranks[minIndex];
			ranks[minIndex] = tmp;
		}
		return ranks;
	}
}
