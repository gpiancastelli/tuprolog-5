package alice.tuprolog;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

public class DoubleTestCase {
	
	@Test public void isAtomic() {
		assertTrue(new alice.tuprolog.Double(0).isAtomic());
	}
	
	@Test public void isAtom() {
		assertFalse(new alice.tuprolog.Double(0).isAtom());
	}
	
	@Test public void isCompound() {
		assertFalse(new alice.tuprolog.Double(0).isCompound());
	}
	
	@Test public void equalsToStruct() {
		alice.tuprolog.Double zero = new alice.tuprolog.Double(0);
		Struct s = new Struct();
		assertFalse(zero.equals(s));
	}
	
	@Test public void equalsToVar() throws InvalidTermException {
		alice.tuprolog.Double one = new alice.tuprolog.Double(1);
		Var x = new Var("X");
		assertFalse(one.equals(x));
	}
	
	@Test public void equalsToDouble() {
		alice.tuprolog.Double zero = new alice.tuprolog.Double(0);
		alice.tuprolog.Double one = new alice.tuprolog.Double(1);
		assertFalse(zero.equals(one));
		alice.tuprolog.Double anotherZero = new alice.tuprolog.Double(0.0);
		assertTrue(anotherZero.equals(zero));
	}
	
	@Ignore("Not implemented")
	@Test public void equalsToFloat() {
		// TODO Test Double numbers for equality with Float numbers
	}
	
	@Test public void equalsToInt() {
		alice.tuprolog.Double doubleOne = new alice.tuprolog.Double(1.0);
		Int integerOne = new Int(1);
		assertFalse(doubleOne.equals(integerOne));
	}
	
	@Ignore("Not implemented")
	@Test public void testEqualsToLong() {
		// TODO Test Double numbers for equality with Long numbers
	}

}
