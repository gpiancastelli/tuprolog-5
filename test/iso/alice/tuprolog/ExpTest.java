package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class ExpTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is exp(0.0).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

    @Test(expected=AssertionError.class)
    public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is exp(1.0).");
        assertEquals(Term.createTerm("2.7182"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is exp(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is exp(0).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is exp(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

}