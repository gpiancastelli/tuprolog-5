package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;

public class RetractTest {
	
	Prolog engine;
	String output;
	
	@Before
	public void setUp() {
		engine = new Prolog();
		try {
			engine.setTheory(new Theory(
					":- dynamic(legs/2).\n" +
			        "legs(A, 4) :- animal(A).\n" +
			        "legs(octopus, 8).\n" +
			        "legs(A, 6) :- insect(A).\n" +
			        "legs(spider, 8).\n" +
			        "legs(B, 2) :- bird(B).\n" +
			        ":- dynamic(insect/1).\n" +
			        "insect(ant).\n" +
			        "insect(bee).\n" +
			        ":- dynamic(foo/1).\n" +
			        "foo(X) :- call(X), call(X).\n" +
			        "foo(X) :- call(X) -> call(X)."));
		} catch (InvalidTheoryException e) {
		}
	}
	
	@Test public void retractLegs() throws PrologException {
		String fact = "retract(legs(octopus, 8)).";
		SolveInfo solution = engine.solve(fact);
		assertTrue(solution.isSuccess());
		String missing = "retract(legs(spider, 6)).";
		solution = engine.solve(missing);
		assertFalse(solution.isSuccess());
		String rule = "retract((legs(X, 2) :- T)).";
		solution = engine.solve(rule);
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("bird", new Var("X")), solution.getTerm("T"));
		String ruleWithAlternatives = "retract((legs(X, Y) :- Z)).";
		solution = engine.solve(ruleWithAlternatives);
		assertEquals(new Int(4), solution.getTerm("Y"));
		assertEquals(new Struct("animal", new Var("X")), solution.getTerm("Z"));
		solution = engine.solveNext();
		assertEquals(new Int(6), solution.getTerm("Y"));
		assertEquals(new Struct("insect", new Var("X")), solution.getTerm("Z"));
		solution = engine.solveNext();
		assertEquals(new Struct("spider"), solution.getTerm("X"));
		assertEquals(new Int(8), solution.getTerm("Y"));
		assertEquals(Struct.TRUE, solution.getTerm("Z"));
		solution = engine.solveNext();
		assertFalse(solution.isSuccess());
		solution = engine.solve(rule);
		assertFalse(solution.isSuccess());
	}
	
	@Test(expected=AssertionError.class)
	public void retractInsect() throws PrologException {
		output = "";
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		String goal = "retract(insect(I)), write(I), retract(insect(bee)), fail.";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
		assertEquals("antbee", output);
		engine.removeAllOutputListeners();
	}
	
	@Test public void retractFoo() throws PrologException {
		String goal = "retract((foo(A) :- A, call(A))).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
		goal = "retract((foo(C) :- A -> B)).";
		solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		Struct callc = new Struct("call", new Var("C"));
		assertEquals(callc, solution.getTerm("A"));
		assertEquals(callc, solution.getTerm("B"));
	}
	
	@Test public void headAsVariable() throws PrologException {
		String goal = "retract((X :- in_eec(Y))).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
		// FIXME Actually throws a RuntimeException
	}
	
	@Test public void headAsNumber() throws PrologException {
		String goal = "retract((4 :- X)).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 4)
	}
	
	@Test public void retractStaticProcedure() throws PrologException {
		String goal = "retract((atom(_) :- X == '[]')).";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
		// TODO Should throw permission_error(modify, static_procedure, atom/1)
	}
	
	@After
	public void tearDown() {
		engine.clearTheory();
	}
	
}
