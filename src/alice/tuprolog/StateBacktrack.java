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

import java.util.List;

/**
 * 
 * @author Alex Benini
 */
public class StateBacktrack extends State {
	
	public StateBacktrack(EngineManager c) {
		this.c = c;
		stateName = "Back";
	}
	
	@Override
	void doJob(Engine e) {
		ChoicePointContext curChoice = e.choicePointSelector.fetch();
		
		// verify ChoicePoint
		if (curChoice == null) {
			e.nextState = c.END_FALSE;
			Struct goal = e.currentContext.currentGoal;
			c.warn("The predicate " + goal.getPredicateIndicator() + " is unknown.");
			return;
		}
		e.currentAlternative = curChoice;
		
		// deunify variables and reload old goal
		e.currentContext = curChoice.executionContext;
		Term curGoal = e.currentContext.goalsToEval.backTo(curChoice.indexSubGoal).getTerm();
		if (!(curGoal instanceof Struct)) {
			e.nextState = c.END_FALSE;
			return;
		}
		e.currentContext.currentGoal = (Struct) curGoal;
		
		// ensure coherence in execution stack
		ExecutionContext curCtx = e.currentContext;
		OneWayList pointer = curCtx.trailingVars;
		OneWayList stopDeunify = curChoice.varsToDeunify;
		
		// Unchecked cast from Object, but safe (thanks, OneWayList!)
		@SuppressWarnings("unchecked")
		List<Var> varsToDeunify = (List<Var>) stopDeunify.getHead();
		
		Var.free(varsToDeunify);
		varsToDeunify.clear();
		SubGoalId fatherIndex;
		// bring parent contexts to a previous state in the demonstration
		do {
			// deunify variables in sibling contexts
			while (pointer != stopDeunify) {
				// Unchecked cast from Object, but safe (thanks, OneWayList!)
				@SuppressWarnings("unchecked")
				List<Var> varsList = (List<Var>) pointer.getHead();
				
				Var.free(varsList);
				pointer = pointer.getTail();
			}
			curCtx.trailingVars = pointer;
			if (curCtx.fatherCtx == null) break;
			stopDeunify = curCtx.fatherVarsList;
			fatherIndex = curCtx.fatherGoalId;
			curCtx = curCtx.fatherCtx;
			curGoal = curCtx.goalsToEval.backTo(fatherIndex).getTerm();
			if (!(curGoal instanceof Struct)) {
				e.nextState = c.END_FALSE;
				return;
			}
			curCtx.currentGoal = (Struct)curGoal;
			pointer = curCtx.trailingVars;
		} while (true);
		
		// set next state
		e.nextState = c.GOAL_EVALUATION;
	}
	
}