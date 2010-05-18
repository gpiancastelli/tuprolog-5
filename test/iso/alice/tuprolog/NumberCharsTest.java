package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NumberCharsTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void integerNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(33, L).");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Struct("3"), new Struct("3")});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void integerNumberWithList() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(33, ['3', '3']).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void realNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(33.0, L).");
		assertTrue(solution.isSuccess());
		Term[] chars = new Term[] {new Struct("3"), new Struct("3"), new Struct("."), new Struct("0")};
		assertEquals(new Struct(chars), solution.getTerm("L"));
	}
	
	@Test public void realListWithExponent() throws PrologException {
		String goal = "number_chars(X, ['3', '.', '3', 'E', '+', '0']).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Double(3.3), solution.getTerm("X"));
	}
	
	@Test public void realNumberWithList() throws PrologException {
		String goal = "number_chars(3.3, ['3', '.', '3', 'E', '+', '0']).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
	}
	
	@Test public void negativeNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(A, ['-', '2', '5']).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(-25), solution.getTerm("A"));
	}
	
	@Test(expected=AssertionError.class)
	public void whitespaceCharacters() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(A, ['\\n', ' ', '3']).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(5), solution.getTerm("A"));
	}
	
	@Test(expected=AssertionError.class)
	public void syntaxError() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(A, ['3', ' ']).");
		assertFalse(solution.isSuccess());
		// TODO Should throw syntax_error
	}
	
	@Test(expected=AssertionError.class) 
	public void hexadecimalNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(A, ['0', x, f]).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(15), solution.getTerm("A"));
	}
	
	@Test(expected=AssertionError.class)
	public void characterIntegerSequence() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(A, ['0', '''', a]).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(97), solution.getTerm("A"));
	}
	
	@Test public void realList() throws PrologException {
		SolveInfo solution = engine.solve("number_chars(A, ['4', '.', '2']).");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Double(4.2), solution.getTerm("A"));
	}
	
	@Test public void realListWithNegativeExponent() throws PrologException {
		String goal = "number_chars(A, ['4', '2', '.', '0', 'e', '-', '1']).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Double(4.2), solution.getTerm("A"));
	}

}
