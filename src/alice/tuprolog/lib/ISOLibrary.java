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

import alice.tuprolog.Functor;
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
	
	// functors
	
	@Functor("sin/1")
	public Term sin(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.sin(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("cos/1")
	public Term cos(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.cos(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("exp/1")
	public Term exp(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.exp(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("atan/1")
	public Term atan(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.atan(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("log/1")
	public Term log(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.log(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("sqrt/1")
	public Term sqrt(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.sqrt(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("abs/1")
	public Term abs(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long)
			return new alice.tuprolog.Int(Math.abs(((Number)val0).intValue()));
		if (val0 instanceof alice.tuprolog.Double || val0 instanceof alice.tuprolog.Float)
			return new alice.tuprolog.Double(Math.abs(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("sign/1")
	public Term sign(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long)
			return new alice.tuprolog.Double(((Number)val0).intValue()>0 ? 1.0: -1.0);
		if (val0 instanceof alice.tuprolog.Double || val0 instanceof alice.tuprolog.Float)
			return new alice.tuprolog.Double(((Number)val0).doubleValue()>0 ? 1.0: -1.0);
		return null;
	}
	
	@Functor("float_integer_part/1")
	public Term floatIntegerPart(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double((long)Math.rint(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("float_fractional_part/1")
	public Term floatFractionalPart(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double fl = ((Number)val0).doubleValue();
			return new alice.tuprolog.Double(Math.abs(fl-Math.rint(fl)));
		}
		return null;
	}
	
	@Functor("float/1")
	public Term toFloat(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(((Number) val0).doubleValue());
		return null;
	}
	
	@Functor("floor/1")
	public Term floor(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new Int((int) Math.floor(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("round/1")
	public Term round(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Long(Math.round(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("truncate/1")
	public Term truncate(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new Int((int) Math.rint(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("ceiling/1")
	public Term ceiling(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new Int((int) Math.ceil(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("div/2")
	public Term div(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		if (val0 instanceof Number && val1 instanceof Number)
			return new alice.tuprolog.Int(((Number) val0).intValue() / ((Number) val1).intValue());
		return null;
	}
	
	@Functor("mod/2")
	public Term mod(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		if (val0 instanceof Number && val1 instanceof Number) {
			int x = ((Number) val0).intValue();
	        int y = ((Number) val1).intValue();
	        int f = new java.lang.Double(Math.floor((double) x / (double) y)).intValue();
	        return new Int(x - (f * y));
		}
		return null;
	}
	
	@Functor("rem/2")
	public Term rem(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		if (val0 instanceof Number && val1 instanceof Number)
			return new alice.tuprolog.Double(Math.IEEEremainder(((Number) val0).doubleValue(), ((Number) val1).doubleValue()));
		return null;
	}
	
	@Override
	public String getTheory(){
		return
		//
		// operators defined by the ISOLibrary theory
		//
		":- op(  300, yfx,  'div'). \n"+
		":- op(  400, yfx,  'mod'). \n"+
		":- op(  400, yfx,  'rem'). \n"+
		":- op(  200, fx,   'sin'). \n"+
		":- op(  200, fx,   'cos'). \n"+
		":- op(  200, fx,   'sqrt'). \n"+
		":- op(  200, fx,   'atan'). \n"+
		":- op(  200, fx,   'exp'). \n"+
		":- op(  200, fx,   'log'). \n"+
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
		//
		"bound(X):-ground(X).\n                                                                                  "+
		"unbound(X):-not(ground(X)).\n                                                                          "+
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
