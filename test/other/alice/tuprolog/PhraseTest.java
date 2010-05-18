package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import alice.tuprolog.lib.DCGLibrary;

public class PhraseTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
		try {
			engine.loadLibrary(new DCGLibrary());
		} catch (InvalidLibraryException e) {
		}
	}
	
	@Test public void simpleGrammar() throws PrologException {
		engine.setTheory(new Theory("a --> []."));
		SolveInfo solution = engine.solve("phrase(a, []).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void nonTerminalSymbol() throws PrologException {
		engine.setTheory(new Theory(
				"a --> [a], b.\n" +
				"b --> []."));
		SolveInfo solution = engine.solve("phrase(a, [a]).");
		assertTrue(solution.isSuccess());
		solution = engine.solve("phrase(b, []).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void multipleProductionsForASymbol() throws PrologException {
		engine.setTheory(new Theory(
				"a --> [a], a, [d].\n" +
				"a --> [b], a, [d].\n" +
				"a --> [c]."));
		SolveInfo solution = engine.solve("phrase(a, [a, b, a, a, c, d, d, d, d]).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void useVariableInProduction() throws PrologException {
		engine.setTheory(new Theory(
				"x(V) --> [a], x(V), [a].\n" +
				"x(V) --> [V]."));
		SolveInfo solution = engine.solve("phrase(x(1), [a, a, a, 1, a, a, a]).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void dcgAction() throws PrologException {
		engine.setTheory(new Theory(
				"x --> [a], x, [a].\n" +
				"x --> [V], { number(V) }."));
		SolveInfo solution = engine.solve("phrase(x, [a, a, a, 151, a, a, a]).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void parseSums() throws PrologException {
		engine.setTheory(new Theory(
				"e --> o, et.\n" +
				"et --> [].\n" +
				"et --> ['+'], e.\n" +
				"o --> ['('], e, [')'].\n" +
				"o --> [X], { number(X) }."));
		SolveInfo solution = engine.solve("phrase(e, [1, '+', '(', 2, '+', 3, ')', '+', 4]).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void evaluateSums() throws PrologException {
		engine.setTheory(new Theory(
				"e(V) --> o(V1), et(V1, V).\n" +
				"et(V, V) --> [].\n" +
				"et(VI, VO) --> ['+'], o(V1), { VI1 is VI + V1 }, et(VI1, VO).\n" +
				"o(V) --> ['('], e(V), [')'].\n" +
				"o(X) --> [X], { number(X) }."));
		SolveInfo solution = engine.solve("phrase(e(V), [1, '+', '(', 2, '+', 3, ')']).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(6), solution.getTerm("V"));
	}
	
	@Test public void parseConjunctions() throws PrologException {
		engine.setTheory(new Theory(
				"e --> t, et.\n" +
				"et --> [].\n" +
				"et --> [and], e.\n" +
				"t --> ['0'].\n" +
				"t --> ['1']."));
		SolveInfo solution = engine.solve("phrase(e, ['0']).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void evaluateConjunctions() throws PrologException {
		engine.setTheory(new Theory(
				"e(V) --> t(W), et(W, V).\n" +
				"et(V, V) --> [].\n" +
				"et(W, V) --> [and], t(V1), { W = 1, V1 = 1, !, W2 = 1 ; W2 = 0 }, et(W2, V).\n" +
				"t(0) --> ['0'].\n" +
				"t(1) --> ['1']."));
		SolveInfo solution = engine.solve("phrase(e(V), ['1']).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("V"));
	}

}
