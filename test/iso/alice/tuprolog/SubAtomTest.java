package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SubAtomTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("sub_atom(abracadabra, 0, 5, _, S2).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'abrac'"), binding);
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("sub_atom(abracadabra, _, 5, 0, S2).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'dabra'"), binding);
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("sub_atom(abracadabra, 3, L, 3, S2).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("L");
        assertEquals(Term.createTerm("5"), binding);
        binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'acada'"), binding);
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("sub_atom(abracadabra, B, 2, A, ab).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("B");
        assertEquals(Term.createTerm("0"), binding);
        binding = solution.getTerm("A");
        assertEquals(Term.createTerm("9"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("B");
        assertEquals(Term.createTerm("7"), binding);
        binding = solution.getTerm("A");
        assertEquals(Term.createTerm("2"), binding);
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("sub_atom('Banana', 3, 2, _, S2).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'an'"), binding);
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("sub_atom('charity', _, 3, _, S2).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'cha'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'har'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'ari'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'rit'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("S2");
        assertEquals(Term.createTerm("'ity'"), binding);
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("sub_atom('ab', Start, Length, _, Sub_atom).");
        assertTrue(solution.isSuccess());
        Term binding = solution.getTerm("Start");
        assertEquals(Term.createTerm("0"), binding);
        binding = solution.getTerm("Length");
        assertEquals(Term.createTerm("0"), binding);
        binding = solution.getTerm("Sub_atom");
        assertEquals(Term.createTerm("''"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("Start");
        assertEquals(Term.createTerm("0"), binding);
        binding = solution.getTerm("Length");
        assertEquals(Term.createTerm("1"), binding);
        binding = solution.getTerm("Sub_atom");
        assertEquals(Term.createTerm("'a'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("Start");
        assertEquals(Term.createTerm("0"), binding);
        binding = solution.getTerm("Length");
        assertEquals(Term.createTerm("2"), binding);
        binding = solution.getTerm("Sub_atom");
        assertEquals(Term.createTerm("'ab'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("Start");
        assertEquals(Term.createTerm("1"), binding);
        binding = solution.getTerm("Length");
        assertEquals(Term.createTerm("0"), binding);
        binding = solution.getTerm("Sub_atom");
        assertEquals(Term.createTerm("''"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("Start");
        assertEquals(Term.createTerm("1"), binding);
        binding = solution.getTerm("Length");
        assertEquals(Term.createTerm("1"), binding);
        binding = solution.getTerm("Sub_atom");
        assertEquals(Term.createTerm("'b'"), binding);
        solution = engine.solveNext();
        assertTrue(solution.isSuccess());
        binding = solution.getTerm("Start");
        assertEquals(Term.createTerm("2"), binding);
        binding = solution.getTerm("Length");
        assertEquals(Term.createTerm("0"), binding);
        binding = solution.getTerm("Sub_atom");
        assertEquals(Term.createTerm("''"), binding);
    }

}