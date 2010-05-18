package alice.tuprolog;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class RepeatTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("repeat, !, fail.");
        assertFalse(solution.isSuccess());
    }

}