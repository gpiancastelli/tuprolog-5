package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CurrentPredicateTest {

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
    
    @Test(expected=AssertionError.class)
    public void test0() throws PrologException {
        SolveInfo solution = engine.solve("current_predicate(dog/0).");
        assertTrue(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("current_predicate(current_predicate/1).");
        assertFalse(solution.isSuccess());
    }

    @Test(expected=AssertionError.class)
    public void test2() throws PrologException {
        SolveInfo solution = engine.solve("current_predicate(elk/Arity).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("Arity");
        assertEquals(Term.createTerm("1"), binding);
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("current_predicate(foo/A).");
        assertFalse(solution.isSuccess());
    }

    @Test(expected=AssertionError.class)
    public void test4() throws PrologException {
        SolveInfo solution = engine.solve("current_predicate(Name/1).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("Name");
        assertEquals(Term.createTerm("elk"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        assertEquals(Term.createTerm("insect"), binding);
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("current_predicate(4).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(predicate_indicator, 4)
    }

}