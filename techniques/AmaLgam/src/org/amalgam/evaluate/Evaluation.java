package org.amalgam.evaluate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;

import org.amalgam.common.Property;
import org.amalgam.models.Bug;
import org.amalgam.models.FileObjs;

public class Evaluation {
	
	private int top1 = 0;
	private int top5 = 0;
	private int top10 = 0;

	public void run(HashMap<String, Bug> bugs, HashMap<String,Double> weights){
		
		
		//추천순위 계산
		for(Bug b : bugs.values()){
			b.makeResults(weights);
		}
			
		//평가
		try {
			makeTopKResult(bugs);
		} catch (IOException e) {
			System.out.println();
			e.printStackTrace();
			
		}
		double map = makeMAP(bugs);
		double mrr = makeMRR(bugs);
		
		//평가 결과 출력		
		System.out.println("Top1 : "+ top1 + "(" + ((double)top1/bugs.size()) +")");
		System.out.println("Top5 : "+ top5 + "(" + ((double)top5/bugs.size()) +")");
		System.out.println("Top10 : "+ top10 + "(" + ((double)top10/bugs.size()) +")");
		System.out.println("MAP:" + map);
		System.out.println("MRR:" + mrr);
		
	}
	/**
	 * 
	 * @param k
	 * @param bugobjs
	 * @param weights
	 * @return
	 * @throws IOException 
	 */
	public int makeTopKResult(HashMap<String, Bug> bugobjs) throws IOException{
		String recommendedPath =  Property.getInstance().WorkDir + File.separator + "recommended" + File.separator;
		File dir = new File(recommendedPath);
		if (!dir.exists()) dir.mkdirs();
				
		BufferedWriter writer = new BufferedWriter(new FileWriter(Property.getInstance().OutputFile));
		
		for(Bug b : bugobjs.values()){
			BufferedWriter fullWriter = new BufferedWriter(new FileWriter(recommendedPath + b.ID + ".txt"));
			
			int local_top1=0;
			int local_top5=0;
			int local_top10=0;
			
			for(int rank=0; rank<b.results.size(); rank++){

				SimpleEntry<Integer, Double> item = b.results.get(rank);
				String fileName = FileObjs.get(item.getKey());
				Double score = item.getValue();
				fullWriter.write(rank + "\t" + score + "\t" + fileName + "\n");
				
				if(!b.groundtruth.containsKey(fileName)) continue;
				writer.write(b.ID + "\t" + fileName + "\t" + rank + "\t" + score + "\n");
				if (rank < 1) local_top1++;
				if (rank < 5) local_top5++;
				if (rank <10) local_top10++;
			}
			fullWriter.close();
			
			if (local_top1 >0 ) top1++;
			if (local_top5 >0 ) top5++;
			if (local_top10 >0 ) top10++;
		}
		writer.close();
		return 1;
	}

	/**
	 *  전체 버그리포트에 대한 MAP 계산
	 * @param bugobjs
	 * @param weights
	 * @return
	 */
	public double makeMAP( HashMap<String, Bug> bugobjs){
		double map = 0;
		for(Bug b : bugobjs.values()){
			
			//한 버그리포트에 대한 precision 계산
			double sum=0;
			int retrieved_d = 0;
			for(int rank =0 ; rank <b.results.size(); rank++){
				SimpleEntry<Integer, Double> item = b.results.get(rank);
				String fileName = FileObjs.get(item.getKey());
				
				if(b.groundtruth.containsKey(fileName)){
					retrieved_d++;
					double precision_i = (double)retrieved_d /(rank+1); 
					sum += precision_i;
				}
			}
			
			map += (sum/b.groundtruth.size());	//av_precision  for 1 bug;
		}
		
		return map/bugobjs.size();
	}
	
	/**
	 * 지정된 weights에 대해서 MRR 계산
	 * @param bugobjs
	 * @param weights
	 * @return
	 */
	public double makeMRR( HashMap<String, Bug> bugobjs){
		
		double mrr = 0;
		for(Bug b : bugobjs.values()){
			
			//한 버그리포트에 대한 precision 계산
			double rr=0;
			for(int rank =0 ; rank <b.results.size(); rank++){
				SimpleEntry<Integer, Double> item = b.results.get(rank);
				String fileName = FileObjs.get(item.getKey());
				
				if(b.groundtruth.containsKey(fileName)){
					rr = (double)1/(rank+1);
					break;
				}
			}
			mrr += rr;
		}
		
		return mrr/bugobjs.size();
	}
	
}
