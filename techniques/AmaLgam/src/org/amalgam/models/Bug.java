package org.amalgam.models;

import java.util.AbstractMap.SimpleEntry;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import org.amalgam.common.Weights;

public class Bug implements Comparable<Bug> {
	public String ID;							//버그 아이디
	public HashMap<String, Integer> groundtruth;		//정답셋 (수정된 파일들)
	public HashMap<Integer, Double> similarityScores; //
	public HashMap<Integer, Double> historicalScores; //
	public Date commitDate ;
	
	//public ArrayList<resultObj> sortedList = null;	//정렬된 결과
	public ArrayList<SimpleEntry<Integer, Double>> results;

	
	public Bug(){
		this.groundtruth = new HashMap<String, Integer>();
		this.similarityScores= new HashMap<Integer, Double>();
		this.historicalScores= new HashMap<Integer, Double>();
		//this.result = new HashMap<String,resultObj>();
	}
	
	
	public Bug(String id){
		this.ID = id;
		this.groundtruth = new HashMap<String, Integer>();
		this.similarityScores= new HashMap<Integer, Double>();
		this.historicalScores= new HashMap<Integer, Double>();
	}
	
	public Bug(String id, Date commitDate){
		this.ID = id;
		this.commitDate = commitDate;
	}
	
	/**
	 * Historycal Score 를 추가
	 * @param fixedFile
	 * @param score
	 */	
	public void addHistoricalScore(String fixedFile, double score){
		int fid = FileObjs.put(fixedFile);
		if (!this.historicalScores.containsKey(fid))
			this.historicalScores.put(fid, score);
	}
	
	/**
	 * Similarity Score 를 추가
	 * @param fixedFile
	 * @param score
	 */	
	public void addSimilarityScore(String fixedFile, double score){
		int fid = FileObjs.put(fixedFile);
		if (!this.similarityScores.containsKey(fid))
			this.similarityScores.put(fid, score);
	}
	

	
	/**
	 * Historical Score 가져오기 with FileName  
	 * @param fixedFile
	 * @param score
	 */	
	public Double getHistoricalScore(String _filename){
		return this.historicalScores.get(FileObjs.put(_filename));
	}
	
	/**
	 * 수정된 파일목록을 추가
	 * @param fixedFile
	 */
	public void addLink(String fixedFile) {
		if(!this.groundtruth.containsKey(fixedFile))
			this.groundtruth.put(fixedFile, 1);
	}
	
	/**
	 * 설정된 가중치에 따라서 전체 파일에 대한 추천 점수 계산.	
	 * @param weights
	 */
	public void  makeResults(HashMap<String, Double> weights){
		results  = new ArrayList<SimpleEntry<Integer, Double>>();
		for(String filename : FileObjs.getFiles()){
			int fid = FileObjs.get(filename);
			Double hscore = this.historicalScores.get(fid);
			Double bscore = this.similarityScores.get(fid);
			if (hscore==null) hscore = 0.0;
			if (bscore==null) bscore = 0.0;
			double total = hscore * weights.get(Weights.HistoricalScoreName)
						 + bscore * weights.get(Weights.BugSimilarityName);
			
			if (total<=0.0) continue;
			results.add(new SimpleEntry<Integer, Double>(fid, total));
		}
		results.sort(new EntryComparator());
	}
	
	/**
	 * Compare Function
	 * @param a
	 * @param b
	 * @return
	 */
	class EntryComparator implements Comparator<SimpleEntry<Integer, Double>>{
	    @Override
	    public int compare(SimpleEntry<Integer, Double> a, SimpleEntry<Integer, Double> b) {
	        return  b.getValue().compareTo(a.getValue());
	    }
	}


	//Compare commit date other bug
	@Override
	public int compareTo(Bug arg0) {
		if(this.commitDate.after(arg0.commitDate))
			return 1;
		else if(this.commitDate.before(arg0.commitDate))
			return -1;
		else 
			return 0;
	}

	//Find bug with Key
	public boolean containResultFile(String _filename) {
		Integer fid = FileObjs.get(_filename);
		if (fid == null) return false;
		if(similarityScores.containsKey(fid) || historicalScores.containsKey(fid))
		//if(this.results.containsKey(name))
			return true;
		return false;
	}
	
	//Find bug with Key
	public boolean containResultFile(Integer _fid) {
		if (_fid == null) return false;
		if(similarityScores.containsKey(_fid) || historicalScores.containsKey(_fid))
		//if(this.results.containsKey(name))
			return true;
		return false;
	}

	
	/**
	 * 랭킹 결과 중 정답 셋의 랭크를 출력.
	 * @param _writer
	 * @throws IOException
	 */
	public void printResult(Writer _writer) throws IOException{
				
		for(int rank=0; rank<results.size(); rank++){

			SimpleEntry<Integer, Double> item = results.get(rank);
			String fileName = FileObjs.get(item.getKey());
			Double score = item.getValue();
			if(this.groundtruth.containsKey(fileName)) continue;

			_writer.write(ID + "\t" + fileName + "\t" + rank + "\t" + score + "\r\n");
		}		
	}
	/**
	 * 결과값 계산.
	 * @param k
	 * @return
	 */
	public boolean IsTopKHit(int k){
		for(int i =0 ; i <k && i < this.results.size(); i++){
			String fileName = FileObjs.get(this.results.get(i).getKey());
			if(this.groundtruth.containsKey(fileName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 개별 버그리포트의 평균 precision 계산. (for MAP)
	 * @return
	 */
	public double getAvPrecision() {
		// TODO Auto-generated method stub
		double sum=0;
		int retrieved_d = 0;
		for(int i =0 ; i <this.results.size(); i++){
			String fileName = FileObjs.get(this.results.get(i).getKey());
			if(this.groundtruth.containsKey(fileName)){
				retrieved_d++;
				double precision_i = (double)retrieved_d /(i+1); 
				sum += precision_i;
			}
		}
		return sum/this.groundtruth.size();
	}
	
	/**
	 * 개별 버그리포트의 RR계산
	 * @return
	 */
	public double getRR(){
		double rr = 0;
		for(int i =0 ; i <this.results.size(); i++){
			String filename = FileObjs.get(this.results.get(i).getKey());
			if(this.groundtruth.containsKey(filename)){
				rr = (double)1/(i+1);
				return rr;
			}
		}
		return rr;
	}
}
