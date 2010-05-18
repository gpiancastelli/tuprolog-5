package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class InvokeReturnsTest {
	// FIXME Tests in this class throw a number of RuntimeExceptions
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void returnsVoid() throws PrologException {
		String goal = "java_object('java.util.ArrayList', [], L), L <- clear returns X.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void passNonexistentParameter() throws PrologException {
		String goal = "java_object('java.util.ArrayList', [], L), L <- clear(10).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void omitReturnValue() throws PrologException {
		String goal = "java_object('java.util.ArrayList', [], L), L <- size.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void nonexistentMethod() throws PrologException {
		String goal = "java_object('java.lang.Object', [], Obj), Obj <- nonExistentMethod.";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void passUnboundParameter() throws PrologException {
		String goal = "java_object('java.lang.Integer', [0], Z), Z <- compareTo(X).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void passParameterOfWrongType() throws PrologException {
		String goal = "java_object('java.lang.Integer', [0], Z), Z <- compareTo('ciao').";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
	}
	
	@Test public void booleanReturnType() throws PrologException {
		String goal = "java_object('java.util.ArrayList', [], L), L <- isEmpty returns true.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void stringReturnType() throws PrologException {
		String goal = "java_object('java.lang.String', ['hello'], S), S <- toUpperCase returns 'HELLO'.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void staticReturnsVoid() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.System') <- gc returns X.");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void staticIntReturnType() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.Integer') <- parseInt('15') returns 15.");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void staticPassParameterOfWrongType() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.Integer') <- parseInt(10) returns N.");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void staticPassUnboundParameter() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.Integer') <- parseInt(X) returns N.");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void staticOmitReturnValue() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.System') <- currentTimeMillis.");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void staticPassNonexistentParameter() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.System') <- currentTimeMillis(10).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void staticNonexistentClass() throws PrologException {
		SolveInfo solution = engine.solve("class('non.existent.Class') <- nonExistentMethod.");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void staticNonexistentMethod() throws PrologException {
		SolveInfo solution = engine.solve("class('java.lang.Integer') <- nonExistentMethod.");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void bindIntReturnValue() throws PrologException {
		String goal = "java_object('java.lang.Integer', [5], N), N <- intValue returns V.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Int(5), solution.getTerm("V"));
	}
	
	@Test public void bindStringReturnValue() throws PrologException {
		String goal = "java_object('java.util.ArrayList', [], X), X <- toArray returns A.";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("$obj_1"), solution.getTerm("A"));
	}
	
	/* See JVM bug 4071957 at http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957 */
	@Test(expected=AssertionError.class)
	public void acceessPublicMethodsOfInnerClasses() throws PrologException {
		Theory t = new Theory("test_map(Size) :-\n" +
				              "    java_object('java.util.HashMap', [], map),\n" +
				              "    map <- put('key1', 'value1'),\n" +
				              "    map <- put('key2', 'value2'),\n" +
				              "    map <- put('key3', 'value3'),\n" +
				              "    map <- entrySet returns entries,\n" +
				              "    entries <- size returns Size.");
		engine.setTheory(t);
		SolveInfo solution = engine.solve("test_map(S).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(3), solution.getTerm("S"));
	}

}
