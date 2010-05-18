package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class LengthTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void atomLength() throws PrologException {
		SolveInfo solution = engine.solve("length(scarlet, 7).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void emptyList() throws PrologException {
		SolveInfo solution = engine.solve("length([], 0).");
		assertTrue(solution.isSuccess());
	}
	
	@Test(expected=AssertionError.class)
	public void variableLength() throws PrologException {
		SolveInfo solution = engine.solve("length(X, 4).");
		assertTrue(solution.isSuccess());
		Term[] items = new Term[] {new Var(), new Var(), new Var(), new Var()};
		assertEquals(new Struct(items), solution.getTerm("X"));
	}
	
	@Test public void variableNegativeLength() throws PrologException {
		SolveInfo solution = engine.solve("length(A, -1).");
		assertFalse(solution.isSuccess());
	}
	
	@Test(expected=AssertionError.class)
	public void generativeEffectOnList() throws PrologException {
		SolveInfo solution = engine.solve("length(L, S).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(0), solution.getTerm("S"));
		assertEquals(new Struct(), solution.getTerm("L"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("S"));
		assertEquals(new Struct(new Term[] {new Var()}), solution.getTerm("L"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("S"));
		assertEquals(new Struct(new Term[] {new Var(), new Var()}), solution.getTerm("L"));
		assertTrue(engine.hasOpenAlternatives());
	}
	
	@Test(expected=AssertionError.class)
	public void generativeEffectOnListTail() throws PrologException {
		SolveInfo solution = engine.solve("length([1, 2 | T], X).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("X"));
		assertEquals(new Struct(), solution.getTerm("T"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(3), solution.getTerm("X"));
		assertEquals(new Struct(new Term[] {new Var()}), solution.getTerm("T"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(4), solution.getTerm("X"));
		assertEquals(new Struct(new Term[] {new Var(), new Var()}), solution.getTerm("T"));
		assertTrue(engine.hasOpenAlternatives());
	}

}
