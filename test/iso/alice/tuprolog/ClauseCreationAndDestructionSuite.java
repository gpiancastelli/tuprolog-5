package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AssertaTest.class,
	                 AssertzTest.class,
	                 RetractTest.class,
	                 AbolishTest.class})
public class ClauseCreationAndDestructionSuite {}
