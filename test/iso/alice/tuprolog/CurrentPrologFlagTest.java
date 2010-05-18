package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CurrentPrologFlagTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(debug, off).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(F, V).");
        assertTrue(solution.isSuccess());
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(5, _).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(atom, 5)
    }

}