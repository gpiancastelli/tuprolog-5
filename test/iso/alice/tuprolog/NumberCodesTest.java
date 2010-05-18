package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NumberCodesTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void integerNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(33, L).");
		assertTrue(solution.isSuccess());
		Struct result = new Struct(new Term[] {new Int(51), new Int(51)});
		assertEquals(result, solution.getTerm("L"));
	}
	
	@Test public void integerNumberWithList() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(33, [0'3, 0'3]).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void realNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(33.0, L).");
		assertTrue(solution.isSuccess());
		Term[] codes = new Term[] {new Int(51), new Int(51), new Int(46), new Int(48)};
		assertEquals(new Struct(codes), solution.getTerm("L"));
	}
	
	@Test public void negativeNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(A, [0'-, 0'2, 0'5]).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(-25), solution.getTerm("A"));
	}
	
	
	@Test(expected=AssertionError.class) 
	public void whitespaceCodes() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(A, [0' , 0'3]).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(3), solution.getTerm("A"));
	}
	
	@Test(expected=AssertionError.class)
	public void hexadecimalNumber() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(A, [0'0, 0'x, 0'f]).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(15), solution.getTerm("A"));
	}
	
	@Test(expected=MalformedGoalException.class) 
	public void characterCodeSequence() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(A, [0'0, 0''', 0'a]).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(97), solution.getTerm("A"));
	}
	
	@Test public void realList() throws PrologException {
		SolveInfo solution = engine.solve("number_codes(A, [0'4, 0'., 0'2]).");
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Double(4.2), solution.getTerm("A"));
	}
	
	@Test public void realListWithNegativeExponent() throws PrologException {
		String goal = "number_codes(A, [0'4, 0'2, 0'., 0'0, 0'e, 0'-, 0'1]).";
		SolveInfo solution = engine.solve(goal);
		assertTrue(solution.isSuccess());
		assertEquals(new alice.tuprolog.Double(4.2), solution.getTerm("A"));
	}

}
