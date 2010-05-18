package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AtomLengthTest.class,
	                 AtomConcatTest.class,
	                 SubAtomTest.class,
	                 AtomCharsTest.class,
	                 AtomCodesTest.class,
	                 CharCodeTest.class,
	                 NumberCharsTest.class,
	                 NumberCodesTest.class})
public class AtomicTermProcessingSuite {}