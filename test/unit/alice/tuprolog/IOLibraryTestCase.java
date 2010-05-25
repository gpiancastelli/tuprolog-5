package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import alice.tuprolog.lib.IOLibrary;

public class IOLibraryTestCase {
	
	@Test public void getPrimitives() {
		Library library = new IOLibrary();
		java.util.List[] primitives = library.getPrimitives();
		assertEquals(3, primitives.length);
		assertEquals(0, primitives[PrimitiveInfo.DIRECTIVE].size());
		assertTrue(primitives[PrimitiveInfo.PREDICATE].size() > 0);
		assertEquals(0, primitives[PrimitiveInfo.FUNCTOR].size());
	}
	
	@Test public void testTab1() throws MalformedGoalException {
		Prolog engine = new Prolog();
		TestOutputListener l = new TestOutputListener();
		engine.addOutputListener(l);
		engine.solve("tab(5).");
		assertEquals("     ", l.output);
	}

}
