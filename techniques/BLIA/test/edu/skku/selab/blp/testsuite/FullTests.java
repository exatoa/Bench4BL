package edu.skku.selab.blp.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.skku.selab.blp.PropertyTest;
import edu.skku.selab.blp.common.BugTest;

@RunWith(Suite.class)
@SuiteClasses({
	PropertyTest.class,
	BugTest.class,
	DAOAllTests.class,
	BLIAAllTests.class}) //, EvaluatorTest.class})
public class FullTests {

}
