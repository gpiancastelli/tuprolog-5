package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class StructIteratorTestCase {
	
	@Test public void emptyIterator() {
		Struct list = new Struct();
		Iterator i = list.listIterator();
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException expected) {}
	}
	
	@Test public void testIteratorCount() {
		Struct list = new Struct(new Term[] {new Int(1), new Int(2), new Int(3), new Int(5), new Int(7)});
		Iterator i = list.listIterator();
		int count = 0;
		for (; i.hasNext(); count++)
			i.next();
		assertEquals(5, count);
		assertFalse(i.hasNext());
	}
	
	@Test public void multipleHasNext() {
		Struct list = new Struct(new Term[] {new Struct("p"), new Struct("q"), new Struct("r")});
		Iterator i = list.listIterator();
		assertTrue(i.hasNext());
		assertTrue(i.hasNext());
		assertTrue(i.hasNext());
		assertEquals(new Struct("p"), i.next());
	}
	
	@Test public void multipleNext() {
		Struct list = new Struct(new Term[] {new Int(0), new Int(1), new Int(2), new Int(3), new Int(5), new Int(7)});
		Iterator i = list.listIterator();
		assertTrue(i.hasNext());
		i.next(); // skip the first term
		assertEquals(new Int(1), i.next());
		assertEquals(new Int(2), i.next());
		assertEquals(new Int(3), i.next());
		assertEquals(new Int(5), i.next());
		assertEquals(new Int(7), i.next());
		// no more terms
		assertFalse(i.hasNext());
		try {
			i.next();
			fail();
		} catch (NoSuchElementException expected) {}
	}
	
	@Test public void removeOperationNotSupported() {
		Struct list = new Struct(new Int(1), new Struct());
		Iterator i = list.listIterator();
		assertNotNull(i.next());
		try {
			i.remove();
			fail();
		} catch (UnsupportedOperationException expected) {}
	}

}
