package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TheoryTestCase {

	@Test public void toStringWithParenthesis() throws InvalidTheoryException {
		String before = "a :- b, (d ; e).";
		Theory theory = new Theory(before);
		String after = theory.toString();
		assertEquals(theory.toString(), new Theory(after).toString());
	}
	
	@Test public void appendClauseLists() throws InvalidTheoryException, MalformedGoalException {
		Term[] clauseList = new Term[] {new Struct("p"), new Struct("q"), new Struct("r")};
		Term[] otherClauseList = new Term[] {new Struct("a"), new Struct("b"), new Struct("c")};
		Theory theory = new Theory(new Struct(clauseList));
		theory.append(new Theory(new Struct(otherClauseList)));
		Prolog engine = new Prolog();
		engine.setTheory(theory);
		assertTrue((engine.solve("p.")).isSuccess());
		assertTrue((engine.solve("b.")).isSuccess());
	}

}
