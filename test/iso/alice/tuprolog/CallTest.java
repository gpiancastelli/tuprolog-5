package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;

public class CallTest {
	
	Prolog engine;
	String output;
	
	@Before
	public void setUp() {
		engine = new Prolog();
		try {
			engine.setTheory(new Theory(
					"b(X) :-\n" +
					"Y = (write(X), X),\n" +
					"call(Y).\n" +
					"a(1).\n" +
					"a(2)."));
		} catch (InvalidTheoryException e) {
		}
	}
	
	@Test public void callCut() throws PrologException {
		SolveInfo solution = engine.solve("call(!).");
		assertTrue(solution.isSuccess());
	}
	
	@Test public void callFail() throws PrologException {
		SolveInfo solution = engine.solve("call(fail).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void callAvoidInstantiationError() throws PrologException {
		SolveInfo solution = engine.solve("call((fail, X)).");
		assertFalse(solution.isSuccess());
	}
	
	@Test public void callAvoidTypeError() throws PrologException {
		SolveInfo solution = engine.solve("call((fail, call(1))).");
		assertFalse(solution.isSuccess());
	}
	
	@Test(expected=AssertionError.class) 
	public void callIndirectInstantiationError() throws PrologException {
		output = "";
		engine.addOutputListener(new OutputListener() {
			@Override
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("b(_).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
		assertEquals("", output);
		engine.removeAllOutputListeners();
	}
	
	@Test(expected=AssertionError.class)
	public void callIndirectTypeError() throws PrologException {
		output = "";
		engine.addOutputListener(new OutputListener() {
			@Override
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("b(3).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 3)
		// According to the standard, the goal should output '3', but this
		// behavior is incompatible with the type_error(callable, 3) that
		// the standard prescribes the goal to throw
		assertEquals("", output);
		engine.removeAllOutputListeners();
	}
	
	@Test public void callBindingNoReexecution() throws PrologException {
		SolveInfo solution = engine.solve("Z = !, call((Z=!, a(X), Z)).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		assertEquals(new Struct("!"), solution.getTerm("Z"));
		assertFalse(solution.hasOpenAlternatives());
	}
	
	@Test public void callBindingWithReexecution() throws PrologException {
		SolveInfo solution = engine.solve("call((Z=!, a(X), Z)).");
		assertTrue(solution.isSuccess());
		assertEquals(new Int(1), solution.getTerm("X"));
		assertEquals(new Struct("!"), solution.getTerm("Z"));
		solution = engine.solveNext();
		assertTrue(solution.isSuccess());
		assertEquals(new Int(2), solution.getTerm("X"));
		assertEquals(new Struct("!"), solution.getTerm("Z"));
		assertFalse(engine.hasOpenAlternatives());
	}
	
	@After
	public void tearDown() {
		engine.clearTheory();
	}
	
	@Test public void callOutputWithInstantiationError() throws PrologException {
		output = "";
		engine.addOutputListener(new OutputListener() {
			@Override
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("call((write(3), X)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
		assertEquals("3", output);
		engine.removeAllOutputListeners();
	}
	
	@Test public void callOutputWithTypeError() throws PrologException {
		output = "";
		engine.addOutputListener(new OutputListener() {
			@Override
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("call((write(3), call(1))).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 1)
		assertEquals("3", output);
		engine.removeAllOutputListeners();
	}
	
	@Test public void callInstantiationError() throws PrologException {
		SolveInfo solution = engine.solve("call(X).");
		assertFalse(solution.isSuccess());
		// TODO Should throw instantiation_error
	}
	
	@Test public void callTypeError() throws PrologException {
		SolveInfo solution = engine.solve("call(1).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, 1)
	}
	
	@Test public void callConjunctionTypeError() throws PrologException {
		SolveInfo solution = engine.solve("call((fail, 1)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, (fail, 1))
	}
	
	@Test public void callConjunctionTypeErrorWithoutOutput() throws PrologException {
		output = "";
		engine.addOutputListener(new OutputListener() {
			@Override
			public void onOutput(OutputEvent e) {
				output += e.getMsg();
			}
		});
		SolveInfo solution = engine.solve("call((write(3), 1)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, (write(3), 1))
		assertEquals("", output);
		engine.removeAllOutputListeners();
	}
	
	@Test public void callDisjunctionTypeError() throws PrologException {
		SolveInfo solution = engine.solve("call((1;true)).");
		assertFalse(solution.isSuccess());
		// TODO Should throw type_error(callable, (1 ; true))
	}

}
