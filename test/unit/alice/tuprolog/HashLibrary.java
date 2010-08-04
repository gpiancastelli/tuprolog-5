/*
 * Created on Nov 5, 2003
 * 
 * Copyright (C)aliCE team at deis.unibo.it
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
 *
 */
package alice.tuprolog;

import java.util.HashMap;
import java.util.Map;

public class HashLibrary extends Library {
	private Map<String, Term> dict;
		
	@Predicate("hashtable/0")
	public boolean hashtable() {
		dict = new HashMap<String, Term>();
		return true;
	} 
	
	@Predicate("put_data/2")
	public boolean putData(Term key, Term object) {
		dict.put(key.toString(), object);
		return true;
	}
	
	@Predicate("get_data/2")
	public boolean getData(Term key, Term res) {
		Term result = (Term) dict.get(key.toString());
		return unify(res, result);
	}
	
	@Predicate("remove_data/2")
	public boolean removeData(Term key) {
		dict.remove(key.toString());
		return true;
	}
}
