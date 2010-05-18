package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({JavaObjectTest.class,
	                 InvokeReturnsTest.class,
	                 AsTest.class,
	                 FieldAccessTest.class,
	                 JavaArrayTest.class})
public class JavaLibrarySuite {}
