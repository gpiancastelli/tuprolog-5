package alice.tuprolog;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TuPrologTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for alice.tuprolog");
		suite.addTestSuite(BuiltInTestCase.class);
		suite.addTestSuite(PrologTestCase.class);
		suite.addTestSuite(IntTestCase.class);
		suite.addTestSuite(IOLibraryTestCase.class);
		suite.addTestSuite(DoubleTestCase.class);
		suite.addTestSuite(SolveInfoTestCase.class);
		suite.addTestSuite(StateRuleSelectionTestCase.class);
		suite.addTestSuite(StructIteratorTestCase.class);
		suite.addTestSuite(StructTestCase.class);
		suite.addTestSuite(TermIteratorTestCase.class);
		suite.addTestSuite(TheoryTestCase.class);
		suite.addTestSuite(TheoryManagerTestCase.class);
		suite.addTestSuite(LibraryTestCase.class);
		suite.addTestSuite(JavaLibraryTestCase.class);
		suite.addTestSuite(ParserTestCase.class);
		suite.addTestSuite(SpyEventTestCase.class);
		suite.addTestSuite(VarTestCase.class);
		return suite;
	}

}
