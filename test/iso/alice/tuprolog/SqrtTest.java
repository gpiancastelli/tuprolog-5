package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public class SqrtTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is sqrt(0.0).");
        assertEquals(Term.createTerm("0.0"), solution.getTerm("X"));
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is sqrt(1).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is sqrt(1.21).");
        assertEquals(Term.createTerm("1.1"), solution.getTerm("X"));
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is sqrt(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test(expected=AssertionError.class)
    public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is sqrt(-1.0).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(undefined)
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("X is sqrt(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

}