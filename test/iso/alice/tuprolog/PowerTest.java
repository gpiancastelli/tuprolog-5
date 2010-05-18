package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class PowerTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is '**'(5, 3).");
        assertEquals(Term.createTerm("125.0"), solution.getTerm("X"));
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is '**'(-5.0, 3).");
        assertEquals(Term.createTerm("-125.0"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is '**'(5, -1).");
        assertEquals(Term.createTerm("0.2"), solution.getTerm("X"));
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is '**'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is '**'(foo, 2).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("X is '**'(5, 3.0).");
        assertEquals(Term.createTerm("125.0"), solution.getTerm("X"));
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("X is '**'(0.0, 0).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

}