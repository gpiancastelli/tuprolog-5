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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Administration of primitive predicates.
 * @author Alex Benini
 */
public class PrimitiveManager {
	
	private Map<IPrimitives, List<PrimitiveInfo>> libHashMap;
	private Map<String, PrimitiveInfo> directiveHashMap;
	private Map<String, PrimitiveInfo> predicateHashMap;
	private Map<String, PrimitiveInfo> functorHashMap;
	
	private Prolog engine;
	
	public PrimitiveManager() {
		libHashMap        = new IdentityHashMap<IPrimitives, List<PrimitiveInfo>>();
		directiveHashMap  = new HashMap<String, PrimitiveInfo>();
		predicateHashMap  = new HashMap<String, PrimitiveInfo>();
		functorHashMap    = new HashMap<String, PrimitiveInfo>();
	}
	
	/**
	 * Configure this Manager
	 */
	void initialize(Prolog vm) {
		engine = vm;
		createPrimitiveInfo(new BuiltIn(vm));
	}
	
	void createPrimitiveInfo(IPrimitives src) {
		List<PrimitiveInfo>[] prims = src.getPrimitives();
		for (PrimitiveInfo p : prims[PrimitiveInfo.DIRECTIVE])
			directiveHashMap.put(p.getKey(), p);
		for (PrimitiveInfo p : prims[PrimitiveInfo.PREDICATE])
			predicateHashMap.put(p.getKey(), p);
		for (PrimitiveInfo p : prims[PrimitiveInfo.FUNCTOR])
			functorHashMap.put(p.getKey(), p);
		List<PrimitiveInfo> libraryPrimitives = new LinkedList<PrimitiveInfo>();
		libraryPrimitives.addAll(prims[PrimitiveInfo.DIRECTIVE]);
		libraryPrimitives.addAll(prims[PrimitiveInfo.PREDICATE]);
		libraryPrimitives.addAll(prims[PrimitiveInfo.FUNCTOR]);
		libHashMap.put(src, libraryPrimitives);
	}
	
	void deletePrimitiveInfo(IPrimitives src) {
		for (PrimitiveInfo p : libHashMap.remove(src)) {
			String k = p.invalidate();
			directiveHashMap.remove(k);
			predicateHashMap.remove(k);
			functorHashMap.remove(k);
		}
	}
	
	/**
	 * Identifies the term passed as argument.
	 *
	 * This involves identifying structs representing built-in
	 * predicates and functors, and setting up related structures
	 * and links.
	 *
	 * @param term the term to be identified
	 * @return term with the identified built-in directive
	 */
	public Term identifyDirective(Term term) {
		identify(term, PrimitiveInfo.DIRECTIVE);
		return term;
	}
	
	public boolean evalAsDirective(Struct d) {
		PrimitiveInfo pd = ((Struct) identifyDirective(d)).getPrimitive();
		if (pd != null) {
			try {
				return pd.evalAsDirective(d);
			} catch (InvocationTargetException ite) {
				Throwable t = ite.getTargetException();
				engine.warn("An exception occurred during the execution of the " +
						    d.getPredicateIndicator() + " directive:\n" + t.getMessage());
				return false;
			} catch (IllegalAccessException e) {
				engine.warn("Cannot access the method defining the" +
					        d.getPredicateIndicator() + " directive.\n");
				return false;
			}
		} else {
			engine.warn("The directive " + d.getPredicateIndicator() + " is unknown.");
			return false;
		}
	}
	
	public void identifyPredicate(Term term) {
		identify(term, PrimitiveInfo.PREDICATE);
	}
	
	public void identifyFunctor(Term term) {
		identify(term, PrimitiveInfo.FUNCTOR);
	}
	
	private void identify(Term term, int typeOfPrimitive) {
		if (term == null)
			return;
		term = term.getTerm();
		if (!(term instanceof Struct))
			return;
		Struct t = (Struct) term;
		
		int arity = t.getArity();
		String name = t.getName();
		//------------------------------------------
		if (name.equals(",") || name.equals("':-'") || name.equals(":-")) {
			for (int c = 0; c < arity; c++)
				identify( t.getArg(c), PrimitiveInfo.PREDICATE);
		} else
			for (int c = 0; c < arity; c++)
				identify( t.getArg(c), PrimitiveInfo.FUNCTOR);
		//------------------------------------------
		//log.debug("Identification "+t);
		PrimitiveInfo prim = null;
		String key = name + "/" + arity;
		
		switch (typeOfPrimitive) {
		case PrimitiveInfo.DIRECTIVE :
			prim = (PrimitiveInfo) directiveHashMap.get(key);	    		
			//log.debug("Assign predicate "+prim+" to "+t);
			break;
		case PrimitiveInfo.PREDICATE :
			prim = (PrimitiveInfo) predicateHashMap.get(key);	    		
			//log.debug("Assign predicate "+prim+" to "+t);
			break;
		case PrimitiveInfo.FUNCTOR :
			prim = (PrimitiveInfo) functorHashMap.get(key);
			//log.debug("Assign functor "+prim+" to "+t);
			break;
		}
		t.setPrimitive(prim);
	}
	
	Library getLibraryDirective(String name, int nArgs) {
		try {
			return (Library) ((PrimitiveInfo) directiveHashMap.get(name + "/" + nArgs)).getSource();			
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	Library getLibraryPredicate(String name, int nArgs) {
		try {
			return (Library) ((PrimitiveInfo) predicateHashMap.get(name + "/" + nArgs)).getSource();			
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	Library getLibraryFunctor(String name, int nArgs) {
		try {
			return (Library) ((PrimitiveInfo) functorHashMap.get(name + "/" + nArgs)).getSource();
		} catch (NullPointerException e) {
			return null;
		}
	}
	
}