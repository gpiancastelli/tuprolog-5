package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class BitwiseOrTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is '\\/'(10, 12).");
        assertEquals(Term.createTerm("14"), solution.getTerm("X"));
    }

    @Test(expected=MalformedGoalException.class) 
    public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is \\/(10, 12).");
        assertEquals(Term.createTerm("14"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is '\\/'(125, 255).");
        assertEquals(Term.createTerm("255"), solution.getTerm("X"));
    }

    @Test(expected=MalformedGoalException.class) 
    public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is \\/(-10, 12).");
        assertNotNull(solution.getTerm("X"));
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is '\\/'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("X is '\\/'(foo, 2).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(integer, foo)
    }

}