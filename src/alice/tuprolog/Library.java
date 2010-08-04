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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This abstract class is the base class for developing
 * tuProlog built-in libraries, which can be dynamically
 * loaded by prolog objects.
 * <p>
 * Each library can expose to engine:
 * <ul>
 * <li> a theory (as a string assigned to theory field)
 * <li> builtin predicates: each method whose signature is
 *       boolean name_arity(Term arg0, Term arg1,...)
 *   is considered a built-in predicate provided by the library
 * <li> builtin evaluable functors: each method whose signature is
 *       Term name_arity(Term arg0, Term arg1,...)
 *   is considered a built-in functors provided by the library
 * </ul>
 * <p>
 */
public abstract class Library implements IPrimitives {
	
	/** prolog core which loaded the library */
	protected Prolog engine;
		
	public Library() {
	}
	
	/**
	 * Gets the name of the library. 
	 * 
	 * By default the name is the class name.
	 * 
	 * @return the library name
	 */
	public String getName() {
		return getClass().getName();
	}
	
	/**
	 * Gets the theory provided with the library
	 *
	 * Empty theory is provided by default.
	 */
	public String getTheory() {
		return "";
	}
	
	/**
	 * Gets the engine to which the library is bound
	 *
	 * @return the engine
	 */
	public Prolog getEngine() {
		return engine;
	}
	
	void setEngine(Prolog en) {
        engine = en;
	}
	
	/**
	 * tries to unify two terms
	 *
	 * The runtime (demonstration) context currently used by the engine
	 * is deployed and altered.
	 */
	protected boolean unify(Term a0,Term a1) {
		return engine.unify(a0,a1);
	}
	
	/**
	 * tries to unify two terms
	 *
	 * The runtime (demonstration) context currently used by the engine
	 * is deployed and altered.
	 */
	protected boolean match(Term a0,Term a1) {
		return engine.match(a0,a1);
	}
	
	
	/**
	 * Evaluates an expression. Returns null value if the argument
	 * is not an evaluable expression
	 *
	 * The runtime (demo) context currently used by the engine
	 * is deployed and altered.
	 */
	protected Term evalExpression(Term term) {
		if (term == null)
			return null;
		Term val = term.getTerm();
		if (val instanceof Struct) {
			Struct t = (Struct) val;
			if (term != t)
				if (!t.isPrimitive())
					engine.identifyFunctor(t);
			if (t.isPrimitive()) {
				PrimitiveInfo bt = t.getPrimitive();
				// check for library functors
				if (bt.isFunctor())
					return bt.evalAsFunctor(t);
			}
		} else if (val instanceof Number) {
			return val;
		}
		return null;
	}
	
	/**
	 * method invoked by prolog engine when library is
	 * going to be removed
	 */
	public void dismiss() {}
	
	/**
	 * method invoked when the engine is going
	 * to demonstrate a goal
	 */
	public void onSolveBegin(Term goal) {}
	
	/**
	 * method invoked when the engine has
	 * finished a demonstration
	 */
	public void onSolveEnd() {}
	
	/**
	 * gets the list of predicates defined in the library
	 */
	public List<PrimitiveInfo>[] getPrimitives() {
		try {
			Method[] mlist = this.getClass().getMethods();
			
			@SuppressWarnings("unchecked")
			List<PrimitiveInfo>[] tablePrimitives = (List<PrimitiveInfo>[]) new List[] {
				new ArrayList<PrimitiveInfo>(), new ArrayList<PrimitiveInfo>(), new ArrayList<PrimitiveInfo>()
			};
			
			for (int i = 0; i < mlist.length; i++) {
				Method method = mlist[i];
				Class<?> returnType = method.getReturnType();
				
				if (method.isAnnotationPresent(Predicate.class)) {
					if (returnType != Boolean.TYPE) {
						String m = "Method " + method.getName() + "is declared " +
						           "as Predicate but does not return a boolean.";
						getEngine().warn(m);
						continue;
					}
					Predicate annotation = method.getAnnotation(Predicate.class);
					String name = annotation.value();
					addValidMethod(tablePrimitives, PrimitiveInfo.PREDICATE, method, name);
				} 
				
				if (method.isAnnotationPresent(Functor.class)) {
					if (returnType != Term.class) {
						String m = "Method " + method.getName() + "is declared " +
				                   "as Functor but does not return a Term.";
						getEngine().warn(m);
						continue;
					}
					Functor annotation = method.getAnnotation(Functor.class);
					String name = annotation.value();
					addValidMethod(tablePrimitives, PrimitiveInfo.FUNCTOR, method, name);
				} 
				
				if (method.isAnnotationPresent(Directive.class)) {
					if (returnType != Boolean.TYPE) {
						String m = "Method " + method.getName() + "is declared " +
						           "as Directive but does not return a boolean.";
						getEngine().warn(m);
						continue;
					}
					Directive annotation = method.getAnnotation(Directive.class);
					String name = annotation.value();
					addValidMethod(tablePrimitives, PrimitiveInfo.DIRECTIVE, method, name);
				}
			}
			return tablePrimitives;
		} catch (Exception e) {
			return null;
		}
	}
	
	private void addValidMethod(List<PrimitiveInfo>[] table, int type, Method method, String name) {
		int index = name.lastIndexOf('/');
		if (index != -1) {
			Class<?>[] clist = method.getParameterTypes();
			try {
				int arity = Integer.parseInt(name.substring(index + 1, name.length()));
				// check the number of arguments
				if (clist.length == arity) {
					boolean valid = true;
					for (int j = 0; j < arity; j++)
						if (!(Term.class.isAssignableFrom(clist[j]))) {
							valid = false;
							break;
						}
					if (valid) {
						PrimitiveInfo p = new PrimitiveInfo(type, name, this, method, arity);
						table[type].add(p);
					}
				}
			} catch (Exception e) {}
		}
	}
	
}