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
 * Number abstract class represents numbers Prolog data type.
 *
 * @see Int
 * @see Long
 * @see Float
 * @see Double
 */
public abstract class Number extends Term {
	
	/**
	 * Returns the value of the number as int.
	 */
	public abstract int intValue();
	
	/**
	 * Returns the value of the number as float.
	 */
	public abstract float floatValue();
	
	/**
	 * Returns the value of the number as long.
	 */
	public abstract long longValue();
	
	/**
	 * Returns the value of the number as double.
	 */
	public abstract double doubleValue();
	
	/** is this term a prolog integer term? */
	public abstract boolean isInteger();
	
	/** is this term a prolog real term? */
	public abstract boolean isReal();
	
	//
		
	public static Number createNumber(String s) {
		Term t = Term.createTerm(s);
		if (t instanceof Number)
			return (Number) t;
		throw new InvalidTermException("Term " + t + " is not a number.");
	}
	
	/** Gets the actual term referred by this Term. */
	@Override
	public Term getTerm() {
		return this;
	}
	
	// checking type and properties of the Term
	
	@Override
	final public boolean isEmptyList() {
		return false;
	}
	
	/** Is this term a constant Prolog term? */
	@Override
	final public boolean isAtomic() {
		return true;
	}
	
	/** Is this term a Prolog compound term? */
	@Override
	final public boolean isCompound() {
		return false;
	}
	
	/** Is this term a Prolog (alphanumeric) atom? */
	@Override
	final public boolean isAtom() {
		return false;
	}
	
	/** Is this term a Prolog list? */
	@Override
	final public boolean isList() {
		return false;
	}
	
	/** Is this term a ground term? */
	@Override
	final public boolean isGround() {
		return true;
	}
	
	//
	
	/** Gets a copy of this term. */
	public Term copy(int idExecCtx) {
		return this;
	}
	
	/**
	 * Gets a copy (with renamed variables) of the term.
	 * <p>
	 * The list argument passed contains the list of variables to be renamed
	 * (if empty list then no renaming)
	 */
	@Override
	Term copy(Map<Var, Var> vMap, int idExecCtx) {
		return this;
	}
	
	/** Gets a copy of the term. */
	@Override
	Term copy(Map<Var, Var> vMap, Map<Var, Var> substMap) {
		return this;
	}
	
	@Override
	long resolveTerm(long count) {
		return count;
	}
	
	@Override
	public void free() {}
	
}