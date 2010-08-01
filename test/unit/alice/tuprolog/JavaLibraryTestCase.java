package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import alice.tuprolog.lib.InvalidObjectIdException;
import alice.tuprolog.lib.JavaLibrary;

public class JavaLibraryTestCase {
	
	@Test public void getPrimitives() {
		Library library = new JavaLibrary();
		List<PrimitiveInfo>[] primitives = library.getPrimitives();
		assertEquals(3, primitives.length);
		assertEquals(0, primitives[PrimitiveInfo.DIRECTIVE].size());
		assertTrue(primitives[PrimitiveInfo.PREDICATE].size() > 0);
		assertEquals(0, primitives[PrimitiveInfo.FUNCTOR].size());
	}
	
	@Test public void anonymousObjectRegistration() throws InvalidTheoryException, InvalidObjectIdException {
		Prolog engine = new Prolog();		
		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
		String theory = "demo(X) :- X <- update. \n";
		engine.setTheory(new Theory(theory));
		TestCounter counter = new TestCounter();
		// check registering behavior
		Struct t = lib.register(counter);
		engine.solve(new Struct("demo", t));
		assertEquals(1, counter.getValue());
		// check unregistering behavior
		lib.unregister(t);
		SolveInfo goal = engine.solve(new Struct("demo", t));
		assertFalse(goal.isSuccess());
	}
	
	@Test public void dynamicObjectsRetrival() throws PrologException {
		Prolog engine = new Prolog();
		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
		String theory = "demo(C) :- \n" +
		                "java_object('alice.tuprolog.TestCounter', [], C), \n" +
		                "C <- update, \n" +
		                "C <- update. \n";			
		engine.setTheory(new Theory(theory));
		SolveInfo info = engine.solve("demo(Obj).");
		Struct id = (Struct) info.getVarValue("Obj");
		TestCounter counter = (TestCounter) lib.getRegisteredDynamicObject(id);
		assertEquals(2, counter.getValue());
	}

}
