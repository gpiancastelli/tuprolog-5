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
 * Customized HashMap for storing clauses in the TheoryManager
 *
 * @author ivar.orstavik@hist.no
 */
class ClauseDatabase extends HashMap {

    void addFirst(String key, ClauseInfo d) {
        LinkedList family = (LinkedList) get(key);
        if (family == null)
            put(key, family = new LinkedList());
        family.addFirst(d);
    }

    void addLast(String key, ClauseInfo d) {
        LinkedList family = (LinkedList) get(key);
        if (family == null)
            put(key, family = new LinkedList());
        family.addLast(d);
    }

    LinkedList abolish(String key) {
        return (LinkedList) remove(key);
    }

    List getPredicates(String key) {
        LinkedList family = (LinkedList) get(key);
        if (family == null)
            return new LinkedList();
        return (List) family.clone();
    }

    public Iterator iterator() {
        return new CompleteIterator(this);
    }

    private static class CompleteIterator implements Iterator {
        Iterator values;
        Iterator workingList;

        public CompleteIterator(ClauseDatabase clauseDatabase) {
            values = clauseDatabase.values().iterator();
        }

        public boolean hasNext() {
            if (workingList != null && workingList.hasNext())
                return true;
            if (values.hasNext()) {
                workingList = ((List) values.next()).iterator();
                return hasNext(); //start again on next workingList
            }
            return false;
        }

        public Object next() {
            return workingList.next();
        }

        public void remove() {
            workingList.remove();
        }
    }

}