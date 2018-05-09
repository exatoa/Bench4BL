package bluir.core;

import bluir.extraction.FactExtractor;
import bluir.extraction.QueryExtractor;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.eclipse.core.runtime.CoreException;

@Parameters(separators = "=")
public class Runner {
	@Parameter(names = { "-task" }, description = "Kind of Task (createquery, createdocs, index, retrieve)")
	static String task;
	@Parameter(names = { "-bugRepoLocation" }, description = "Location of Bug Repository")
	static String bugRepositoryLocation;
	@Parameter(names = { "-codeLocation" }, description = "Location of Source Code")
	static String codeDirectory;
	@Parameter(names = { "-docLocation" }, description = "Location of Document Collection")
	static String documentCollectionDirectory;
	@Parameter(names = { "-resultPath" }, description = "Location where the results will be stored in.")
	static String resultPath;
	@Parameter(names = { "-indexLocation" }, description = "Location where the index will be stored in.")
	static String indexLocation;
	@Parameter(names = { "-queryFilePath" }, description = "Location of Query File including query file name.")
	static String queryFilePath;
	@Parameter(names = { "-topN" }, description = "The number of files that will be retrieved.")
	static int topN = 10;

	public static void main(String[] args) {
		Runner runner = new Runner();

		new JCommander(runner, args);

		if (task == null) {
			System.out.println("Please specify a task.");
			System.out.println("BLUiR -task createdocs or index or retrieve");
			return;
		}
		if (task.equals("createdocs")) {
			createDocs(codeDirectory, documentCollectionDirectory);
		} else if (task.equals("index")) {
			index(documentCollectionDirectory, indexLocation);
		} else if (task.equals("createquery")) {
			createQuery(bugRepositoryLocation, queryFilePath);
		} else if (task.equals("retrieve")) {
			retrieve(queryFilePath, resultPath, indexLocation);
		} else {
			System.out.println("Please provide a valid task option.");
			System.out.println("BLUiR -task createquery or createdocs or index or retrieve");
			return;
		}
	}

	static void createQuery(String bugRepoLoc, String queryPath) {
		if ((bugRepoLoc == null) || (queryPath == null)) {
			System.out.println(
					"You have to provide the bug repo (xml file) location, and specify the location where the query will be stored.");
			System.out.println("-bugRepoLocation\n-queryFilePath\n");
			return;
		}
		try {
			System.out.println("Query creation is in progress...This may take a few minutes.");
			System.out.println("bugPath = " + bugRepoLoc + "\t\tqueryPath=" + queryPath);
			File dir = new File(queryPath);
			if (!dir.getParentFile().exists())
				dir.getParentFile().mkdirs();
			QueryExtractor.extractSumDesField(bugRepoLoc, queryPath);
		} catch (IOException e) {
			System.out.println("Please check your bug repo or query file path...");
		}
	}

	static void createDocs(String codeLocation, String docLocation) {
		if ((codeLocation == null) || (docLocation == null)) {
			System.out.println(
					"You have to provide the code location, and specify the location where the document collection will be stored.");
			System.out.println("-codeLocation\n-docLocation\n");
			return;
		}
		try {
			FactExtractor.extractEclipseFacts(codeLocation, docLocation);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	static void index(String docsLocation, String indexLocation) {
		String indriPath = null;
		try {
			BufferedReader br=null;
			try {
				br = new BufferedReader(new FileReader("resource/Settings.txt"));
				indriPath = br.readLine().split("=")[1];
			}
			catch (FileNotFoundException e)
			{
				System.out.println("Settings File Not Found!");
				e.printStackTrace();
				return;
			}
			finally{
				br.close();
			}
			
			if (docsLocation == null || indexLocation == null) {
				System.out.println("You have to provide the document collection location, and specify the location where the index will be stored.");
				System.out.println("-docLocation\n-indexLocation\n");
				return;
			}
		
			//index Dir 폴더 생성.
			File indexDir = new File(indexLocation);
			if (!indexDir.exists()) {
				boolean success = indexDir.mkdirs();
				if (!success) {
					System.out.println("Could not create the Index directory.");
					System.out.println("Stopping Execution....");
					return;
				}
			}
			
			System.out.println(indriPath);
			if (!indriPath.endsWith(File.separator)) {
				indriPath = indriPath + File.separator;
			}
			
			String command = indriPath + "buildindex/IndriBuildIndex -corpus.path=" + docsLocation + " -corpus.class=trectext -index=" + indexLocation + " -memory=2000M -stemmer.name=Krovetz stopwords fields";
			
			executeIndexCommand(command);
			System.out.println("Index created successfully.");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	static void retrieve(String queryFilePath, String resultPath, String indexLocation) {
		String indriPath = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader("Settings.txt"));
			try {
				indriPath = br.readLine().split("=")[1];
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Problems with Settings file!");
			}
			finally{
				try {
					br.close();
				} catch (IOException e) {
					System.out.println("Problems with Settings file!");
					return;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Settings File Not Found!");
			return;
		}

		if ((resultPath == null) || (indexLocation == null) || (queryFilePath == null)) {
			System.out.println("You have to provide both the index location and result path.");
			System.out.println("-queryFilePath\n-indexLocation\n-resultPath\n-topN [Optional]");
			return;
		}

		if (!indriPath.endsWith(File.separator)) {
			indriPath = indriPath + File.separator;
		}

		String command = indriPath + "runquery/IndriRunQuery " + queryFilePath + "	-count=" + topN + " -index="
				+ indexLocation + "	-trecFormat=true -rule=method:tfidf,k1:1.0,b:0.3";

		executeRetrievalCommand(command, resultPath);
	}

	private static void executeRetrievalCommand(String command, String resultPath) {
		System.out.println(
				"Retrieval is in progress...This may take from few minutes to even hours depending on the number of queries and size of document collection.");
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(resultPath));
		} catch (IOException e1) {
			System.out.println("Problems with result file path");
			return;
		}

		try {
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				try {
					bw.write(line);
					bw.newLine();
				} catch (IOException e) {
					bw.close();
					System.out.println("Problems in writing result");
					return;
				}
			}
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			bw.close();
		} catch (IOException e) {
			System.out.println("Problems in closing results file after writing.");
			return;
		}
		System.out.println("Results are stored successfully in the specified file :-)");
	}

	private static String executeIndexCommand(String command) {
		StringBuffer output = new StringBuffer();

		try {
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
	}

	void helper() {
		System.out.println("BLUiR -task createdocs or index or retrieve");
	}
}
