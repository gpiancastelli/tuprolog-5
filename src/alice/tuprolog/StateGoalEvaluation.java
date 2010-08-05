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

/**
 * 
 * @author Alex Benini
 */
public class StateGoalEvaluation extends State {
	
	public StateGoalEvaluation(EngineManager c) {
		this.c = c;
		stateName = "Eval";
	}
	
	@Override
	void doJob(Engine e) {
		if (e.currentContext.currentGoal.isPrimitive()) {
			// Get the primitive
			PrimitiveInfo primitive = e.currentContext.currentGoal.getPrimitive();
			try {
				e.nextState =
					(primitive.evalAsPredicate(e.currentContext.currentGoal)) ?
							c.GOAL_SELECTION : c.BACKTRACK;
			} catch (HaltException he) {
				e.nextState = c.END_HALT;
			} catch (Throwable t) {
				// TODO Manage exceptions in Prolog
				t.printStackTrace();
				e.nextState = c.END_HALT;
			}
			// Increment the demonstration steps counter
			e.nDemoSteps++;
		} else
			e.nextState = c.RULE_SELECTION;
	}
	
}