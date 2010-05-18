package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;

/*
 * This class gathers tests for tuProlog bugs that have not been tested
 * in any other more fine-grained way (e.g. through a unit test) and that
 * the ISO standard tests alone were not able to catch. It is a messy
 * melting-pot, probably in need of a proper reorganization.
 */
public class BugsTest {
	
	Prolog engine;
	String output;
	
	@Before
	public void setUp() {
		engine = new Prolog();
		output = "";
	}
	
	@Test public void numberUnification() throws PrologException {
		String[] goals = new String[] {"0.0 = 0.", "0.9 = 0.", "0 = 0.9."};
		for (String goal : goals) {
			SolveInfo solution = engine.solve(goal);
			assertFalse(solution.isSuccess());
		}
	}
	
	@Test public void numberComparison() throws PrologException {
		String[] goals = new String[] {"1.0 == 1.", "1 == 1.0."};
		for (String goal : goals) {
			SolveInfo solution = engine.solve(goal);
			assertFalse(solution.isSuccess());
		}
	}
	
	@Test public void expandingSubgoals() throws PrologException {
		engine.setTheory(new Theory(
				"a.\n" +
				"p((a,fail)).\n" +
				"p((a))."));
		SolveInfo solution = engine.solve("p(X), X.");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("a"), solution.getTerm("X"));
	}
	
	@Test public void arithmeticOperations() throws PrologException {
		SolveInfo solution = engine.solve("X is 1169658768269 - 531550800000.");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Long(638107968269L), solution.getTerm("X"));
		
		solution = engine.solve("X is -2147475543 - 9000.");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Long(-2147484543L), solution.getTerm("X"));
		
		solution = engine.solve("X is 1169658768269 + 531550800000.");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Long(1701209568269L), solution.getTerm("X"));
		
		solution = engine.solve("X is 2147483543 + 8000.");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Long(2147491543L), solution.getTerm("X"));
		
		solution = engine.solve("X is 1474845 * 3634.");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Long(5359586730L), solution.getTerm("X"));
		
		solution = engine.solve("X is 4651880372 / -1.");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Long(-4651880372L), solution.getTerm("X"));
		
		solution = engine.solve("X is 4651880372 // -1.");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Long(-4651880372L), solution.getTerm("X"));
	}
	
	@Test public void metaInterpretation() throws PrologException {
		Theory t = new Theory("search(E, [E|_]).\n" +
				              "search(E, [_|T]) :- search(E, T).\n" +
				              "solve(true).\n" +
				              "solve((A, B)) :- !, solve(A), solve(B).\n" +
				              "solve(G) :- clause(G, B), solve(B).");
		engine.setTheory(t);
		SolveInfo solution = engine.solve("solve(search(X, [1,2,3,1])).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("X"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(3), solution.getTerm("X"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		solution = engine.solveNext();
		assertFalse(solution.isSuccess());
	}
	
	/* =../2 (univ) bugs */
	
	@Test public void buildListFromAtom() throws PrologException {
		SolveInfo solution = engine.solve("a =.. L.");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Struct("a")});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void buildListFromNumber() throws PrologException {
		SolveInfo solution = engine.solve("3 =.. L.");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Int(3)});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void fillOneElementListWithAtom() throws PrologException {
		SolveInfo solution = engine.solve("a =.. [X].");
		assertTrue(solution.isSuccess());
		Struct result = new Struct("a");
		assertEquals(result, solution.getTerm("X"));
	}
	
	/* Operator Management */
	
	@Test public void verifySimpleOperatorExistence() throws PrologException {
		SolveInfo solution = engine.solve("current_op(_, _, '+').");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void defineListOfOperators() throws PrologException {
		SolveInfo solution = engine.solve("op(10, yfx, ['@', ':']), current_op(10, yfx, Op).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct(":"), solution.getTerm("Op"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("@"), solution.getTerm("Op"));
	}
	
	/* Functor Identification */
	
	@Test public void isTriggersConstructedFunctorEvaluation() throws PrologException {
		SolveInfo solution = engine.solve("X is 5, Y =.. ['+', X, 2], K is Y.");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(5), solution.getTerm("X"));
		Struct sum = new Struct("+", new Int(5), new Int(2));
		assertEquals(sum, solution.getTerm("Y"));
		assertEquals(new Int(7), solution.getTerm("K"));
	}
	
	@Test public void comparisonOperatorTriggersConstructedFunctorEvaluation() throws PrologException {
		SolveInfo solution = engine.solve("X is 5, Y =.. ['+', X, 2], 10 > Y.");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(5), solution.getTerm("X"));
		Struct sum = new Struct("+", new Int(5), new Int(2));
		assertEquals(sum, solution.getTerm("Y"));
	}
	
	/* Asserting/Retracting Clauses */
	
	@Test public void assertionsWithBindings() throws PrologException {
		engine.setTheory(new Theory("ops(s).\nops(y).\nops(z)."));
		SolveInfo assertion = engine.solve("ops(A), assert(ops_result(A)).");
		assertTrue(assertion.isSuccess());
		assertion = engine.solveNext();
		assertTrue(assertion.isSuccess());
		assertion = engine.solveNext();
		assertTrue(assertion.isSuccess());
		
		SolveInfo queryAssertionResults = engine.solve("ops_result(X).");
		assertTrue(queryAssertionResults.isSuccess());
		assertEquals(new Struct("s"), queryAssertionResults.getTerm("X"));
		queryAssertionResults = engine.solveNext();
		assertTrue(queryAssertionResults.isSuccess());
		assertEquals(new Struct("y"), queryAssertionResults.getTerm("X"));
		queryAssertionResults = engine.solveNext();
		assertTrue(queryAssertionResults.isSuccess());
		assertEquals(new Struct("z"), queryAssertionResults.getTerm("X"));
	}
	
	@Test public void retractallRemovesFactsAndRules() throws PrologException {
		engine.setTheory(new Theory("p(0) :- !.\np(1)."));
		SolveInfo removal = engine.solve("retractall(p(X)).");
		assertTrue(removal.isSuccess());
		SolveInfo query = engine.solve("p(X).");
		assertFalse(query.isSuccess());
	}
	
	/* Call Conjunction */
	
	@Test public void simpleCallConjunction() throws PrologException {
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		String goal = "write(1), X = ','(write(a), write(b)), X, write(2), write(3).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals("1ab23", output);
	}
	
	@Test public void callConjunctionOnTheory() throws PrologException {
		engine.setTheory(new Theory("b :- write(b1).\nb :- write(b2).\nb :- write(b3)."));
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		String goal = "X = ','(write(b0), b), X, write(after), fail.";
		SolveInfo solution = engine.solve(goal);
		assertFalse(solution.isSuccess());
		assertEquals("b0b1afterb2afterb3after", output);
	}
	
	/* Cuts on the correct level */
	
	@Test public void exclusiveClauses() throws PrologException {
		engine.setTheory(new Theory("ops :- fail.\nops :- !,fail.\nops :- true."));
		SolveInfo solution = engine.solve("ops.");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void cutInIfThenElse() throws PrologException {
		engine.setTheory(new Theory("p :- a, (a -> write(a) ; write(b)), fail.\na.\na."));
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("p.");
		assertFalse(solution.isSuccess());
		assertEquals("aa", output);
	}
	
	@Test public void cutInDoubleDisjunctionOnTheSameLevel() throws PrologException {
		engine.setTheory(new Theory("a :- ';'((b, c, ';'((write(cut), !, fail), true)), fail).\n" +
				                    "a :- write(a).\n" +
				                    "b :- write(b1).\n" +
				                    "b :- write(b2).\n" +
				                    "c :- write(c1).\n" +
				                    "c :- write(c2)."));
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("a.");
		assertFalse(solution.isSuccess());
		assertEquals("b1c1cut", output);
	}
	
	@Test public void cutInDoubleDisjunctionOnDifferentLevels() throws PrologException {
		engine.setTheory(new Theory("x :- ';'((y, z), fail).\n" +
				                    "x :- write(x).\n" +
				                    "y :- write(y1).\n" +
				                    "y :- write(y2).\n" +
				                    "z :- ';'((write(cut), !, fail), true).\n" +
				                    "z :- write(z)."));
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("x.");
		assertTrue(solution.isSuccess());
		assertEquals("y1cuty2cutx", output);
	}
	
	@Test public void cutInMetaCall() throws PrologException {
		engine.setTheory(new Theory("go :- fail.\n" +
				                    // X meta-call
				                    "go :- write(1), X = ','(write(cut), !), X, write(2), fail.\n" +
				                    "go :- write(3)."));
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo metaCallX = engine.solve("go.");
		assertTrue(metaCallX.isSuccess());
		assertEquals("1cut23", output);
		
		engine.setTheory(new Theory("go :- fail.\n" +
				                    // call(X) meta-call
				                    "go :- write(1), X = ','(write(cut), !), call(X), write(2), fail.\n" +
				                    "go :- write(3)."));
		output = "";
		SolveInfo metaCallCallX = engine.solve("go.");
		assertTrue(metaCallCallX.isSuccess());
		assertEquals("1cut23", output);
	}
	
	/* 
	 * The following are additional tests on the interaction between cut
	 * and ->/2 or ;/2, written in the process of fixing SourceForge bug
	 * #1675798. Not sure about their usefulness, though.
	 */
	
	@Test public void additionalCutTest0() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "go :- print(ingo), nl, a(X),\n" +
				              "      (print(X), nl -> fail ; print(ingoalt), nl), fail ;\n" +
				              "                              print(altouter), nl, fail.\n" +
				              "go :- print(ingo2),nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("go.");
		assertTrue(solution.isSuccess());
		String result = "ingo\n" +
		                "a\n" +
		                "b\n" +
		                "c\n" +
		                "altouter\n" +
		                "ingo2\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest1() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "goa :- print(ingoX), nl, a(X), print(X), print('X'), nl, !, fail ;\n" +
				              "       print(ingoaltX), nl, fail.\n" +
				              "goa :- print(ingo2X), nl.\n" +
				              "goa :- print(ingo3X), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("goa.");
		assertFalse(solution.isSuccess());
		String result = "ingoX\naX\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest2() throws PrologException {
		Theory t = new Theory("gob :- print(ingoX), nl, (print(Y), print('X'), nl, !, fail ;\n" +
				              "       print(ingoaltX), nl), fail.\n" +
				              "gob :- print(ingo2X), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("gob.");
		assertFalse(solution.isSuccess());
		String result = "ingoX\nY_e1X\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest3() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "b(bbb).\n" +
				              "goc :- print(aaa), nl, a(X),\n" +
				              "       (print(hasAA_), print(X), nl -> b(B),\n" +
				              "        print(hasBB_), print(B), nl, fail ; print(altern))\n" +
				              "        ; print(alternate), nl, fail.\n" +
				              "goc :- print(a222), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("goc.");
		assertTrue(solution.isSuccess());
		String result = "aaa\n" +
		                "hasAA_a\n" +
		                "hasBB_bbb\n" +
		                "hasAA_b\n" +
		                "hasBB_bbb\n" +
		                "hasAA_c\n" +
		                "hasBB_bbb\n" +
		                "alternate\n" +
		                "a222\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest4() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "goe :- print(ingoX), nl, a(X), (print(X), print('X'), nl -> fail ;\n" +
				              "       print(ingoaltX), nl), fail.\n" +
				              "goe :- print(ingo2X), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("goe.");
		assertTrue(solution.isSuccess());
		String result = "ingoX\naX\nbX\ncX\ningo2X\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest5() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "b(bbb).\n" +
				              "gof :- print(aaa), nl, a(X),\n" +
				              "       (print(hasAA_), print(X), nl, !,\n" +
				              "        b(B), print(hasBB_), print(B), nl, fail ; print(altern)) ;\n" +
				              "       print(alternate), nl, fail.\n" +
				              "gof :- print(a222), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("gof.");
		assertFalse(solution.isSuccess());
		String result = "aaa\nhasAA_a\nhasBB_bbb\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest6() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "b(bbb).\n" +
				              "gog :- print(aaa), nl, a(X),\n" +
				              "       (print(hasAA_), print(X), nl, b(B),\n" +
				              "                       print(hasBB_), print(B), nl, fail ;\n" +
				              "                       print(altern), nl, fail) ;\n" +
				              "        print(alternate), nl, fail.\n" +
				              "gog :- print(a222), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("gog.");
		assertTrue(solution.isSuccess());
		String result = "aaa\n" +
		                "hasAA_a\n" +
		                "hasBB_bbb\n" +
		                "altern\n" +
		                "hasAA_b\n" +
		                "hasBB_bbb\n" +
		                "altern\n" +
		                "hasAA_c\n" +
		                "hasBB_bbb\n" +
		                "altern\n" +
		                "alternate\n" +
		                "a222\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest7() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "goh :- print(ingoX), nl,\n" +
				              "       (a(X), print(X), print('X'), nl -> print(good)\n" +
				              "       ; a(X), print(why_), print(X), nl, fail), fail.\n" +
				              "goh :- print(ingo2X), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("goh.");
		assertTrue(solution.isSuccess());
		String result = "ingoX\naX\ngoodingo2X\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest8() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "goi :- print(ingoi), nl, a(X), print(X), nl ->\n" +
				              "       (print(gotit), nl ; print(again), nl, fail).\n" +
				              "goi :- print(ingoi222), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("goi.");
		assertTrue(solution.isSuccess());
		String result = "ingoi\na\ngotit\n";
		assertEquals(result, output);
		
		output = "";
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		result = "again\ningoi222\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest9() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "goj :- print(ingoj), nl, !, (a(X), print(X), nl ->\n" +
				              "                            (print(gotit), nl ; print(again), nl), fail).\n" +
				              "goj :- print(ingoj222), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("goj.");
		assertFalse(solution.isSuccess());
		String result = "ingoj\na\ngotit\nagain\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest10() throws PrologException {
		Theory t = new Theory("a(a).\n" +
				              "a(b).\n" +
				              "a(c).\n" +
				              "gok :- print(ingok), nl, !," +
				              "       (a(X), print(X), nl, (print(gotit), nl ; print(again), nl), fail).\n" +
				              "gok :- print(ingok222), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("gok.");
		assertFalse(solution.isSuccess());
		String result = "ingok\n" +
		                "a\n" +
		                "gotit\n" +
		                "again\n" +
		                "b\n" +
		                "gotit\n" +
		                "again\n" +
		                "c\n" +
		                "gotit\n" +
		                "again\n";
		assertEquals(result, output);
	}
	
	@Test public void additionalCutTest11() throws PrologException {
		Theory t = new Theory("gol :- goll.\n" +
				              "gol :- print(golc2), nl.\n" +
				              "goll :- print(first), nl, gocp, fail ; print(ingol), nl, gocp, fail ;\n" +
				              "        (print(lc), nl -> fail ; print(howdidwegethere1)), nl ;\n" +
				              "        print(howhow), nl, fail.\n" +
				              "goll :- print(howdidwegethere2), nl.\n" +
				              "gocp :- print(gocpa), nl ; print(gocpb), nl.");
		engine.setTheory(t);
		engine.addOutputListener(new OutputListener() {
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("gol.");
		assertTrue(solution.isSuccess());
		String result = "first\n" +
		                "gocpa\n" +
		                "gocpb\n" +
		                "ingol\n" +
		                "gocpa\n" +
		                "gocpb\n" +
		                "lc\n" +
		                "howhow\n" +
		                "howdidwegethere2\n";
		assertEquals(result, output);
		
		output = "";
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		result = "golc2\n";
		assertEquals(result, output);
	}

}
