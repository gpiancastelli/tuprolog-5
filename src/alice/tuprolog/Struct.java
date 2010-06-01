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

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Struct class represents both compound prolog term
 * and atom term (considered as 0-arity compound).
 */
public class Struct extends Term {
	
	/** name of the structure */
	private String name;
	/** args array */
	private Term[] args;
	/** arity **/
	private int arity;
	/** to speedup hash map operation */
	private String predicateIndicator;
	/** primitive behaviour */
	private transient PrimitiveInfo primitive;
	/** it indicates if the term is resolved */
	private boolean resolved=false;
	
	/**
	 * Builds a compound term, with a series of arguments.
	 * If no arguments are passed, builds an atom.
	 */
	public Struct(String f, Term... args) {
		this(f, args.length);
		for (int i = 0; i < args.length; i++)
			if (args[i] == null)
				throw new InvalidTermException("Arguments of a Struct cannot be null");
			else
				this.args[i] = args[i];
	}
	
	/**
	 * Builds a structure representing an empty list
	 */
	public Struct() {
		this("[]", 0);
		resolved = true;
	}
	
	/**
	 * Builds a list providing head and tail
	 */
	public Struct(Term h, Term t) {
		this(".", 2);
		args[0] = h;
		args[1] = t;
	}
	
	/**
	 * Builds a list specifying the elements
	 */
	public Struct(Term[] args) {
		this(args, 0);
	}
	
	private Struct(Term[] args, int index) {
		this(".", 2);
		if (index < args.length) {
			this.args[0] = args[index];
			this.args[1] = new Struct(args, index+1);
		} else {
			// build an empty list
			name = "[]";
			arity = 0;
			this.args = null;
		}
	}
	
	/**
	 * Builds a compound, with a linked list of arguments
	 */
	Struct(String f, LinkedList<Term> args) {
		name = f;
		arity = args.size();
		if (arity > 0) {
			this.args = new Term[arity];
			for(int c = 0; c < arity; c++)
				this.args[c] = args.removeFirst();
		}
		predicateIndicator = name + "/" + arity;
		resolved = false;
	}
	
	private Struct(int arity) {
		this.arity = arity;
		args = new Term[arity];
	}
	
	private Struct(String name,int arity) {
		if (name == null)
			throw new InvalidTermException("The functor of a Struct cannot be null");
		if (name.length() == 0 && arity > 0)
			throw new InvalidTermException("The functor of a non-atom Struct cannot be an empty string");
		this.name = name;
		this.arity = arity;
		if (arity > 0)
			args = new Term[arity];
		predicateIndicator = name + "/" + arity;
		resolved = false;
	}
	
	/** @deprecated Use Struct#getPredicateIndicator instead. */
	String getHashKey() {
		return getPredicateIndicator();
	}
	
	String getPredicateIndicator() {
		return predicateIndicator;
	}
	
	/**
	 * Gets the number of elements of
	 * this structure
	 */
	public int getArity() {
		return arity;
	}
	
	/**
	 * Gets the functor name  of this structure
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the i-th element of this structure
	 *
	 * No bound check is done
	 */
	public Term getArg(int index) {
		return args[index];
	}
	
	/**
	 * Sets the i-th element of this structure
	 *
	 * (Only for internal service)
	 */
	void setArg(int index, Term argument) {
		args[index] = argument;
	}
	
	/**
	 * Gets the i-th element of this structure
	 *
	 * No bound check is done. It is equivalent to
	 * <code>getArg(index).getTerm()</code>
	 */
	public Term getTerm(int index) {
		return args[index].getTerm();
	}
	
	
	// checking type and properties of the Term
	
	/** is this term a prolog numeric term? */
	public boolean isNumber() {
		return false;
	}
	
	/** is this term a struct  */
	public boolean isStruct() {
		return true;
	}
	
	/** is this term a variable  */
	public boolean isVar() {
		return false;
	}
	
	
	// check type services
	
	public boolean isAtomic() {
		return  arity == 0;
	}
	
	public boolean isCompound() {
		return arity > 0;
	}
	
	public boolean isAtom() {
		return (arity == 0 || isEmptyList());
	}
	
	public boolean isList() {
		return (name.equals(".") && arity == 2 && args[1].isList()) || isEmptyList();
	}
	
	public boolean isGround() {
		for (int i=0; i<arity; i++) {
			if (!args[i].isGround()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check is this struct is clause or directive
	 */
	public boolean isClause() {
		return(name.equals(":-") && arity > 1 && args[0].getTerm() instanceof Struct);
		//return(name.equals(":-") && arity == 2 && arg[0].getTerm() instanceof Struct);
	}    
	
	public Term getTerm() {
		return this;
	}
	
	//
	
	/**
	 * Gets an argument inside this structure, given its name
	 * 
	 * @param name name of the structure 
	 * @return the argument or null if not found
	 */
	public Struct getArg(String name) {
		if (arity == 0) {
			return null;
		}
		for (int i=0; i<args.length; i++) {
			if (args[i] instanceof Struct) {
				Struct s = (Struct) args[i];
				if (s.getName().equals(name)) {
					return s;
				}
			}
		}
		for (int i=0; i<args.length; i++) {
			if (args[i] instanceof Struct) {
				Struct s = (Struct)args[i];
				Struct sol = s.getArg(name);
				if (sol!=null) {
					return sol;
				}
			}
		}
		return null;
	}
	
	
	//
	
	/**
	 * Test if a term is greater than other
	 */
	public boolean isGreater(Term t) {
		t = t.getTerm();
		if (!(t instanceof Struct)) {
			return true;
		} else {
			Struct ts = (Struct) t;
			int tarity = ts.arity;
			if (arity > tarity) {
				return true;
			} else if (arity == tarity) {
				if (name.compareTo(ts.name) > 0) {
					return true;
				} else if (name.compareTo(ts.name) == 0) {
					for (int c = 0;c < arity;c++) {
						if (args[c].isGreater(ts.args[c])) {
							return true;
						} else if (!args[c].isEqual(ts.args[c])) {
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * Test if a term is equal to other
	 */
	public boolean isEqual(Term t) {
		t = t.getTerm();
		if (t instanceof Struct) {
			Struct ts = (Struct) t;
			if (arity == ts.arity && name.equals(ts.name)) {
				for (int c = 0;c < arity;c++) {
					if (!args[c].isEqual(ts.args[c])) {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	//
	
	
	/**
	 * Gets a copy of this structure
	 * @param vMap is needed for register occurence of same variables
	 */
	Term copy(AbstractMap vMap, int idExecCtx) {
		Struct t = new Struct(arity);
		t.resolved  = resolved;
		t.name      = name;
		t.predicateIndicator   = predicateIndicator;
		t.primitive = primitive;
		for (int c = 0;c < arity;c++) {
			t.args[c] = args[c].copy(vMap, idExecCtx);
		}
		return t;
	}
	
	
	/**
	 * Gets a copy of this structure
	 * @param vMap is needed for register occurence of same variables
	 */
	Term copy(AbstractMap vMap, AbstractMap substMap) {
		Struct t = new Struct(arity);
		t.resolved  = false;
		t.name      = name;
		t.predicateIndicator   = predicateIndicator;
		t.primitive = null;
		for (int c = 0;c < arity;c++) {
			t.args[c] = args[c].copy(vMap, substMap);
		}
		return t;
	}
	
	
	/**
	 * resolve term
	 */
	long resolveTerm(long count) {
		if (resolved) {
			return count;
		} else {
			LinkedList vars = new LinkedList();
			return resolveTerm(vars,count);
		}
	}
	
	
	/**
	 * Resolve name of terms
	 * @param vl list of variables resolved
	 * @param count start timestamp for variables of this term
	 * @return next timestamp for other terms
	 */
	long resolveTerm(LinkedList vl,long count) {
		long newcount=count;
		for (int c = 0;c < arity;c++) {
			Term term=args[c];
			if (term!=null) {
				//--------------------------------
				// we want to resolve only not linked variables:
				// so linked variables must get the linked term
				term=term.getTerm();
				//--------------------------------
				if (term instanceof Var) {
					Var t = (Var) term;
					t.setTimestamp(newcount++);
					if (!t.isAnonymous()) {
						// searching a variable with the same name in the list
						String name= t.getName();
						Iterator it = vl.iterator();
						Var found = null;
						while (it.hasNext()) {
							Var vn = (Var) it.next();
							if (name.equals(vn.getName())) {
								found=vn;
								break;
							}
						}
						if (found != null) {
							args[c] = found;
						} else {
							vl.add(t);
						}
					}
				} else if (term instanceof Struct) {
					newcount = ( (Struct) term ).resolveTerm(vl,newcount);
				}
			}
		}
		resolved = true;
		return newcount;
	}
	
	// services for list structures
	
	/**
	 * Is this structure an empty list?
	 */
	public boolean isEmptyList() {
		return name.equals("[]") && arity == 0;
	}
	
	/**
	 * Gets the head of this structure, which is supposed to be a list.
	 * 
	 * <p>
	 * Gets the head of this structure, which is supposed to be a list.
	 * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
	 * </p>
	 */
	public Term listHead() {
		if (!isList())
			throw new UnsupportedOperationException("The structure " + this + " is not a list.");
		return args[0].getTerm();
	}
	
	/**
	 * Gets the tail of this structure, which is supposed to be a list.
	 * 
	 * <p>
	 * Gets the tail of this structure, which is supposed to be a list.
	 * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
	 * </p>
	 */
	public Struct listTail() {
		if (!isList())
			throw new UnsupportedOperationException("The structure " + this + " is not a list.");
		return (Struct) args[1].getTerm() ;
	}
	
	/**
	 * Gets the number of elements of this structure, which is supposed to be a list.
	 * 
	 * <p>
	 * Gets the number of elements of this structure, which is supposed to be a list.
	 * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
	 * </p>
	 */
	public int listSize() {
		if (!isList())
			throw new UnsupportedOperationException("The structure " + this + " is not a list.");
		Struct t = this;
		int count = 0;
		while (!t.isEmptyList()) {
			count++;
			t = (Struct) t.args[1].getTerm();
		}
		return count;
	}
	
	/**
	 * Gets an iterator on the elements of this structure, which is supposed to be a list.
	 * 
	 * <p>
	 * Gets an iterator on the elements of this structure, which is supposed to be a list.
	 * If the callee structure is not a list, throws an <code>UnsupportedOperationException</code>
	 * </p>
	 */
	public java.util.Iterator listIterator() {
		if (!isList())
			throw new UnsupportedOperationException("The structure " + this + " is not a list.");
		return new StructIterator(this);
	}
	
	// hidden services
	
	/**
	 * Gets a list Struct representation, with the functor as first element.
	 */
	Struct toList() {
		Struct t = new Struct();
		for(int c = arity - 1;c >= 0;c--) {
			t = new Struct(args[c].getTerm(),t);
		}
		return new Struct(new Struct(name),t);
	}
	
	
	/**
	 * Gets a flat Struct from this structure considered as a List
	 *
	 * If this structure is not a list, null object is returned
	 */
	Struct fromList() {
		Term ft = args[0].getTerm();
		if (!ft.isAtom()) {
			return null;
		}
		Struct at = (Struct) args[1].getTerm();
		LinkedList al = new LinkedList();
		while (!at.isEmptyList()) {
			if (!at.isList()) {
				return null;
			}
			al.addLast(at.getTerm(0));
			at = (Struct) at.getTerm(1);
		}
		return new Struct(((Struct) ft).name, al);
	}
	
	
	/**
	 * Appends an element to this structure supposed to be a list
	 */
	public void append(Term t) {
		if (isEmptyList()) {
			name = ".";
			arity = 2;
			args = new Term[arity];
			args[0] = t; args[1] = new Struct();
		} else if (args[1].isList()) {
			((Struct) args[1]).append(t);
		} else {
			args[1] = t;
		}
	}
	
	
	/**
	 * Inserts (at the head) an element to this structure supposed to be a list
	 */
	void insert(Term t) {
		Struct co=new Struct();
		co.args[0]=args[0];
		co.args[1]=args[1];
		args[0] = t;
		args[1] = co;
	}
	
	//
	
	/**
	 * Try to unify two terms
	 * @param t the term to unify
	 * @param vl1 list of variables unified
	 * @param vl2 list of variables unified
	 * @return true if the term is unifiable with this one
	 */
	boolean unify(List vl1,List vl2,Term t) {
		// In fase di unificazione bisogna annotare tutte le variabili della struct completa.
		t = t.getTerm();
		if (t instanceof Struct) {
			Struct ts = (Struct) t;
			if ( arity == ts.arity && name.equals(ts.name)) {
				for (int c = 0;c < arity;c++) {
					if (!args[c].unify(vl1,vl2,ts.args[c])) {
						return false;
					}
				}
				return true;
			}
		} else if (t instanceof Var) {
			return t.unify(vl2, vl1, this);
		}
		return false;
	}
	
	
	/** dummy method */
	public void free() {}
	
	//
	
	/**
	 * Set primitive behaviour associated at structure
	 */
	void setPrimitive(PrimitiveInfo b) {
		primitive = b;
	}
	
	/**
	 * Get primitive behaviour associated at structure
	 */
	public PrimitiveInfo getPrimitive() {
		return primitive;
	}
	
	/**
	 * Check if this term is a primitive struct
	 */
	public boolean isPrimitive() {
		return primitive != null;
	}
	
	//
	
	/**
	 * Gets the string representation of this structure
	 *
	 * Specific representations are provided for lists and atoms.
	 * Names starting with upper case letter are enclosed in apices.
	 */
	public String toString() {
		// empty list case
		if (isEmptyList()) return "[]";
		// list case
		if (name.equals(".") && arity == 2) {
			return ("[" + toString0() + "]");
		} else if (name.equals("{}")) {
			return ("{" + toString0_bracket() + "}");
		} else {
			String s = (Parser.isAtom(name) ? name : "'" + name + "'");
			if (arity > 0) {
				s = s + "(";
				for (int c = 1;c < arity;c++) {
					if (!(args[c - 1] instanceof Var)) {
						s = s + args[c - 1].toString() + ",";
					} else {
						s = s + ((Var)args[c - 1]).toStringFlattened() + ",";
					}
				}
				if (!(args[arity - 1] instanceof Var)) {
					s = s + args[arity - 1].toString() + ")";
				} else {
					s = s + ((Var)args[arity - 1]).toStringFlattened() + ")";
				}
			}
			return s;
		}
	}
	
	private String toString0() {
		Term h = args[0].getTerm();
		Term t = args[1].getTerm();
		if (t.isList()) {
			Struct tl = (Struct) t;
			if (tl.isEmptyList()) {
				return h.toString();
			}
			if (h instanceof Var) {
				return (((Var)h).toStringFlattened() + "," + tl.toString0());
			} else {
				return (h.toString() + "," + tl.toString0());
			}
		} else {
			String h0;
			String t0;
			if (h instanceof Var) {
				h0 = ((Var)h).toStringFlattened();
			} else {
				h0 = h.toString();
			}
			if (t instanceof Var) {
				t0 = ((Var)t).toStringFlattened();
			} else {
				t0 = t.toString();
			}
			return (h0 + "|" + t0);
		}
	}
	
	private String toString0_bracket() {
		if (arity == 0) {
			return "";
		} else if (arity==1 && !((args[0] instanceof Struct) && ((Struct)args[0]).getName().equals(","))){
			return args[0].getTerm().toString();
		} else {
			// comma case 
			Term head = ((Struct)args[0]).getTerm(0);
			Term tail = ((Struct)args[0]).getTerm(1);
			StringBuffer buf = new StringBuffer(head.toString());
			while (tail instanceof Struct && ((Struct)tail).getName().equals(",")){
				head = ((Struct)tail).getTerm(0);
				buf.append(","+head.toString());
				tail = ((Struct)tail).getTerm(1);
			}
			buf.append(","+tail.toString());
			return buf.toString();
			//	return arg[0]+","+((Struct)arg[1]).toString0_bracket();
		}
	}
	
	private String toStringAsList(OperatorManager op) {
		Term h = args[0];
		Term t = args[1].getTerm();
		if (t.isList()) {
			Struct tl = (Struct)t;
			if (tl.isEmptyList()){
				return h.toStringAsArgY(op,0);
			}
			return (h.toStringAsArgY(op,0) + "," + tl.toStringAsList(op));
		} else {
			return (h.toStringAsArgY(op,0) + "|" + t.toStringAsArgY(op,0));
		}
	}
	
	String toStringAsArg(OperatorManager op,int prio,boolean x) {
		int      p = 0;
		String   v = "";
		
		if (name.equals(".") && arity == 2) {
			if (args[0].isEmptyList()) {
				return("[]");
			} else {
				return("[" + toStringAsList(op) + "]");
			}
		} else if (name.equals("{}")) {
			return("{" + toString0_bracket() + "}");
		}
		
		if (arity == 2) {
			if ((p = op.opPrio(name,"xfx")) >= OperatorManager.OP_LOW) {
				return(
						(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
						args[0].toStringAsArgX(op,p) +
						" " + name + " "      +
						args[1].toStringAsArgX(op,p) +
						(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
			}
			if ((p = op.opPrio(name,"yfx")) >= OperatorManager.OP_LOW) {
				return(
						(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
						args[0].toStringAsArgY(op,p) +
						" " + name + " "      +
						args[1].toStringAsArgX(op,p) +
						(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
			}
			if ((p = op.opPrio(name,"xfy")) >= OperatorManager.OP_LOW) {
				if (!name.equals(",")) {
					return(
							(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
							args[0].toStringAsArgX(op,p) +
							" " + name + " "      +
							args[1].toStringAsArgY(op,p) +
							(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
				} else {
					return(
							(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
							args[0].toStringAsArgX(op,p) +
							//",\n\t"+
							","+
							args[1].toStringAsArgY(op,p) +
							(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
				}
			}
		}
		else if (arity == 1) {
			if ((p = op.opPrio(name,"fx")) >= OperatorManager.OP_LOW) {
				return(
						(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
						name + " "            +
						args[0].toStringAsArgX(op,p) +
						(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
			}
			if ((p = op.opPrio(name,"fy")) >= OperatorManager.OP_LOW) {
				return(
						(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
						name + " "            +
						args[0].toStringAsArgY(op,p) +
						(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
			}
			if ((p = op.opPrio(name,"xf")) >= OperatorManager.OP_LOW) {
				return(
						(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
						args[0].toStringAsArgX(op,p) +
						" " + name + " "      +
						(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
			}
			if ((p = op.opPrio(name,"yf")) >= OperatorManager.OP_LOW) {
				return(
						(((x && p >= prio) || (!x && p > prio)) ? "(" : "") +
						args[0].toStringAsArgY(op,p) +
						" " + name + " "      +
						(((x && p >= prio) || (!x && p > prio)) ? ")" : ""));
			}
		}
		v = (Parser.isAtom(name) ? name : "'" + name + "'");
		if (arity == 0) {
			return v;
		}
		v = v + "(";
		for (p = 1;p < arity;p++) {
			v = v + args[p - 1].toStringAsArgY(op,0) + ",";
		}
		v = v + args[arity - 1].toStringAsArgY(op,0);
		v = v + ")";
		return v;
	}
	
	public Term iteratedGoalTerm() {
		if (name.equals("^") && arity == 2) {
			Term goal = getTerm(1);
			return goal.iteratedGoalTerm();
		} else
			return super.iteratedGoalTerm();
	}
	
}