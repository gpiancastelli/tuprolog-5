package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BagofTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, fail, S).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, (X=1; X=2), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, (X=1; X=2), X).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test3() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, (X=Y; X=Z), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[Y, Z]"), binding);
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("bagof(1, (Y=1; Y=2), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("1"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("2"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test5() throws PrologException {
        SolveInfo solution = engine.solve("bagof(f(X, Y), (X=a; Y=b), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[f(a, _), f(_, b)]"), binding);
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, Y^((X=1, Y=1) ; (X=2, Y=2)), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test7() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, Y^((X=1; Y=1) ; (X=2, Y=2)), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, _, 2]"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test8() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, (Y^(X=1; Y=2) ; X=3), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[3]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("_"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test9() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, (X=Y; X=Z; Y=1), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[Y, Z]"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[_]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("1"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test10() throws PrologException {
        engine.setTheory(new Theory("a(1, f(_)).\na(2, f(_))."));
        SolveInfo solution = engine.solve("bagof(X, a(X, Y), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1, 2]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("f(_)"), binding);
        engine.clearTheory();
    }

    @Test public void test11() throws PrologException {
        engine.setTheory(new Theory("b(1, 1).\n" +
                                    "b(1, 1).\n" +
                                    "b(1, 2).\n" +
                                    "b(2, 1).\n" +
                                    "b(2, 2).\n" +
                                    "b(2, 2)."));
        SolveInfo solution = engine.solve("bagof(X, b(X, Y), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1, 1, 2]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("1"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1, 2, 2]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("2"), binding);
        engine.clearTheory();
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, Y^Z, L).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test13() throws PrologException {
        SolveInfo solution = engine.solve("bagof(X, 1, L).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(callable, 1)
    }

}