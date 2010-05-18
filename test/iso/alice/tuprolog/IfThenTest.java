package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IfThenTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void ifTrueThenTrue() throws PrologException {
		SolveInfo solution = engine.solve("'->'(true, true).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void ifTrueThenFail() throws PrologException {
		SolveInfo solution = engine.solve("'->'(true, fail).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void ifFailThenTrue() throws PrologException {
		SolveInfo solution = engine.solve("'->'(fail, true).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void noReexecution() throws PrologException {
		SolveInfo solution = engine.solve("'->'(true, X=1).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		assertFalse(engine.hasOpenAlternatives());
	}
	
	@Test public void noReexecutionWithDisjunctiveCondition() throws PrologException {
		SolveInfo solution = engine.solve("'->'(';'(X=1, X=2), true).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		assertFalse(engine.hasOpenAlternatives());
	}
	
	@Test public void reexecutionWithDisjunctiveBody() throws PrologException {
		SolveInfo solution = engine.solve("'->'(true, ';'(X=1, X=2)).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("X"));
		assertFalse(engine.hasOpenAlternatives());
	}

}
