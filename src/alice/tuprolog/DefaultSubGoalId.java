package alice.tuprolog;

/**
 * Identifier of single subGoal during the demo.
 * @author Alex Benini
 *
 */
public class DefaultSubGoalId implements SubGoalId {
	
	private SubGoalTree root;
	private int index;
	private DefaultSubGoalId parent; 
	
	DefaultSubGoalId(DefaultSubGoalId parent, SubGoalTree root, int index) {
		this.parent = parent;
		this.root = root;
		this.index = index;
	}
	
	DefaultSubGoalId getParent() {
		return parent;
	}
	
	SubGoalTree getRoot() {
		return root;
	}
	
	int getIndex() {
		return index;
	}
	
	
	
	public String toString() {
		return root.getChild(index).toString();
	}

}