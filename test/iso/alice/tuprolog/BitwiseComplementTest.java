package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class BitwiseComplementTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is '\\'('\\'(10)).");
        assertEquals(Term.createTerm("10"), solution.getTerm("X"));
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is \\(\\(10)).");
        assertEquals(Term.createTerm("10"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is \\(10).");
        assertNotNull(solution.getTerm("X"));
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is '\\'(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test(expected=AssertionError.class)
    public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is '\\'(2.5).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(integer, 2.5)
    }

}