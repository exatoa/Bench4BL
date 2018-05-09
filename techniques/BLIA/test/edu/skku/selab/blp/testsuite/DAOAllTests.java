package edu.skku.selab.blp.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.skku.selab.blp.db.dao.BugDAOTest;
import edu.skku.selab.blp.db.dao.CommitDAOTest;
import edu.skku.selab.blp.db.dao.ExperimentResultDAOTest;
import edu.skku.selab.blp.db.dao.IntegratedAnalysisDAOTest;
import edu.skku.selab.blp.db.dao.SourceFileDAOTest;

@RunWith(Suite.class)
@SuiteClasses({
	BugDAOTest.class,
	CommitDAOTest.class,
	ExperimentResultDAOTest.class,
	IntegratedAnalysisDAOTest.class,
	SourceFileDAOTest.class})
public class DAOAllTests {

}
