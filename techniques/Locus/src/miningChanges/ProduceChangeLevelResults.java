package miningChanges;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import generics.Bug;
import generics.Pair;
import main.Main;
import utils.FileToLines;
import utils.ReadBugsFromXML;
import utils.WriteLinesToFile;

public class ProduceChangeLevelResults {
	public String loc = main.Main.settings.get("workingLoc");
	private HashMap<Integer,HashSet<String>> inducingRevisions;
	private HashMap<String,Long> revisionTime;
	public List<Bug> bugs;
	public HashMap<Integer, HashMap<String,Double>> hunkResults;
	
	public boolean loadOracles() {
		String filename = main.Main.changeOracle;
//		System.out.println(filename);
		File file = new File(filename);
		if (!file.exists()) {
			System.err.println("could not find change level oracles");
			return false;
		}
		List<String> lines = FileToLines.fileToLines(filename);
		int index = 0;
		bugs = ReadBugsFromXML.getFixedBugsFromXML(main.Main.settings.get("bugReport"));
		inducingRevisions = new HashMap<Integer,HashSet<String>>();
		while (index < lines.size()) {
//			System.out.println(lines.get(index));
			String[] splits = lines.get(index).split("\t");
			int bid = Integer.parseInt(splits[0]);
			HashSet<String> revisions = new HashSet<String>();
			for (int i = 1; i < splits.length; i++)
				revisions.add(splits[i]);
			inducingRevisions.put(bid, revisions);
			index++;
		}
		return true;
	}
	
	public void loadResults() {

		String resultFile = loc + File.separator + "results_change" + ".txt";
		File file = new File(resultFile);
		if (!file.exists()) {
			ObtainVSMScore ovs = new ObtainVSMScore();
			hunkResults = ovs.obtainSimilarity(true);
		} else {
			System.out.println("Results of change level exists, read from results_change.txt");
			hunkResults = new HashMap<>();
			List<String> lines = FileToLines.fileToLines(resultFile);
			for (String line : lines) {
				String[] splits = line.split("\t");
				int sid = Integer.parseInt(splits[0]);
				hunkResults.put(sid, new HashMap<>());
				for (int i = 1; i < splits.length; i++)
					hunkResults.get(sid).put(splits[i].split(":")[0], Double.parseDouble(splits[i].split(":")[1]));
			}
		}
	}
	
	public void loadRevisionTime() throws ParseException {
		List<String> lines = FileToLines.fileToLines(main.Main.settings.get("workingLoc") + File.separator + "logOneline.txt");
		revisionTime = new HashMap<String, Long>();
		for (String line : lines) {
//			System.out.println(line);
			String[] splits = line.split("\t");
			String revisionNO = splits[0];
			Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH).parse(splits[2]);
			revisionTime.put(revisionNO, date.getTime());
		}
	}
	
	public void integrateResults() {
		List<List<Integer>> ranks = new ArrayList<List<Integer>>();
		List<String> resultsLines = new ArrayList<String>();
		double belta2 = Main.belta2;
		for (Bug bug : bugs) {
			int bid = bug.id;
			HashMap<String,Double> results = hunkResults.get(bid);
			List<Pair<String, Long>> changeRanks = new ArrayList<Pair<String,Long>>();
			for (String change : results.keySet()) {
				if (revisionTime.containsKey(change) && revisionTime.get(change) < bug.reportTime)
					changeRanks.add(new Pair<String,Long>(change, revisionTime.get(change)));
				else changeRanks.add(new Pair<String, Long>(change, Long.MAX_VALUE));
			}
			Collections.sort(changeRanks);
			HashMap<String, Double> timeScore = new HashMap<String,Double>();
			for (int i = 0; i < changeRanks.size(); i++) {
				int index = changeRanks.size() - i - 1;
				timeScore.put(changeRanks.get(index).getKey(), 1.0 / (i + 1));
			}
				
			
			
			for (String change : results.keySet()) {
//				System.out.println(change + "\t" + revisionTime.get(change));
				if (revisionTime.containsKey(change) && revisionTime.get(change) > bug.reportTime) {
					continue;
				}
				if (timeScore.containsKey(change))
					results.put(change, results.get(change) + belta2 * timeScore.get(change));
			}
			
			List<Pair<String, Double>> finalRanks = new ArrayList<Pair<String,Double>>();
			for (String change : results.keySet()) {
				finalRanks.add(new Pair<String,Double>(change, results.get(change)));
			}
			
			Collections.sort(finalRanks);
			List<Integer> rank = new ArrayList<Integer>();
			
			for (int i = 0; i < finalRanks.size(); i++) {
				int index = finalRanks.size() - i - 1;
				if (inducingRevisions.get(bid).contains(finalRanks.get(index).getKey()))
					rank.add(i);
			}
			ranks.add(rank);
			System.out.println(bid + "\t" + rank.toString());
		}
		
		int N = 10;
		double[] topN = EvaluationMetric.topN(ranks, N);
		double map = EvaluationMetric.MAP(ranks);
		double mrr = EvaluationMetric.MRR(ranks);
		resultsLines.add("map:\t" + map);
		resultsLines.add("mrr:\t" + mrr);
		
		System.out.println(map + "\t" + mrr);
		for (int i = 0; i < N; i++) {
			System.out.print(topN[i] + "\t");
			resultsLines.add("top@" + (i + 1) + "\t" + topN[i]);
		}
		System.out.println();
		String filename = main.Main.settings.get("workingLoc") + File.separator + "changeLevelResults.txt";
		WriteLinesToFile.writeLinesToFile(resultsLines, filename);
	}
	
	public void getFinalResults() throws ParseException {
		if (loadOracles()) {
			System.out.println("Calculating similarities...");
			loadResults();
			System.out.println("Finish calculating similarities");
			loadRevisionTime();
			System.out.println("Integrating final results...");
			integrateResults();
		}
	}
}
