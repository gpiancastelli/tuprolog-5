package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CallTest.class,
	                 DisjunctionTest.class,
	                 IfThenTest.class,
	                 IfThenElseTest.class})
public class ControlConstructsSuite {}
