/*
 * tuProlog - Copyright (C) 2001-2002  aliCE team at deis.unibo.it
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

import java.util.Map;

/**
 * Number abstract class represents numbers prolog data type
 *
 * @see Int
 * @see Long
 * @see Float
 * @see Double
 */
public abstract class Number extends Term {
	
	/**
	 *  Returns the value of the number as int
	 */
	public abstract int intValue();
	
	/**
	 *  Returns the value of the number as float
	 */
	public abstract float floatValue();
	
	/**
	 *  Returns the value of the number as long
	 */
	public abstract long longValue();
	
	/**
	 *  Returns the value of the number as double
	 */
	public abstract double doubleValue();
	
	
	/** is this term a prolog integer term? */
	public abstract boolean isInteger();
	
	/** is this term a prolog real term? */
	public abstract boolean isReal();
	
	//
	
	/** is an int Integer number? 
     * @deprecated Use <tt>instanceof Int</tt> instead. */
    public abstract boolean isTypeInt();

    /** is an int Integer number?
     * @deprecated Use <tt>instanceof Int</tt> instead. */
	public abstract boolean isInt();
	
	/** is a float Real number? 
     * @deprecated Use <tt>instanceof alice.tuprolog.Float</tt> instead. */
    public abstract boolean isTypeFloat();

    /** is a float Real number?
     * @deprecated Use <tt>instanceof alice.tuprolog.Float</tt> instead. */
	public abstract boolean isFloat();
	
	/** is a double Real number? 
     * @deprecated Use <tt>instanceof alice.tuprolog.Double</tt> instead.*/
    public abstract boolean isTypeDouble();

    /** is a double Real number?
     * @deprecated Use <tt>instanceof alice.tuprolog.Double</tt> instead. */
	public abstract boolean isDouble();
	
	/** is a long Integer number? 
     * @deprecated Use <tt>instanceof alice.tuprolog.Long</tt> instead. */
    public abstract boolean isTypeLong();

    /** is a long Integer number?
     * @deprecated Use <tt>instanceof alice.tuprolog.Long</tt> instead. */
	public abstract boolean isLong();
	
	public static Number createNumber(String s) {
		Term t = Term.createTerm(s);
		if (t instanceof Number)
			return (Number) t;
		throw new InvalidTermException("Term " + t + " is not a number.");
	}
	
	/**
	 * Gets the actual term referred by this Term.
	 */
	public Term getTerm() {
		return this;
	}
	
	// checking type and properties of the Term
	
	/** is this term a prolog numeric term? */
	final public boolean isNumber() {
		return true;
	}
	
	/** is this term a struct  */
	final public boolean isStruct() {
		return false;
	}
	
	/** is this term a variable  */
	final public boolean isVar() {
		return false;
	}
	
	final public boolean isEmptyList() {
		return false;
	}
	
	//
	
	/** is this term a constant prolog term? */
	final public boolean isAtomic() {
		return true;
	}
	
	/** is this term a prolog compound term? */
	final public boolean isCompound() {
		return false;
	}
	
	/** is this term a prolog (alphanumeric) atom? */
	final public boolean isAtom() {
		return false;
	}
	
	/** is this term a prolog list? */
	final public boolean isList() {
		return false;
	}
	
	/** is this term a ground term? */
	final public boolean isGround() {
		return true;
	}
	
	//
	
	/**
	 * gets a copy of this term.
	 */
	public Term copy(int idExecCtx) {
		return this;
	}
	
	/**
	 * gets a copy (with renamed variables) of the term.
	 * <p>
	 * the list argument passed contains the list of variables to be renamed
	 * (if empty list then no renaming)
	 */
	Term copy(Map<Var, Var> vMap, int idExecCtx) {
		return this;
	}
	
	/**
	 * gets a copy of the term.
	 */
	Term copy(Map<Var, Var> vMap, Map<Var, Var> substMap) {
		return this;
	}
	
	long resolveTerm(long count) {
		return count;
	}
	
	/**
	 *
	 */
	public void free() {}
	
	void restoreVariables() {}
	
}