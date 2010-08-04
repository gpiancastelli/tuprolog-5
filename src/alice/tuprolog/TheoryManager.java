/*
 * tuProlog - Copyright (C) 2001-2007  aliCE team at deis.unibo.it
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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * This class defines the Theory Manager who manages the clauses/theory often
 * referred to as the Prolog database. The theory (as a set of clauses) are
 * stored in the ClauseDatabase which in essence is a HashMap grouped by
 * functor/arity.
 * <p/>
 * The TheoryManager functions logically, as prescribed by ISO Standard 7.5.4
 * section. The effects of assertions and retractions shall not be undone if
 * the program subsequently backtracks over the assert or retract call, as
 * prescribed by ISO Standard 7.7.9 section.
 * <p/>
 * To use the TheoryManager one should primarily use the methods assertA,
 * assertZ, consult, retract, abolish and find.
 * <p/>
 *
 * rewritten by:
 * @author ivar.orstavik@hist.no
 *
 * @see Theory
 */
public class TheoryManager {

    private ClauseDatabase dynamicDBase;
    private ClauseDatabase staticDBase;
    private Prolog engine;
    private PrimitiveManager primitiveManager;
    private Stack<Struct> startGoalStack;
    Theory lastConsultedTheory;

    void initialize(Prolog vm) {
        dynamicDBase = new ClauseDatabase();
        staticDBase = new ClauseDatabase();
        lastConsultedTheory = new Theory();
        engine = vm;
        primitiveManager = engine.getPrimitiveManager();
    }

    /**
     * Inserting of a clause at the head of the database.
     */
    void assertA(Struct clause, boolean dyn, String libName, boolean backtrackable) {
        ClauseInfo d = new ClauseInfo(toClause(clause), libName);
        String key = d.getHead().getPredicateIndicator();
        if (dyn) {
        	dynamicDBase.addFirst(key, d);
        	if (staticDBase.containsKey(key)) {
            	engine.warn("A static predicate with signature " + key + " has been overriden.");
        	}
        } else
        	staticDBase.addFirst(key, d);
        engine.spy("INSERTA: " + d.getClause() + "\n");
    }

    /**
     * Inserting of a clause at the end of the database.
     */
    void assertZ(Struct clause, boolean dyn, String libName, boolean backtrackable) {
        ClauseInfo d = new ClauseInfo(toClause(clause), libName);
        String key = d.getHead().getPredicateIndicator();
        if (dyn) {
        	dynamicDBase.addLast(key, d);
        	if (staticDBase.containsKey(key)) {
        		engine.warn("A static predicate with signature " + key + " has been overriden.");
        	}
        } else
        	staticDBase.addLast(key, d);
        engine.spy("INSERTZ: " + d.getClause() + "\n");
    }

    /**
     * Removing from database the first clause with head unifying with clause.
     */
    ClauseInfo retract(Struct cl) {
    	Struct clause = toClause(cl);
    	Struct struct = ((Struct) clause.getArg(0));
        LinkedList<ClauseInfo> family = dynamicDBase.get(struct.getPredicateIndicator());
        if (family == null)
            return null;
        for (Iterator<ClauseInfo> it = family.iterator(); it.hasNext();) {
            ClauseInfo d = it.next();
            if (clause.match(d.getClause())) {
                it.remove();
                engine.spy("DELETE: " + d.getClause() + "\n");
                return new ClauseInfo(d.getClause(), null);
            }
        }
        return null;
    }

    /**
     * Removing from database all the clauses corresponding to the
     * predicate indicator passed as a parameter.
     */
    boolean abolish(Struct pi) {
    	String key = pi.toStringWithoutApices();
        LinkedList<ClauseInfo> abolished = dynamicDBase.abolish(key);
        if (abolished != null)
        	engine.spy("ABOLISHED: " + key + " number of clauses = " + abolished.size() + "\n");
        return true;
    }

    /**
     * Returns a family of clauses with functor and arity equals
     * to the functor and arity of the term passed as a parameter.
     */
    List<ClauseInfo> find(Term headt) {
        if (headt instanceof Struct) {
        	String key = ((Struct) headt).getPredicateIndicator();
        	List<ClauseInfo> list = dynamicDBase.getPredicates(key);
        	if (list.isEmpty())
        		list = staticDBase.getPredicates(key);
        	return list;
        }
        	
        if (headt instanceof Var){
//            List l = new LinkedList();
//            for (Iterator iterator = clauseDBase.iterator(); iterator.hasNext();) {
//                ClauseInfo ci =  (ClauseInfo) iterator.next();
//                if(ci.dynamic)
//                    l.add(ci);
//            }
//            return l;
            throw new RuntimeException();
        }
        return new LinkedList<ClauseInfo>();
    }

    /**
     * Consults a theory.
     *
     * @param theory        theory to add
     * @param dynamicTheory if it is true, then the clauses are marked as dynamic
     * @param libName       if it not null, then the clauses are marked to belong to the specified library
     */
    void consult(Theory theory, boolean dynamicTheory, String libName) throws InvalidTheoryException {
    	startGoalStack = new Stack<Struct>();

		// iterate all clauses in theory and assert them
    	try {
			for (Iterator<Term> it = theory.iterator(engine); it.hasNext();) {
				Struct d = (Struct) it.next();
				if (!runDirective(d))
					assertZ(d, dynamicTheory, libName, true);
			}
    	} catch (InvalidTermException e) {
    		throw new InvalidTheoryException(e.getMessage());
    	}
		
		if (libName == null)
			//lastConsultedTheory.append(theory);
			lastConsultedTheory = theory;
	}

    /**
     * Binds clauses in the database with the corresponding
     * primitive predicate, if any.
     */
    void rebindPrimitives() {
    	for (ClauseInfo d : dynamicDBase)
    		for (AbstractSubGoalTree e : d.getBody()) {
            	Term t = ((SubGoalElement) e).getValue();
            	primitiveManager.identifyPredicate(t);
            }
    }

    /**
     * Clears the clause database.
     */
    void clear() {
    	dynamicDBase = new ClauseDatabase();
    }

    /**
     * Remove all the clauses of library theory.
     */
    void removeLibraryTheory(String libName) {
        for (Iterator<ClauseInfo> allClauses = staticDBase.iterator(); allClauses.hasNext();) {
            ClauseInfo d = allClauses.next();
            if (d.libName != null && libName.equals(d.libName))
                allClauses.remove();
        }
    }

    private boolean runDirective(Struct c) {
        if ("':-'".equals(c.getName()) || ":-".equals(c.getName()) && c.getArity() == 1 && c.getTerm(0) instanceof Struct) {
            Struct dir = (Struct) c.getTerm(0);
            return primitiveManager.evalAsDirective(dir);
        } else
        	return false;
    }

    /**
     * Gets a clause from a generic Term.
     */
    private Struct toClause(Struct t) {
    	// TODO bad, slow way of cloning. requires approximately twice the time necessary
        t = (Struct) Term.createTerm(t.toString(), this.engine.getOperatorManager());
        if (!t.isClause())
            t = new Struct(":-", t, new Struct("true"));
        primitiveManager.identifyPredicate(t);
        return t;
    }

    void solveTheoryGoal() {
        Struct s = null;
        while (!startGoalStack.empty()) {
            s = (s == null) ?
                    (Struct) startGoalStack.pop() :
                    new Struct(",", (Struct) startGoalStack.pop(), s);
        }
        if (s != null) {
            try {
                engine.solve(s);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Add a goal eventually defined by last parsed theory.
     */
    void addStartGoal(Struct g) {
        startGoalStack.push(g);
    }

    /**
     * Saves the database on a output stream.
     */
    boolean save(OutputStream os, boolean onlyDynamic) {
        try {
            new DataOutputStream(os).writeBytes(getTheory(onlyDynamic));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Gets current theory.
     *
     * @param onlyDynamic if true, fetches only dynamic clauses
     */
    public String getTheory(boolean onlyDynamic) {
        StringBuilder buffer = new StringBuilder();
        for (ClauseInfo d : dynamicDBase)
            buffer.append(d.toString(engine.getOperatorManager())).append("\n");
        if (!onlyDynamic)
        	for (ClauseInfo d : staticDBase)
                buffer.append(d.toString(engine.getOperatorManager())).append("\n");
        return buffer.toString();
    }

    /**
     * Gets last consulted theory.
     *
     * @return last theory
     */
    Theory getLastConsultedTheory() {
        return lastConsultedTheory;
    }

}