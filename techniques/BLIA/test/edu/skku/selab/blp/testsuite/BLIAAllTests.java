package edu.skku.selab.blp.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.skku.selab.blp.blia.analysis.BliaTest;
import edu.skku.selab.blp.blia.analysis.ScmRepoAnalyzerTest;
import edu.skku.selab.blp.blia.indexer.BugCorpusCreatorTest;
import edu.skku.selab.blp.blia.indexer.BugVectorCreatorTest;
import edu.skku.selab.blp.blia.indexer.GitCommitLogCollectorTest;
import edu.skku.selab.blp.blia.indexer.SourceFileCorpusCreatorTest;
import edu.skku.selab.blp.blia.indexer.SourceFileVectorCreatorTest;

@RunWith(Suite.class)
@SuiteClasses({
	BliaTest.class,
	ScmRepoAnalyzerTest.class,
	BugCorpusCreatorTest.class, BugVectorCreatorTest.class,
	GitCommitLogCollectorTest.class, 
	SourceFileCorpusCreatorTest.class, SourceFileVectorCreatorTest.class})
public class BLIAAllTests {

}
