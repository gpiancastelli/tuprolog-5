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
import java.lang.reflect.Method;

/**
 * Primitive class referring to a built-in predicate or functor.
 *
 * @see Struct
 */
public class PrimitiveInfo {
	
	enum Type {
		DIRECTIVE,
		PREDICATE,
		FUNCTOR
	}
	
	private Type type;
	/** method to be call when evaluating the built-in*/
	private Method method;
	/** library object where the built-in is defined */
	private IPrimitives source;
	/** for optimization purposes */
	private Term[] primitive_args;
	private String primitive_key;
	
	public PrimitiveInfo(Type type, String key, Library lib, Method m, int arity) throws NoSuchMethodException {
		if (m == null)
			throw new NoSuchMethodException();
		this.type = type;
		primitive_key = key;
		source = lib;
		method = m;
		primitive_args = new Term[arity];
	}
	
	/**
	 * Method to invalidate primitives. It's called just mother library removed.
	 */
	public String invalidate() {
		String key = primitive_key;
		primitive_key = null;
		return key;
	}
	
	public String getKey() {
		return primitive_key;
	}
	
	public boolean isDirective() {
		return (type == Type.DIRECTIVE);
	}
	
	public boolean isFunctor() {
		return (type == Type.FUNCTOR);
	}
	
	public boolean isPredicate() {
		return (type == Type.PREDICATE);
	}
	
	public Type getType() {
		return type;
	}
	
	public IPrimitives getSource() {
		return source;
	}
	
	/**
	 * Evaluates the primitive as a directive.
	 * @return true if the directive had success, false otherwise 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException
	 */
	public boolean evalAsDirective(Struct g) throws IllegalAccessException, InvocationTargetException {
		for (int i = 0; i < primitive_args.length; i++)
			primitive_args[i] = g.getTerm(i);
		return ((Boolean) method.invoke(source, (Object[]) primitive_args)).booleanValue();
	}
	
	/**
	 * Evaluates the primitive as a predicate.
	 * @throws Exception if invocation primitive failure
	 */
	public boolean evalAsPredicate(Struct g) throws Throwable {
		for (int i = 0; i < primitive_args.length; i++)
			primitive_args[i] = g.getArg(i);
		try {
			return ((Boolean) method.invoke(source, (Object[]) primitive_args)).booleanValue();
		} catch (InvocationTargetException e) {
			// throw new Exception(e.getCause());
			throw e.getCause();
		}
	}
	
	/**
	 * Evaluates the primitive as a functor.
	 */
	public Term evalAsFunctor(Struct g) {
		try {
			for (int i = 0; i < primitive_args.length; i++)
				primitive_args[i] = g.getTerm(i);
			return ((Term) method.invoke(source, (Object[]) primitive_args));
		} catch (Exception ex) {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return "[ primitive: method " + method.getName() + " - " +
		       primitive_args + " - N args: " + primitive_args.length +
		       " - " + source.getClass().getName() + " ]\n";
	}
	
}