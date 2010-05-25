package alice.tuprolog;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class VarTestCase {
	
	@Test public void isAtomic() {
		assertFalse(new Var("X").isAtomic());
	}
	
	@Test public void isAtom() {
		assertFalse(new Var("X").isAtom());
	}
	
	@Test public void isCompound() {
		assertFalse(new Var("X").isCompound());
	}

}
