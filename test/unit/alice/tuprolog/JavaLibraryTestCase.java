package alice.tuprolog;

import alice.tuprolog.lib.InvalidObjectIdException;
import alice.tuprolog.lib.JavaLibrary;
import junit.framework.TestCase;

public class JavaLibraryTestCase extends TestCase {
	
	public void testGetPrimitives() {
		Library library = new JavaLibrary();
		java.util.List[] primitives = library.getPrimitives();
		assertEquals(3, primitives.length);
		assertEquals(0, primitives[PrimitiveInfo.DIRECTIVE].size());
		assertTrue(primitives[PrimitiveInfo.PREDICATE].size() > 0);
		assertEquals(0, primitives[PrimitiveInfo.FUNCTOR].size());
	}
	
	public void testAnonymousObjectRegistration() throws InvalidTheoryException, InvalidObjectIdException {
		Prolog engine = new Prolog();		
		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
		String theory = "demo(X) :- X <- update. \n";
		engine.setTheory(new Theory(theory));
		TestCounter counter = new TestCounter();
		// check registering behaviour
		Struct t = lib.register(counter);
		engine.solve(new Struct("demo", t));
		assertEquals(1, counter.getValue());
		// check unregistering behaviour
		lib.unregister(t);
		SolveInfo goal = engine.solve(new Struct("demo", t));
		assertFalse(goal.isSuccess());
	}
	
	public void testDynamicObjectsRetrival() throws PrologException {
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
