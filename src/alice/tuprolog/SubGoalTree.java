package alice.tuprolog;
import java.util.*;


public class SubGoalTree extends AbstractSubGoalTree {
	
	private ArrayList terms;
	
	public SubGoalTree() {
		terms = new ArrayList();
	}
	
	public void addChild(Term term) {
		SubGoalElement l = new SubGoalElement(term);
		terms.add(l);
	}
	
	public SubGoalTree addChild() {
		SubGoalTree r = new SubGoalTree();
		terms.add(r);
		return r;
	}
	
	public AbstractSubGoalTree getChild(int i) {
		return (AbstractSubGoalTree)terms.get(i);
	}
	
	public Iterator iterator() {
		return terms.iterator();
	}
	
	public int size() {
		return terms.size();
	}
	
	public boolean isLeaf() { return false; }
	public boolean isRoot() { return true; }
	
	public String toString() {
		String result = " [ ";
		Iterator i = terms.iterator();
		if (i.hasNext())
			result = result + ((AbstractSubGoalTree)i.next()).toString();
		while (i.hasNext()) {
			result = result + " , " + ((AbstractSubGoalTree)i.next()).toString();
		}
		return result + " ] ";
	}
	
}