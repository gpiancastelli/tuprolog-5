package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AtomConcatTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("atom_concat('hello', ' world', 'small world').");
        assertFalse(solution.isSuccess());
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("atom_concat('hello', ' world', S3).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S3");
        assertEquals(Term.createTerm("'hello world'"), binding);
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("atom_concat(T, ' world', 'small world').");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("T");
        assertEquals(Term.createTerm("'small'"), binding);
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("atom_concat(T1, T2, 'hello').");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("T1");
        assertEquals(Term.createTerm("''"), binding);
        binding = solution.getTerm("T2");
        assertEquals(Term.createTerm("'hello'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("T1");
        assertEquals(Term.createTerm("'h'"), binding);
        binding = solution.getTerm("T2");
        assertEquals(Term.createTerm("'ello'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("T1");
        assertEquals(Term.createTerm("'he'"), binding);
        binding = solution.getTerm("T2");
        assertEquals(Term.createTerm("'llo'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("T1");
        assertEquals(Term.createTerm("'hel'"), binding);
        binding = solution.getTerm("T2");
        assertEquals(Term.createTerm("'lo'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("T1");
        assertEquals(Term.createTerm("'hell'"), binding);
        binding = solution.getTerm("T2");
        assertEquals(Term.createTerm("'o'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("T1");
        assertEquals(Term.createTerm("'hello'"), binding);
        binding = solution.getTerm("T2");
        assertEquals(Term.createTerm("''"), binding);
        assertFalse(engine.hasOpenAlternatives());
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("atom_concat(small, V2, V4).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

}