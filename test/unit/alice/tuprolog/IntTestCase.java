package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class IntTestCase {
	
	@Test public void isAtomic() {
		assertTrue(new Int(0).isAtomic());
	}
	
	@Test public void isAtom() {
		assertFalse(new Int(0).isAtom());
	}
	
	@Test public void isCompound() {
		assertFalse(new Int(0).isCompound());
	}
	
	@Test public void equalsToStruct() {
		Struct s = new Struct();
		Int zero = new Int(0);
		assertFalse(zero.equals(s));
	}
	
	@Test public void equalsToVar() throws InvalidTermException {
		Var x = new Var("X");
		Int one = new Int(1);
		assertFalse(one.equals(x));
	}
	
	@Test public void equalsToInt() {
		Int zero = new Int(0);
		Int one = new Int(1);
		assertFalse(zero.equals(one));
		Int anotherZero = new Int(1-1);
		assertTrue(anotherZero.equals(zero));
	}
	
	@Ignore("Not implemented")
	@Test public void equalsToLong() {
		// TODO Test Int numbers for equality with Long numbers
	}
	
	@Test public void equalsToDouble() {
		Int integerOne = new Int(1);
		alice.tuprolog.Double doubleOne = new alice.tuprolog.Double(1);
		assertFalse(integerOne.equals(doubleOne));
	}
	
	@Ignore("Not implemented")
	@Test public void equalsToFloat() {
		// TODO Test Int numbers for equality with Float numbers
	}

}
