package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AtomCodesTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void emptyAtom() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes('', L).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct(), solution.getTerm("L"));
	}
	
	@Test public void emptyListAtom() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes([], L).");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Int(91), new Int(93)});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void singleQuote() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes('''', L).");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Int(39), new Struct());
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void simpleAtom() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes('ant', L).");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Int(97), new Int(110), new Int(116)});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void simpleList() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes(Str, [0's, 0'o, 0'p]).");
		assertTrue(solution.isSuccess());
		assertEquals(new Struct("sop"), solution.getTerm("Str"));
	}
	
	@Test public void subatomToListTail() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes('North', [0'N | X]).");
		assertTrue(solution.isSuccess());
		Term[] codes = new Term[] {new Int(111), new Int(114), new Int(116), new Int(104)};
		assertEquals(new Struct(codes), solution.getTerm("X"));
	}
	
	@Test public void wrongList() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes('soap', [0's, 0'o, 0'p]).");
		assertFalse(solution.isSuccess());
	}
	
	@Test(expected=AssertionError.class)
	public void everythingAsAVariable() throws PrologException {
		SolveInfo solution = engine.solve("atom_codes(X, Y).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
	}

}
