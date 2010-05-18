package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({PhraseTest.class,
	                 LengthTest.class,
	                 JavaLibrarySuite.class,
	                 BugsTest.class})
public class OtherPredicatesSuite {}
