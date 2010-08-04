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
	
	/** operator mapping*/
	private String[][] opMappingCached;
	
	public Library(){
		opMappingCached = getSynonymMap();
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
	 * Gets the synonym mapping, as array of
	 * elements like  { synonym, original name}
	 */
	public String[][] getSynonymMap() {
		return null;
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
				String name = mlist[i].getName();
				
				Class<?>[] clist = mlist[i].getParameterTypes();
				Class<?> returnType = mlist[i].getReturnType();
				
				int type;
				if (mlist[i].isAnnotationPresent(Predicate.class)) {
					if (returnType != Boolean.TYPE) {
						String m = "Method " + name + "is declared as Predicate but " +
						           "does not return a boolean.";
						getEngine().warn(m);
						continue;
					}
					type = PrimitiveInfo.PREDICATE;
				} else if (mlist[i].isAnnotationPresent(Functor.class)) {
					if (returnType != Term.class) {
						String m = "Method " + name + "is declared as Functor but " +
				                   "does not return a Term.";
						getEngine().warn(m);
						continue;
					}
					type = PrimitiveInfo.FUNCTOR;
				} else if (mlist[i].isAnnotationPresent(Directive.class))
					type = PrimitiveInfo.DIRECTIVE;
				else continue;
				
				int index = name.lastIndexOf('_');
				if (index != -1) {
					try {
						int arity = Integer.parseInt(name.substring(index + 1, name.length()));
						// check argument number
						if (clist.length == arity) {
							boolean valid = true;
							for (int j=0; j<arity; j++) {
								if (!(Term.class.isAssignableFrom(clist[j]))) {
									valid = false;
									break;
								}
							}
							if (valid) {
								String rawName = name.substring(0,index);
								String key = rawName + "/" + arity;
								PrimitiveInfo prim = new PrimitiveInfo(type, key, this, mlist[i], arity);
								tablePrimitives[type].add(prim);
								//
								// adding also or synonyms
								//
								String[] stringFormat = {"directive", "predicate", "functor"};
								if (opMappingCached != null) {
									for (int j = 0; j < opMappingCached.length; j++){
										String[] map = opMappingCached[j];
										if (map[2].equals(stringFormat[type]) && map[1].equals(rawName)) {
											key = map[0] + "/" + arity;
											prim = new PrimitiveInfo(type, key, this, mlist[i], arity);
											tablePrimitives[type].add(prim);
										}
									}
								}
							}
						}
					} catch (Exception ex) {}
				}
				
			}
			return tablePrimitives;
		} catch (Exception ex) {
			return null;
		}
	}
	
}