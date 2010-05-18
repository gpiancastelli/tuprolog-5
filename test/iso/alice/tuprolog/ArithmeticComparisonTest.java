package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ArithmeticComparisonTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("'=:='(0, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("'=\\='(0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("'<'(0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("'>'(0, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("'>='(0, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("'=<'(0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("'=:='(1.0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("'=\\='(1.0, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("'<'(1.0, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("'>'(1.0, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("'>='(1.0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("'=<'(1.0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("'=:='(3 * 2, 7 - 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test13() throws PrologException {
        SolveInfo solution = engine.solve("'=\\='(3 * 2, 7 - 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("'<'(3 * 2, 7 - 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test15() throws PrologException {
        SolveInfo solution = engine.solve("'>'(3 * 2, 7 - 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test16() throws PrologException {
        SolveInfo solution = engine.solve("'>='(3 * 2, 7 - 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test17() throws PrologException {
        SolveInfo solution = engine.solve("'=<'(3 * 2, 7 - 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test18() throws PrologException {
        SolveInfo solution = engine.solve("'=:='(X, 5).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test(expected=AssertionError.class)
    public void test19() throws PrologException {
        SolveInfo solution = engine.solve("'=\\='(X, 5).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test20() throws PrologException {
        SolveInfo solution = engine.solve("'<'(X, 5).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test21() throws PrologException {
        SolveInfo solution = engine.solve("'>'(X, 5).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test22() throws PrologException {
        SolveInfo solution = engine.solve("'>='(X, 5).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test23() throws PrologException {
        SolveInfo solution = engine.solve("'=<'(X, 5).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

}