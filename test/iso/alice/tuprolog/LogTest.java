package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class LogTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is log(1.0).");
        assertEquals(Term.createTerm("0.0"), solution.getTerm("X"));
    }

    @Test(expected=AssertionError.class) 
    public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is log(2.7182).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is log(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test(expected=AssertionError.class) 
    public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is log(0).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(undefined)
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is log(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test(expected=AssertionError.class) 
    public void test5() throws PrologException {
        SolveInfo solution = engine.solve("X is log(0.0).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(undefined)
    }

}