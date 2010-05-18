package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class BitwiseLeftShiftTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is '<<'(16, 2).");
        assertEquals(Term.createTerm("64"), solution.getTerm("X"));
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is '<<'(19, 2).");
        assertEquals(Term.createTerm("76"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is '<<'(-16, 2).");
        assertNotNull(solution.getTerm("X"));
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is '<<'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is '<<'(foo, 2).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(integer, foo)
    }

}