package generics;


import java.util.ArrayList;
import java.util.List;

public class Patch {
	public int id;
	public int bid;
	public String diff;
	public String preFile;
	public String postFile;
	public String sourceFile;
	public String preChangeSet;
	public String postChangeSet;
	public List<String> content;
	public List<Hunk> hunks;
	
	
	public Patch(int id, int bid, String diff, String preFile, String postFile) {
		this.id = id;
		this.bid = bid;
		this.diff = diff;
		this.preFile = preFile;
		this.postFile = postFile;
		content = new ArrayList<String>();
		hunks = new ArrayList<Hunk>();
		parseDiff();
	}
	
	// This constructor is for patches extracted from commid log, thus without a patch id
	public Patch(String diff, String preFile, String postFile) {
		this.id = 0;
		this.bid = 0;
		this.diff = diff;
		this.preFile = preFile;
		this.postFile = postFile;
		content = new ArrayList<String>();	
		hunks = new ArrayList<Hunk>();
		parseDiff();
	}
	
	private void parseDiff() {
		String[] tmp = diff.split(" ");
		if (tmp.length != 6) {
			System.out.print("!");
			//System.out.println("Parse Error !!! " + diff);
			return;
		}
		sourceFile = tmp[5];
		preChangeSet = tmp[2];
		postChangeSet = tmp[4];	
	}
	
	public void addContent(String a) {
		content.add(a);
	}
	
	public void addContent(List<String> content) {
		for (int i = 0; i < content.size(); i++) 
			this.content.add(content.get(i));
	}
	
	public void extractHunks() {
		if (hunks.size() == 0) {
			Hunk hunk = null;
			for (int i = 0; i < content.size(); i++) {
				//System.out.println(content.get(i));
				if (content.get(i).startsWith("@@")) {
					if (hunk != null) hunks.add(hunk);
					String[] tmp = content.get(i).split(" ");
					if (!tmp[1].contains(",") || !tmp[2].contains(",")) continue;
//					System.out.println(content.get(i));
					int bs = Integer.parseInt(tmp[1].substring(1,tmp[1].indexOf(",")));
					int bl = Integer.parseInt(tmp[1].substring(tmp[1].indexOf(",") + 1));
					int as = Integer.parseInt(tmp[2].substring(1,tmp[2].indexOf(",")));
					int al = Integer.parseInt(tmp[2].substring(tmp[2].indexOf(",") + 1));
					hunk = new Hunk(bs,bl,as,al,postFile.trim(),preChangeSet,postChangeSet);
				}
				else {
					if (hunk != null) 
						hunk.addCode(content.get(i));
				}
			}
			hunks.add(hunk);
		}
	}
	
	public String getContent() {
		String ans = "";
		for (int i = 0; i < content.size(); i++)
			ans += (content.get(i) + "\n");
		return ans;
	}
}
