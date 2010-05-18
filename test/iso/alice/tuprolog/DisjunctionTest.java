package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DisjunctionTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void simpleDisjunction() throws PrologException {
		SolveInfo solution = engine.solve("';'(true, fail).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void disjunctionCutFail() throws PrologException {
		SolveInfo solution = engine.solve("';'((!, fail), true).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void disjunctionCutAvoidError() throws PrologException {
		SolveInfo solution = engine.solve("';'(!, call(3)).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void disjunctionWithUnification() throws PrologException {
		SolveInfo solution = engine.solve("';'((X=1, !), X=2).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
	}
	
	@Test public void disjunctionWithReexecution() throws PrologException {
		SolveInfo solution = engine.solve("','(';'(X=1, X=2), ';'(true, !)).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		assertFalse(engine.hasOpenAlternatives());
	}

}
