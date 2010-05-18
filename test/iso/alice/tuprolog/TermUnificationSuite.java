package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({NotUnifiableTest.class,
	                 UnifyTest.class,
	                 UnifyWithOccursCheckTest.class})
public class TermUnificationSuite {}