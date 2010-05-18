package alice.tuprolog;

import junit.framework.TestCase;
import alice.tuprolog.event.*;

public class SpyEventTestCase extends TestCase {
	
	public void testToString() {
		String msg = "testConstruction";
		SpyEvent e = new SpyEvent(new Prolog(), msg);
		assertEquals(msg, e.toString());
	}

}
