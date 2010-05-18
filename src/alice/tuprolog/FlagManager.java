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

import java.util.*;


/**
 * Administrator of flags declared
 * @author Alex Benini
 */
class FlagManager {
	
	/* flag list */
	private ArrayList flags;
	
	/**  mediator owner of the manager*/
	protected Prolog  mediator;
	
	FlagManager() {
		flags = new ArrayList();
	}
	
	/**
	 * Config this Manager
	 */
	public void initialize(Prolog vm) {
		mediator = vm;
	}
	
	
	/**
	 * Defines a new flag
	 */
	public boolean defineFlag(String name, Struct valueList, Term defValue, boolean modifiable, String libName) {
		flags.add(new Flag(name, valueList, defValue, modifiable, libName));
		return true;
	}
	
	public boolean setFlag(String name, Term value) {
		java.util.Iterator it = flags.iterator();
		while (it.hasNext()) {
			Flag flag = (Flag) it.next();
			if (flag.getName().equals(name)) {
				if (flag.isModifiable() && flag.isValidValue(value)) {
					flag.setValue(value);
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}    
	
	public Struct getPrologFlagList() {
		Struct flist = new Struct();
		java.util.Iterator it = flags.iterator();
		while (it.hasNext()) {
			Flag fl = (Flag) it.next();
			flist = new Struct(new Struct("flag", new Struct(fl.getName()), fl.getValue()), flist);
		}
		return flist;
	}
	
	public Term getFlag(String name) {
		java.util.Iterator it = flags.iterator();
		while (it.hasNext()) {
			Flag fl = (Flag) it.next();
			if (fl.getName() == name) {
				return fl.getValue();
			}
		}
		return null;
	}
	
}
