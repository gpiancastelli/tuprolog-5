package alice.tuprolog;

import alice.tuprolog.event.LibraryEvent;
import alice.tuprolog.event.PrologEventAdapter;
import alice.tuprolog.event.QueryEvent;
import alice.tuprolog.event.TheoryEvent;

public class TestPrologEventAdapter extends PrologEventAdapter {
	String firstMessage = "";
	String secondMessage = "";
    
    public void theoryChanged(TheoryEvent ev) {
    	firstMessage = ev.getOldTheory().toString();
    	secondMessage = ev.getNewTheory().toString();
    }
    
    public void newQueryResultAvailable(QueryEvent ev) {
    	firstMessage = ev.getSolveInfo().getQuery().toString();
    	secondMessage = ev.getSolveInfo().toString();
    }
    
    public void libraryLoaded(LibraryEvent ev) {
    	firstMessage = ev.getLibraryName();
    }

    public void libraryUnloaded(LibraryEvent ev) {
    	firstMessage = ev.getLibraryName();
    }
}
