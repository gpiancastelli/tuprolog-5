package alice.tuprolog;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ControlConstructsSuite.class,
	                 TermUnificationSuite.class,
	                 TypeTestingSuite.class,
	                 TermComparisonSuite.class,
	                 TermCreationAndDecompositionSuite.class,
	                 ArithmeticEvaluationSuite.class,
	                 ArithmeticComparisonSuite.class,
	                 ClauseRetrivalAndInformationSuite.class,
	                 ClauseCreationAndDestructionSuite.class,
	                 AllSolutionsSuite.class,
	                 // Not implemented: StreamSelectionAndControlSuite.class
	                 // Not implemented: CharacterInputAndOutputSuite.class
	                 // Not implemented: ByteInputAndOutputSuite.class
	                 // Not implemented: TermInputAndOutputSuite.class
	                 LogicAndControlSuite.class,
	                 AtomicTermProcessingSuite.class,
	                 ImplementationDefinedHooksSuite.class,
	                 SimpleArithmeticFunctorsSuite.class,
	                 OtherArithmeticFunctorsSuite.class,
	                 BitwiseFunctorsSuite.class})
public class ISOAcceptanceSuite {}
