package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

public class ParserTestCase {
	
	@Test public void readingTerms() throws InvalidTermException {
		Parser p = new Parser("hello.");
		Struct result = new Struct("hello");
		assertEquals(result, p.nextTerm(true));
	}
	
	@Test public void readingEOF() throws InvalidTermException {
		Parser p = new Parser("");
		assertNull(p.nextTerm(false));
	}
	
	@Test public void unaryPlusOperator() {
		Parser p = new Parser("n(+100).\n");
        // SICStus Prolog interprets "n(+100)" as "n(100)"
		// GNU Prolog interprets "n(+100)" as "n(+(100))"
		// The ISO Standard says + is not a unary operator
		try {
			assertNotNull(p.nextTerm(true));
			fail();
		} catch (InvalidTermException e) {}
	}
	
	@Test public void unaryMinusOperator() throws InvalidTermException {
		Parser p = new Parser("n(-100).\n");
		// TODO Check the interpretation by other engines
		// SICStus Prolog interprets "n(+100)" as "n(100)"
		// GNU Prolog interprets "n(+100)" as "n(+(100))"
		// What does the ISO Standard say about that?
		Struct result = new Struct("n", new Int(-100));
		result.resolveTerm();
		assertEquals(result, p.nextTerm(true));
	}
	
	@Test public void binaryMinusOperator() throws InvalidTermException {
		String s = "abs(3-11)";
		Parser p = new Parser(s);
		Struct result = new Struct("abs", new Struct("-", new Int(3), new Int(11)));
		assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void listWithTail() throws InvalidTermException {
		Parser p = new Parser("[p|Y]");
		Struct result = new Struct(new Struct("p"), new Var("Y"));
		result.resolveTerm();
		assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void braces() throws InvalidTermException {
		String s = "{a,b,[3,{4,c},5],{a,b}}";
		Parser parser = new Parser(s);
		assertEquals(s, parser.nextTerm(false).toString());
	}
	
	@Test public void univOperator() throws InvalidTermException {
		Parser p = new Parser("p =.. q.");
		Struct result = new Struct("=..", new Struct("p"), new Struct("q"));
		assertEquals(result, p.nextTerm(true));
	}
	
	@Test public void dotOperator() throws InvalidTermException {
		String s = "class('java.lang.Integer').'MAX_VALUE'";
		DefaultOperatorManager om = new DefaultOperatorManager();
		om.opNew(".", "xfx", 600);
		Parser p = new Parser(om, s);
		Struct result = new Struct(".", new Struct("class", new Struct("java.lang.Integer")),
				                        new Struct("MAX_VALUE"));
		assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void bracketedOperatorAsTerm() throws InvalidTermException {
		String s = "u (b1) b2 (b3)";
		DefaultOperatorManager om = new DefaultOperatorManager();
		om.opNew("u", "fx", 200);
		om.opNew("b1", "yfx", 400);
		om.opNew("b2", "yfx", 500);
		om.opNew("b3", "yfx", 300);
		Parser p = new Parser(om, s);
		Struct result = new Struct("b2", new Struct("u", new Struct("b1")), new Struct("b3"));
		assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void bracketedOperatorAsTerm2() throws InvalidTermException {
		String s = "(u) b1 (b2) b3 a";
		DefaultOperatorManager om = new DefaultOperatorManager();
		om.opNew("u", "fx", 200);
		om.opNew("b1", "yfx", 400);
		om.opNew("b2", "yfx", 500);
		om.opNew("b3", "yfx", 300);
		Parser p = new Parser(om, s);
		Struct result = new Struct("b1", new Struct("u"), new Struct("b3", new Struct("b2"), new Struct("a")));
		assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void integerBinaryRepresentation() throws InvalidTermException {
		String n = "0b101101";
		Parser p = new Parser(n);
		alice.tuprolog.Number result = new Int(45);
		assertEquals(result, p.nextTerm(false));
		String invalid = "0b101201";
		try {
			new Parser(invalid).nextTerm(false);
			fail();
		} catch (InvalidTermException expected) {}
	}
	
	@Test public void integerOctalRepresentation() throws InvalidTermException {
		String n = "0o77351";
		Parser p = new Parser(n);
		alice.tuprolog.Number result = new Int(32489);
		assertEquals(result, p.nextTerm(false));
		String invalid = "0o78351";
		try {
			new Parser(invalid).nextTerm(false);
			fail();
		} catch (InvalidTermException expected) {}
	}
	
	@Test public void integerHexadecimalRepresentation() throws InvalidTermException {
		String n = "0xDECAF";
		Parser p = new Parser(n);
		alice.tuprolog.Number result = new Int(912559);
		assertEquals(result, p.nextTerm(false));
		String invalid = "0xG";
		try {
			new Parser(invalid).nextTerm(false);
			fail();
		} catch (InvalidTermException expected) {}
	}
	
	@Test public void emptyDCGAction() throws InvalidTermException {
		String s = "{}";
		Parser p = new Parser(s);
		Struct result = new Struct("{}");
		assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void singleDCGAction() throws InvalidTermException {
		String s = "{hello}";
		Parser p = new Parser(s);
		Struct result = new Struct("{}", new Struct("hello"));
		assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void multipleDCGAction() throws InvalidTermException {
		String s = "{a, b, c}";
		Parser p = new Parser(s);
		Struct result = new Struct("{}",
                                   new Struct(",", new Struct("a"),
                                       new Struct(",", new Struct("b"), new Struct("c"))));
		assertEquals(result, p.nextTerm(false));
	}
	
	@Ignore("This is an error both in 2.0.1 and in 2.1... don't know why, though.")
	@Test public void dcgActionWithOperators() throws Exception {
        String input = "{A =.. B, hotel, 2}";
        Struct result = new Struct("{}",
                            new Struct(",", new Struct("=..", new Var("A"), new Var("B")),
                                new Struct(",", new Struct("hotel"), new Int(2))));
        result.resolveTerm();
        Parser p = new Parser(input);
        assertEquals(result, p.nextTerm(false));
	}
	
	@Test public void missingDCGActionElement() {
		String s = "{1, 2, , 4}";
		Parser p = new Parser(s);
		try {
			p.nextTerm(false);
			fail();
		} catch (InvalidTermException expected) {}
	}
	
	@Test public void dcgActionCommaAsAnotherSymbol() {
		String s = "{1 @ 2 @ 4}";
		Parser p = new Parser(s);
		try {
			p.nextTerm(false);
			fail();
		} catch (InvalidTermException expected) {}
	}
	
	@Test public void uncompleteDCGAction() {
		String s = "{1, 2,}";
		Parser p = new Parser(s);
		try {
			p.nextTerm(false);
			fail();
		} catch (InvalidTermException expected) {}
		
		s = "{1, 2";
		p = new Parser(s);
		try {
			p.nextTerm(false);
			fail();
		} catch (InvalidTermException expected) {}
	}

	@Test public void multilineComments() throws InvalidTermException {
		String theory = "t1." + "\n" +
		                "/*" + "\n" +
		                "t2" + "\n" +
		                "*/" + "\n" +
		                "t3." + "\n";
		Parser p = new Parser(theory);
		assertEquals(new Struct("t1"), p.nextTerm(true));
		assertEquals(new Struct("t3"), p.nextTerm(true));
	}
	
	@Test public void singleQuotedTermWithInvalidLineBreaks() {
		String s = "out('"+
		           "can_do(X).\n"+
		           "can_do(Y).\n"+
	               "').";
		Parser p = new Parser(s);
		try {
			p.nextTerm(true);
			fail();
		} catch (InvalidTermException expected) {}
	}
	
	// TODO More tests on Parser
	
	// Character code for Integer representation
	
	// :-op(500, yfx, v). 3v2 NOT CORRECT, 3 v 2 CORRECT
	// 3+2 CORRECT, 3 + 2 CORRECT
	
	// +(2, 3) is now acceptable
	// what about f(+)

}
