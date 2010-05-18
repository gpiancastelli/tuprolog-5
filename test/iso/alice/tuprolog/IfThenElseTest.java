package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IfThenElseTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void thenTrue() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(true, true), fail).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void elseTrue() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(fail, true), true).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void thenFail() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(true, fail), fail).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void elseFail() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(fail, true), fail).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void thenUnify() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(true, X=1), X=2).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
	}
	
	@Test public void elseUnify() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(fail, X=1), X=2).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("X"));
	}
	
	@Test public void reexecutionWithDisjunctiveBody() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(true, ';'(X=1, X=2)), true).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("X"));
	}
	
	@Test public void noReexecutionWithDisjunctiveCondition() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'(';'(X=1, X=2), true), true).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		assertFalse(engine.hasOpenAlternatives());
	}
	
	@Test public void cutCondition() throws PrologException {
		SolveInfo solution = engine.solve("';'('->'((!, fail), true), true).");
		assertTrue(solution.isSuccess());
	}

}
