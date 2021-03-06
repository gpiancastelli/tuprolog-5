package alice.tuprolog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SubGoalTree extends AbstractSubGoalTree implements Iterable<AbstractSubGoalTree> {
	
	private List<AbstractSubGoalTree> terms;
	
	public SubGoalTree() {
		terms = new ArrayList<AbstractSubGoalTree>();
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
		return terms.get(i);
	}

	@Override
	public Iterator<AbstractSubGoalTree> iterator() {
		return terms.iterator();
	}
	
	public int size() {
		return terms.size();
	}
	
	@Override
	public boolean isLeaf() {
		return false;
	}
	
	@Override
	public boolean isRoot() {
		return true;
	}
	
	@Override
	public String toString() {
		String result = " [ ";
		Iterator<AbstractSubGoalTree> i = terms.iterator();
		if (i.hasNext())
			result = result + i.next().toString();
		while (i.hasNext()) {
			result = result + " , " + i.next().toString();
		}
		return result + " ] ";
	}
	
}