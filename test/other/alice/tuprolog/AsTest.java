package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AsTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void castAsPrimitive() throws PrologException {
		String goal = "java_object('java.lang.Integer', [5], I)," +
				      "class('java.lang.Integer') <- toString(I as int) returns '5'.";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void castToObject() throws PrologException {
		String goal = "java_object('java.lang.String', ['hello'], S1), " +
				      "java_object('java.lang.String', ['world'], S2), " +
				      "S2 <- compareTo(S1 as 'java.lang.Object') returns X, X > 0.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void castToNonexistentClass() throws PrologException {
		String goal = "java_object('java.lang.String', ['hello'], S1), " +
				      "java_object('java.lang.String', ['world'], S2), " +
				      "S2 <- compareTo(S1 as 'non.existant.Class') returns X.";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void castObjectToWrongParameterType() throws PrologException {
		String goal = "java_object('java.util.ArrayList', [], L), " +
				      "java_object('java.lang.String', ['hello'], S), " +
				      "S <- compareToIgnoreCase(L as 'java.util.List') returns X.";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void castParameterToWrongType() throws PrologException {
		String goal = "java_object('java.lang.String', ['hello'], S), " +
				      "java_object('java.lang.Integer', [2], I), " +
				      "S <- indexOf(I as 'java.util.List') returns N.";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}

}
