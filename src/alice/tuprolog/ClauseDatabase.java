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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Customized HashMap for storing clauses in the TheoryManager
 *
 * @author ivar.orstavik@hist.no
 */
class ClauseDatabase extends HashMap<String, LinkedList<ClauseInfo>> implements Iterable<ClauseInfo> {

	private static final long serialVersionUID = 5446149929214947819L;

	void addFirst(String key, ClauseInfo d) {
        LinkedList<ClauseInfo> family = get(key);
        if (family == null)
            put(key, family = new LinkedList<ClauseInfo>());
        family.addFirst(d);
    }

    void addLast(String key, ClauseInfo d) {
        LinkedList<ClauseInfo> family = get(key);
        if (family == null)
            put(key, family = new LinkedList<ClauseInfo>());
        family.addLast(d);
    }

    LinkedList<ClauseInfo> abolish(String key) {
        return remove(key);
    }

    @SuppressWarnings("unchecked")
	List<ClauseInfo> getPredicates(String key) {
        LinkedList<ClauseInfo> family = get(key);
        if (family == null)
            return new LinkedList<ClauseInfo>();
        // Unchecked warning: clone returns an object, the cast to
        // List<ClauseInfo> is needed. The cast is safe, because we
        // know to only have LinkedList<ClauseInfo> in the map.
        return (List<ClauseInfo>) family.clone();
    }

    public Iterator<ClauseInfo> iterator() {
        return new CompleteIterator(this);
    }

    private static class CompleteIterator implements Iterator<ClauseInfo> {
        Iterator<LinkedList<ClauseInfo>> values;
        Iterator<ClauseInfo> workingList;

        public CompleteIterator(ClauseDatabase clauseDatabase) {
            values = clauseDatabase.values().iterator();
        }

        public boolean hasNext() {
            if (workingList != null && workingList.hasNext())
                return true;
            if (values.hasNext()) {
                workingList = ((List<ClauseInfo>) values.next()).iterator();
                return hasNext(); // start again on next workingList
            }
            return false;
        }

        public ClauseInfo next() {
            return workingList.next();
        }

        public void remove() {
            workingList.remove();
        }
    }

}