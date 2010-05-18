package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class OnceTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("once(!).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("once(repeat).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("once(fail).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("once(X = f(X)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("once(!), (X=1; X=2).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("1"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("X");
        assertEquals(Term.createTerm("2"), binding);
    }

}