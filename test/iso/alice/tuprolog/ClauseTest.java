package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ClauseTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
        try {
            engine.setTheory(new Theory(
            		":- dynamic(cat/0).\n" +
            		"cat.\n" +
            		":- dynamic(dog/0).\n" +
            		"dog :- true.\n" +
            		"elk(X) :- moose(X).\n" +
            		":- dynamic(legs/2).\n" +
            		"legs(A, 6) :- insect(A).\n" +
            		"legs(A, 7) :- A, call(A).\n" +
            		":- dynamic(insect/1).\n" +
            		"insect(ant).\n" +
            		"insect(bee).\n"));
        } catch (InvalidTheoryException e) {
        }
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("clause(cat, true).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("clause(dog, true).");
        assertTrue(solution.isSuccess());
    }

    @Test(expected=AssertionError.class)
    public void test2() throws PrologException {
        SolveInfo solution = engine.solve("clause(legs(I, 6), Body).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("Body");
        assertEquals(Term.createTerm("insect(I)"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test3() throws PrologException {
        SolveInfo solution = engine.solve("clause(legs(C, 7), Body).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("Body");
        assertEquals(Term.createTerm("(call(C), call(C))"), binding);
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("clause(insect(I), T).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("I");
        assertEquals(Term.createTerm("ant"), binding);
        binding = solution.getTerm("T");
        assertEquals(Term.createTerm("true"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("I");
        assertEquals(Term.createTerm("bee"), binding);
        binding = solution.getTerm("T");
        assertEquals(Term.createTerm("true"), binding);
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("clause(x, Body).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("clause(legs(A, 6), insect(f(A))).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("clause(_, B).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
        // FIXME Actually throws a RuntimeException
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("clause(4, X).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(callable, 4)
    }

    @Test(expected=AssertionError.class)
    public void test9() throws PrologException {
        SolveInfo solution = engine.solve("clause(elk(N), Body).");
        assertFalse(solution.isSuccess());
        // TODO Should throw permission_error(access, private_procedure, elk/1)
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("clause(atom(_), Body).");
        assertFalse(solution.isSuccess());
        // TODO Should throw permission_error(access, private_procedure, atom/1)
    }

}