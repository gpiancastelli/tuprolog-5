package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CurrentPrologFlagTest.class,
	                 SetPrologFlagTest.class})
public class ImplementationDefinedHooksSuite {}