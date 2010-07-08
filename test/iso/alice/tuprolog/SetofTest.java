package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SetofTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, fail, S).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (X=1; X=2), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (X=1; X=2), X).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (X=2; X=1), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (X=2; X=2), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[2]"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test5() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (X=Y; X=Z), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[Y, Z]"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test6() throws PrologException {
        SolveInfo solution = engine.solve("setof(1, (Y=2; Y=1), L).");
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
    public void test7() throws PrologException {
        SolveInfo solution = engine.solve("setof(f(X, Y), (X=a; Y=b), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[f(_, b), f(a, _)]"), binding);
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, Y^((X=1, Y=1) ; (X=2, Y=2)), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[1, 2]"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test9() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, Y^((X=1; Y=1) ; (X=2, Y=2)), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[_, 1, 2]"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test10() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (Y^(X=1; Y=2) ; X=3), S).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S");
        assertEquals(Term.createTerm("[3]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("_"), binding);
    }

    @Test(expected=AssertionError.class)
    public void test11() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (X=Y; X=Z; Y=1), S).");
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
    public void test12() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, member(X, [V, U, f(U), f(V)]), [a, b, f(a), f(b)]).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("U");
        assertEquals(Term.createTerm("b"), binding);
        binding = solution.getTerm("V");
        assertEquals(Term.createTerm("a"), binding);
    }
    
    @Test(expected=AssertionError.class) 
    public void test13() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, member(X, [V, U, f(U), f(V)]), [a, b, f(b), f(a)]).");
        assertFalse(solution.isSuccess());
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, (exists(U,V)^member(X, [V, U, f(U), f(V)])), [a, b, f(b), f(a)]).");
        assertTrue(solution.isSuccess());
    }

//	  FIXME: Fucked up. When run in ISOAcceptanceSuite, it throws an assertion
//	         error if there's no expected property, but doesn't throw it if the
//	         property is present. Works if this test case is run stand-alone. 
//    @Test(expected=AssertionError.class)
//    public void test15() throws PrologException {
//        SolveInfo solution = engine.solve("setof(X, member(X, [f(U,b), f(V,c)]), L).");
//        assertTrue(solution.isSuccess());
//        Term binding = solution.getTerm("L");
//        assertEquals(Term.createTerm("[f(U,b), f(V,c)]"), binding);
//    }

    @Test public void test16() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, member(X, [f(U,b), f(V,c)]), [f(a,c), f(a,b)]).");
        assertFalse(solution.isSuccess());
    }

    @Test(expected=AssertionError.class) 
    public void test17() throws PrologException {
        SolveInfo solution = engine.solve("setof(X, member(X, [f(b,U), f(c,V)]), [f(b,a), f(c,a)]).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("U");
        assertEquals(Term.createTerm("a"), binding);
        binding = solution.getTerm("V");
        assertEquals(Term.createTerm("a"), binding);
    }

    @Test(expected=AssertionError.class) 
    public void test18() throws PrologException {
        engine.setTheory(new Theory("a(1, f(_)).\na(2, f(_))."));
        SolveInfo solution = engine.solve("setof(X, a(X, Y), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1, 2]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("f(_)"), binding);
        engine.clearTheory();
    }

    @Test public void test19() throws PrologException {
        engine.setTheory(new Theory("b(1, 1).\n" +
                                    "b(1, 1).\n" +
                                    "b(1, 2).\n" +
                                    "b(2, 1).\n" +
                                    "b(2, 2).\n" +
                                    "b(2, 2)."));
        SolveInfo solution = engine.solve("setof(X, b(X, Y), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1, 2]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("1"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1, 2]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("2"), binding);
        engine.clearTheory();
    }

    @Test public void test20() throws PrologException {
        engine.setTheory(new Theory("b(1, 1).\n" +
                                    "b(1, 1).\n" +
                                    "b(1, 2).\n" +
                                    "b(2, 1).\n" +
                                    "b(2, 2).\n" +
                                    "b(2, 2)."));
        SolveInfo solution = engine.solve("setof(X-Xs, Y^setof(Y, b(X, Y), Xs), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1-[1, 2], 2-[1,2]]"), binding);
        engine.clearTheory();
    }

    @Test(expected=AssertionError.class)
    public void test21() throws PrologException {
        engine.setTheory(new Theory("b(1, 1).\n" +
                                    "b(1, 1).\n" +
                                    "b(1, 2).\n" +
                                    "b(2, 1).\n" +
                                    "b(2, 2).\n" +
                                    "b(2, 2)."));
        SolveInfo solution = engine.solve("setof(X-Xs, setof(Y, b(X, Y), Xs), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1-[1, 2], 2-[1, 2]]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("_"), binding);
        engine.clearTheory();
    }

    @Test(expected=AssertionError.class) 
    public void test22() throws PrologException {
        engine.setTheory(new Theory("d(1, 1).\n" +
                                    "d(1, 2).\n" +
                                    "d(1, 1).\n" +
                                    "d(2, 2).\n" +
                                    "d(2, 1).\n" +
                                    "d(2, 2)."));
        SolveInfo solution = engine.solve("setof(X-Xs, bagof(Y, d(X, Y), Xs), L).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("[1-[1, 2, 1], 2-[2, 1, 2]]"), binding);
        binding = solution.getTerm("Y");
        assertEquals(Term.createTerm("_"), binding);
        engine.clearTheory();
    }

}