package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UnifyWithOccursCheckTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(1, 1).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(_, _).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(1, 2).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(1, 1.0).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(g(X), f(f(X))).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(f(X, 1), f(a(X))).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(f(X, Y, X), f(a(X), a(Y), Y, 2)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(X, a(X)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(f(X, 1), f(a(X), 2)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(f(1, X, 1), f(2, a(X), 2)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(f(1, X), f(2, a(X))).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(f(X, Y, X, 1), f(a(X), a(Y), Y, 2)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(X, 1).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("1"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test13() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(X, Y).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("Y"), binding);
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(X, Y), unify_with_occurs_check(X, abc).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("abc"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("abc"), binding);
    }

    @Test public void test15() throws PrologException {
        SolveInfo solution = engine.solve("unify_with_occurs_check(f(X, def), f(def, Y)).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("def"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("def"), binding);
    }

}