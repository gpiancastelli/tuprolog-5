/*
 * Created on 1-ott-2005
 *
 */
package alice.tuprolog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import alice.tuprolog.event.LibraryEvent;
import alice.tuprolog.event.WarningEvent;

/**
 * @author Alex Benini
 *
 */
class LibraryManager {
	
	/* dynamically loaded built-in libraries */
	private List<Library> currentLibraries;
	
	/*  */
	private Prolog prolog;
	private TheoryManager theoryManager;
	private PrimitiveManager primitiveManager;
	
	
	LibraryManager(){
		currentLibraries = new ArrayList<Library>();
	}
	
	/**
	 * Configure this Manager
	 */
	void initialize(Prolog vm){
		prolog = vm;
		theoryManager = vm.getTheoryManager();
		primitiveManager = vm.getPrimitiveManager();
	}
	
	/**
	 * Loads a library.
	 *
	 * If a library with the same name is already present,
	 * a warning event is notified and the request is ignored.
	 *
	 * @param the name of the Java class containing the library to be loaded
	 * @return the reference to the Library just loaded
	 * @throws InvalidLibraryException if name is not a valid library
	 */
	public synchronized Library loadLibrary(String className) throws InvalidLibraryException {
		Library lib = null;
		try {
			lib = (Library) Class.forName(className).newInstance();
			String name = lib.getName();
			Library alib = getLibrary(name);
			if (alib != null) {
				if (prolog.isWarning()) {
					String msg = "library " + alib.getName() + " already loaded.";
					prolog.notifyWarning(new WarningEvent(prolog, msg));
				}
				return alib;
			}
		} catch (Exception ex){
			throw new InvalidLibraryException(className, -1, -1);
		}
		bindLibrary(lib);
		LibraryEvent ev = new LibraryEvent(prolog, lib.getName());
		prolog.notifyLoadedLibrary(ev);
		return lib;
	}   
	
	/**
	 * Loads a specific instance of a library.
	 *
	 * If a library of the same class is already present,
	 * a warning event is notified. Then, the current instance of
	 * that library is discarded, and the new instance gets loaded.
	 *
	 * @param lib the (Java class) name of the library to be loaded
	 * @throws InvalidLibraryException if name is not a valid library
	 */
	public synchronized void loadLibrary(Library lib) throws InvalidLibraryException {
		String name = lib.getName();
		Library alib = getLibrary(name);
		if (alib != null) {
			if (prolog.isWarning()) {
				String msg = "library " + alib.getName() + " already loaded.";
				prolog.notifyWarning(new WarningEvent(prolog, msg));
			}
			unloadLibrary(name);
		}
		bindLibrary(lib);
		LibraryEvent ev = new LibraryEvent(prolog, lib.getName());
		prolog.notifyLoadedLibrary(ev);
	}
	
	/**
	 * Gets the list of current libraries loaded
	 *
	 * @return the list of the library names
	 */
	public synchronized String[] getCurrentLibraries() {
		String[] libs = new String[currentLibraries.size()];
		for (int i = 0; i < libs.length; i++)
			libs[i] = ((Library) currentLibraries.get(i)).getName();
		return libs;
	}
	
	/**
	 * Unloads a previously loaded library
	 *
	 * @param name of the library to be unloaded
	 * @throws InvalidLibraryException if name is not a valid loaded library
	 */
	public synchronized void unloadLibrary(String name) throws InvalidLibraryException {
		boolean found = false;
		for (Iterator<Library> i = currentLibraries.iterator(); i.hasNext();) {
			Library library = i.next();
			if (library.getName().equals(name)) {
				found = true;
				i.remove();
				library.dismiss();
				primitiveManager.deletePrimitiveInfo(library);
				break;
			}
		}
		if (!found) {
			throw new InvalidLibraryException();
		}
		theoryManager.removeLibraryTheory(name);
		theoryManager.rebindPrimitives();
		LibraryEvent ev = new LibraryEvent(prolog, name);
		prolog.notifyUnloadedLibrary(ev);
	}
	
	
	/**
	 * Binds a library.
	 *
	 * @param lib is library object
	 * @return the reference to the Library just loaded
	 * @throws InvalidLibraryException if name is not a valid library
	 */
	private Library bindLibrary(Library lib) throws InvalidLibraryException {
		try {
			String name = lib.getName();
			lib.setEngine(prolog);
			currentLibraries.add(lib);
			// set primitives
			primitiveManager.createPrimitiveInfo(lib);
			// set theory
			String th = lib.getTheory();
			if (th != null) {
				theoryManager.consult(new Theory(th), false, name);
				theoryManager.solveTheoryGoal();
			}
			// in current theory there could be predicates and functors
			// which become built-in after library loading
			theoryManager.rebindPrimitives();
			//
			return lib;
		} catch (InvalidTheoryException ex) {
			//System.out.println("line "+ex.line+"  "+ex.pos);
			throw new InvalidLibraryException(lib.getName(),ex.line,ex.pos);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new InvalidLibraryException(lib.getName(),-1,-1);
		}
	}
	
	/**
	 * Gets the reference to a loaded library
	 *
	 * @param name the name of the library already loaded
	 * @return the reference to the library loaded, null if the library is
	 *         not found
	 */
	public synchronized Library getLibrary(String name) {
		for (Library library : currentLibraries)
			if (library.getName().equals(name))
				return library;
		return null;
	}
	
	
	public synchronized void onSolveBegin(Term g) {
		for (Library library : currentLibraries)
			library.onSolveBegin(g);
	}
	
	
	public synchronized void onSolveEnd() {
		for (Library library : currentLibraries)
			library.onSolveEnd();
	}
	
}