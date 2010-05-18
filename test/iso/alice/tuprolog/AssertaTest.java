package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AssertaTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
		try {
			engine.setTheory(new Theory(
					":- dynamic(legs/2).\n" +
			        "legs(A, 6) :- insect(A).\n" +
			        ":- dynamic(insect/1).\n" +
			        "insect(ant).\n" +
			        "insect(bee)."));
		} catch (InvalidTheoryException e) {
		}
	}
	
	@Test public void databaseModifications() throws PrologException {
		String fact = "asserta(legs(octopus, 8)).";
		SolveInfo solution = engine.solve(fact); 
		assertTrue(solution.isSuccess());
		String rule = "asserta((legs(A, 4) :- animal(A))).";
		solution = engine.solve(rule);
		assertTrue(solution.isSuccess());
		String anotherRule = "asserta((foo(X) :- X, call(X))).";
		solution = engine.solve(anotherRule);
		assertTrue(solution.isSuccess());
		// FIXME Equality between theories...
//		Theory result = new Theory(
//				":- dynamic(legs/2).\n" +
//		        "legs(A, 4) :- animal(A).\n" +
//		        "legs(octopus, 8).\n" +
//		        "legs(A, 6) :- insect(A).\n" +
//		        ":- dynamic(insect/1).\n" +
//		        "insect(ant).\n" +
//		        "insect(bee).\n" +
//		        ":- dynamic(foo/1).\n" +
//		        "foo(X) :- call(X), call(X).");
//		assertEquals(result, engine.getTheory());
	}
	
	@Test public void assertVariable() throws PrologException {
		SolveInfo solution = engine.solve("asserta(_).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
	}
	
	@Test public void assertNumber() throws PrologException {
		SolveInfo solution = engine.solve("asserta(4).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 4)
	}
	
	@Test(expected=AssertionError.class)
	public void assertBodyAsNumber() throws PrologException {
		SolveInfo solution = engine.solve("asserta((foo :- 4)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 4)
	}
	
	@Test(expected=AssertionError.class)
	public void assertStaticProcedure() throws PrologException {
		SolveInfo solution = engine.solve("asserta((atom(_) :- true)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw permission_error(modify, static_procedure, atom/1)
	}
	
	@After
	public void tearDown() {
		engine.clearTheory();
	}

}
