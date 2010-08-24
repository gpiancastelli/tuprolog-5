package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import alice.tuprolog.lib.IOLibrary;

public class IOLibraryTestCase {
	
	@Test public void getPrimitives() {
		Library library = new IOLibrary();
		Map<PrimitiveInfo.Type, List<PrimitiveInfo>> primitives = library.getPrimitives();
		assertEquals(3, primitives.size());
		assertEquals(0, primitives.get(PrimitiveInfo.Type.DIRECTIVE).size());
		assertTrue(primitives.get(PrimitiveInfo.Type.PREDICATE).size() > 0);
		assertEquals(0, primitives.get(PrimitiveInfo.Type.FUNCTOR).size());
	}
	
	@Test public void testTab1() throws MalformedGoalException {
		Prolog engine = new Prolog();
		TestOutputListener l = new TestOutputListener();
		engine.addOutputListener(l);
		engine.solve("tab(5).");
		assertEquals("     ", l.output);
	}

}
