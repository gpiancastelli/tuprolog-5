package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class JavaObjectTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void nonexistentClass() throws PrologException {
		SolveInfo solution = engine.solve("java_object('non.existant.Class', [], _).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void nonexistentConstructor() throws PrologException {
		SolveInfo solution = engine.solve("java_object('java.lang.Integer', [], _).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void unboundParameter() throws PrologException {
		SolveInfo solution = engine.solve("java_object('java.lang.String', [X], _).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void alreadyBoundObject() throws PrologException {
		String goal = "java_object('java.lang.Integer', [0], Z),\n" +
		              "java_object('java.lang.Integer', [1], Z).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void twoEqualObjects() throws PrologException {
		String goal = "java_object('java.lang.Integer', [0], zero),\n" +
		              "java_object('java.lang.Integer', [0], expected),\n" +
		              "zero <- equals(expected) returns true.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void variableObjectName() throws PrologException {
		String goal = "java_object('java.lang.Object', [], X1),\n" +
		              "java_object('java.lang.Object', [], X2).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("$obj_0"), solution.getTerm("X1"));
		assertEquals(new Struct("$obj_1"), solution.getTerm("X2"));
	}
	
	@Test public void verifyUnderlyingObject() throws PrologException {
		String goal = "java_object('java.lang.Integer[]', [0], A),\n" +
				      "A <- getClass returns C,\n" +
				      "C <- getName returns N.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("[Ljava.lang.Integer;"), solution.getTerm("N"));
	}
	
	@Test public void variableAsParameter() throws PrologException {
		String goal = "java_object('java.lang.Integer', [0], Zero),\n" +
				      "java_object('java.lang.Integer', [0], Expected),\n" +
				      "Zero <- equals(Expected) returns Result.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(Struct.TRUE, solution.getTerm("Result"));
	}

}
