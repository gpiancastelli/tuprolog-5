package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class FieldAccessTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void getStaticUnboundField() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.Integer').MAX_VALUE <- get(V).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void getStaticFieldValue() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.Integer').'MAX_VALUE' <- get(V), V > 0.");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void getStaticNonexistentField() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.Integer').'NON_EXISTENT_FIELD' <- get(X).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void getNonexistentMember() throws PrologException {
		String goal = "java_object('java.awt.Point', [], P), P.nonExistantField <- get(X).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setNonexistentMember() throws PrologException {
		String goal = "java_object('java.awt.Point', [], P), P.nonExistantField <- set(0).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setUnboundMemberValue() throws PrologException {
		SolveInfo solution = engine.solve("java_object('java.awt.Point', [], P), P.y <- set(X).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setAndGetValueAreEquals() throws PrologException {
		String goal = "java_object('java.awt.GridBagConstraints', [], C), " +
				      "java_object('java.awt.Insets', [1,1,1,1], I1), " +
				      "C.insets <- set(I1), C.insets <- get(I2), I1 == I2.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void getIntMember() throws PrologException {
		String goal = "java_object('java.awt.Point', [], P), P.x <- get(X).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Int(0), solution.getTerm("X"));
	}
	
	@Test public void setAndGetIntMember() throws PrologException {
		String goal = "java_object('java.awt.Point', [], P), P.y <- set(5), P.y <- get(Y).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Int(5), solution.getTerm("Y"));
	}

}
