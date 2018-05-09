package bluir.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.CoreException;

import bluir.evaluation.Evaluation;
import bluir.extraction.FactExtractor;
import bluir.extraction.QueryExtractor;

public class Core {
	private final String indriBinPath = Property.getInstance().IndriPath;

	private final String docsLocation = Property.getInstance().WorkDir + "docs";
	private final String indexLocation = Property.getInstance().WorkDir + "index";
	private final String bugFilePath = Property.getInstance().BugFilePath;
	private final String queryFilePath = Property.getInstance().WorkDir + "query";
	private final String indriQueryResult = Property.getInstance().WorkDir + "indriQueryResult";
	private final String workDir = Property.getInstance().WorkDir;
	private int topN = Property.getInstance().topN;

	public void process() {
		if (!createQueryIndex())
			return;
		if (!createDocs())
			return;
		if (!index())
			return;
		if (!retrieve())
			return;
		if (!evaluation())
			return;

		System.out.println("finished");
	}

	boolean createQueryIndex() {
		try {
			System.out.print("create query...");
			int repoSize = QueryExtractor.extractSumDesField(bugFilePath, queryFilePath);
			System.out.println(repoSize + " created successfully :-)");
		} catch (IOException e) {
			System.out.println("Please check your bug repo or query file path...");
			return false;
		}
		return true;
	}

	boolean createDocs() {
		try {
			System.out.print("create docs...");

			if (!FactExtractor.extractEclipseFacts(Property.getInstance().SourceCodeDir, docsLocation))
				return false;

			System.out.println(Property.getInstance().FileCount + " file processed!");
			topN = Property.getInstance().FileCount;

		} catch (IOException | CoreException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.err.println("Error occurs when we're creating docs folder!");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	boolean index() {
		try {
			System.out.print("Create indexes....");

			// index Dir 폴더 생성.
			File indexDir = new File(indexLocation);
			if (!indexDir.exists())
				if (!indexDir.mkdirs())
					throw new Exception();

			// program command
			String command = indriBinPath + "IndriBuildIndex -corpus.path=" + docsLocation + ""
					+ " -corpus.class=trectext -index=" + indexLocation + ""
					+ " -memory=2000M -stemmer.name=Krovetz stopwords fields";
			
			// execute command
			BufferedWriter bw = new BufferedWriter(new FileWriter(this.workDir + "IndexLog.txt"));

			Process p = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				bw.write(line + "\n");
			}
			p.waitFor();
			bw.close();

			// executeIndexCommand(command);
			System.out.println("successfully Done!");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error occurs while we're working with file IO");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Error occurs while we're working with process");
			System.err.println("Stopping Execution....");
			return false;
		}
		return true;
	}

	boolean retrieve() {
		System.out.print("Retrieval is in progress...");

		BufferedWriter bw = null;
		try {
			
			String command = indriBinPath + "IndriRunQuery " + queryFilePath + " -count=" + topN
					+ " -index=" + indexLocation + " -trecFormat=true -rule=method:tfidf,k1:1.0,b:0.3";
			
			bw = new BufferedWriter(new FileWriter(this.indriQueryResult));

			Process p = Runtime.getRuntime().exec(command);

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				bw.write(line);
				bw.newLine();
			}
			p.waitFor();

			System.out.println("Done!");

		} catch (IOException e1) {
			System.out.println("Problems with result file io");
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Problems in closing results file after writing.");
				return false;
			}

		}
		return true;
	}

	boolean evaluation() {
		try {
			System.out.print("Evaluating....");

			new Evaluation().evaluate();

			System.out.println("Done!");

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
