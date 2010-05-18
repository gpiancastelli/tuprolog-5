package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AtomicTest.class,
	                 CompoundTest.class,
	                 IntegerTest.class,
	                 NumberTest.class,
	                 NonvarTest.class,
	                 AtomTest.class,
	                 VarTest.class,
	                 FloatTest.class})
public class TypeTestingSuite {}