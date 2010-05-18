package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ClauseTest.class,
	                 CurrentPredicateTest.class})
public class ClauseRetrivalAndInformationSuite {}