package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({OnceTest.class,
	                 NotTest.class,
	                 RepeatTest.class})
public class LogicAndControlSuite {}