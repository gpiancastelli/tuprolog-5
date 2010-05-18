package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AtanTest.class,
	                 SinTest.class,
	                 CosTest.class,
	                 ExpTest.class,
	                 LogTest.class,
	                 PowerTest.class,
	                 SqrtTest.class})
public class OtherArithmeticFunctorsSuite {}