package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class TheoryManagerTestCase {
	
	@Test public void unknownDirective() throws InvalidTheoryException {
		String theory = ":- unidentified_directive(unknown_argument).";
		Prolog engine = new Prolog();
		TestWarningListener warningListener = new TestWarningListener();
		engine.addWarningListener(warningListener);
		engine.setTheory(new Theory(theory));
		assertTrue(warningListener.warning.indexOf("unidentified_directive/1") > 0);
		assertTrue(warningListener.warning.indexOf("is unknown") > 0);
	}
	
	@Test public void failedDirective() throws InvalidTheoryException {
		String theory = ":- load_library('UnknownLibrary').";
		Prolog engine = new Prolog();
		TestWarningListener warningListener = new TestWarningListener();
		engine.addWarningListener(warningListener);
		engine.setTheory(new Theory(theory));
		assertTrue(warningListener.warning.indexOf("load_library/1") > 0);
		assertTrue(warningListener.warning.indexOf("InvalidLibraryException") > 0);
	}
	
	@Test public void assertNotBacktrackable() throws PrologException {
		Prolog engine = new Prolog();
		SolveInfo firstSolution = engine.solve("assertz(a(z)).");
		assertTrue(firstSolution.isSuccess());
		assertFalse(firstSolution.hasOpenAlternatives());
	}
	
	@Test public void abolish() throws PrologException {
		Prolog engine = new Prolog();
		String theory = "test(A, B) :- A is 1+2, B is 2+3.";
		engine.setTheory(new Theory(theory));
		TheoryManager manager = engine.getTheoryManager();
		Struct testTerm = new Struct("test", new Struct("a"), new Struct("b"));
		List<ClauseInfo> testClauses = manager.find(testTerm);
		assertEquals(1, testClauses.size());
		manager.abolish(new Struct("test/2"));
		testClauses = manager.find(testTerm);
		// The predicate should also disappear completely from the clause
		// database, i.e. ClauseDatabase#get(f/a) should return null
		assertEquals(0, testClauses.size());
	}
	
	// TODO test retractall: ClauseDatabase#get(f/a) should return an
	// empty list
	
}
