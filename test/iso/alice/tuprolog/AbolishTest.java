package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class AbolishTest {
	
	Prolog engine;
	
	@Before
	public void setUp() {
		engine = new Prolog();
	}
	
	@Test public void abolishProcedure() throws PrologException {
		SolveInfo solution = engine.solve("abolish(foo/2).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void procedureIndicatorWithoutArity() throws PrologException {
		SolveInfo solution = engine.solve("abolish(foo/_).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
	}
	
	@Test(expected=AssertionError.class) 
	public void abolishNotAProcedureIndicator() throws PrologException {
		SolveInfo solution = engine.solve("abolish(foo).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(predicate_indicator, foo)
		solution = engine.solve("abolish(foo(_)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(predicate_indicator, foo(_))
	}
	
	@Test(expected=AssertionError.class) 
	public void abolishStaticProcedure() throws PrologException {
		SolveInfo solution = engine.solve("abolish(abolish/1).");
		assertFalse(solution.isSuccess());
		// TODO Should throw permission_error(modify, static_procedure, abolish/1)
	}

}
