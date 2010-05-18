package alice.tuprolog;

import junit.framework.TestCase;

public class IntTestCase extends TestCase {
	
	public void testIsAtomic() {
		assertTrue(new Int(0).isAtomic());
	}
	
	public void testIsAtom() {
		assertFalse(new Int(0).isAtom());
	}
	
	public void testIsCompound() {
		assertFalse(new Int(0).isCompound());
	}
	
	public void testEqualsToStruct() {
		Struct s = new Struct();
		Int zero = new Int(0);
		assertFalse(zero.equals(s));
	}
	
	public void testEqualsToVar() throws InvalidTermException {
		Var x = new Var("X");
		Int one = new Int(1);
		assertFalse(one.equals(x));
	}
	
	public void testEqualsToInt() {
		Int zero = new Int(0);
		Int one = new Int(1);
		assertFalse(zero.equals(one));
		Int anotherZero = new Int(1-1);
		assertTrue(anotherZero.equals(zero));
	}
	
	public void testEqualsToLong() {
		// TODO Test Int numbers for equality with Long numbers
	}
	
	public void testEqualsToDouble() {
		Int integerOne = new Int(1);
		alice.tuprolog.Double doubleOne = new alice.tuprolog.Double(1);
		assertFalse(integerOne.equals(doubleOne));
	}
	
	public void testEqualsToFloat() {
		// TODO Test Int numbers for equality with Float numbers
	}

}
