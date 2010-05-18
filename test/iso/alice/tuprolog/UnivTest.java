package alice.tuprolog;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnivTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(foo(a, b), [foo, a, b]).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(1, [1]).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(foo(a, b), [foo, b, a]).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(f(X), [f, u(X)]).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, [foo, a, b]).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("foo(a, b)"), binding);
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(foo(a, b), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[foo, a, b]"), binding);
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(foo(X, b), [foo, a, Y]).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("a"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("b"), binding);
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, Y).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, [foo, a | Y]).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, [foo | bar]).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(list, [foo | bar])
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, [Foo, bar]).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, [3, 1]).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(atom, 3)
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, [1.1, foo]).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(atom, 1.1)
    }

    @Test public void test13() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, [a(b), 1]).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(atom, a(b))
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("'=..'(X, 4).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(list, 4)
    }

}