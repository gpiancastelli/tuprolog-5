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
package alice.tuprolog.lib;

import alice.tuprolog.Int;
import alice.tuprolog.Library;
import alice.tuprolog.Number;
import alice.tuprolog.Predicate;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import alice.tuprolog.Var;

/**
 * This class represents a tuProlog library providing most of the built-ins
 * predicates and functors defined by ISO standard.
 *
 * Library/Theory dependency:  BasicLibrary
 */
public class ISOLibrary extends Library {
	
	public ISOLibrary() {
	}
	
	@Predicate("atom_length/2")
	public boolean atomLength(Term arg, Term len) {
		arg = arg.getTerm();
		if (!(arg instanceof Struct))
			return false;
		Struct arg0 = (Struct) arg;
		if (!arg0.isAtom())
			return false;
		return unify(len,new Int(arg0.getName().length()));
	}
	
	@Predicate("atom_chars/2")
	public boolean atomChars(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (arg0 instanceof Var) {
			if (!arg1.isList())
				return false;
			Struct list = (Struct) arg1;
			if (list.isEmptyList())
				return unify(arg0, new Struct(""));
			String st = "";
			while (!(list.isEmptyList())) {
				String st1 = list.getTerm(0).toString();
				try {
					if (st1.startsWith("'") && st1.endsWith("'"))
						st1 = st1.substring(1, st1.length() - 1);
				} catch (Exception ex) {};
				st = st.concat(st1);
				list = (Struct) list.getTerm(1);
			}
			return unify(arg0, new Struct(st));
		} else {
			if (!arg0.isAtom())
				return false;
			String st = ((Struct)arg0).getName();
			Term[] tlist = new Term[st.length()];
			for (int i = 0; i < st.length(); i++)
				tlist[i] = new Struct(new String(new char[] { st.charAt(i) }));
			Struct list = new Struct(tlist);
			
			return unify(arg1,list);
		}
	}
	
	@Predicate("char_code/2")
	public boolean charCode(Term arg0, Term arg1) {
		arg0 = arg0.getTerm();
		arg1 = arg1.getTerm();
		if (arg1 instanceof Var) {
			if (arg0.isAtom()) {
				String st = ((Struct) arg0).getName();
				if (st.length() <= 1)
					return unify(arg1, new Int(st.charAt(0)));
			}
		} else if ((arg1 instanceof Int) || (arg1 instanceof alice.tuprolog.Long)) {
			char c = (char) ((Number) arg1).intValue();
			return unify(arg0, new Struct("" + c));
		}
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
	public String getTheory(){
		return
		//
		// operators defined by the ISOLibrary theory
		//
//		":- op(  300, yfx,  'div'). \n"+
//		":- op(  400, yfx,  'mod'). \n"+
//		":- op(  400, yfx,  'rem'). \n"+
//		":- op(  200, fx,   'sin'). \n"+
//		":- op(  200, fx,   'cos'). \n"+
//		":- op(  200, fx,   'sqrt'). \n"+
//		":- op(  200, fx,   'atan'). \n"+
//		":- op(  200, fx,   'exp'). \n"+
//		":- op(  200, fx,   'log'). \n"+
		//
		// flags defined by the ISOLibrary theory
		//
		":- flag(bounded, [true,false], true, false).\n"+
		":- flag(max_integer, [" + new Integer(Integer.MAX_VALUE).toString() + "], " + new Integer(Integer.MAX_VALUE).toString() + ",false).\n"+
		":- flag(min_integer, [" + new Integer(Integer.MIN_VALUE).toString() + "], " + new Integer(Integer.MIN_VALUE).toString() + ",false).\n"+
		":- flag(integer_rounding_function, [up,down], down, false).\n"+
		":- flag(char_conversion,[on,off],off,false).\n"+
		":- flag(debug,[on,off],off,false).\n"+
		":- flag(max_arity, [" + new Integer(Integer.MAX_VALUE).toString() + "], " + new Integer(Integer.MAX_VALUE).toString() + ",false).\n"+
		":- flag(undefined_predicate, [error,fail,warning], fail, false).\n"+
		":- flag(double_quotes, [atom,chars,codes], atom, false).\n"+
		//
		"atom_concat(F,S,R) :- atom_chars(F,FL),atom_chars(S,SL),!,append(FL,SL,RS),atom_chars(R,RS).\n          " +
		"atom_concat(F,S,R) :- atom_chars(R,RS),append(FL,SL,RS),atom_chars(F,FL),atom_chars(S,SL).\n            " +
		"atom_codes(A,L):-atom_chars(A,L1),!,chars_codes(L1,L).\n"+
		"atom_codes(A,L):-chars_codes(L1,L),atom_chars(A,L1).\n"+
		"chars_codes([],[]).\n"+
		"chars_codes([X|L1],[Y|L2]):-char_code(X,Y),chars_codes(L1,L2).\n"+
		"sub_atom(Atom,B,L,A,Sub):-atom_chars(Atom,L1),atom_chars(Sub,L2),!,sub_list(L2,L1,B),length(L2,L), length(L1,Len), A is Len-(B+L).\n"+
		"sub_atom(Atom,B,L,A,Sub):-atom_chars(Atom,L1),sub_list(L2,L1,B),atom_chars(Sub,L2),length(L2,L), length(L1,Len), A is Len-(B+L).\n"+
		"sub_list([],_,0).\n"+
		"sub_list([X|L1],[X|L2],0):- sub_list_seq(L1,L2).\n"+
		"sub_list(L1,[_|L2],N):- sub_list(L1,L2,M), N is M + 1.\n"+
		"sub_list_seq([],L).\n"+
		"sub_list_seq([X|L1],[X|L2]):-sub_list_seq(L1,L2).\n"+
		"number_chars(Number,List):-num_atom(Number,Struct),atom_chars(Struct,List),!.\n"+
		"number_chars(Number,List):-atom_chars(Struct,List),num_atom(Number,Struct).\n"+
		"number_codes(Number,List):-num_atom(Number,Struct),atom_codes(Struct,List),!.\n"+
		"number_codes(Number,List):-atom_codes(Struct,List),num_atom(Number,Struct).\n";
		//
		// ISO default
		//"current_prolog_flag(changeable_flags,[ char_conversion(on,off), debug(on,off), undefined_predicate(error,fail,warning),double_quotes(chars,codes,atom) ]).\n" +
		//"current_prolog_flag(changeable_flags,[]).\n" +
	}
	
}
