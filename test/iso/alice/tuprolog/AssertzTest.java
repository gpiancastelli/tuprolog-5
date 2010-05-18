package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssertzTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
		try {
			engine.setTheory(new Theory(
					":- dynamic(legs/2).\n" +
			        "legs(A, 4) :- animal(A).\n" +
			        "legs(octopus, 8).\n" +
			        "legs(A, 6) :- insect(A).\n" +
			        ":- dynamic(insect/1).\n" +
			        "insect(ant).\n" +
			        "insect(bee).\n" +
			        ":- dynamic(foo/1).\n" +
			        "foo(X) :- call(X), call(X).\n"));
		} catch (InvalidTheoryException e) {
		}
	}
	
	@After
	public void tearDown() {
		engine.clearTheory();
	}
	
	@Test public void databaseModifications() throws PrologException {
		String fact = "assertz(legs(spider, 8)).";
		SolveInfo solution = engine.solve(fact); 
		assertTrue(solution.isSuccess());
		String rule = "assertz((legs(B, 2) :- bird(B))).";
		solution = engine.solve(rule);
		assertTrue(solution.isSuccess());
		String anotherRule = "assertz((foo(X) :- X -> call(X))).";
		solution = engine.solve(anotherRule);
		assertTrue(solution.isSuccess());
		// FIXME Equality between theories...
//		Theory result = new Theory(
//				":- dynamic(legs/2).\n" +
//		        "legs(A, 4) :- animal(A).\n" +
//		        "legs(octopus, 8).\n" +
//		        "legs(A, 6) :- insect(A).\n" +
//		        "legs(spider, 8).\n" +
//		        "legs(B, 2) :- bird(B).\n" +
//		        ":- dynamic(insect/1).\n" +
//		        "insect(ant).\n" +
//		        "insect(bee).\n" +
//		        ":- dynamic(foo/1).\n" +
//		        "foo(X) :- call(X), call(X).\n" +
//		        "foo(X) :- call(X) -> call(X).");
//		assertEquals(result, engine.getTheory());
	}
	
	@Test public void assertVariable() throws PrologException {
		SolveInfo solution = engine.solve("assertz(_).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
	}
	
	@Test public void assertNumber() throws PrologException {
		SolveInfo solution = engine.solve("assertz(4).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 4)
	}
	
	@Test(expected=AssertionError.class)
	public void assertBodyAsNumber() throws PrologException {
		SolveInfo solution = engine.solve("assertz((foo :- 4)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 4)
	}
	
	@Test(expected=AssertionError.class) 
	public void assertStaticProcedure() throws PrologException {
		SolveInfo solution = engine.solve("assertz((atom(_) :- true)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw permission_error(modify, static_procedure, atom/1)
	}

}
