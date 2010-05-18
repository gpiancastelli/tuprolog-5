package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class JavaArrayTest {
	// FIXME Tests in this class throw a number of RuntimeExceptions
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void getOutOfBound() throws PrologException {
		String goal = "java_object('java.lang.Integer[]', [3], A), java_array_get(A, 4, Obj).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void getPrimitiveOutOfBound() throws PrologException {
		String goal = "java_object('java.awt.Polygon', [], P), " +
				      "P.xpoints <- get(XP), java_array_get_boolean(XP, 2, V).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setUnboundElement() throws PrologException {
		String goal = "java_object('java.lang.String[]', [5], A), java_array_set(A, 2, X).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setElementOfWrongType() throws PrologException {
		String goal = "java_object('java.lang.Integer[]', [5], A), java_array_set(A, 2, zero).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setElementOfWrongPrimitiveType() throws PrologException {
		String goal = "java_object('java.awt.Polygon', [], P), " +
				      "P.xpoints <- get(XP), java_array_set_boolean(XP, 3, 2).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void lengthOfNonarrayObject() throws PrologException {
		String goal = "java_object('java.lang.Object', [], Obj), java_array_length(Obj, Size).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void getElementFromNonarrayObject() throws PrologException {
		String goal = "java_object('java.lang.Object', [], Obj), java_array_get(Obj, 0, X).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void getPrimitiveFromNonarrayObject() throws PrologException {
		String goal = "java_object('java.lang.Object', [], Obj), java_array_get_int(Obj, 0, X).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setElementOnNonarrayObject() throws PrologException {
		String goal = "java_object('java.lang.Integer', [0], I), java_array_set(I, 0, 5).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setPrimitiveOnNonarrayObject() throws PrologException {
		String goal = "java_object('java.lang.Integer', [0], I), java_array_set_int(I, 0, 5).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void setAndGetElementAreEquals() throws PrologException {
		String goal = "java_object('java.lang.Object[]', [3], A), " +
				      "java_object('java.lang.Object', [], Obj), " +
				      "java_array_set(A, 2, Obj), java_array_get(A, 2, X), X == Obj.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void arrayLength() throws PrologException {
		String goal = "java_object('java.lang.Integer[]', [3], A), java_array_length(A, Size).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Int(3), solution.getTerm("Size"));
	}
	
	@Test public void getValueFromUninizializedArray() throws PrologException {
		String goal = "java_object('java.lang.Integer[]', [3], A), java_array_get(A, 0, I).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Var(), solution.getTerm("I"));
	}
	
	@Test(expected=AssertionError.class)
	public void getPrimitiveElement() throws PrologException {
		String goal = "java_object('java.awt.Polygon', [], P), " +
				      "P.xpoints <- get(XP), java_array_get_int(XP, 3, V).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Int(0), solution.getTerm("V"));
	}
	
	@Test(expected=AssertionError.class)
	public void getPrimitiveElementWithAutomaticCast() throws PrologException {
		String goal = "java_object('java.awt.Polygon', [], P), " +
				      "P.xpoints <- get(XP), java_array_get_float(XP, 3, V).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Float(0.0f), solution.getTerm("V"));
	}
	
	@Test public void setAndGetIntegerElement() throws PrologException {
		String goal = "java_object('java.lang.Integer[]', [3], A), " +
				      "java_object('java.lang.Integer', [2], Two), " +
				      "java_array_set(A, 2, Two), java_array_get(A, 2, X).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("X"));
	}
	
	@Test(expected=AssertionError.class)
	public void setAndGetPrimitiveElement() throws PrologException {
		String goal = "java_object('java.awt.Polygon', [], P), P.xpoints <- get(XP), " +
				      "java_array_set_int(XP, 3, 2), java_array_get_int(XP, 3, V).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("V"));
	}
	
	@Test(expected=AssertionError.class)
	public void getPrimitiveElementWithoutAutomaticCast() throws PrologException {
		String goal = "java_object('java.awt.Polygon', [], P), P.xpoints <- get(XP), " +
				      "java_array_set_float(XP, 3, 2), java_array_get_int(XP, 3, V).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Float(2.0f), solution.getTerm("V"));
	}

}
