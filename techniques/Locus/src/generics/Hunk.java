package generics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Hunk {
	public String sourceFile;
	public String preChangeSet;
	public String postChangeSet;
	public List<String> codes;

/* 
 *  The following four variable means the 
 *  start point and length of the hunk in the source code before modification and
 *  start point and length of the hunk in the source code after the modification
 * */
	public int bs;
	public int bl;
	public int as;
	public int al;
	
	/*
	 *  -1 means delete
	 *  0  means do not modifying
	 *  1  means add
	 */
	public List<Integer> mark; 
	
	public Hunk(int bs, int bl, int as, int al, String sourceFile, String preChangeSet, String postChangeSet) {
		this.bs = bs;
		this.bl = bl;
		this.as = as;
		this.al = al;
		this.sourceFile = sourceFile;
		this.preChangeSet = preChangeSet;
		this.postChangeSet = postChangeSet;
		codes = new ArrayList<String>();
		mark = new ArrayList<Integer>();
	}
	
	public void addCode(String code) {
		// Remember to remove the code mark and store the code into mark

		if (code.startsWith("-")) {
			code = code.substring(1);
			mark.add(-1);
		}
		else if (code.startsWith("+")) {
			code = code.substring(1);
			mark.add(1);
		}
		else mark.add(0);
		codes.add(code);
	}
	
	public void setCodes(List<String> codes) {
		this.codes = codes;
	}
	
	public boolean isSemantic() {
		boolean flag = false;
		for (int i = 0; i < codes.size(); i++) {
			if (mark.get(i) == 1 || mark.get(i) == -1) {
				String code = codes.get(i).trim();
				if (!code.equals("") && !code.startsWith("*") && !code.startsWith("//") && !code.startsWith("/*")) {
					flag = true;
					break;
				}
			}
		}
		return flag;
	}
	
	public String getPostLine(int index) {
//		System.out.println(mark.toString());
		int rIndex = 0;
		int count = 0;
		while (count < index) {
			if (mark.get(rIndex) != 1) count++;
			rIndex++;
		}
		if (mark.get(rIndex - 1) != 1) { 
			if (codes.get(rIndex - 1).trim().length() <= 5)
				return "";
			else return codes.get(rIndex - 1).trim();
		}
		else return "";
	}
	
	public boolean isValid(String line) {
		line = line.trim();
		if (line.equals("") || line.startsWith("/*") || line.startsWith("//") || line.startsWith("*")) return false;
		return true;
	}
	
	public HashSet<Integer> getChangedLines() {
		HashSet<Integer> lines = new HashSet<Integer>();
		List<Integer> preMark = new ArrayList<Integer>();
		List<String> preLine = new ArrayList<String>();
		for (int i = 0; i < mark.size(); i++)
			if (mark.get(i) != 1) {
				preLine.add(codes.get(i));
				preMark.add(mark.get(i));
			}
		for (int i = this.bs; i < this.bl + this.bs; i++) {
			if (preMark.get(i - this.bs) == -1 && isValid(preLine.get(i - this.bs))) lines.add(i);
		}
		return lines;
	}
	
	public HashSet<Integer> getAllLines() {
		HashSet<Integer> lines = new HashSet<Integer>();
		List<Integer> preMark = new ArrayList<Integer>();
		List<String> preLine = new ArrayList<String>();
		for (int i = 0; i < mark.size(); i++)
			if (mark.get(i) != 1) {
				preLine.add(codes.get(i));
				preMark.add(mark.get(i));
			}
		for (int i = this.bs; i < this.bl + this.bs; i++) {
			if (isValid(preLine.get(i - this.bs))) lines.add(i);
		}
		return lines;	
	}
	
	
	public String toString() {
		String format = preChangeSet + "\t" + postChangeSet + "\t" + sourceFile + "\n";
		format += "-" + bs + "," + bl + "\t+" + as + "," + al;
		for (String code : codes) {
			format += "\n" + code;
		}
		return format;
	}
}

