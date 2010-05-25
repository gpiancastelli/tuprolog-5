package alice.tuprolog;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StateRuleSelectionTestCase {
	
	@Test public void unknownPredicateInQuery() throws MalformedGoalException {
		Prolog engine = new Prolog();
		TestWarningListener warningListener = new TestWarningListener();
		engine.addWarningListener(warningListener);
		String query = "p(X).";
		engine.solve(query);
		assertTrue(warningListener.warning.indexOf("p/1") > 0);
		assertTrue(warningListener.warning.indexOf("is unknown") > 0);
	}
	
	@Test public void unknownPredicateInTheory() throws InvalidTheoryException, MalformedGoalException {
		Prolog engine = new Prolog();
		TestWarningListener warningListener = new TestWarningListener();
		engine.addWarningListener(warningListener);
		String theory = "p(X) :- a, b. \nb.";
		engine.setTheory(new Theory(theory));
		String query = "p(X).";
		engine.solve(query);
		assertTrue(warningListener.warning.indexOf("a/0") > 0);
		assertTrue(warningListener.warning.indexOf("is unknown") > 0);
	}

}
