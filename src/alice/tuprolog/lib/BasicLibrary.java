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
package alice.tuprolog.lib;

import alice.tuprolog.Agent;
import alice.tuprolog.Functor;
import alice.tuprolog.Int;
import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.Library;
import alice.tuprolog.Number;
import alice.tuprolog.Operator;
import alice.tuprolog.Predicate;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;

/**
 * This class defines a set of basic built-in
 * predicates for the tuProlog engine
 *
 * Library/Theory dependency: none
 */
public class BasicLibrary extends Library {
	
	public BasicLibrary() {
	}
	
	//
	// meta-predicates
	//
	
	/**
	 * sets a new theory provided as a text
	 */
	@Predicate("set_theory/1")
	public boolean setTheory(Term th) {
		Struct theory = (Struct) th.getTerm();
		try {
			if (!theory.isAtom())
				return false;
			getEngine().setTheory(new Theory(theory.getName()));
			return true;
		} catch (InvalidTheoryException ex) {
			System.err.println("Invalid theory at line " + ex.line);
			return false;
		}
	}
	
	/**
	 *  adds a new theory provided as a text
	 */
	@Predicate("add_theory/1")
	public boolean addTheory(Term th) {
		Struct theory = (Struct) th.getTerm();
		try {
			if (!theory.isAtom())
				return false;
			getEngine().addTheory(new Theory(theory.getName()));
			return true;
		} catch (InvalidTheoryException ex) {
			System.err.println("Invalid theory at line "+ex.line);
			return false;
		}
	}
	
	/** gets current theory text */
	@Predicate("get_theory/1")
	public boolean getTheory(Term arg) {
		arg = arg.getTerm();
		try {
			Term theory = new Struct(getEngine().getTheory().toString());
			return (unify(arg,theory));
		} catch (Exception ex) {
			return false;
		}
	}
	
	@Predicate("load_library/2")
	public boolean loadLibrary(Term className, Term libName) {
		Struct clName = (Struct) className.getTerm();
		libName = libName.getTerm();
		try {
			Library lib = getEngine().loadLibrary(clName.toStringWithoutApices());
			return unify(libName,new Struct(lib.getName()));
		} catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * Loads a library constructed from a theory.
	 * 
	 * @param theory theory text
	 * @param libName name of the library
	 * @return true if the library has been successfully loaded.
	 */
	@Predicate("load_library_from_theory/2")
	public boolean loadLibraryFromTheory(Term th, Term libName) {
		Struct theory = (Struct) th.getTerm();
		Struct libN = (Struct) libName.getTerm();
		try {
			if (!theory.isAtom())
				return false;
			if (!libN.isAtom())
				return false;
			Theory t = new Theory(theory.getName());
			TheoryLibrary thlib = new TheoryLibrary(libN.getName(),t);
			getEngine().loadLibrary(thlib);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	@Predicate("get_operators_list/1")
	public boolean getOperatorsList(Term argument) {
		Term arg = argument.getTerm();
		Struct list = new Struct();
		for (Operator o : getEngine().getCurrentOperatorList())
			list = new Struct(
					 new Struct("op", new alice.tuprolog.Int(o.prio),
					                  new Struct(o.type),
					                  new Struct(o.name)),
					          list);
		return unify(arg, list);
	}
	
	/**
	 * spawns a separate prolog agent
	 * providing it a theory text
	 */
	@Predicate("agent/1")
	public boolean agent(Term th) {
		Struct theory = (Struct) th.getTerm();
		try {
			new Agent(theory.toStringWithoutApices()).spawn();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * spawns a separate prolog agent
	 * providing it a theory text and a goal
	 */
	@Predicate("agent/2")
	public boolean agentWithGoal(Term th, Term g) {
		Struct theory = (Struct) th.getTerm();
		Struct goal = (Struct) g.getTerm();
		try {
			new Agent(theory.toStringWithoutApices(),
                      goal.toString() + ".").spawn();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	@Predicate("spy/0")
	public boolean spy() {
		getEngine().setSpy(true);
		return true;
	}
	
	@Predicate("nospy/0")
	public boolean noSpy() {
		getEngine().setSpy(false);
		return true;
	}
	
	@Predicate("warning/0")
	public boolean warning() {
		getEngine().setWarning(true);
		return true;
	}
	
	@Predicate("nowarning/0")
	public boolean noWarning() {
		getEngine().setWarning(false);
		return true;
	}
	
	//
	// term type inspection
	//
	
	@Predicate("constant/1")
	public boolean isConstant(Term t) {
		t = t.getTerm();
		return (t.isAtomic());
	}
	
	@Predicate("number/1")
	public boolean isNumber(Term t) {
		return (t.getTerm() instanceof Number);
	}
	
	@Predicate("integer/1")
	public boolean isInteger(Term t) {
		t = t.getTerm();
		if (!(t instanceof Number))
			return false;
		return ((Number) t).isInteger();
	}
	
	@Predicate("float/1")
	public boolean isFloat(Term t) {
		t = t.getTerm();
		if (!(t instanceof Number))
			return false;
		return ((Number) t).isReal();
	}
	
	@Predicate("atom/1")
	public boolean isAtom(Term t) {
		t = t.getTerm();
		return (t.isAtom());
	}
	
	@Predicate("compound/1")
	public boolean isCompound(Term t) {
		t = t.getTerm();
		return t.isCompound();
	}
	
	@Predicate("list/1")
	public boolean isList(Term t) {
		t = t.getTerm();
		return (t.isList());
	}
	
	@Predicate("var/1")
	public boolean isVar(Term t) {
		t = t.getTerm();
		return (t instanceof Var);
	}
	
	@Predicate("nonvar/1")
	public boolean isNotVar(Term t) {
		t = t.getTerm();
		return !(t instanceof Var);
	}
	
	@Predicate("atomic/1")
	public boolean isAtomic(Term t) {
		t = t.getTerm();
		return t.isAtomic();
	}
	
	@Predicate("ground/1")
	public boolean isGround(Term t) {
		t = t.getTerm();
		return (t.isGround());
	}
	
	//
	// term/expression comparison
	//
	
	@Predicate("expression_equality/2")
	public boolean expressionEquality(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0==null || val1==null || !(val0 instanceof Number) || !(val1 instanceof Number)) {
			return false;
		}
		alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
		alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
		if (val0n.isInteger() && val1n.isInteger()) {
			return (val0n.intValue() == val1n.intValue()) ? true : false;
		} else {
			return (val0n.doubleValue() == val1n.doubleValue()) ? true : false;
		}
	}
	
	@Predicate("expression_greater_than/2")
	public boolean expressionGreaterThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return expressionGreaterThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}
	
	@Predicate("expression_less_or_equal_than/2")
	public boolean expressionLessOrEqualThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return !expressionGreaterThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}

	private boolean expressionGreaterThan(alice.tuprolog.Number num0, alice.tuprolog.Number num1) {
		if (num0.isInteger() && num1.isInteger()) {
			return num0.intValue() > num1.intValue();
		} else {
			return num0.doubleValue() > num1.doubleValue();
		}
	}
	
	@Predicate("expression_less_than/2")
	public boolean expressionLessThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return expressionLessThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}
	
	@Predicate("expression_greater_or_equal_than/2")
	public boolean expressionGreaterOrEqualThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return !expressionLessThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}

	private boolean expressionLessThan(alice.tuprolog.Number num0, alice.tuprolog.Number num1) {
		if (num0.isInteger() && num1.isInteger()) {
			return num0.intValue() < num1.intValue();
		} else {
			return num0.doubleValue() < num1.doubleValue();
		}
	}
	
	@Predicate("term_equality/2")
	public boolean termEquality(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		return arg0.isEqual(arg1);
	}
	
	@Predicate("term_greater_than/2")
	public boolean termGreaterThan(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		return arg0.isGreater(arg1);
	}
	
	@Predicate("term_less_than/2")
	public boolean termLessThan(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		return !(arg0.isGreater(arg1) || arg0.isEqual(arg1));
	}
	
	@Functor("+/1")
	public Term expressionPlus(Term arg0) {
		Term val0 = evalExpression(arg0);
		if (val0!=null && val0 instanceof Number)
			return val0;
		else
			return null;
	}
	
	@Functor("-/1")
	public Term expressionMinus(Term arg1) {
		Term val0 = evalExpression(arg1);
		if (val0!=null && val0 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			if (val0n instanceof Int)
				return new Int(val0n.intValue() * -1);
			else if (val0n instanceof alice.tuprolog.Double)
				return new alice.tuprolog.Double(val0n.doubleValue() * -1);
			else if (val0n instanceof alice.tuprolog.Long)
				return new alice.tuprolog.Long(val0n.longValue() * -1);
			else if (val0n instanceof alice.tuprolog.Float)
				return new alice.tuprolog.Float(val0n.floatValue() * -1);
			else
				return null;
		} else
			return null;
	}
	
	@Functor("\\/1")
	public Term expressionBitwiseNot(Term arg0) {
		Term val0 = evalExpression(arg0);
		if (val0!=null && val0 instanceof Number)
			return new Int(~((alice.tuprolog.Number) val0).intValue());
		else
			return null;
	}
	
	alice.tuprolog.Number getIntegerNumber(long num) {
		if (num > Integer.MIN_VALUE && num < Integer.MAX_VALUE)
			return new Int((int) num);
		else
			return new alice.tuprolog.Long(num);
	}
	
	@Functor("+/2")
	public Term expressionPlus(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0!=null && val1!=null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			if (val0n.isInteger() && (val1n.isInteger()))
				return getIntegerNumber(val0n.longValue() + val1n.longValue());
			else
				return new alice.tuprolog.Double(val0n.doubleValue() + val1n.doubleValue());
		} else
			return null;
	}
	
	@Functor("-/2")
	public Term expressionMinus(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0!=null && val1!=null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			if (val0n.isInteger() && (val1n.isInteger()))
				return getIntegerNumber(val0n.longValue() - val1n.longValue());
			else
				return new alice.tuprolog.Double(val0n.doubleValue() - val1n.doubleValue());
		} else
			return null;
	}
	
	@Functor("*/2")
	public Term expressionMultiply(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0!=null && val1!=null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			if (val0n.isInteger() && (val1n.isInteger()))
				return getIntegerNumber(val0n.longValue() * val1n.longValue());
			else
				return new alice.tuprolog.Double(val0n.doubleValue() * val1n.doubleValue());
		} else
			return null;
	}
	
	@Functor("//2")
	public Term expressionDiv(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			Number val0n = (Number) val0;
			Number val1n = (Number) val1;
			alice.tuprolog.Double result = new alice.tuprolog.Double(val0n.doubleValue()/val1n.doubleValue());
			if (val0n.isInteger() && val1n.isInteger())
				return getIntegerNumber(result.longValue());
			else
				return result;
		} else
			return null;
	}
	
	@Functor("///2")
	public Term expressionIntegerDiv(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return getIntegerNumber(val0n.longValue() / val1n.longValue());
		} else
			return null;
	}
	
	@Functor("**/2")
	public Term expressionPow(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return new alice.tuprolog.Double(Math.pow(val0n.doubleValue(), val1n.doubleValue()));
		} else
			return null;
	}
	
	@Functor(">>/2")
	public Term expressionBitwiseShiftRight(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return new Int(val0n.intValue() >> val1n.intValue());
		} else
			return null;
	}
	
	@Functor("<</2")
	public Term expressionBitwiseShiftLeft(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return new Int(val0n.intValue() << val1n.intValue());
		} else
			return null;
	}
	
	@Functor("/\\/2")
	public Term expressionBitwiseAnd(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return new Int(val0n.intValue() & val1n.intValue());
		} else
			return null;
	}
	
	@Functor("\\//2")
	public Term expressionBitwiseOr(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return new Int(val0n.intValue() | val1n.intValue());
		} else
			return null;
	}
	
	//
	// text/atom manipulation predicates
	//
	
	/**
	 * bidirectional text/term conversion.
	 */
	@Predicate("text_term/2")
	public boolean textTerm(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (!arg0.isGround()) {
			return unify(arg0,new Struct(arg1.toString()));
		} else {
			try {
				String text = arg0.toStringWithoutApices();
				return unify(arg1,getEngine().toTerm(text));
			} catch (Exception ex) {
				return false;
			}
		}
	}
	
	@Predicate("text_concat/3")
	public boolean textConcat(Term source1, Term source2, Term dest) {
		source1 = source1.getTerm();
		source2 = source2.getTerm();
		dest = dest.getTerm();
		if (source1.isAtom() && source2.isAtom())
			return unify(dest,
					new Struct( ((Struct)source1).getName()+ ((Struct)source2).getName()));
		else
			return false;
	}
	
	@Predicate("num_atom/2")
	public boolean numAtom(Term arg0,Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (arg1 instanceof Var) {
			if (!(arg0 instanceof Number))
				return false;
			alice.tuprolog.Number n0 = (alice.tuprolog.Number) arg0;
			String st = null;
			if (n0.isInteger())
				st = new java.lang.Integer(n0.intValue()).toString();
			else
				st = new java.lang.Double(n0.doubleValue()).toString();
			return unify(arg1,new Struct(st));
		} else {
			if (!arg1.isAtom())
				return false;
			String st = ((Struct) arg1).getName();
			try {
				if (st.startsWith("'") && st.endsWith("'"))
					st = st.substring(1, st.length() - 1);
			} catch (Exception ex) {}
			Term term = null;
			try {
				term = new alice.tuprolog.Int(java.lang.Integer.parseInt(st));
			} catch (Exception ex) {}
			if (term == null)
				try {
					term = new alice.tuprolog.Double(java.lang.Double.parseDouble(((Struct)arg1).getName()));
				} catch (Exception ex){}
			if (term == null)
				return false;
			return unify(arg0,term);
		}
	}
	
	@Override
	public String getTheory() {
		return
		//
		// operators defined by the BasicLibrary theory
		//
		"':-'(op( 1200, fx,   ':-')). \n"+
		":- op( 1200, xfx,  ':-'). \n"+
		":- op( 1200, fx,   '?-'). \n"+
		":- op( 1100, xfy,  ';'). \n"+
		":- op( 1050, xfy,  '->'). \n"+
		":- op( 1000, xfy,  ','). \n"+
		":- op(  900, fy,   '\\+'). \n"+
		":- op(  900, fy,   'not'). \n"+
		//
		":- op(  700, xfx,  '='). \n"+
		":- op(  700, xfx,  '\\='). \n"+
		":- op(  700, xfx,  '=='). \n"+
		":- op(  700, xfx,  '\\=='). \n"+
		//
		":- op(  700, xfx,  '@>'). \n"+
		":- op(  700, xfx,  '@<'). \n"+
		":- op(  700, xfx,  '@=<'). \n"+
		":- op(  700, xfx,  '@>='). \n"+
		":- op(  700, xfx,  '=:='). \n"+
		":- op(  700, xfx,  '=\\='). \n"+
		":- op(  700, xfx,  '>'). \n"+
		":- op(  700, xfx,  '<'). \n"+
		":- op(  700, xfx,  '=<'). \n"+
		":- op(  700, xfx,  '>='). \n"+
		//
		":- op(  700, xfx,  'is'). \n"+
		":- op(  700, xfx,  '=..'). \n"+
		":- op(  500, yfx,  '+'). \n"+
		":- op(  500, yfx,  '-'). \n"+
		":- op(  500, yfx,  '/\\'). \n"+
		":- op(  500, yfx,  '\\/'). \n"+
		":- op(  400, yfx,  '*'). \n"+
		":- op(  400, yfx,  '/'). \n"+
		":- op(  400, yfx,  '//'). \n"+
		":- op(  400, yfx,  '>>'). \n"+
		":- op(  400, yfx,  '<<'). \n"+
		":- op(  400, yfx,  'rem'). \n" +
		":- op(  400, yfx,  'mod'). \n" +
		":- op(  200, xfx,  '**'). \n"+
		":- op(  200, xfy,  '^'). \n"+
		":- op(  200, fy,   '\\'). \n"+
		":- op(  200, fy,   '-'). \n"+
		//
		// flag management
		//
		"current_prolog_flag(Name,Value) :- get_prolog_flag(Name,Value),!.\n"+
		"current_prolog_flag(Name,Value) :- flag_list(L), member(flag(Name,Value),L).\n"+
		//
		// expression/term comparison
		//
		"'=:='(X,Y):- expression_equality(X,Y). \n"+
		"'=\\='(X,Y):- not expression_equality(X,Y). \n"+
		"'>'(X,Y):- expression_greater_than(X,Y). \n"+
		"'<'(X,Y):- expression_less_than(X,Y). \n"+
		"'>='(X,Y):- expression_greater_or_equal_than(X,Y). \n"+
		"'=<'(X,Y):- expression_less_or_equal_than(X,Y). \n"+
		"'=='(X,Y):- term_equality(X,Y).\n"+
		"'\\=='(X,Y):- not term_equality(X,Y).\n"+
		"'@>'(X,Y):- term_greater_than(X,Y).\n"+
		"'@<'(X,Y):- term_less_than(X,Y).\n"+
		"'@>='(X,Y):- not term_less_than(X,Y).\n"+
		"'@=<'(X,Y):- not term_greater_than(X,Y).\n"+
		//
		// meta-predicates
		//
		"'=..'(T, [T]) :- atomic(T), !. \n" +
		"'=..'(T,L)    :- compound(T),!,'$tolist'(T,L). \n                                                          " +
		"'=..'(T,L)    :- nonvar(L),'$fromlist'(T,L). \n                                                          " +
	    "functor(Term, Name, Arity) :- atomic(Term), !, Name = Term, Arity = 0. \n" +
		"functor(Term, Name, Arity) :- compound(Term), !, Term =.. [Name | Args], length(Args, Arity). \n" +
		"functor(Term, Name, Arity) :- var(Term), atomic(Name), Arity == 0, !, Term = Name. \n" +
		"functor(Term, Name, Arity) :- var(Term), atom(Name), I is Arity, integer(I), I > 0, newlist([], I, L), Term =.. [Name | L]. \n" +
		"arg(N,C,T):- nonvar(N), C =.. [_|Args], element(N,Args,T).\n"+
		"clause(H, B) :- L = [], '$find'(H, L), copy_term(L, LC), member((':-'(H, B)), LC). \n" +
		//
		// call/1 is coded both in Prolog, to feature the desired opacity
		// to cut, and in Java as a primitive built-in, to account for
		// goal transformations that should be performed before execution
		// as mandated by ISO Standard, see section 7.8.3.1
		"call(G) :- '$call'(G). \n" +
		"'\\+'(P):- P,!,fail.\n                                                                            "+
		"'\\+'(_).\n                                                                                             "+
		"C -> T ; B :- !, or((call(C), !, call(T)), '$call'(B)). \n" +
		"C -> T :- call(C), !, call(T). \n" +
		"or(A, B) :- '$call'(A). \n" +
		"or(A, B) :- '$call'(B). \n" +
		"A ; B :- A =.. ['->', C, T], !, ('$call'(C), !, '$call'(T) ; '$call'(B)). \n" +
		"A ; B :- '$call'(A). \n" +
		"A ; B :- '$call'(B). \n "+
		"unify_with_occurs_check(X,Y):-X=Y.\n                                                                     "+
		"current_op(Pri,Type,Name):-get_operators_list(L),member(op(Pri,Type,Name),L).\n                          "+
		"once(X) :- myonce(X).\n                                                                                  "+
		"myonce(X):-X,!.\n                                                                                        "+
		"repeat. \n                                                                                              " +
		"repeat        :- repeat. \n                                                                             " +
		"not(G)        :- G,!,fail. \n                                                                     " +
		"not(_). \n                                                                                              " +
		//
		// All solutions predicates
		//
		"findall(Template, Goal, Instances) :- \n" +
		"L = [], \n" +
		"'$findall0'(Template, Goal, L), \n" +
		"Instances = L. \n" +
		"'$findall0'(Template, Goal, L) :- \n" +
		"call(Goal), \n" +
		"copy_term(Template, CL), \n" +
		"'$append'(CL, L), \n" +
		"fail. \n" +
		"'$findall0'(_, _, _). \n" +
		"variable_set(T, []) :- atomic(T), !. \n" +
		"variable_set(T, [T]) :- var(T), !. \n" +
		"variable_set([H | T], [SH | ST]) :- \n" + 
		"variable_set(H, SH), variable_set(T, ST). \n" +
		"variable_set(T, S) :- \n" +
		"T =.. [_ | Args], variable_set(Args, L), flatten(L, FL), no_duplicates(FL, S), !. \n" +
		"flatten(L, FL) :- '$flatten0'(L, FL), !. \n" +
		"'$flatten0'(T, []) :- nonvar(T), T = []. \n" +
		"'$flatten0'(T, [T]) :- var(T). \n" +
		"'$flatten0'([H | T], [H | FT]) :- \n" +
		"not(islist(H)), !, '$flatten0'(T, FT). \n" +
		"'$flatten0'([H | T], FL) :- \n" +
		"'$flatten0'(H, FH), '$flatten0'(T, FT), append(FH, FT, FL). \n" +
		"islist([]). \n" +
		"islist([_|L]):- islist(L). \n " +
		"existential_variables_set(Term, Set) :- '$existential_variables_set0'(Term, Set), !. \n" +
		"'$existential_variables_set0'(Term, []) :- var(Term), !. \n" +
		"'$existential_variables_set0'(Term, []) :- atomic(Term), !. \n" +
		"'$existential_variables_set0'(V ^ G, Set) :- \n" +
		"variable_set(V, VS), '$existential_variables_set0'(G, EVS), append(VS, EVS, Set). \n" +
		"'$existential_variables_set0'(Term, []) :- nonvar(Term), !. \n" +
		"free_variables_set(Term, WithRespectTo, Set) :- \n" +
		"variable_set(Term, VS), \n" +
		"variable_set(WithRespectTo, VS1), existential_variables_set(Term, EVS1), append(VS1, EVS1, BV), \n" +
		"list_difference(VS, BV, List), no_duplicates(List, Set), !. \n" +
		"list_difference(List, Subtrahend, Difference) :- '$ld'(List, Subtrahend, Difference). \n" +
		"'$ld'([], _, []). \n" +
		"'$ld'([H | T], S, D) :- is_member(H, S), !, '$ld'(T, S, D). \n" +
		"'$ld'([H | T], S, [H | TD]) :- '$ld'(T, S, TD). \n" +
		"no_duplicates([], []). \n" +
		"no_duplicates([H | T], L) :- is_member(H, T), !, no_duplicates(T, L). \n" +
		"no_duplicates([H | T], [H | L]) :- no_duplicates(T, L). \n" +
		"is_member(E, [H | _]) :- E == H, !. \n" +
		"is_member(E, [_ | T]) :- is_member(E, T). \n" +
		"'$wt_list'([], []). \n" +
		"'$wt_list'([W + T | STail], [WW + T | WTTail]) :- copy_term(W, WW), '$wt_list'(STail, WTTail). \n" +
		"'$s_next'(Witness, WT_List, S_Next) :- copy_term(Witness, W2), '$s_next0'(W2, WT_List, S_Next), !. \n" +
		"bagof(Template, Goal, Instances) :- \n" +
		"free_variables_set(Goal, Template, Set), \n" +
		"Witness =.. [witness | Set], \n" +
		"iterated_goal_term(Goal, G), \n" +
		"findall(Witness + Template, G, S), \n" +
		"'$bagof0'(Witness, S, Instances). \n" +
		"'$bagof0'(_, [], _) :- !, fail. \n" +
		"'$bagof0'(Witness, S, Instances) :- \n" +
		"'$wt_list'(S, WT_List), \n" +
		"'$wt_unify'(Witness, WT_List, T_List), \n" +
		"Instances = T_List. \n" +
		"'$bagof0'(Witness, S, Instances) :- \n" +
		"'$wt_list'(S, WT_List), \n" +
		"'$s_next'(Witness, WT_List, S_Next), \n" +
		"'$bagof0'(Witness, S_Next, Instances). \n" +
		"setof(Template, Goal, Instances) :- \n" +
		"bagof(Template, Goal, List), \n" +
		"quicksort(List, '@<', OrderedList), \n" +
		"no_duplicates(OrderedList, Instances). \n" +
		//
		// theory management predicates
		//
		"assert(C) :- assertz(C). \n" +
		"retract(Rule) :- Rule = ':-'(Head, Body), !, clause(Head, Body), '$retract'(Rule). \n" +
		"retract(Fact) :- clause(Fact, true), '$retract'(Fact). \n" +
		"retractall(Head) :- findall(':-'(Head, Body), clause(Head, Body), L), '$retract_clause_list'(L), !. \n" +
		"'$retract_clause_list'([]). \n" +
		"'$retract_clause_list'([E | T]) :- !, '$retract'(E), '$retract_clause_list'(T). \n" +
		//
		// auxiliary predicates
		//
		"member(E,[E|_]). \n                                                                                     " +
		"member(E,[_|L]):- member(E,L). \n                                                                       " +
		"length(L, S) :- number(S), !, lengthN(L, S), !. \n" +
		"length(L, S) :- var(S), lengthX(L, S). \n" +
		"lengthN([],0). \n" +
		"lengthN(_, N) :- N < 0, !, fail. \n" +
		"lengthN([_|L], N) :- lengthN(L,M), N is M + 1. \n" +
		"lengthX([],0). \n" +
		"lengthX([_|L], N) :- lengthX(L,M), N is M + 1. \n" +
		"append([],L2,L2). \n                                                                                    " +
		"append([E|T1],L2,[E|T2]):- append(T1,L2,T2). \n                                                         " +
		"reverse(L1,L2):- reverse0(L1,[],L2). \n                                                                 " +
		"reverse0([],Acc,Acc). \n                                                                                " +
		"reverse0([H|T],Acc,Y):- reverse0(T,[H|Acc],Y). \n                                                       " +
		"delete(E,[],[]). \n                                                                                     " +
		"delete(E,[E|T],L):- !,delete(E,T,L). \n                                                                 " +
		"delete(E,[H|T],[H|L]):- delete(E,T,L). \n                                                               " +
		"element(1,[E|L],E):- !. \n                                                                              " +
		"element(N,[_|L],E):- M is N - 1,element(M,L,E). \n                                                      " +
		"newlist(Ls,0,Ls):- !. \n                                                                                " +
		"newlist(Ls,N,Ld):- M is N - 1,newlist([_|Ls],M,Ld). \n                                                  " +
		"quicksort([],Pred,[]).                             \n" +
		"quicksort([X|Tail],Pred,Sorted):-                  \n"+
		"   split(X,Tail,Pred,Small,Big),                   \n"+
		"   quicksort(Small,Pred,SortedSmall),              \n"+
		"   quicksort(Big,Pred,SortedBig),                  \n"+
		"   append(SortedSmall,[X|SortedBig],Sorted).       \n"+
		"split(_,[],_,[],[]).                               \n"+
		"split(X,[Y|Tail],Pred,Small,[Y|Big]):-             \n"+
		"   Predicate =..[Pred,X,Y],                        \n"+
		"   call(Predicate),!,                              \n"+
		"   split(X,Tail,Pred,Small,Big).                   \n"+
		"split(X,[Y|Tail],Pred,[Y|Small],Big):-             \n"+
		"   split(X,Tail,Pred,Small,Big).                   \n";
	}
	
	// Internal Java predicates which are part of the bagof/3 and setof/3 algorithm
	
	@Predicate("$wt_unify/3")
	public boolean wtUnify(Term witness, Term wtList, Term tList) {
		Struct list = (Struct) wtList.getTerm();
		Struct result = new Struct();
		for (Term wt : list) {
			Struct element = (Struct) wt;
			Term w = element.getArg(0); 
			Term t = element.getArg(1);
			if (unify(witness, w))
				result.append(t);
		}
		return unify(tList, result);
	}
	
	@Predicate("$s_next0/3")
	public boolean sNext(Term witness, Term wtList, Term sNext) {
		Struct list = (Struct) wtList.getTerm();
		Struct result = new Struct();
		for (Term wt : list) {
			Struct element = (Struct) wt;
			Term w = element.getArg(0);
			if (!unify(witness, w))
				result.append(element);
		}
		return unify(sNext, result);
	}
	
	@Predicate("iterated_goal_term/2")
	public boolean iteratedGoalTerm(Term term, Term goal) {
		Term t = term.getTerm();
		Term igt = t.iteratedGoalTerm();
		return unify(igt, goal);
	}
	
}