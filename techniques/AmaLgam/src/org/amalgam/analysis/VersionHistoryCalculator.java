package org.amalgam.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.amalgam.common.Property;
import org.amalgam.models.Bug;
import org.amalgam.models.CommitItem;
import org.amalgam.models.FileObjs;


/**
 * 버전 컨트롤 시스템에 존재하는 모든 파일들에 대해서 버전 히스토리를 로드하여 히스토리 스코어 계산.
 * 생성자에서 커밋정보를 로드하고, 입력받은 버그리포트에 대해서 historical score를 계산.
 * @author Zeck
 *
 */
public class VersionHistoryCalculator {
	
	private HashMap<Integer, HashSet<CommitItem>> fileHistories;	//all fixed File in Source code Repository,  commit dates.

	public VersionHistoryCalculator(HashMap<Integer, HashSet<CommitItem>> _fileHistories) throws IOException, ParseException {
		this.fileHistories = _fileHistories;
	}

	/**
	 * 지정된 버그리포트에 대해서 Version History Suspecious Scoure를 계산.
	 * @param bug
	 * @param days_back
	 * @param topk
	 * @return
	 */
	public HashMap<Integer, Double> computeBugSuspeciousScore(Bug bug, int days_back, int topk) {

		//get Start Date
		Date commitDate = bug.commitDate;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(commitDate);
		calendar.add(Calendar.DAY_OF_YEAR, -days_back);
		Date startDate = calendar.getTime();
		
		long rangeTimeInMin = (commitDate.getTime() - startDate.getTime()) / (1000 * 60);
		
		

		//버전 히스토리에 존재하는 모든 파일에 대해서,
		HashMap<Integer, Double> fileHistoryScores = new HashMap<Integer, Double>();
		for (Integer fileID : this.fileHistories.keySet()) {

			//해당 버그리포트의 결과 파일에 포함되지 않으면 out
			if (!bug.containResultFile(fileID))	continue;
			
			//입력받은 버그리포트와의 history score 계산.
			double score = 0;
			for (CommitItem commit : fileHistories.get(fileID)) 
			{
				// CommitDate(bug)-15 < CommitDate(file) < CommitDate(bug)
				Date fileDate = commit.commitDate;
				if (!(fileDate.before(commitDate) && !fileDate.before(startDate))) continue;
			
				double normalized_t = (double) ((fileDate.getTime() - startDate.getTime()) / (1000 * 60))	/ (double) rangeTimeInMin;
				score += 1 / (1 + Math.exp(-12 * normalized_t + 12));
			
			}
			if (score > 0) {
				fileHistoryScores.put(fileID, score);
			}
		}
		// sort and get topk result
		if (topk != 0)
			return getTopk(fileHistoryScores, topk);

		return fileHistoryScores;
	}

	/**
	 * 수정된 파일들에 대해서 TopN개의 파일을 추천
	 * @param fileHistoryScores
	 * @param topk
	 * @return
	 */
	private HashMap<Integer, Double> getTopk(HashMap<Integer, Double> fileHistoryScores, int topk) {
		HashMap<Integer, Double> topKResult = new HashMap<Integer, Double>();
		
//		ArrayList<Entry<String,Double>> results = new ArrayList<Entry<String,Double>>();
//		for (Entry<String,Double> scores : fileHistoryScores.entrySet()) {
//			results.add(scores);
//		}		
		ArrayList<Entry<Integer, Double>> results = new ArrayList<Entry<Integer, Double>>(fileHistoryScores.entrySet());
		results.sort(new EntryComparator());
		
		for (int i = 0; i < topk && i < results.size(); i++) {
			topKResult.put(results.get(i).getKey(), results.get(i).getValue());
		}

		return topKResult;
	}
	
	/**
	 * Compare Function
	 * @param a
	 * @param b
	 * @return
	 */
	class EntryComparator implements Comparator<Entry<Integer, Double>>{
	    @Override
	    public int compare(Entry<Integer, Double> a, Entry<Integer, Double> b) {
	        return  b.getValue().compareTo(a.getValue());
	    }
	}

	/**
	 * Historical Score 계산 결과를 저장
	 * @param bugObjs
	 * @throws IOException
	 */
	public void storeScores(HashMap<String, Bug> bugObjs) throws IOException
	{
		String historicalSocre = Property.getInstance().WorkDir + Property.getInstance().Separator + "Historical_Score.txt";
		BufferedWriter bw = new BufferedWriter(new FileWriter(historicalSocre));
		for (Bug bug : bugObjs.values()) {
			
			for (Integer fid : bug.historicalScores.keySet()){
				
				String filename = FileObjs.get(fid);
				Double score = bug.historicalScores.get(fid);
				if (score==null) continue;
				
				//output
				bw.write(bug.ID + " " + filename + " null " + score);
				bw.newLine();
			}
		}
		bw.close();
	}


}
