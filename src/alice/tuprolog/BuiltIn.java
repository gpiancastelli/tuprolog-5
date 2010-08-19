/*
 * tuProlog - Copyright (C) 2001-2007 aliCE team at deis.unibo.it
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package alice.tuprolog;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.IdentityHashMap;

/**
 * Library of built-in predicates
 * @author Alex Benini
 */
public class BuiltIn extends Library {
	
	private EngineManager    engineManager;
	private TheoryManager    theoryManager;
	private LibraryManager   libraryManager;
	private FlagManager      flagManager;
	private PrimitiveManager primitiveManager;
	private OperatorManager  operatorManager;
	
	public BuiltIn(Prolog mediator) {
		super();
		setEngine(mediator);
		engineManager    = mediator.getEngineManager();
		theoryManager    = mediator.getTheoryManager();
		libraryManager   = mediator.getLibraryManager();
		flagManager      = mediator.getFlagManager();
		primitiveManager = mediator.getPrimitiveManager();
		operatorManager  = mediator.getOperatorManager();
	}
	
	@Predicate("fail/0")
	public boolean fail() {
		return false;
	}
	
	@Predicate("true/0")
	public boolean success() {
		return true;
	}
	
	@Predicate("halt/0")
	public boolean halt() throws HaltException {
		throw new HaltException();
	}
	
	@Predicate("halt/1")
	public boolean halt(Term arg0) throws HaltException {
		if (arg0 instanceof Number)
			throw new HaltException(((Number)arg0).intValue());
		return false;
	}
	
	@Predicate("!/0")
	public boolean cut() {
		engineManager.cut();
		return true;
	}
	
	@Predicate("asserta/1")
	public boolean assertA(Term arg0) {
		arg0 = arg0.getTerm();
		if (arg0 instanceof Struct) {
			theoryManager.assertA((Struct) arg0, true, null,false);
			return true;
		}
		return false;
	}
	
	@Predicate("assertz/1")
	public boolean assertZ(Term arg0) {
		arg0 = arg0.getTerm();
		if (arg0 instanceof Struct) {
			theoryManager.assertZ((Struct) arg0, true, null,false);
			return true;
		}
		return false;
	}
	
	@Predicate("$retract/1")
	public boolean retract(Term arg0) {
		arg0 = arg0.getTerm();
		if (!(arg0 instanceof Struct))
			return false;
		Struct sarg0 = (Struct) arg0;
		ClauseInfo c = theoryManager.retract(sarg0);
		// if clause to retract found -> retract + true
		if (c != null) {
			Struct clause = null;
			if (!sarg0.isClause())
				clause = new Struct(":-", arg0, new Struct("true"));
			else
				clause = sarg0;
			unify(clause,c.getClause());
			return true;
		}
		return false;
	}
	
	@Predicate("abolish/1")
	public boolean abolish(Term arg0) {
		arg0 = arg0.getTerm();
		if (!(arg0 instanceof Struct) || !arg0.isGround())
			return false;
		return theoryManager.abolish((Struct) arg0);
	}	
	
	/**
	 * Loads a library, given its Java class name.
	 */
	@Directive("load_library/1")
	@Predicate("load_library/1")
	public boolean loadLibrary(Term arg0) {
		arg0 = arg0.getTerm();
		if (!arg0.isAtom())
			return false;
		try {
			libraryManager.loadLibrary(((Struct) arg0).getName());
			return true;
		} catch (Exception e) {
			String m = "An exception has been thrown during the execution " +
			           "of the load_library/1 directive.\n" + e.getMessage();
			getEngine().warn(m);
			return false;
		}
	}
	
	/**
	 * Unloads a library, given its Java class name.
	 */
	@Predicate("unload_library/1")
	public boolean unloadLibrary(Term arg0) {
		arg0 = arg0.getTerm();
		if (!arg0.isAtom())
			return false;
		try {
			libraryManager.unloadLibrary(((Struct) arg0).getName());
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * Get flag list: flag_list(-List)
	 */
	@Predicate("flag_list/1")
	public boolean flagList(Term arg0) {
		arg0 = arg0.getTerm();
		Struct flist = flagManager.getPrologFlagList();
		return unify(arg0, flist);
	}
	
	
	@Predicate(",/2")
	public boolean comma(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		Struct s = new Struct(",", arg0, arg1);
		engineManager.pushSubGoal(ClauseInfo.extractBody(s));
		return true;
	}
	
	/**
	 * It is the same as call/1, but it is not opaque to cut.
	 */
	@Predicate("$call/1")
	public boolean call(Term goal) {
		goal = goal.getTerm();
		if ((goal instanceof Var) || !isCallable(goal))
			return false;
		goal = convertTermToGoal(goal);
		if (goal == null)
			return false;
		engineManager.identify(goal);
		engineManager.pushSubGoal(ClauseInfo.extractBody(goal));
		return true;
	}
	
	/**
	 * Convert a term to a goal before executing it by means of
	 * call/1. See section 7.6.2 of the ISO Standard for details.
	 * <ul>
	 * <li>If T is a variable then G is the control construct call,
	 * whose argument is T.</li>
	 * <li>If the principal functor of T is t �,�/2 or ;/2 or ->/2,
	 * then each argument of T shall also be converted to a goal.</li>
	 * <li>If T is an atom or compound term with principal functor FT,
	 * then G is a predication whose predicate indicator is FT, and
	 * the arguments, if any, of T and G are identical.</li>
	 * </ul>
	 * Note that a variable X and a term call(X) are converted to
	 * identical bodies. Also note that if T is a number, then there
	 * is no goal which corresponds to T.
	 */
	static Term convertTermToGoal(Term term) {
		if (term instanceof Number)
			return null;
		term = term.getTerm();
		if (term instanceof Var)
			return new Struct("call", term);
		if (term instanceof Struct) {
			Struct s = (Struct) term;
			String pi = s.getPredicateIndicator();
			if (pi.equals(";/2") || pi.equals(",/2") || pi.equals("->/2")) {
				for (int i = 0; i < s.getArity(); i++) {
					Term t = s.getArg(i);
					Term arg = convertTermToGoal(t);
					if (arg == null)
						return null;
					s.setArg(i, arg);
				}
			}
		}
		return term;
	}

	/**
	 * A callable term is an atom of a compound term. See
	 * the ISO Standard definition in section 3.24.
	 */
	private boolean isCallable(Term goal) {
		return (goal.isAtom() || goal.isCompound());
	}
	
	@Predicate("is/2")
	public boolean is(Term arg0, Term arg1) {
		Term val1 = evalExpression(arg1);
		if (val1 == null)
			return false;
		else
			return unify(arg0.getTerm(), val1);
	}
	
	@Predicate("=/2")
	public boolean doUnify(Term arg0, Term arg1) {
		return unify(arg0, arg1);
	}
	
	// \=
	@Predicate("\\=/2")
	public boolean doNotUnify(Term arg0, Term arg1) {
		return !unify(arg0, arg1);
	}	
	
	// $tolist
	@Predicate("$tolist/2")
	public boolean toList(Term arg0, Term arg1) {
		// transform arg0 to a list, unify it with arg1
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (arg0 instanceof Struct) {
			Term val0 = ((Struct) arg0).toList();
			return (val0 != null && unify(arg1, val0));
		}
		return false;
	}
	
	// $fromlist
	@Predicate("$fromlist/2")
	public boolean fromList(Term arg0, Term arg1) {
		// get the compound representation of the list
		// provided as arg1, and unify it with arg0
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (!arg1.isList())
			return false;
		Term val1 = ((Struct) arg1).fromList();
		return val1 != null && unify(arg0, val1);
	}	
	
	@Predicate("copy_term/2")
	public boolean copyTerm(Term arg0, Term arg1) {
		// unify arg1 with a renamed copy of arg0
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		int id = engineManager.env.nDemoSteps;
		return unify(arg1, arg0.copy(new IdentityHashMap<Var, Var>(), id));
	}	
	
	// $append
	@Predicate("$append/2")
	public boolean append(Term arg0, Term arg1) {
		// append arg0 to arg1
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (!arg1.isList())
			return false;
		((Struct) arg1).append(arg0);
		return true;
	}	
	
	// $find
	@Predicate("$find/2")
	public boolean find(Term arg0, Term arg1) {
		// look for clauses whose head unifies with arg0 and enqueue them to list arg1
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (!arg1.isList() || arg0 instanceof Var)
			return false;
		for (ClauseInfo b : theoryManager.find(arg0))
			if (match(arg0,b.getHead())) {
				b.getClause().resolveTerm();
				((Struct) arg1).append(b.getClause());
			}
		return true;
	}	
	
	// set_prolog_flag(+Name,@Value)
	@Predicate("set_prolog_flag/2")
	public boolean setPrologFlag(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if ((!arg0.isAtom() && !(arg0 instanceof Struct)) || !arg1.isGround())
			return false;
		String name = arg0.toString();
		return flagManager.setFlag(name,arg1);
	}	
	
	
	// get_prolog_flag(@Name,?Value)
	@Predicate("get_prolog_flag/2")
	public boolean getPrologFlag(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (!arg0.isAtom() && !(arg0 instanceof Struct))
			return false;
		String name = arg0.toString();
		Term value = flagManager.getFlag(name);
		return (value != null) ? unify(value,arg1) : false;
	}
	
	@Directive("op/3")
	@Predicate("op/3")
	public boolean operator(Term arg0, Term arg1, Term arg2) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		arg2 = arg2.getTerm();
		if (!(arg0 instanceof Number) || !arg1.isAtom() || (!arg2.isAtom() && !arg2.isList()))
			return false;
		String specifier = ((Struct) arg1).getName();
		if (arg2.isList()) {
			for (Term t : (Struct) arg2) {
				Struct operator = (Struct) t;
				createOperator(operator.getName(), specifier, ((Number) arg0).intValue());
			}
		} else
			createOperator(((Struct) arg2).getName(), specifier, ((Number) arg0).intValue());
		return true;
	}

	private void createOperator(String symbol, String specifier, int priority) {
		if (specifier.equals("fx")|specifier.equals("fy")|specifier.equals("xf")|specifier.equals("yf")|
				specifier.equals("xfx")|specifier.equals("yfx")|specifier.equals("xfy"))
			operatorManager.opNew(symbol, specifier, priority);
	}	
	
	@Directive("flag/4")
	public boolean flag(Term flagName, Term flagSet, Term flagDefault, Term flagModifiable) {
		flagName       = flagName.getTerm();
		flagSet        = flagSet.getTerm();
		flagDefault    = flagDefault.getTerm();
		flagModifiable = flagModifiable.getTerm();
		if (flagSet.isList() && (flagModifiable.equals(Term.TRUE) || flagModifiable.equals(Term.FALSE))) {
			String libName = ""; // TODO What's the future of libName?
			flagManager.defineFlag(flagName.toString(), (Struct)flagSet, flagDefault, flagModifiable.equals(Term.TRUE), libName);
			return true;
		} else
			return false;
	}
	
	@Directive("initialization/1")
	public boolean initialization(Term goal) {
		goal = goal.getTerm();
		if (goal instanceof Struct) {
			primitiveManager.identifyPredicate(goal);
			theoryManager.addStartGoal((Struct) goal);
			return true;
		} else
			return false;
	}
	
	@Directive("include/1")
	public boolean include(Term theory) throws InvalidTheoryException, IOException {
		theory = theory.getTerm();
		try {
			engine.addTheory(new Theory(new FileInputStream(theory.toStringWithoutApices())));
			return true;
		} catch (Exception e) {
			String m = "An exception has been thrown during the execution " +
			           "of the include/1 directive.\n" + e.getMessage();
			getEngine().warn(m);
			return false;
		}
	}
	
}