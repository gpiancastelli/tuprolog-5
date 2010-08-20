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

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * 
 * @author Alex Benini
 */
public class EngineManager {
	
	private Prolog           mediator;
	private TheoryManager    theoryManager;
	private PrimitiveManager primitiveManager;
	private LibraryManager   libraryManager;
	
	/* Current environment */
	Engine env;
	/* Last environment used */
	private Engine last_env;
	/* Stack environments of nested solving */
	private LinkedList<Engine> stackEnv = new LinkedList<Engine>();
	
	private SolveInfo sinfo;
	
	/**
	 * Configure this Manager
	 */
	void initialize(Prolog vm) {
		mediator		 = vm;
		theoryManager    = vm.getTheoryManager();
		primitiveManager = vm.getPrimitiveManager();
		libraryManager   = vm.getLibraryManager();
	}
	
	void spy(String action, Engine env) {
		mediator.spy(action,env);
	}
	
	void warn(String message) {
		mediator.warn(message);
	}
	
	/**
	 * Solves a query
	 *
	 * @param g the term representing the goal to be demonstrated
	 * @return the result of the demonstration
	 * @see SolveInfo
	 */
	public SolveInfo solve(Term query) {
		try {
			query.resolveTerm();
			
			libraryManager.onSolveBegin(query);
			primitiveManager.identifyPredicate(query);
//			theoryManager.transBegin();
			
			freeze();
			env = new Engine(this, query);
			sinfo = env.solve();
			defreeze();
			
			if (!sinfo.hasOpenAlternatives())
				solveEnd();
			return sinfo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new SolveInfo(query);
		}
	}
	
	/**
	 * Gets next solution
	 *
	 * @return the result of the demonstration
	 * @throws NoMoreSolutionException if no more solutions are present
	 * @see SolveInfo
	 **/
	public synchronized SolveInfo solveNext() throws NoMoreSolutionException {
		if (hasOpenAlternatives()) {
			refreeze();
			sinfo = env.solveNext();
			defreeze();
			if (!sinfo.hasOpenAlternatives())
				solveEnd();
			return sinfo;
		} else
			throw new NoMoreSolutionException();
	}
	
	/**
	 * Halts current solve computation
	 */
	public void solveHalt() {
		env.mustStop();
	}
	
	/**
	 * Accepts current solution
	 */
	public void solveEnd() {
//		theoryManager.transEnd(sinfo.isSuccess());
//		theoryManager.optimize();
		libraryManager.onSolveEnd();
	}
	
	private void freeze() {
		if (env == null)
			return;
		try {
			if (stackEnv.getLast() == env)
				return;
		} catch(NoSuchElementException e) {}
		stackEnv.addLast(env);
	}
	
	private void refreeze() {
		freeze();
		env = last_env;    		
	}
	
	private void defreeze() {
		last_env = env;
		if (stackEnv.isEmpty())
			return;
		env = stackEnv.removeLast();
	}
	
	/*
	 * Utility functions for Finite State Machine
	 */
	
	List<ClauseInfo> find(Term t) {
		return theoryManager.find(t);
	}
	
	void identify(Term t) {
		primitiveManager.identifyPredicate(t);
	}
	
//	void saveLastTheoryStatus() {
//		theoryManager.transFreeze();
//	}
	
	void pushSubGoal(SubGoalTree goals) {
		env.currentContext.goalsToEval.pushSubGoal(goals);
	}
	
	void cut() {
		env.choicePointSelector.cut(env.currentContext.choicePointAfterCut);
	}
	
	ExecutionContext getCurrentContext() {
		return (env == null) ? null : env.currentContext;
	}
	
	/**
	 * Asks for the presence of open alternatives to be explored
	 * in current demonstration process.
	 *
	 * @return true if open alternatives are present
	 */
	boolean hasOpenAlternatives() {
		if (sinfo == null)
			return false;
		return sinfo.hasOpenAlternatives();
	}
	
	/**
	 * Checks if the demonstration process was stopped by an halt command.
	 * 
	 * @return true if the demonstration was stopped
	 */
	boolean isHalted() {
		if (sinfo == null)
			return false;
		return sinfo.isHalted();
	}
	
}