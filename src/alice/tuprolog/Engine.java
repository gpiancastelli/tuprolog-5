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
 * Core engine.
 * 
 * @author Alex Benini
 */
public class Engine {
	
	enum State {
		INIT,
		GOAL_EVALUATION,
		GOAL_SELECTION,
		RULE_SELECTION,
		BACKTRACK,
		FALSE,
		TRUE,
		TRUE_CP,
		HALT
	}
	
	private State state;
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
		this.query = query;
		mustStop = false;
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
	
	SolveInfo solve() {
		state = State.INIT;
		return run();
	}
	
	SolveInfo solveNext() {
		state = State.BACKTRACK;
		return run();
	}

	/** The Finite State Machine representing the engine's core. */
	private SolveInfo run() {
		while (state != State.FALSE && state != State.TRUE &&
				state != State.TRUE_CP && state != State.HALT) {
			if (mustStop) {
				state = State.FALSE;
				break;
			}
			String action = state.toString();
			switch (state) {
			case INIT:
				state = init();
				break;
			case GOAL_EVALUATION:
				state = goalEvaluation();
				break;
			case GOAL_SELECTION:
				state = goalSelection();
				break;
			case RULE_SELECTION:
				state = ruleSelection();
				break;
			case BACKTRACK:
				state = backtrack();
				break;
			}
			manager.spy(action, this);
		}
		List<Var> vars = new ArrayList<Var>();
		Struct goal = (Struct) startGoal.copyResult(goalVars, vars);
		return new SolveInfo(query, goal, state, vars);
	}
	
	/** Initial state of demonstration. */
	State init() {
		// prepare goal
		Map<Var, Var> goalVars = new LinkedHashMap<Var, Var>();
		startGoal = (Struct) query.copy(goalVars, 0);
		this.goalVars = goalVars.values();
		
		// initialize first executionContext
		ExecutionContext eCtx = new ExecutionContext(0);
		eCtx.goalsToEval = new SubGoalStore();
		eCtx.goalsToEval.load(ClauseInfo.extractBody(startGoal));
		eCtx.clause = (Struct) query;
		eCtx.depth = 0;
		
		// initialize VM environment
		initialize(eCtx);
		
		return State.GOAL_SELECTION;
	}
	
	State goalEvaluation() {
		if (!currentContext.currentGoal.isPrimitive())
			return State.RULE_SELECTION;
		State nextState = null;
		// get the primitive
		PrimitiveInfo primitive = currentContext.currentGoal.getPrimitive();
		try {
			nextState = primitive.evalAsPredicate(currentContext.currentGoal) ?
					State.GOAL_SELECTION : State.BACKTRACK;
		} catch (HaltException he) {
			nextState = State.HALT;
		} catch (Throwable t) {
			// TODO Manage exceptions in Prolog
			t.printStackTrace();
			nextState = State.HALT;
		}
		// increment the demonstration steps counter
		nDemoSteps++;
		return nextState;
	}
	
	State goalSelection() {
		Term curGoal = null;
		State nextState = null;
		while (curGoal == null) {
			curGoal = currentContext.goalsToEval.fetch();
			if (curGoal == null) {
				// demo termination
				if (currentContext.fatherCtx == null) {
					// verify ChoicePoint
					nextState = (choicePointSelector.existChoicePoint()) ?
							State.TRUE_CP : State.TRUE;
					break;
				}
				// Case: removing an execution context
				currentContext = currentContext.fatherCtx;
			} else {
				// Case: identify curGoal
				Term goal_app = curGoal.getTerm();
				if (!(goal_app instanceof Struct)) {
					nextState = State.FALSE;
					break;
				}
	
				// Code inserted to allow evaluation of meta-clause
				// such as p(X) :- X. When evaluating directly terms,
				// they are converted to execution of a call/1 predicate.
				// This enables the dynamic linking of built-ins for
				// terms coming from outside the demonstration context.
				if (curGoal != goal_app)
					curGoal = new Struct("call", goal_app);
	
				currentContext.currentGoal = (Struct) curGoal;
				nextState = State.GOAL_EVALUATION;
			}
		} // end while
		return nextState;
	}
	
	State ruleSelection() {
		// identify compatibleGoals and
		// ascertain whether we come from backtracking.
		Struct goal = currentContext.currentGoal;
		boolean fromBacktracking = true;
		ChoicePointContext alternative = currentAlternative;
		ClauseStore clauseStore;
		currentAlternative = null;
		if (alternative == null) {
			// from normal evaluation
			fromBacktracking = false;
			List<Var> varsList = new ArrayList<Var>();
			currentContext.trailingVars = new OneWayList<List<Var>>(varsList, currentContext.trailingVars);
			clauseStore = ClauseStore.build(goal, varsList, manager.find(goal));
			if (clauseStore == null)
				return State.BACKTRACK;
		} else
			clauseStore = alternative.compatibleGoals;
		
		// choose a rule from those potentially compatible
		ClauseInfo clause = clauseStore.fetch();
		
		// build ExecutionContext and ChoicePointContext
		ExecutionContext ec = new ExecutionContext(nDemoSteps++);
		ExecutionContext curCtx = currentContext;
		ec.clause = clause.getClause();
		// head and body with refresh variables (clause copied)
		clause.performCopy(ec.getId());
		ec.headClause = clause.getHeadCopy();
		ec.goalsToEval = new SubGoalStore();
		ec.goalsToEval.load(clause.getBodyCopy());
		// The following block encodes cut functionalities, and hardcodes the
		// special treatment that ISO Standard reserves for goal disjunction:
		// section 7.8.6.1 prescribes that ;/2 must be transparent to cut.
		ec.choicePointAfterCut = choicePointSelector.getPointer();
		if (alternative != null) {
			ChoicePointContext choicePoint = alternative;
			int depth = alternative.executionContext.depth;
			ec.choicePointAfterCut = choicePoint.prevChoicePointContext;
			Struct currentGoal = choicePoint.executionContext.currentGoal;
			while (currentGoal.getName().equals(";") && currentGoal.getArity() == 2) {
				if (choicePoint.prevChoicePointContext != null) {
					int distance = depth - choicePoint.prevChoicePointContext.executionContext.depth;
					while (distance == 0 && choicePoint.prevChoicePointContext != null) {
						ec.choicePointAfterCut = choicePoint.prevChoicePointContext.prevChoicePointContext;
						choicePoint = choicePoint.prevChoicePointContext;
					}
					if (distance == 1 && choicePoint.prevChoicePointContext != null) {
						ec.choicePointAfterCut = choicePoint.prevChoicePointContext.prevChoicePointContext;
						currentGoal = choicePoint.prevChoicePointContext.executionContext.currentGoal;
						choicePoint = choicePoint.prevChoicePointContext;
					} else
						break;
				} else
					break;
			}
		}
		
		Struct curGoal = curCtx.currentGoal;
		List<Var> unifiedVars = (List<Var>) currentContext.trailingVars.getHead();
		curGoal.unify(unifiedVars, unifiedVars, ec.headClause);
		
		ec.haveAlternatives = clauseStore.haveAlternatives();
		
		// cpc creation
		if (ec.haveAlternatives && !fromBacktracking) {
			ChoicePointContext cpc = new ChoicePointContext();
			cpc.compatibleGoals = clauseStore;
	//			c.saveLastTheoryStatus();
			cpc.executionContext = curCtx;
			cpc.indexSubGoal = curCtx.goalsToEval.getCurrentGoalId();
			cpc.varsToDeunify = currentContext.trailingVars;
			choicePointSelector.add(cpc);
		}
		// cpc destruction
		if (!ec.haveAlternatives && fromBacktracking) {
			choicePointSelector.removeUnusedChoicePoints();
		}
		
		ec.performTailRecursionOptimization(this);
		
		ec.saveParentState();
		ec.depth = currentContext.depth + 1;
		currentContext = ec;
		return State.GOAL_SELECTION;
	}
	
	State backtrack() {
		ChoicePointContext curChoice = choicePointSelector.fetch();
		
		// verify ChoicePoint
		if (curChoice == null) {
			Struct goal = currentContext.currentGoal;
			manager.warn("The predicate " + goal.getPredicateIndicator() + " is unknown.");
			return State.FALSE;
		}
		currentAlternative = curChoice;
		
		// deunify variables and reload old goal
		currentContext = curChoice.executionContext;
		Term curGoal = currentContext.goalsToEval.backTo(curChoice.indexSubGoal).getTerm();
		if (!(curGoal instanceof Struct))
			return State.FALSE;
		currentContext.currentGoal = (Struct) curGoal;
		
		// ensure coherence in execution stack
		ExecutionContext curCtx = currentContext;
		OneWayList<List<Var>> pointer = curCtx.trailingVars;
		OneWayList<List<Var>> stopDeunify = curChoice.varsToDeunify;
		List<Var> varsToDeunify = (List<Var>) stopDeunify.getHead();
		Var.free(varsToDeunify);
		varsToDeunify.clear();
		
		SubGoalId fatherIndex;
		// bring parent contexts to a previous state in the demonstration
		do {
			// deunify variables in sibling contexts
			while (pointer != stopDeunify) {
				Var.free(pointer.getHead());
				pointer = pointer.getTail();
			}
			curCtx.trailingVars = pointer;
			if (curCtx.fatherCtx == null)
				break;
			stopDeunify = curCtx.fatherVarsList;
			fatherIndex = curCtx.fatherGoalId;
			curCtx = curCtx.fatherCtx;
			curGoal = curCtx.goalsToEval.backTo(fatherIndex).getTerm();
			if (!(curGoal instanceof Struct))
				return State.FALSE;
			curCtx.currentGoal = (Struct)curGoal;
			pointer = curCtx.trailingVars;
		} while (true);
		
		return State.GOAL_EVALUATION;
	}
	
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