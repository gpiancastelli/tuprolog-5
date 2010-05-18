package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NotTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("'\\+'(true).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("\\+(!).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("'\\+'((!, false)).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("'\\+'(4 = 5).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("\\+(X = f(X)).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("(X=1; X=2), \\+((!, fail)).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("1"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("X");
        assertEquals(Term.createTerm("2"), binding);
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("\\+(3).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(callable, 3)
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("'\\+'(X).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

}