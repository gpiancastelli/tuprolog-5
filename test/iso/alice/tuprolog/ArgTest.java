package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ArgTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("arg(1, foo(a, b), a).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("arg(1, foo(a, b), b).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("arg(0, foo(a, b), foo).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("arg(3, foo(3, 4), N).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("arg(1, foo(X), u(X)).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("arg(1, foo(a, b), X).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("a"), binding);
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("arg(1, foo(X, b), a).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("a"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test7() throws PrologException {
        SolveInfo solution = engine.solve("arg(1, foo(X, b), Y).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("Y"), binding);
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("arg(X, foo(a, b), a).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("arg(1, X, a).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("arg(0, atom, a).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(compound, atom)
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("arg(0, 3, a).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(compound, 3)
    }

}