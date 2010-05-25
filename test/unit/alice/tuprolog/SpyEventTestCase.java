package alice.tuprolog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import alice.tuprolog.event.SpyEvent;

public class SpyEventTestCase {
	
	@Test public void stringRepresentation() {
		String msg = "testConstruction";
		SpyEvent e = new SpyEvent(new Prolog(), msg);
		assertEquals(msg, e.toString());
	}

}
