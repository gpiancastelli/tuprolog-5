package alice.tuprolog;

public class SubGoalStore {
	
	private SubGoalTree goals;
	
	private SubGoalTree commaStruct;
	private int index;
	
	private DefaultSubGoalId curSGId;
		
	public SubGoalStore() {
		commaStruct = goals = new SubGoalTree();
		index = 0;
		curSGId = null;
	}
	
	public boolean load(SubGoalTree subGoals) {
		commaStruct = goals = subGoals;
		return true;
	}
	
	/**
	 * Return ClauseStore to the i-th state.
	 */
	public Term backTo(SubGoalId identifier) {
		popSubGoal((DefaultSubGoalId) identifier);
		index--;
		return fetch();
	}
	
	public void pushSubGoal(SubGoalTree subGoals) {
		curSGId = new DefaultSubGoalId(curSGId, commaStruct, index);
		commaStruct = subGoals;
		index = 0;
	}
	
	private void popSubGoal(DefaultSubGoalId id) {
		commaStruct = id.getRoot();
		index = id.getIndex();
		curSGId = id.getParent();
	}
	
	/**
	 * Returns the clause to load.
	 */
	public Term fetch() {
		if (index >= commaStruct.size()) {
			if (curSGId == null) {
				return null;
			} else {
				popSubGoal(curSGId);
				return fetch();
			}
		} else {
			AbstractSubGoalTree s = commaStruct.getChild(index);
			index++;
			if (s instanceof SubGoalTree) {
				pushSubGoal( (SubGoalTree) s );
				return fetch();
			} else {
				return ( (SubGoalElement) s ).getValue();
			}
		}		
	}
	
	/**
	 * Index of the goal currently being executed.
	 */
	public SubGoalId getCurrentGoalId() {
		return new DefaultSubGoalId(curSGId, commaStruct, index);
	}
	
	public boolean haveSubGoals() {
		return index <= goals.size();
	}
	
	@Override
	public String toString() {
		return "goals: "+goals+" "+
		"index: "+index;
	}
	
	/*
	 * Methods for spyListeners
	 */
	
	public SubGoalTree getSubGoals() {
		return goals;
	}
	
	public int getIndexNextSubGoal() {
		return index;
	}

}