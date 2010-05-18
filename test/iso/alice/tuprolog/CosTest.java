package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CosTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is cos(0.0).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is cos(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is cos(0).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

    public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is cos(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test(expected=AssertionError.class)
    public void test4() throws PrologException {
        SolveInfo solution = engine.solve("PI is atan(1.0) * 4, X is cos(PI / 2.0).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("X");
        assertEquals(Term.createTerm("0.0"), binding);
        binding = solution.getTerm("PI");
        assertEquals(Term.createTerm("3.14159"), binding);
    }

}