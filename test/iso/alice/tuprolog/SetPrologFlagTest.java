package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SetPrologFlagTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test(expected=AssertionError.class)
    public void test0() throws PrologException {
        SolveInfo solution = engine.solve("set_prolog_flag(unknown, fail).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("set_prolog_flag(X, off).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("set_prolog_flag(5, decimals).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(atom, 5)
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("set_prolog_flag(date, 'July 1988').");
        assertFalse(solution.isSuccess());
        // TODO Should throw domain_error(flag, date)
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("set_prolog_flag(debug, trace).");
        assertFalse(solution.isSuccess());
        // TODO Should throw domain_error(flag_value, debug+trace)
    }

}