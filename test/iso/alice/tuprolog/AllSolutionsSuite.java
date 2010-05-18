package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({FindallTest.class,
	                 BagofTest.class,
	                 SetofTest.class})
public class AllSolutionsSuite {}