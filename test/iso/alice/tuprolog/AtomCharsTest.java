package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AtomCharsTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void emptyAtom() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars('', L).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct(), solution.getTerm("L"));
	}
	
	@Test public void emptyListAtom() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars([], L).");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Struct("["), new Struct("]")});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test(expected=AssertionError.class)
	public void singleQuote() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars('''', L).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("'"), solution.getTerm("L"));
	}
	
	@Test public void simpleAtom() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars('ant', L).");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Struct("a"), new Struct("n"), new Struct("t")});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void simpleList() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars(Str, ['s', 'o', 'p']).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("sop"), solution.getTerm("Str"));
	}
	
	@Test public void subatomToListTail() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars('North', ['N' | X]).");
		assertTrue(solution.isSuccess());
		Term[] chars = new Term[] {new Struct("o"), new Struct("r"), new Struct("t"), new Struct("h")};
		assertEquals(new Struct(chars), solution.getTerm("X"));
	}
	
	@Test public void wrongList() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars('soap', ['s', 'o', 'p']).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void everythingIsAVariable() throws PrologException {
		SolveInfo solution = engine.solve("atom_chars(X, Y).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
	}

}
