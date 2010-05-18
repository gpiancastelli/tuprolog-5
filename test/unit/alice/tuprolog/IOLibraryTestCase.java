package alice.tuprolog;

import alice.tuprolog.lib.IOLibrary;
import junit.framework.TestCase;

public class IOLibraryTestCase extends TestCase {
	
	public void testGetPrimitives() {
		Library library = new IOLibrary();
		java.util.List[] primitives = library.getPrimitives();
		assertEquals(3, primitives.length);
		assertEquals(0, primitives[PrimitiveInfo.DIRECTIVE].size());
		assertTrue(primitives[PrimitiveInfo.PREDICATE].size() > 0);
		assertEquals(0, primitives[PrimitiveInfo.FUNCTOR].size());
	}
	
	public void testTab1() throws MalformedGoalException {
		Prolog engine = new Prolog();
		TestOutputListener l = new TestOutputListener();
		engine.addOutputListener(l);
		engine.solve("tab(5).");
		assertEquals("     ", l.output);
	}

}
