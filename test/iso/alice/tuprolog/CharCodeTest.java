package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CharCodeTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void singleCharacter() throws PrologException {
		SolveInfo solution = engine.solve("char_code('a', Code).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(97), solution.getTerm("Code"));
	}
	
	@Test public void singleCode() throws PrologException {
		SolveInfo solution = engine.solve("char_code(Str, 99).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("c"), solution.getTerm("Str"));
	}
	
	@Test public void nonAsciiCode() throws PrologException {
		SolveInfo solution = engine.solve("char_code(Str, 163).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("£"), solution.getTerm("Str"));
	}
	
	@Test public void wrongCode() throws PrologException {
		SolveInfo solution = engine.solve("char_code('b', 84).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void characterAsAtom() throws PrologException {
		SolveInfo solution = engine.solve("char_code('ab', Int).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(character, ab)
	}
	
	@Test public void everythingAsAVariable() throws PrologException {
		SolveInfo solution = engine.solve("char_code(C, I).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
	}

}
