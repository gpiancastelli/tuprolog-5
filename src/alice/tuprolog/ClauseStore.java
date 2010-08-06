package alice.tuprolog;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * A list of clauses belonging to the same family as a goal. A family is
 * composed by clauses with the same functor and arity.
 */
public class ClauseStore {
	
	private OneWayList<ClauseInfo> clauses;
	private Term goal;
	private List<Var> vars;
	private boolean haveAlternatives;
	
	private ClauseStore(Term goal, List<Var> vars) {
		this.goal = goal;
		this.vars = vars;
		clauses = null;
	}
	
	/**
	 * Load a clause family.
	 * @param familyClauses
	 */
	public static ClauseStore build(Term goal, List<Var> vars, List<ClauseInfo> familyClauses) {
		ClauseStore clauseStore = new ClauseStore(goal, vars);
		clauseStore.clauses = OneWayList.transform(familyClauses);
		if (clauseStore.clauses == null || !clauseStore.existCompatibleClause())
			return null;
		return clauseStore;
	}
	
	/**
	 * Return the clause to load.
	 */
	public ClauseInfo fetch() {
		if (clauses == null)
			return null;
		deunify(vars);
		if (!checkCompatibility(goal))
			return null;
		ClauseInfo clause = (ClauseInfo) clauses.getHead();
		clauses = clauses.getTail();
		haveAlternatives = checkCompatibility(goal);
		return clause;
	}
	
	public boolean haveAlternatives() {
		return haveAlternatives;
	}
	
	/**
	 * Verify if there is a term in compatibleGoals compatible with goal. 
	 * @param goal
	 * @param compGoals
	 * @return true if compatible or false otherwise.
	 */
	protected boolean existCompatibleClause() {
		List<Term> saveUnifications = deunify(vars);
		boolean found = checkCompatibility(goal);
		reunify(vars, saveUnifications);
		return found;
	}
	
	/**
	 * Save bindings of variables to deunify
	 * @param varsToDeunify
	 * @return binding of variables
	 */
	private List<Term> deunify(List<Var> varsToDeunify) {
		List<Term> saveUnifications = new ArrayList<Term>();
		// temporarily deunifying variables
		for (Var v : varsToDeunify) {
			saveUnifications.add(v.getLink());
			v.free();
		}
		return saveUnifications;
	}
	
	/**
	 * Restore previous unifications into variables.
	 * @param varsToReunify
	 * @param saveUnifications
	 */
	private void reunify(List<Var> varsToReunify, List<Term> saveUnifications) {
		int size = varsToReunify.size();
		ListIterator<Var> it1 = varsToReunify.listIterator(size);
		ListIterator<Term> it2 = saveUnifications.listIterator(size);
		// Only the first occurrence of a variable gets its binding saved;
		// following occurrences get a null instead. So, to avoid clashes
		// between those values, and avoid random variable deunification,
		// the reunification is made starting from the end of the list.
		while (it1.hasPrevious())
			it1.previous().setLink(it2.previous());
	}
	
	/**
	 * Verify if a clause exists that is compatible with goal.
	 * As a side effect, clauses that are not compatible get
	 * discarded from the currently examined family.
	 * @param goal
	 */
	private boolean checkCompatibility(Term goal) {
		if (clauses == null) return false;
		ClauseInfo clause = null;
		do {
			clause = (ClauseInfo) clauses.getHead();
			if (goal.match(clause.getHead())) return true;
			clauses = clauses.getTail();
		} while (clauses != null);
		return false;
	}
	
	@Override
	public String toString() {
		return "clauses: "+clauses+"\n"+
		"goal: "+goal+"\n"+
		"vars: "+vars+"\n";
	}
	
	/*
	 * Methods for spyListeners
	 */
	
	public List<ClauseInfo> getClauses() {
		List<ClauseInfo> l = new ArrayList<ClauseInfo>();
		OneWayList<ClauseInfo> t = clauses;
		while (t != null) {
			l.add(t.getHead());
			t = t.getTail();
		}
		return l;
	}
	
	public Term getMatchGoal() {
		return goal;
	}
	
	public List<Var> getVarsForMatch() {
		return vars;
	}
	
}