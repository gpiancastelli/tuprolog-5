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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alex Benini
 */
public class Engine {
	
	State nextState;
	Term query;
	Struct startGoal;
	Collection<Var> goalVars;
	
	int nDemoSteps;
	
	ExecutionContext currentContext; 
	//ClauseStore clauseSelector;
	ChoicePointContext currentAlternative;
	
	ChoicePointStore choicePointSelector;
	
	boolean mustStop;
	EngineManager manager;
	
	public Engine(EngineManager manager, Term query) {
		this.manager = manager;		
		this.nextState = manager.INIT;
		this.query = query;
		this.mustStop = false;
	}
	
	void initialize(ExecutionContext eCtx) {
		currentContext = eCtx;
		choicePointSelector = new ChoicePointStore();
		nDemoSteps = 1;
		currentAlternative = null;
	}
	
	void mustStop() {
		mustStop = true;
	}
	
	/**
	 * Core of engine, Finite State Machine.
	 */
	StateEnd run() {
		String action;
		do {
			if (mustStop) {
				nextState = manager.END_FALSE;
				break;
			}
			action = nextState.toString();
			
			nextState.doJob(this);
			
			manager.spy(action, this);
			
		} while (!(nextState instanceof StateEnd));
		nextState.doJob(this);
		return (StateEnd) nextState;
	}
	
	void prepareGoal() {
		Map<Var, Var> goalVars = new LinkedHashMap<Var, Var>();
		startGoal = (Struct) query.copy(goalVars, 0);
		this.goalVars = goalVars.values();
	}
	
//	void cut() {
//		choicePointSelector.cut(currentContext.depth -1);
//	}
	
	@Override
	public String toString() {
		try {
			return "ExecutionStack: \n" + currentContext + "\n" +
			       "ChoicePointStore: \n" + choicePointSelector + "\n\n";
		} catch(Exception ex) {
			return "";
		}
	}
	
	/*
	 * Methods for spyListeners
	 */
	
	public Term getQuery() {
		return query;
	}
	
	public int getNumDemoSteps() {
		return nDemoSteps;
	}
	
	public List<ExecutionContext> getExecutionStack() {
		List<ExecutionContext> l = new ArrayList<ExecutionContext>();
		ExecutionContext t = currentContext;
		while (t != null) {
			l.add(t);
			t = t.fatherCtx;
		}
		return l;
	}
	
	public ChoicePointStore getChoicePointStore() {
		return choicePointSelector;
	}
	
}