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
 * Float class represents the float Prolog data type.
 */
public class Float extends Number {
	
	private float value;
	
	public Float(float v) {
		value = v;
	}
	
	/** Returns the value of the Float as int. */
	@Override
	final public int intValue() {
		return (int) value;
	}
	
	/** Returns the value of the Float as float. */
	@Override
	final public float floatValue() {
		return value;
	}
	
	/** Returns the value of the Float as double. */
	@Override
	final public double doubleValue() {
		return value;
	}
	
	/** Returns the value of the Float as long. */
	@Override
	final public long longValue() {
		return (long) value;
	}
	
	/** Is this term a Prolog integer term? */
	@Override
	final public boolean isInteger() {
		return false;
	}
	
	/** Is this term a Prolog real term? */
	@Override
	final public boolean isReal() {
		return true;
	}
		
	/**
	 * Returns true if this Float term is grater that the term provided.
	 * For number term argument, the int value is considered.
	 */
	@Override
	public boolean isGreater(Term t) {
		t = t.getTerm();
		if (t instanceof Number) {
			return value>((Number)t).floatValue();
		} else if (t instanceof Struct) {
			return false;
		} else if (t instanceof Var) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns true if this Float term is equal that the term provided.
	 * For number term argument, the int value is considered.
	 */
	@Override
	public boolean isEqual(Term t) {
		t = t.getTerm();
		if (t instanceof Number) {
			return value == ( (Number) t ).floatValue();
		} else {
			return false;
		}
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
			if (t instanceof Number && ((Number) t).isReal()) {
				return value == ((Number) t).floatValue();
			} else {
				return false;
			}
	}
	
	@Override
	public String toString() {
		return java.lang.Float.toString(value);
	}
	
}