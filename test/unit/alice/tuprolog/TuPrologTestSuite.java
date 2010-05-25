package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({BuiltInTestCase.class,
	                 DoubleTestCase.class,
	                 IntTestCase.class,
	                 IOLibraryTestCase.class,
	                 JavaLibraryTestCase.class,
	                 LibraryTestCase.class,
	                 ParserTestCase.class,
	                 PrologTestCase.class,
	                 SolveInfoTestCase.class,
	                 SpyEventTestCase.class,
	                 StateRuleSelectionTestCase.class,
	                 StructIteratorTestCase.class,
	                 StructTestCase.class,
	                 TermIteratorTestCase.class,
	                 TheoryManagerTestCase.class,
	                 TheoryTestCase.class,
	                 VarTestCase.class})
public class TuPrologTestSuite {}