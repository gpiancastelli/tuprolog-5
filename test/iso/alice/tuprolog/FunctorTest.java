package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class FunctorTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("functor(foo(a, b, c), foo, 3).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("functor(foo(a), foo, 2).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("functor(foo(a), fo, 1).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("functor([_|_], '.', 2).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("functor([], [], 0).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("functor(foo(a, b, c), X, Y).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("foo"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("3"), binding);
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("functor(X, foo, 3).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("foo(_, _, _)"), binding);
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("functor(X, foo, 0).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("foo"), binding);
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("functor(mats(A, B), A, B).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("A");
        assertEquals(Term.createTerm("mats"), binding);
        binding = solution.getTerm("B");
        assertEquals(Term.createTerm("2"), binding);
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("functor(1, X, Y).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("1"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("0"), binding);
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("functor(X, 1.1, 0).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("1.1"), binding);
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("functor(X, Y, 3).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("functor(X, foo, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test13() throws PrologException {
        SolveInfo solution = engine.solve("functor(X, foo, a).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(integer, a)
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("functor(F, 1.5, 1).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(atom, 1.5)
    }

    @Test public void test15() throws PrologException {
        SolveInfo solution = engine.solve("functor(F, foo(a), 1).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(atomic, foo(a))
    }

    @Test public void test16() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(max_arity, A), X is A + 1, functor(T, foo, X).");
        assertFalse(solution.isSuccess());
        // TODO Should throw representation_error(max_arity)
    }

    @Test public void test17() throws PrologException {
        SolveInfo solution = engine.solve("Minus1 is 0 - 1, functor(F, foo, Minus1).");
        assertFalse(solution.isSuccess());
        // TODO Should throw domain_error(not_less_than_zero, -1)
    }

}