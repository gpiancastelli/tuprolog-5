package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class FindallTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("findall(X, (X=2; X=1), [1, 2]).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("findall(X, (X=1; X=2), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test // (expected=AssertionError.class)
    public void test2() throws PrologException {
        SolveInfo solution = engine.solve("findall(X+Y, (X=1), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1 + _]"), binding);
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("findall(X, fail, L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[]"), binding);
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("findall(X, (X=1; X=1), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, 1]"), binding);
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("findall(X, (X=1; X=2), [X, Y]).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("1"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("2"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test6() throws PrologException {
        SolveInfo solution = engine.solve("findall(X, Goal, S).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test(expected=AssertionError.class)
    public void test7() throws PrologException {
        SolveInfo solution = engine.solve("findall(X, 4, S).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(callable, 4)
    }

}