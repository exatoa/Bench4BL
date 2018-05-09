package miningChanges;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import main.Main;
import utils.ReadBugsFromXML;
import utils.ReadFileToList;
import utils.WriteLinesToFile;
import generics.Bug;
import generics.Pair;
import utils.FileToLines;
import java.util.Date;
import java.text.SimpleDateFormat;


public class ObtainVSMScore {
	public String loc = main.Main.settings.get("workingLoc");
	public HashMap<Integer, List<String>> bugTermList;
	public List<Bug> bugs;
	public HashMap<Integer,List<Integer>> bugCLTIndex;
	public HashMap<String,List<Integer>> logCLTs;
	public HashMap<String,List<Integer>> hunkCLTs;
	public HashMap<String,HashSet<Integer>> bugRelatedHunks;
	public List<List<String>> hunkTermList;
	public List<String> hunkIndex;	
	public HashMap<String,Integer> cltIndex;
	public HashMap<Integer, String> hunkChangeMap;
	public HashMap<Integer, String> hunkSourceMap;
	public HashSet<String> validCommits;
	public HashSet<Integer> validHunks;
	
	private static HashMap<Integer,HashSet<String>> potentialChanges;
	
	public void loadCLTIndex() {
		logCLTs = new HashMap<String,List<Integer>>();
		hunkCLTs = new HashMap<String,List<Integer>>();
		bugCLTIndex = new HashMap<Integer,List<Integer>>();
		
		String cltIndexName = loc + File.separator + "codeLikeTerms.txt";
		List<String> lines = FileToLines.fileToLines(cltIndexName);
		cltIndex = new HashMap<String,Integer>();
		for (String line : lines) {
			cltIndex.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
		}
		String filename = loc + File.separator + "commitCLTIndex.txt";
		lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			logCLTs.put(tmp[0], new ArrayList<Integer>());
//			System.out.println(line);
			for (int i = 1; i < tmp.length; i++)
				logCLTs.get(tmp[0]).add(Integer.parseInt(tmp[i]));
		}
		
		filename = loc + File.separator + "hunkCLTIndex.txt";
		lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			hunkCLTs.put(tmp[0], new ArrayList<Integer>());
//			System.out.println(line);
			for (int i = 1; i < tmp.length; i++)
				hunkCLTs.get(tmp[0]).add(Integer.parseInt(tmp[i]));
		}
		
		filename = loc + File.separator + "bugCLTIndex.txt";
		lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] tmp = line.split("\t");
			int bid = Integer.parseInt(tmp[0]);
			bugCLTIndex.put(bid, new ArrayList<Integer>());
			for (int i = 1; i < tmp.length; i++)
				bugCLTIndex.get(bid).add(Integer.parseInt(tmp[i]));
		}
	}

	
	public void loadBugFiles() {
		bugs = ReadBugsFromXML.getFixedBugsFromXML(main.Main.settings.get("bugReport"));
		bugTermList = new HashMap<Integer, List<String>>();
		String bugDir = loc + File.separator + "bugText";
	
		for (Bug bug : bugs) {
			int bugId = bug.id;
			List<String> lines = FileToLines.fileToLines(bugDir + File.separator + bugId + ".txt");
			bugTermList.put(bugId, lines);
		}

		String filename = loc + File.separator + "concernedCommits.txt";
		List<String> lines = FileToLines.fileToLines(filename);
		potentialChanges = new HashMap<>();
		for (String line : lines) {
			String[] splits = line.split("\t");
			int bid = Integer.parseInt(splits[0]);
			String[] changes = splits[1].substring(1, splits[1].length() - 1).split(",");
			potentialChanges.put(bid, new HashSet<>());
			for (String change : changes) {
				potentialChanges.get(bid).add(change.trim());
			}
		}
	}
	
	public void loadHunkFiles() {
		
		hunkTermList = new ArrayList<List<String>>();
		String hunkIndexName = loc + File.separator + "hunkIndex.txt";
		List<String> lines = FileToLines.fileToLines(hunkIndexName);
		HashSet<Integer> semanticHunks = new HashSet<Integer>();
		hunkIndex = new ArrayList<String>();
		int index = 0;
		hunkChangeMap = new HashMap<>();
		for (String line : lines) {
			String[] split = line.split("\t");
			hunkIndex.add(split[0]);
			hunkChangeMap.put(index, split[0].split("@")[0]);
			if (split[1].equals("true"))
				semanticHunks.add(index);
			index++;
		}
//		hunkIndex = FileToLines.fileToLines(hunkIndexName);
		hunkSourceMap = new HashMap<>();
		validHunks = new HashSet<Integer>();
		validCommits = new HashSet<String>();
		String filename = loc + File.separator + "sourceHunkLink.txt";
		lines = FileToLines.fileToLines(filename);
		for (String line : lines) {
			String[] split = line.split("\t");
			for (int i = 1; i < split.length; i++) {
				int hid = Integer.parseInt(split[i]);
				hunkSourceMap.put(hid, split[0]);
				if (semanticHunks.contains(hid))
					validHunks.add(hid);
				validCommits.add(hunkIndex.get(hid).split("@")[0]);
			}
		}
		
		HashMap<String, List<String>> hunkCodeTerms = ReadFileToList.readStringListsWithString(loc+File.separator + "hunkCode.txt");
		HashMap<String, List<String>> hunkLogTerms = ReadFileToList.readStringListsWithString(loc+File.separator + "hunkLog.txt");
		
		for (int i = 0; i < hunkIndex.size(); i++) {
			String line = hunkIndex.get(i);
			List<String> terms = new ArrayList<String>();
			terms.addAll(hunkCodeTerms.get(line));
			terms.addAll(hunkLogTerms.get(line));

//			filename = loc + File.separator + "hunkLog" + File.separator + line;
//			terms.addAll(FileToLines.fileToLines(filename));
//			filename = loc + File.separator + "hunkCode" + File.separator + line;
//			terms.addAll(FileToLines.fileToLines(filename));
			hunkTermList.add(terms);
		}
	}
	
	public HashSet<String> corpusNL = new HashSet<String>();
	public HashSet<String> relatedEntities = new HashSet<String>();
	public HashMap<String,Integer> corpusInverseIndexNL = new HashMap<String,Integer>();
	public List<String> corpusIndexNL = new ArrayList<String>();
	public HashMap<Integer, HashMap<Integer,Double>> hunkTermFreqNL = new HashMap<Integer, HashMap<Integer,Double>>();	
	public HashMap<Integer, Double> termHunkFreqNL = new HashMap<Integer,Double>();
	public HashMap<Integer, Integer> termHunkCountNL = new HashMap<Integer,Integer>();
	public HashMap<Integer, HashSet<String>> termEntityCountNL = new HashMap<Integer,HashSet<String>>();
	public HashSet<Integer> processedHunksNL = new HashSet<Integer>();
	
	public void updateCorpusNL(List<Integer> hunkIds, boolean isChangeLevel) {
		HashSet<String> newTerms = new HashSet<String>();
		HashSet<Integer> newHunkIndex = new HashSet<Integer>();
		for (int hid : hunkIds) {
			if (processedHunksNL.contains(hid)) continue;
			List<String> hunkTerm = hunkTermList.get(hid);
			for (String term : hunkTerm) {
				if (!corpusNL.contains(term))
					newTerms.add(term);
			}
			if (isChangeLevel)
				relatedEntities.add(hunkChangeMap.get(hid));
			else 
				relatedEntities.add(hunkSourceMap.get(hid));
			processedHunksNL.add(hid);
			newHunkIndex.add(hid);
		}
		
		//System.out.println("\t\t["+getTimeString()+"][new hunk size:\t" + newHunkIndex.size());
		
		for (String term : newTerms) {
			corpusNL.add(term);
			int index = corpusIndexNL.size();
			corpusInverseIndexNL.put(term, index);
			corpusIndexNL.add(term);
		}
		
		// calculate term frequency for the new hunks
		for (int hid : newHunkIndex) {
			List<String> hunkTerms = hunkTermList.get(hid);
			HashMap<Integer,Integer> tmp = new HashMap<Integer,Integer>();
			for (String term : hunkTerms) {
				int index = corpusInverseIndexNL.get(term);
				if (!tmp.containsKey(index)) tmp.put(index, 1);
				else tmp.put(index, tmp.get(index) + 1);
			}
			HashMap<Integer,Double> tmp1 = new HashMap<Integer,Double>();
			for (int tid : tmp.keySet()) {
				tmp1.put(tid,  Math.log(tmp.get(tid)) + 1);
			}
			hunkTermFreqNL.put(hid, tmp1);
		}
		
		// update the reverse term frequency for all terms in the corpus
		for (int hid : newHunkIndex) {
			HashMap<Integer,Double> tmp = hunkTermFreqNL.get(hid);
			for (int index : tmp.keySet()) {
				
				if (termHunkCountNL.containsKey(index)) 
					termHunkCountNL.put(index, termHunkCountNL.get(index) + 1);
				else termHunkCountNL.put(index, 1);
				
				if (!termEntityCountNL.containsKey(index))
					termEntityCountNL.put(index, new HashSet<String>());
				if (isChangeLevel)
					termEntityCountNL.get(index).add(hunkChangeMap.get(hid));
				else termEntityCountNL.get(index).add(hunkSourceMap.get(hid));
				termHunkFreqNL.put(index, Math.log(processedHunksNL.size() * 1.0 / termHunkCountNL.get(index)));
			}
		}
	}
	
	public HashMap<Integer,Double> getVSMScoreNL(Bug bug, List<Integer> hunkId, boolean isChangeLevel) {
		HashMap<Integer,Double> results = new HashMap<Integer,Double>();
		int bid = bug.id;
		List<String> bugTerm = bugTermList.get(bid);		
		HashMap<Integer,Integer> bugTermCount = new HashMap<Integer,Integer>();
		HashMap<Integer,Double> bugTermFreq = new HashMap<Integer,Double>();
		
		for (String term : bugTerm) {
			if (corpusNL.contains(term)) continue;
			corpusNL.add(term);
			int index = corpusIndexNL.size();
			corpusInverseIndexNL.put(term, index);
			corpusIndexNL.add(term);
		}
		
		for (String term : bugTerm) {
			int index = corpusInverseIndexNL.get(term);
			if (!bugTermCount.containsKey(index)) bugTermCount.put(index, 1);
			else bugTermCount.put(index, bugTermCount.get(index) + 1);
		}
		
		for (int index : bugTermCount.keySet()) {
			bugTermFreq.put(index, Math.log(bugTermCount.get(index)) + 1);
		}
		
		double bugNorm = 0;
		for (int k : bugTermFreq.keySet()) {
			if (termHunkFreqNL.containsKey(k))
				bugNorm += bugTermFreq.get(k) * bugTermFreq.get(k) * termHunkFreqNL.get(k) * termHunkFreqNL.get(k);
		}
		for (int index = 0; index < hunkId.size(); index++) {
			int hid = hunkId.get(index);
			HashMap<Integer,Double> termFreq = hunkTermFreqNL.get(hid);
			double hunkNorm = 0;
			HashSet<Integer> intersect = new HashSet<Integer>();
			for (int k : termFreq.keySet()) {
				hunkNorm += termFreq.get(k) * termFreq.get(k) * termHunkFreqNL.get(k) * termHunkFreqNL.get(k);
			}
			intersect.addAll(termFreq.keySet());
			intersect.retainAll(bugTermFreq.keySet());
			double consine = 0;
			for (int k : intersect) {
				consine += bugTermFreq.get(k) * termFreq.get(k) * termHunkFreqNL.get(k) * termHunkFreqNL.get(k);
				
			}
			double similarity = consine / (Math.sqrt(bugNorm) * Math.sqrt(hunkNorm));
			results.put(hid, similarity);
		}
		return results;
		
	}
	
	private HashMap<Integer,Integer> getIndexCount(List<Integer> list) {
		HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
		for (int clt : list) {
			if (!count.containsKey(clt))
				count.put(clt, 0);
			count.put(clt, count.get(clt) + 1);
		}
		return count;
	}
	
	public HashMap<Integer,Double> getVSMScoreCLT(Bug bug, List<Integer> hunkId, boolean isChangeLevel) {
		HashMap<Integer,Double> results = new HashMap<Integer,Double>();
		int bugId = bug.id;
		List<Integer> bugIndex = bugCLTIndex.get(bugId);
		HashMap<Integer,Integer> bugCLTCount = getIndexCount(bugIndex);
		HashMap<Integer,Double> bugCLTFreq = new HashMap<Integer,Double>();
		for (int clt : bugCLTCount.keySet()) {
			bugCLTFreq.put(clt, Math.log(bugCLTCount.get(clt)) + 1);
		}
		
		HashSet<String> relatedEntities = new HashSet<String>();
		
		/**
		 * Calculate the CLT frequencies of hunks
		 */
		HashMap<Integer,HashMap<Integer,Double>> hunkCLTFreq = new HashMap<Integer,HashMap<Integer,Double>>();
		for (int hunk : hunkId) {
			if (!hunkCLTFreq.containsKey(hunk)) {
				List<Integer> clts = new ArrayList<Integer>();
				
				String[] tmp = hunkIndex.get(hunk).split("@");
				if (logCLTs.containsKey(tmp[0])) 
					clts.addAll(logCLTs.get(tmp[0]));
				
//				for (int term : fileTerm) {
//					if (term != 411) clts.add(term);
//				}
				if (hunkCLTs.containsKey(hunk + ":f")) {
					List<Integer> fileTerm = hunkCLTs.get(hunk + ":f");
					clts.addAll(fileTerm);
					clts.addAll(hunkCLTs.get(hunk + ":-1"));
					clts.addAll(hunkCLTs.get(hunk + ":0"));
					clts.addAll(hunkCLTs.get(hunk + ":1"));
				}
				HashMap<Integer,Integer> cltCount = getIndexCount(clts);
				HashMap<Integer,Double> cltFreq = new HashMap<Integer,Double>();
				for (int clt : cltCount.keySet()) {
					cltFreq.put(clt, Math.log(cltCount.get(clt)) + 1);
				}
				hunkCLTFreq.put(hunk, cltFreq);
				
				if (isChangeLevel)
					relatedEntities.add(hunkChangeMap.get(hunk));
				else 
					relatedEntities.add(hunkSourceMap.get(hunk));
				
			}
		}
//		System.out.println(relatedEntities.toString());
		HashMap<Integer, Integer> cltCount = new HashMap<Integer,Integer>(); 
		HashMap<Integer, HashSet<String>> termEntityCount = new HashMap<Integer,HashSet<String>>();
		for (int hunk : hunkId) {
			HashMap<Integer,Double> cltFreq = hunkCLTFreq.get(hunk);
			for (int index : cltFreq.keySet()) {
				if (!cltCount.containsKey(index))
					cltCount.put(index, 1);
				else cltCount.put(index, cltCount.get(index) + 1);
				
				if (!termEntityCount.containsKey(index))
					termEntityCount.put(index, new HashSet<String>());

				if (isChangeLevel)
					termEntityCount.get(index).add(hunkChangeMap.get(hunk));
				else 
					termEntityCount.get(index).add(hunkSourceMap.get(hunk));
			}
		}
		
		double[] inverseFreq = new double[cltIndex.size()];
		for (int i = 0; i < cltIndex.size(); i++) {

			if (cltCount.containsKey(i)) {
				inverseFreq[i] = (termEntityCount.get(i).size() == 0) ? 0 : Math.log(hunkId.size() * 1.0 / cltCount.get(i));
//				System.out.println("contains:" + i + "\t" + termEntityCount.get(i));
			} else inverseFreq[i] = 0;
		}
		double bugNorm = 0;
		for (int index : bugCLTFreq.keySet()) {
			bugNorm += bugCLTFreq.get(index) * bugCLTFreq.get(index) * inverseFreq[index] * inverseFreq[index];
		}	
		for (int hunk : hunkId) {
			HashMap<Integer,Double> cltFreq = hunkCLTFreq.get(hunk);
			double hunkNorm = 0;
			for (int index : cltFreq.keySet()) {
				hunkNorm += cltFreq.get(index) * cltFreq.get(index) * inverseFreq[index] * inverseFreq[index];
			}
			HashSet<Integer> intersect = new HashSet<Integer>();
			intersect.addAll(cltFreq.keySet());
			intersect.retainAll(bugCLTFreq.keySet());
//			System.out.println(cltFreq.toString());
//			System.out.println(bugCLTFreq.toString());
			double cosine = 0;
			for (int index : intersect) {
				cosine += bugCLTFreq.get(index) * cltFreq.get(index) * inverseFreq[index] * inverseFreq[index];
			}
			double similarity = 0;
			if (bugNorm > 0 && hunkNorm > 0) 
				similarity = cosine / (Math.sqrt(bugNorm) * Math.sqrt(hunkNorm));
			results.put(hunk, similarity);
			
		}		
		return results;
	}
	
	public HashMap<String,Double> getRankingResults(HashMap<Integer,Double> results, boolean isChangeLevel) {
		HashMap<String,Double> sourceSimis = new HashMap<String,Double>();
		for (int hunk : results.keySet()) {
			String eid = "";
			if (isChangeLevel)
				eid = hunkChangeMap.get(hunk);
			else eid = hunkSourceMap.get(hunk);
			if (eid.equals("")) continue;
//			if (!semanticHids.contains(hunk)) continue;
			if (!sourceSimis.containsKey(eid)) {
				sourceSimis.put(eid, results.get(hunk));
			}
			else if (sourceSimis.get(eid) < results.get(hunk)) {
				sourceSimis.put(eid, results.get(hunk));
			}
		}
		return sourceSimis;
	}
	
	private HashMap<String,Double> combineResults(HashMap<String,Double> r1, HashMap<String,Double> r2, double ratio) {
		HashMap<String,Double> r = new HashMap<String,Double>();
		double a = 1.0;
		HashSet<String> indexes = new HashSet<String>();
		indexes.addAll(r1.keySet());
		indexes.addAll(r2.keySet());
		for (String index : indexes) {
			double v1 = 0;
			double v2 = 0;
			if (!r1.containsKey(index) || Double.isNaN(r1.get(index))) v1 = 0;
			else v1 = r1.get(index);
			
			if (!r2.containsKey(index) || Double.isNaN(r2.get(index))) v2 = 0;
			else v2 = r2.get(index);
			r.put(index, v1 * a + v2 * ratio);
		}
		return r;
	}
	
	public String getTimeString() {
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		String str = dayTime.format(new Date(time));
		return str;
	}
	
	public HashMap<Integer, HashMap<String, Double>> getResults(boolean isChangeLevel) {
		List<String> linesNL = new ArrayList<String>();
		List<String> linesCLT = new ArrayList<String>();
		List<String> combineResults = new ArrayList<String>();
		HashMap<Integer, HashMap<String, Double>> bugChangeResults = new HashMap<Integer, HashMap<String,Double>>();
		String resultNLFile = loc + File.separator + "resultsNL_" + (isChangeLevel ? "change":"file") + ".txt";
		String resultCLTFile = loc + File.separator + "resultsCLT_" + (isChangeLevel ? "change":"file") + ".txt";
		String resultFile = loc + File.separator + "results_" + (isChangeLevel ? "change":"file") + ".txt";
		double lambda = Main.lambda;
		
		List<Pair<Integer, Long>> bugRank = new ArrayList<Pair<Integer, Long>>();
		for (int i = 0; i < bugs.size(); i++)
			bugRank.add(new Pair<Integer,Long>(i, bugs.get(i).reportTime));
		
		Collections.sort(bugRank);
		
		int count = 0;
		int percent = 0;
		int max = bugRank.size();
		System.out.println("Calculating scores for "+bugRank.size()+" bugs...");
		for (int b = 0; b < bugRank.size(); b++) {
			count++;
			
			Bug bug = bugs.get(bugRank.get(b).getKey());
			int bid = bug.id;
			List<Integer> hunks = new ArrayList<Integer>();
			for (int i = 0; i < hunkIndex.size(); i++) {
				if (potentialChanges.get(bid).contains(hunkChangeMap.get(i)) && validHunks.contains(i))
					hunks.add(i);
			}
			System.out.println("["+ count + "/"+ bugRank.size() +"] processing bug:" + bid + "\t" + hunks.size());
			List<Integer> NLHunksList = new ArrayList<Integer>(hunks);
			List<Integer> CLTHunksList = new ArrayList<Integer>(hunks);
			
			//Calculated Score
			updateCorpusNL(NLHunksList, isChangeLevel);
			HashMap<Integer,Double> resultNL = getVSMScoreNL(bug,NLHunksList,isChangeLevel);
			HashMap<Integer,Double> resultCLT = getVSMScoreCLT(bug,CLTHunksList,isChangeLevel);
			
			//Convert HashMap to line text by using StringBuilder // 2018-01-28 modified
			StringBuilder buffer = new StringBuilder();
			buffer.append(bug.id);
			for (int hid : resultNL.keySet()) {
				buffer.append("\t").append(hid).append(":").append(resultNL.get(hid));				
			}			
			linesNL.add(buffer.toString());
			
			//Convert HashMap to line text by using StringBuilder // 2018-01-28 modified
			buffer = new StringBuilder();
			buffer.append(bug.id);
			for (int hid : resultCLT.keySet()) {
				buffer.append("\t").append(hid).append(":").append(resultCLT.get(hid));				
			}			
			linesCLT.add(buffer.toString());
			
			//System.out.println("\t\t["+getTimeString()+"]["+ count + "/"+ bugRank.size() +"] linesCLT added: ");
			
			double bugCLTWeight = lambda * bugCLTIndex.get(bid).size() * 1.0 / bugTermList.get(bid).size();
			if (bugCLTWeight > 1) bugCLTWeight = 1;
			
			//System.out.println("\t\t["+getTimeString()+"]["+ count + "/"+ bugRank.size() +"] weighting out: ");
			
			HashMap<String,Double> entitySimisNL = getRankingResults(resultNL, isChangeLevel);
			HashMap<String,Double> entitySimisCLT = getRankingResults(resultCLT, isChangeLevel);			
			HashMap<String, Double> result = combineResults(entitySimisNL, entitySimisCLT, bugCLTWeight);			

			//Convert map into the line text (using StringBuilder)  // 2018-01-28
			buffer = new StringBuilder();
			buffer.append(bug.id);
			for (String entity : result.keySet()) {
				buffer.append("\t").append(entity).append(":").append(result.get(entity));				
			}
			combineResults.add(buffer.toString());
			
			//add the result 
			bugChangeResults.put(bid, result);
			
			//show progress
			int newpercent = (int)((count*100) / (double)max);					
			if (newpercent > percent){
				if (newpercent != 0 && newpercent%10==0)
					System.out.print(",");
				else if(newpercent%2==0)
					System.out.print(".");
				percent = newpercent;
			}		
		}
		System.out.print("saving...");
		WriteLinesToFile.writeLinesToFile(linesCLT, resultCLTFile);
		WriteLinesToFile.writeLinesToFile(linesNL, resultNLFile);
		WriteLinesToFile.writeLinesToFile(combineResults, resultFile);
		System.out.println("Done");
		return bugChangeResults;
	}
	
	public HashMap<Integer, HashMap<String, Double>> obtainSimilarity(boolean isChangeLevel) {
		loadBugFiles();
		System.out.println("loaded bugFiles.");
		loadCLTIndex();
		System.out.println("loaded CLTIndex.");
		loadHunkFiles();
		System.out.println("loaded hunkFiles.");
		return getResults(isChangeLevel);
	}
	
}
