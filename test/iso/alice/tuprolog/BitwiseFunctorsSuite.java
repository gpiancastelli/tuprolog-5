package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({BitwiseRightShiftTest.class,
	                 BitwiseOrTest.class,
	                 BitwiseComplementTest.class,
	                 BitwiseLeftShiftTest.class,
	                 BitwiseAndTest.class})
public class BitwiseFunctorsSuite {}