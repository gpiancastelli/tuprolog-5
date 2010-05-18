package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NotUnifiableTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(1, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test1() throws PrologException {
        SolveInfo solution = engine.solve("\\=(X, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(X, Y).");
        assertFalse(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test3() throws PrologException {
        SolveInfo solution = engine.solve("\\=(_, _).");
        assertFalse(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test4() throws PrologException {
        SolveInfo solution = engine.solve("\\=(f(X, def), f(def, Y)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(1, 2).");
        assertTrue(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test6() throws PrologException {
        SolveInfo solution = engine.solve("\\=(1, 1.0).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(g(X), f(f(X))).");
        assertTrue(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test8() throws PrologException {
        SolveInfo solution = engine.solve("\\=(f(X, 1), f(a(X))).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(f(X, Y, X), f(a(X), a(Y), Y, 2)).");
        assertTrue(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test10() throws PrologException {
        SolveInfo solution = engine.solve("\\=(X, a(X)).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(f(X, 1), f(a(X), 2)).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(f(1, X, 1), f(2, a(X), 2)).");
        assertTrue(solution.isSuccess());
    }

    @Test(expected=MalformedGoalException.class)
    public void test13() throws PrologException {
        SolveInfo solution = engine.solve("\\=(f(1, X), f(2, a(X))).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("'\\='(f(X, Y, X, 1), f(a(X), a(Y), Y, 2)).");
        assertTrue(solution.isSuccess());
    }

}