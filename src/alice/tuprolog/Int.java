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

import java.util.List;

/**
 * Int class represents the integer Prolog data type.
 */
public class Int extends Number {
	
	private int value;
	
	public Int(int v) {
		value = v;
	}
	
	/** Returns the value of the Integer as int. */
	@Override
	final public int intValue() {
		return value;
	}
	
	/** Returns the value of the Integer as float. */
	@Override
	final public float floatValue() {
		return (float) value;
	}
	
	/** Returns the value of the Integer as double. */
	@Override
	final public double doubleValue() {
		return (double) value;
	}
	
	/** Returns the value of the Integer as long. */
	@Override
	final public long longValue() {
		return value;
	}
	
	/** Is this term a Prolog integer term? */
	@Override
	final public boolean isInteger() {
		return true;
	}
	
	/** Is this term a Prolog real term? */
	@Override
	final public boolean isReal() {
		return false;
	}
		
	/**
	 * Returns true if this integer term is grater that the term provided.
	 * For number term argument, the int value is considered.
	 */
	@Override
	public boolean isGreater(Term t) {
		t = t.getTerm();
		if (t instanceof Number) {
			return value>((Number)t).intValue();
		} else if (t instanceof Struct) {
			return false;
		} else if (t instanceof Var) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true if this integer term is equal to the term provided.
	 */
	@Override
	public boolean isEqual(Term t) {
		t = t.getTerm();
		if (t instanceof Number) {
			Number n = (Number) t;
			if (!n.isInteger())
				return false;
			return (long) value == n.longValue();
		} else
			return false;
	}
	
	/**
	 * Tries to unify a term with the provided term argument.
	 * This service is to be used in demonstration context.
	 */
	@Override
	boolean unify(List<Var> vl1, List<Var> vl2, Term t) {
		t = t.getTerm();
		if (t instanceof Var) {
			return t.unify(vl2, vl1, this);
		} else
			if (t instanceof Number && ((Number) t).isInteger()) {
				return value == ((Number) t).intValue();
			} else {
				return false;
			}
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}
	
}