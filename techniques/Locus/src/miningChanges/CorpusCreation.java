package miningChanges;

import java.io.File;
import java.util.*;
import utils.ChangeLocator;
import utils.ExtractCodeElementsFromSourceFile;
import utils.FileListUnderDirectory;
import utils.FileToLines;
import utils.GitHelp;
import utils.ReadBugsFromXML;
import generics.Bug;
import generics.Pair;
import generics.Commit;
import generics.Hunk;
import utils.Splitter;
import utils.Stem;
import utils.Stopword;
import utils.WriteLinesToFile;

public class CorpusCreation {

	public static String loc = main.Main.settings.get("workingLoc");
	public static String repo = main.Main.settings.get("repoDir");
	public static HashSet<String> concernedCommits;
	public static HashMap<String,String> changeMap;
	public static HashMap<String,Integer> sourceFileIndex;
	
	public static void getCommitsOneLine() throws Exception{
		String logFile = loc + File.separator + "logOneline.txt";
		File file = new File(logFile);
		if (!file.exists()) {
			String content = GitHelp.getAllCommitOneLine(logFile);
			WriteLinesToFile.writeToFiles(content, logFile);
		}
	}

	public static void loadCommits() {
		String commitFile = "";
		if (main.Main.settings.containsKey("concernedCommit"))
			commitFile = main.Main.settings.get("concernedCommit");
		else commitFile = loc + File.separator + "concernedCommits.txt";
		List<String> lines = null;
		concernedCommits = new HashSet<String>();
		lines = FileToLines.fileToLines(commitFile);
		for (String line : lines) {
 //         System.out.println(line);
			String tmp = line.split("\t")[1];
			String[] commits = tmp.substring(1, tmp.length() - 1).split(",");
			for (String commit : commits)
			concernedCommits.add(commit.trim());
		}
//		System.out.println(concernedCommits.toString());
		changeMap = ChangeLocator.getShortChangeMap();
	}
	
	public static List<String> getProcessedWords(String content) {
		String[] words = Splitter.splitSourceCode(content);
		List<String> processedWords = new ArrayList<String>();
		for (String word : words) {
			word = Stem.stem(word.toLowerCase());
			if (Stopword.isEnglishStopword(word) || Stopword.isKeyword(word)) continue;
			if (word.trim().equals("")) continue;
			processedWords.add(word);
		}
		return processedWords;
	}
	
	private static int getFileIndex(String filename) {
		List<Pair<Integer,Double>> candidateFileSet = new ArrayList<Pair<Integer,Double>>();
		String[] splits = filename.split("\\.");
        String className = splits[splits.length - 2] + "." + splits[splits.length - 1];
        HashSet<String> fileTokens = new HashSet<>();
        for (String split : splits)
            fileTokens.add(split);
        for (String source : sourceFileIndex.keySet()) {
            if (!source.endsWith(className))
                continue;
            splits = source.split("\\.");
            HashSet<String> sourceTokens = new HashSet<>();
            for (String split : splits)
                sourceTokens.add(split);
            HashSet<String> tokens = new HashSet<>(fileTokens);
            tokens.retainAll(sourceTokens);
            HashSet<String> allTokens = new HashSet<>(fileTokens);
            allTokens.addAll(sourceTokens);
            candidateFileSet.add(new Pair<>(sourceFileIndex.get(source), tokens.size() * 1.0 / allTokens.size()));
		}
		Collections.sort(candidateFileSet);
		int index = -1;
		if (candidateFileSet.size() > 0)
		    index = candidateFileSet.get(candidateFileSet.size() - 1).getKey();
		return index;
	}

	private static int getFileIndexExactMatch(String filename) {
		for (String source : sourceFileIndex.keySet()) {
			if (source.contains(filename) || filename.contains(source))
				return sourceFileIndex.get(source);
		}
		return -1;
	}
	
	public static void processBugReports() {
		String proDir = main.Main.settings.get("workingLoc");
		List<Bug> bugs = ReadBugsFromXML.getFixedBugsFromXML(main.Main.settings.get("bugReport"));
		File file = new File(proDir + File.separator + "bugText");
		if (!file.exists()) file.mkdir();
		
		System.out.println("The number of bugs:" + bugs.size());
		ExtractCodeLikeTerms eclt = new ExtractCodeLikeTerms();
		HashMap<String, Integer> cltMaps = eclt.extractCodeLikeTerms();
		List<String> lines = new ArrayList<String>();
		List<String> bugSourceLink = new ArrayList<String>();
		for (Bug bug : bugs) {
			String bugContent = bug.toString();
			List<String> processedWords = getProcessedWords(bugContent);
			String fileName = file.getAbsolutePath() + File.separator + bug.id + ".txt";
			WriteLinesToFile.writeLinesToFile(processedWords, fileName);
			String content = bug.summary + " " + bug.description;
			List<String> clts = eclt.extractCLTFromNaturalLanguage(content);
//			System.out.println(bug.id + "\t" + clts.toString());
            String line = "" + bug.id;
			for (String clt : clts) {
				if (cltMaps.containsKey(clt))
				    line += "\t" + cltMaps.get(clt);
			}
			lines.add(line);
			
			line = bug.id + "";
			for (String buggyFile : bug.buggyFiles) {
				int index = getFileIndexExactMatch(buggyFile);
				if (index == -1) continue;
                line += "\t" + index;
			}
			bugSourceLink.add(line);
		}
		String filename = main.Main.settings.get("workingLoc") + File.separator + "bugCLTIndex.txt";
		WriteLinesToFile.writeLinesToFile(lines, filename);
		filename = main.Main.settings.get("workingLoc") + File.separator + "bugSourceIndex.txt";
		WriteLinesToFile.writeLinesToFile(bugSourceLink, filename);
	}
	
	public static boolean processHunks() throws Exception {
        String saveFile = loc + File.separator + "hunkIndex.txt";
        File file = new File(saveFile);
        if (file.exists() && file.length() != 0) {
            System.out.println("Hunks have already been extracted");
            return true;
        }
        System.out.print("Extracting Hunks");
		String revisionLoc = loc + File.separator + "revisions";
		if (main.Main.settings.containsKey("revisionsLoc"))
			revisionLoc = main.Main.settings.get("revisionsLoc");
//		file = new File(revisionLoc);
//		if (!file.exists())
//			file.mkdir();
		
//		File f = new File(loc + File.separator + "hunkCode");
//		if (!f.exists()) f.mkdir();
//		f = new File(loc + File.separator + "hunkLog");
//		if (!f.exists()) f.mkdir();
		
		int count = 0;
		int percent = 0;
		int max = concernedCommits.size();
		List<String> hunkIndex = new ArrayList<String>();
		List<String> hashCLTIndex = new ArrayList<String>();
		List<String> hunkCLTIndex = new ArrayList<String>();
		HashMap<String, List<String>> hunkCodeTerms = new HashMap<String, List<String>>();			
		HashMap<String, List<String>> hunkLogTerms = new HashMap<String, List<String>>();
		
		ExtractCodeLikeTerms eclt = new ExtractCodeLikeTerms();
		HashMap<String,Integer> cltMaps = eclt.extractCodeLikeTerms();
		HashMap<Integer, HashSet<Integer>> sourceHunkLinks = new HashMap<Integer, HashSet<Integer>>();
		for (String hash : concernedCommits) {
			count++;
			//System.out.println(count + ":" + concernedCommits.size());
			
			if (!changeMap.containsKey(hash)) continue;
			//System.out.println(fullHash);
			// adaptpr for project ChangeLocator
			
			String fullHash = changeMap.get(hash);
            String parentPath = revisionLoc + File.separator + fullHash.substring(0,2)+ File.separator +fullHash.substring(2,4);
			String commitFile = parentPath + File.separator + fullHash + ".txt";

			file = new File(parentPath);
            if (!file.exists()) file.mkdir();
			
			file = new File(commitFile);
			if (!file.exists() || file.length() == 0) {
                String content = GitHelp.gitShow(hash, repo);
                WriteLinesToFile.writeToFiles(content, commitFile);
			}
			
			Commit commit = utils.ReadHunksFromLog.readOneCommitWithHunkGit(commitFile);
			List<Hunk> hunks = commit.getAllHunks();
			if (hunks == null) continue;
//			System.out.println(hash + "\t" + hunks.size());
			
			String content = commit.description;
			for (String tmp : commit.files) {
				content += " " + tmp;
			}
			List<String> clts = eclt.extractCLTFromNaturalLanguage(content);
			String line = "" + hash;
			for (String clt : clts) {
//				if (cltMaps.get(clt) == null) System.out.println(clts.toString());
                if (cltMaps.containsKey(clt))
				    line += "\t" + cltMaps.get(clt);
			}
			hashCLTIndex.add(line);
			
			for (Hunk hunk : hunks) {
				boolean flag = isValid(hunk);
//				System.out.println(flag);
				if (!flag) continue;
				String sourceFile = hunk.sourceFile;
				
				if (!sourceFile.endsWith(".java")) continue;
				
				sourceFile = sourceFile.substring(2,sourceFile.length()).replace("/", ".");
				
				String savePath = hash + "@" + hunk.preChangeSet + "@" + hunk.postChangeSet + "@" + sourceFile + "@" + hunk.bs + "@" + hunk.bl + "@" + hunk.as + "@" + hunk.al + ".txt";
				
				content = commit.description + " " + commit.userName;
				List<String> words = CorpusCreation.getProcessedWords(content);
				hunkLogTerms.put(savePath, words);
//				WriteLinesToFile.writeLinesToFile(words, loc + File.separator + "hunkLog" + File.separator + savePath);

				content = sourceFile + " ";
				List<String> codes = hunk.codes;
				for (String code : codes) {
					content += code + " ";
				}
				words = CorpusCreation.getProcessedWords(content);
				hunkCodeTerms.put(savePath, words);
				//WriteLinesToFile.writeLinesToFile(words, loc + File.separator + "hunkCode" + File.separator + savePath);				
				
				int index = hunkIndex.size();
				int sourceIndex = getFileIndexExactMatch(sourceFile);
				if (sourceIndex == -1) continue;
                if (!sourceHunkLinks.containsKey(sourceIndex)) {
					sourceHunkLinks.put(sourceIndex, new HashSet<Integer>());
				}
				sourceHunkLinks.get(sourceIndex).add(index);
				
				hunkIndex.add(savePath + "\t" + hunk.isSemantic());
				
				String unchangedCode = "";
				String deleteCode = "";
				String addCode = "";
				
				List<Integer> mark = hunk.mark;
				for (int j = 0; j < codes.size(); j++) {
					String code = codes.get(j);
					if (mark.get(j) == -1) deleteCode += " " + code;
					else if (mark.get(j) == 0) unchangedCode += " " + code;
					else if (mark.get(j) == 1) addCode += " " + code;
				}
				
				clts = eclt.extractCLTFromCodeSnippet(sourceFile);
				line = index + ":f";
				for (String clt : clts) {
					if (cltMaps.containsKey(clt))
					    line += "\t" + cltMaps.get(clt);
				}
				hunkCLTIndex.add(line);
				clts = eclt.extractCLTFromCodeSnippet(unchangedCode);
				line = index + ":0";
				for (String clt : clts) {
					if (cltMaps.containsKey(clt))
					    line += "\t" + cltMaps.get(clt);
				}
				hunkCLTIndex.add(line);
				clts = eclt.extractCLTFromCodeSnippet(deleteCode);
				line = index + ":-1";
				for (String clt : clts) {
					if (cltMaps.containsKey(clt))
					    line += "\t" + cltMaps.get(clt);
				}
				hunkCLTIndex.add(line);
				clts = eclt.extractCLTFromCodeSnippet(addCode);
				line = index + ":1";
				for (String clt : clts) {
					if (cltMaps.containsKey(clt))
                        line += "\t" + cltMaps.get(clt);
				}
				hunkCLTIndex.add(line);
			}			

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
		WriteLinesToFile.writeStringListsWithString(hunkLogTerms, loc + File.separator + "hunkLog.txt");
		WriteLinesToFile.writeStringListsWithString(hunkCodeTerms, loc + File.separator + "hunkCode.txt");
		WriteLinesToFile.writeLinesToFile(hunkIndex, saveFile);
		WriteLinesToFile.writeLinesToFile(hashCLTIndex, loc + File.separator + "commitCLTIndex.txt");
		WriteLinesToFile.writeLinesToFile(hunkCLTIndex, loc + File.separator + "hunkCLTIndex.txt");
		
		List<String> lineStrings = new ArrayList<String>();
		for (int sourceId : sourceHunkLinks.keySet()) {
			String line = "" + sourceId;
			HashSet<Integer> hids = sourceHunkLinks.get(sourceId);
			for (int hid : hids)
				line += "\t" + hid;
			lineStrings.add(line);
		}
		WriteLinesToFile.writeLinesToFile(lineStrings, loc + File.separator + "sourceHunkLink.txt");
		System.out.println("Done.");
		
		return true;
	}
	
	private static boolean isValid(Hunk hunk) {
		
		if (hunk == null) return false;
		if (hunk.sourceFile == null || hunk.preChangeSet == null || hunk.postChangeSet == null || hunk.codes == null) 
			return false;
		else if (hunk.codes.size() == 0) return false;
		else return true;
	}
	
	public static void processSourceFiles() {
		String codeLikeTermFile = loc + File.separator + "codeLikeTerms.txt";
		String indexFilename = loc + File.separator + "sourceFileIndex.txt";
		
		// make index file if there is no exist the according file.
        File indexFile = new File(indexFilename);
        if (!indexFile.exists()) {
        	
        	List<String> classList = new ArrayList<String>();
    		HashSet<String> allCodeTermsHashSet = new HashSet<String>();
            List<String> files = FileListUnderDirectory.getFileListUnder(main.Main.sourceDir, ".java");
    		
    		for (int i = 0; i < files.size(); i++) {
    			String className = files.get(i).replace("/", ".");
//    			System.out.println(className);
    			className = className.replace("\\", ".");
    			className = className.replace("..", ".");
    			String[] clts = className.split("\\.");
    			// 2018-01-19 :: removed for including test files.
    			//if (!loc.toLowerCase().contains("zxing") && className.toLowerCase().contains("test")) continue;
    			String prefix = main.Main.settings.get("sourceDir").replace("/", ".");
    			prefix = prefix.replace("\\", ".");
    			prefix = prefix.replace("..", ".");
    			int index = className.indexOf(prefix);
    			className = className.substring(index + prefix.length() + 1);
    			classList.add(className);
    			
    			for (String term : clts) {
    				if (!term.contains(".")) {
    					String tmp2 = term.toLowerCase();
    					allCodeTermsHashSet.add(tmp2);

    				}
    				else {
    					String tmp2 = term.substring(0,term.indexOf(".")).toLowerCase();
    					allCodeTermsHashSet.add(tmp2);
    				}
    			}
    			// 2018-01-19 :: removed for including test files.
    			//if (className.toLowerCase().contains("test")) continue;
    			HashSet<String> codeElements = ExtractCodeElementsFromSourceFile.extractCodeElements(files.get(i));
    			allCodeTermsHashSet.addAll(codeElements);
    		}
    		
    		HashMap<String, Integer> cltMap = new HashMap<String, Integer>();
    		for (String item : allCodeTermsHashSet) {
    			
    			if (!isValid(item.toLowerCase())) continue;
                if (!cltMap.containsKey(item.toLowerCase()))
                    cltMap.put(item.toLowerCase(), cltMap.size());
    		}
    		
    		List<String> lines = new ArrayList<String>();
    		for (String key : cltMap.keySet())
    			lines.add(key + "\t" + cltMap.get(key));
    		
    		WriteLinesToFile.writeLinesToFile(classList, indexFilename);
    		WriteLinesToFile.writeLinesToFile(lines, codeLikeTermFile);        	
        }

        sourceFileIndex = new HashMap<>();
        List<String> lines = FileToLines.fileToLines(indexFilename);
        int count = 0;
        for (String line : lines) {
            sourceFileIndex.put(line, count++);
        }
        return;

	}
	
	private static boolean isValid(String term) {
		boolean flag = true;
		if (term.length() < 5 && !term.contains("_")) flag = false;
		if (Stopword.isEnglishStopword(term) || Stopword.isKeyword(term)) flag = false;
		return flag;
	}
	
	public static boolean createCorpus() throws Exception {
		loadCommits();
		System.out.println("Indexing source files....");
        processSourceFiles();
        System.out.println("Indexing bug reports....");
		processBugReports();
		System.out.println("Indexing hunks...");
		return processHunks();
	}
}
