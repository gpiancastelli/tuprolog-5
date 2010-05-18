package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({UnivTest.class,
	                 CopyTermTest.class,
	                 FunctorTest.class,
	                 ArgTest.class})
public class TermCreationAndDecompositionSuite {}