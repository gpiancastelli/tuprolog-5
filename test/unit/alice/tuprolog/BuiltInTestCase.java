package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class BuiltInTestCase {
	
	@Test public void convertTermToGoal() throws InvalidTermException {
		Term t = new Var("T");
		Struct result = new Struct("call", t);
		assertEquals(result, BuiltIn.convertTermToGoal(t));
		assertEquals(result, BuiltIn.convertTermToGoal(new Struct("call", t)));
		
		t = new Int(2);
		assertNull(BuiltIn.convertTermToGoal(t));
		
		t = new Struct("p", new Struct("a"), new Var("B"), new Struct("c"));
		result = (Struct) t;
		assertEquals(result, BuiltIn.convertTermToGoal(t));
		
		Var linked = new Var("X");
		linked.setLink(new Struct("!"));
		Term[] arguments = new Term[] { linked, new Var("Y") };
		Term[] results = new Term[] { new Struct("!"), new Struct("call", new Var("Y")) };
		assertEquals(new Struct(";", results), BuiltIn.convertTermToGoal(new Struct(";", arguments)));
		assertEquals(new Struct(",", results), BuiltIn.convertTermToGoal(new Struct(",", arguments)));
		assertEquals(new Struct("->", results), BuiltIn.convertTermToGoal(new Struct("->", arguments)));
	}

}
