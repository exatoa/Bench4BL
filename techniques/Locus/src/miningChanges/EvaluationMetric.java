package miningChanges;

import java.util.List;


public class EvaluationMetric {
	public static double MRR(List<List<Integer>> ranks) {
		double ans = 0;
		int size = 0;
		for (int i = 0; i < ranks.size(); i++) {
			List<Integer> rank = ranks.get(i);
//			System.out.println(rank.get(0) + 1);
			ans += RR(rank);
			if (ranks.get(i).size() > 0)
				size++;
		}
		return ans / size;
	}
	
	public static double RR(List<Integer> rank) {
		if (rank.size() == 0) return 0;
		double ans = 1.0 / (rank.get(0) + 1);
		return ans;
	}
	
	public static double MRLR(List<List<Integer>> ranks) {
		double ans = 0;
		for (int i = 0; i < ranks.size(); i++) {
			List<Integer> rank = ranks.get(i);
//			System.out.println(rank.get(0) + 1);
			ans += 1.0 / (rank.get(rank.size() - 1) + 1);
		}
		return ans / ranks.size();
	}
	
	public static double MAP(List<List<Integer>> ranks) {
		double ans = 0;
		int size = 0;
		for (int i = 0; i < ranks.size(); i++) {
			
			ans += AP(ranks.get(i));
//			System.out.println(AP(ranks.get(i)));
			if (ranks.get(i).size() > 0)
				size++;
		}
		return ans / size;
	}
	
	public static double AP(List<Integer> rank) {

		double tmp = 0.0;
		if (rank.size() == 0) return tmp;
		for (int j = 0; j < rank.size(); j++) {
			int r = j + 1;
			tmp += r * 1.0 /(rank.get(j) + 1);
		}
		return tmp / rank.size();
		
	}

	public static double[] topN(List<List<Integer>> ranks, int N) {
		int[] rank = new int[N];
		double[] results = new double[N];
		int size = 0;
		for (int i = 0; i < ranks.size(); i++) {
			if (ranks.get(i).size() > 0) {
				size++;
				int tmp = ranks.get(i).get(0);
				if (tmp < N) {
					rank[tmp] ++;
				}
			}
		}
		int tot = 0;
		for (int i = 0; i < N; i++) {
			results[i] = (rank[i] + tot) * 1.0 / size;
			tot += rank[i];
		}
		return results;
	}
	
	
	
	public static double[] topNMCR(List<List<Integer>> ranks, int N) {
		//int[][] rank = new int[ranks.size()][N];
		double[][] results = new double[ranks.size()][N];
		for (int i = 0; i < ranks.size(); i++) {
			int[] rank = new int[N];
			int index = 0;
			while (index < ranks.get(i).size() && ranks.get(i).get(index) < N) {
//				System.out.println(index + "\t" + ranks.get(i).get(index));
				rank[ranks.get(i).get(index)]++;
				index++;
			}
			int tot = 0;
			for (int j = 0; j < N; j++) {
				results[i][j] = (rank[j] + tot) * 1.0 / Math.min(j+1, ranks.get(i).size());
				tot += rank[j];
			}
		}
		
		double[] finalResults = new double[N];
		for (int i = 0; i < N; i++) {
			double tot = 0;
			for (int j = 0; j < ranks.size(); j++)
				tot += results[j][i];
			finalResults[i] = tot / ranks.size();
		}
		return finalResults;
	}
	
	public static void main(String[] args) {
		System.out.println("Hello World");
	}
}
