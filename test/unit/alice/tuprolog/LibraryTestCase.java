package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LibraryTestCase {
	
	@Test public void libraryFunctor() throws PrologException {
		Prolog engine = new Prolog();
		engine.loadLibrary(new TestLibrary());
		SolveInfo goal = engine.solve("N is sum(1, 3).");
		assertTrue(goal.isSuccess());
		assertEquals(new Int(4), goal.getVarValue("N"));
	}
	
	@Test public void libraryPredicate() throws PrologException {
		Prolog engine = new Prolog();
		engine.loadLibrary(new TestLibrary());
		TestOutputListener l = new TestOutputListener();
		engine.addOutputListener(l);
		engine.solve("println(sum(5)).");
		assertEquals("sum(5)", l.output);
	}

}
