package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TermComparisonTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(1.0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("'@<'(1.0, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("'\\=='(1, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(aardvark, zebra).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(short, short).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(short, shorter).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("'@>='(short, shorter).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("'@<'(foo(a, b), north(a)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("'@>'(foo(b), foo(a)).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("'@<'(foo(a, X), foo(b, Y)).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("'@<'(foo(X, a), foo(Y, b)).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(X, X).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("'=='(X, X).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test13() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(X, Y).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("'=='(X, Y).");
        assertFalse(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test15() throws PrologException {
        SolveInfo solution = engine.solve("\\==(_, _).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test16() throws PrologException {
        SolveInfo solution = engine.solve("'=='(_, _).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test17() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(_, _).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test18() throws PrologException {
        SolveInfo solution = engine.solve("'@=<'(foo(X, a), foo(Y, b)).");
        assertTrue(solution.isSuccess());
    }

}