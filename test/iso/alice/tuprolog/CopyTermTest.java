package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CopyTermTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(X, Y).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(X, 3).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(_, a).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(_, _).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(a, b).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(a+X, X+b), copy_term(a+X, X+b).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(demoen(X, X), demoen(Y, f(Y))).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(a+X, X+b).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("a"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test8() throws PrologException {
        SolveInfo solution = engine.solve("copy_term(X+X+Y, A+B+B).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("A");
        assertEquals(Term.createTerm("B"), binding);
    }

}