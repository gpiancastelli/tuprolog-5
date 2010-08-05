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
import java.util.Map;

/**
 * This class represents a variable term.
 * Variables are identified by a name (which must starts with
 * an upper case letter) or the anonymous ('_') name.
 *
 * @see Term
 */
public class Var extends Term {
	
	final static String ANY = "_";
	
	// the name identifying the var
	private String name;
	private String completeName;
	
	/** link is used for unification process */
	private Term  link;
	/** timestamp is used for fix variables order */
	private long timestamp;
	/** id of ExecCtx owners of this variable, useful for renaming */
	private int id;	
	
	/**
	 * Creates a variable identified by a name.
	 *
	 * The name must starts with an upper case letter or the underscore. If an underscore is
	 * specified as a name, the variable is anonymous.
	 *
	 * @param n is the name
	 * @throws InvalidTermException if n is not a valid Prolog variable name
	 */
	public Var(String n) {
		link = null;
		id = -1; //no execCtx owners
		if (n.equals(ANY)) {
			name = null;
			completeName = null;
		} else if (Character.isUpperCase(n.charAt(0)) || (n.startsWith(ANY))) {
			name = n;
			completeName = n;
		} else {
			throw new InvalidTermException("Illegal variable name: " + n);
		}
	}
	
	/**
	 * Creates an anonymous variable.
	 *
	 * This is equivalent to build a variable with name "_".
	 */
	public Var() {
		name = null;
		completeName = null;
		link = null;
		id = ORIGINAL;
		timestamp = 0;
	}
	
	/**
	 * Creates a internal engine variable.
	 *
	 * @param n is the name
	 * @param id is the id of ExecCtx
	 * @param alias code to discriminate external vars
	 * @param time is timestamp
	 */
	private Var(String n, int id, int alias, long time) {
		name = n;
		timestamp = time;
		link  = null;
		if(id < 0) id = ORIGINAL;
		rename(id,alias);
	}
	
	/* Identify kind of renaming */
	final static int ORIGINAL = -1;
	final static int PROGRESSIVE = -2;
	
	/**
	 * Rename variable (assign completeName). 
	 */
	void rename(int idExecCtx, int count) {
		id = idExecCtx;
		if (idExecCtx > -1) {
			completeName = name + "_e" + idExecCtx;
		}
		if (id == ORIGINAL) completeName = name;
		if (id == PROGRESSIVE) completeName = "_"+count;
	}
	
	/**
	 * Gets a copy of this variable.
	 *
	 * If the variable is not present in the list passed as argument,
	 * a copy of this variable is returned and added to the list. If instead
	 * a variable with the same time identifier is found in the list,
	 * then the variable in the list is returned.
	 */
	@Override
	Term copy(Map<Var, Var> vMap, int idExecCtx) {
		Term tt = getTerm();
		if (tt == this) {
			Var v = (Var) vMap.get(this);
			if (v == null) {
				// No occurrence of v before
				v = new Var(name, idExecCtx, 0, timestamp);
				vMap.put(this, v);
			}
			return v;
		} else
			return tt.copy(vMap, idExecCtx);
	}
	
	/** Gets a copy of this variable. */
	@Override
	Term copy(Map<Var, Var> vMap, Map<Var, Var> substMap) {
		Var v;
		Object temp = vMap.get(this);
		if (temp == null) {
			v = new Var(null, Var.PROGRESSIVE, vMap.size(), timestamp);//name,Var.PROGRESSIVE,vMap.size(),timestamp);
			vMap.put(this,v);
		} else
			v = (Var) temp;
		Term t = getTerm();
		if (t instanceof Var) {
			Object tt = substMap.get(t);
			if (tt == null) {
				substMap.put((Var) t, v);
				v.link = null;
			} else
				v.link = (tt != v) ? (Var)tt : null;
		}
		if (t instanceof Struct)
			v.link = t.copy(vMap, substMap);
		if (t instanceof Number)
			v.link = t;
		return v;
	}
	
	/** De-unify the variable. */
	@Override
	public void free() {
		link = null;
	}
	
	/** De-unify the variables in the list. */
	public static void free(List<Var> varsUnified) {
		for (Var v : varsUnified)
			v.free();
	}
	
	/** Gets the name of the variable. */
	public String getName() {
		if (name != null) {
			return completeName;
		} else {
			//return ANY+timestamp;
			return ANY;
		}
	}
	
	/** Gets the name of the variable. */
	public String getOriginalName() {
		if (name != null) {
			return name;
		} else {
			//return ANY+timestamp;
			return ANY + hashCode();
		}
	}
	
	/**
	 * Gets the term which is referred by the variable.
	 *
	 * For unbound variable it is the variable itself, while
	 * for bound variable it is the bound term.
	 */
	@Override
	public Term getTerm() {
		Term tt = this;
		Term t  = link;
		while (t != null ) {
			tt = t;
			if (t instanceof Var)
				t  = ((Var) t).link;
			else
				break;
		}
		return tt;
	}
	
	/**
	 *  Gets the term which is direct referred by the variable.
	 */
	Term getLink() {
		return link;
	}
	
	/** Set the term which is direct bound. */
	void setLink(Term l) {
		link = l;
	}
	
	/** Set the timestamp. */
	void setTimestamp(long t) {
		timestamp = t;
	}
	
	//
	
	@Override
	public boolean isEmptyList() {
		Term t=getTerm();
		if (t==this) {
			return false;
		} else {
			return t.isEmptyList();
		}
	}
	
	@Override
	public boolean isAtomic() {
		Term t=getTerm();
		if (t==this) {
			return false;
		} else {
			return t.isAtomic();
		}
	}
	
	@Override
	public boolean isCompound() {
		Term t=getTerm();
		if (t==this) {
			return false;
		} else {
			return t.isCompound();
		}
	}
	
	@Override
	public boolean isAtom() {
		Term t=getTerm();
		if (t==this) {
			return false;
		} else {
			return t.isAtom();
		}
	}
	
	@Override
	public boolean isList() {
		Term t = getTerm();
		if (t == this)
			return false;
		else
			return t.isList();
	}
	
	@Override
	public boolean isGround(){
		Term t=getTerm();
		if (t==this) {
			return false;
		} else {
			return t.isGround();
		}
	}
	
	//
	
	/** Tests if this variable is ANY. */
	public boolean isAnonymous() {
		return name == null;
	}
	
	/** Tests if this variable is bound. */
	public boolean isBound() {
		return link != null;
	}
	
	/**
	 * Finds variable occurrence in a Struct, doing occur-check.
	 * (It was called findIn.)
	 */
	private boolean occurCheck(List<Var> vl, Struct t) {
		int arity = t.getArity();
		for (int c = 0; c < arity; c++) {
			Term at = t.getTerm(c);
			if (at instanceof Struct) {
				if (occurCheck(vl, (Struct) at))
					return true;
			} else
				if (at instanceof Var) {
					Var v = (Var) at;
					if (v.link == null)
						vl.add(v);
					if (this == v)
						return true;
				}
		}
		return false;
	}
	
	//
	
	/** Resolve the occurrence of variables in a Term. */
	@Override
	long resolveTerm(long count) {
		Term tt=getTerm();
		if (tt != this) {
			return tt.resolveTerm(count);
		} else {
			timestamp = count;
			return count++;
		}
	}
	
	//
	
	/**
	 * Variable unification.
	 * <p>
	 * First, verify the Term eventually already unified with the same Var
	 * if the Term exist, unify var with that term, in order to handle situation
	 * as (A = p(X) , A = p(1)) which must produce X/1.
	 * <p>
	 * If instead the var is not already unified, then:
	 * <p>
	 * if the Term is a var bound to X, then try unification with X
	 * so for example if A=1, B=A then B is unified to 1 and not to A
	 * (note that it's coherent with chronological backtracking:
	 * the eventually backtracked A unification is always after
	 * backtracking of B unification.
	 * <p>
	 * if are the same Var, unification must succeed, but without any new
	 * bindings (to avoid cycles for extends in A = B, B = A)
	 * <p>
	 * if the term is a number, then it's a success and new link is created
	 * (retractable by means of a code)
	 * <p>
	 * if the term is a compound, then occur check test is executed:
	 * the var must not appear in the compound ( avoid X=p(X),
	 * or p(X,X)=p(Y,f(Y)) ); if occur check is ok
	 * then it's success and a new link is created (retractable by a code)
	 */
	@Override
	boolean unify(List<Var> vl1, List<Var> vl2, Term t) {
		Term tt = getTerm();
		if(tt == this) {
			t = t.getTerm();
			if (t instanceof Var) {
				if (this == t) {
					try {
						vl1.add(this);            	
					} catch (NullPointerException e) {
						/* vl1==null means nothing interesting for the caller */
					}
					return true;
				}
			} else
				if (t instanceof Struct) {
					// occur-check
					if (occurCheck(vl2, (Struct)t)) {
						return false;
					}
				} else
					if (!(t instanceof Number)) {
						return false;
					}
			link = t;
			try {
				vl1.add(this);            	
			} catch (NullPointerException e) {
				/* vl1==null mean nothing interesting for the caller */
			}
			//System.out.println("VAR "+name+" BOUND to "+link+" - time: "+time+" - mark: "+mark);
			return true;
		} else {
			return (tt.unify(vl1, vl2, t));
		}
	}
	
	@Override
	public boolean isGreater(Term t) {
		Term tt = getTerm();
		if (tt == this) {
			t = t.getTerm();
			if (!(t instanceof Var)) return false;
			return timestamp > ((Var)t).timestamp;
		}
		else {
			return tt.isGreater(t);
		}
	}
	
	@Override
	public boolean isEqual(Term t) {
		Term tt = getTerm();
		if(tt == this) {
			t = t.getTerm();
			return (t instanceof Var && timestamp == ((Var)t).timestamp);
		} else {
			return tt.isEqual(t);
		}
	}
	
	/**
	 * Gets the string representation of this variable.
	 *
	 * For bounded variables, the string is <Var Name>/<bound Term>.
	 */
	@Override
	public String toString() {
		Term tt = getTerm();
		if (name != null) {
			if (tt == this){
				return completeName;
			} else {
				return (completeName + " / " + tt.toString());
			}
		} else {
			if (tt == this) {
				return ANY + hashCode();
			} else {
				return tt.toString();
			}
		}
	}
	
	/**
	 * Gets the string representation of this variable, providing
	 * the string representation of the linked term in the case of
	 * bound variable.
	 */
	public String toStringFlattened() {
		Term tt = getTerm();
		if (name != null) {
			if (tt == this) {
				return completeName;
			} else {
				return tt.toString();
			}
		} else {
			if (tt == this) {
				return ANY + hashCode();
			} else {
				return tt.toString();
			}
		}
	}
		
}