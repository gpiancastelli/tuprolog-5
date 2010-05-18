package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IsTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("'is'(3, 3).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("'is'(3, 3.0).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("'is'(foo, 77).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("'is'(Result, 3 + 11.0).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("Result");
        assertEquals(Term.createTerm("14.0"), binding);
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X = 1 + 2, Y is X * 3.");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("1 + 2"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("9"), binding);
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("'is'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

}