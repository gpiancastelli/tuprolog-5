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
package alice.tuprolog.lib;

import static alice.tuprolog.Agent.loadText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import alice.tuprolog.Int;
import alice.tuprolog.Library;
import alice.tuprolog.Predicate;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;

/**
 * This class provides basic I/O predicates.
 *
 * Library/Theory Dependency: BasicLibrary
 */
public class IOLibrary extends Library {
	
	protected String inputStreamName = "stdin";
	protected InputStream inputStream = System.in;
	protected String outputStreamName = "stdout";
	protected OutputStream outputStream = System.out;
	private Random gen = new Random();
	
	
	public IOLibrary() {
		gen.setSeed(System.currentTimeMillis());
	}
	
	@Predicate("see/1")
	public boolean see(Term arg) throws Exception {
		Struct arg0 = (Struct) arg.getTerm();
		if (!arg0.isAtom())
			return false;
		if (inputStream != System.in)
			inputStream.close();
		if (arg0.getName().equals("stdin"))
			inputStream = System.in;
		else
			inputStream = new FileInputStream(((Struct) arg0).getName());
		inputStreamName = ((Struct) arg0).getName();
		return true;
	}
	
	@Predicate("seen/0")
	public boolean seen() throws Exception {
		if (inputStream != System.in) {
			inputStream.close();
			inputStream = System.in;
			inputStreamName = "stdin";
		}
		return true;
	}
	
	@Predicate("seeing/1")
	public boolean seeing(Term t) {
		return unify(t, new Struct(inputStreamName));
	}
	
	@Predicate("tell/1")
	public boolean tell(Term arg) throws Exception {
		Struct arg0 = (Struct) arg.getTerm();
		if (!arg0.isAtom())
			return false;
		if (outputStream != System.out)
			outputStream.close();
		if (arg0.getName().equals("stdout"))
			outputStream = System.out;
		else
			outputStream = new FileOutputStream(((Struct) arg0).getName());
		outputStreamName = ((Struct) arg0).getName();
		return true;
	}
	
	@Predicate("told/0")
	public boolean told() throws Exception {
		if (outputStream != System.out) {
			outputStream.close();
			outputStream = System.out;
			outputStreamName = "stdout";
		}
		return true;
	}
	
	@Predicate("telling/1")
	public boolean telling(Term arg0) {
		return unify(arg0, new Struct(outputStreamName));
	}
	
	@Predicate("put/1")
	public boolean put(Term arg) throws Exception {
		Struct arg0 = (Struct) arg.getTerm();
		if (!arg0.isAtom())
			return false;
		else {
			String ch = arg0.getName();
			if (ch.length() > 1)
				return false;
			else {
				if (outputStreamName.equals("stdout"))
					getEngine().stdOutput(ch);
				else
					outputStream.write((byte) ch.charAt(0));
				return true;
			}
		}
	}
	
	@Predicate("get0/1")
	public boolean get0(Term arg0) throws Exception {
		int ch = inputStream.read();
		if (ch == -1)
			return unify(arg0, new Int(-1));
		else
			return unify(arg0, new Struct(new Character((char) ch).toString()));
	}
	
	@Predicate("get/1")
	public boolean get(Term arg0) throws Exception {
		int ch = 0;
		do {
			ch = inputStream.read();
		} while (ch < 0x20 && ch >= 0);
		if (ch == -1)
			return unify(arg0, new Int(-1));
		else
			return unify(arg0, new Struct(new Character(((char) ch)).toString()));
	}
	
	@Predicate("tab/1")
	public boolean tab(alice.tuprolog.Number arg) throws Exception {
		int n = arg.intValue();
		if (outputStreamName.equals("stdout"))
			for (int i = 0; i < n; i++)
				getEngine().stdOutput(" ");
		else
			for (int i = 0; i < n; i++)
				outputStream.write(0x20);
		return true;
	}
	
	@Predicate("read/1")
	public boolean read(Term arg0) throws Exception {
		arg0 = arg0.getTerm();
		try {
			int ch = 0;
			
			boolean open_apices = false;
			//boolean just_open_apices = false;
			boolean open_apices2 = false;
			//boolean just_open_apices2 = false;
			
			String st = "";
			do {
				ch = inputStream.read();
				if (ch == -1)
					break;
				
				boolean can_add = true;
				
				if (ch=='\'') {
					if (!open_apices)
						open_apices = true;
					else
						open_apices = false;
				} else if (ch=='\"') {
					if (!open_apices2)
						open_apices2 = true;
					else
						open_apices2 = false;
				} else {
					if (ch=='.') {
						if (!open_apices && !open_apices2)
							break;
					}
				}
				
				
				if (can_add)
					st += new Character(((char) ch)).toString();
			} while (true);
			
			return unify(arg0, getEngine().toTerm(st));
		} catch (Exception ex) {
			return false;
		}
	}
	
	@Predicate("write/1")
	public boolean write(Term arg0) throws Exception {
		arg0 = arg0.getTerm();
		try {
			if (outputStreamName.equals("stdout"))
				getEngine().stdOutput(arg0.toString());
			else
				outputStream.write(arg0.toString().getBytes());
			return true;
		} catch (Exception ex) {
			return false;
		}
	}
	
	@Predicate("print/1")
	public boolean print(Term arg0) throws Exception {
		arg0 = arg0.getTerm();
		try {
			if (outputStreamName.equals("stdout"))
				getEngine().stdOutput(arg0.toStringWithoutApices());
			else
				outputStream.write(arg0.toStringWithoutApices().getBytes());
			return true;
		} catch (Exception ex){
			return false;
		}
	}
	
	@Predicate("nl/0")
	public boolean nl() throws Exception {
		if (outputStreamName.equals("stdout"))
			getEngine().stdOutput("\n");
		else
			outputStream.write('\n');
		return true;
	}
	
	/**
	 * reads a source text from a file.
	 * <p>
	 * It's useful used with agent predicate:
	 * text_from_file(File,Source), agent(Source).
	 */
	@Predicate("text_from_file/2")
	public boolean textFromFile(Term file_name, Term text) {
		Struct fileName = (Struct) file_name.getTerm();
		try {
			Struct goal = new Struct(loadText(
					((Struct) fileName).toStringWithoutApices()));
			return unify(text, goal);
		} catch (Exception ex) {
			//ex.printStackTrace();
			return false;
		}
	}
	
	// miscellaneous
	
	@Predicate("rand_float/1")
	public boolean randFloat(Term t) {
		return unify(t, new alice.tuprolog.Double(gen.nextFloat()));
	}
	
	@Predicate("rand_int/2")
	public boolean randInt(Term argNum, Term num) {
		alice.tuprolog.Number arg = (alice.tuprolog.Number) argNum.getTerm();
		return unify(num, new Int(gen.nextInt(arg.intValue())));
	}
	
	@Override
	public String getTheory() {
		return
		"consult(File) :- text_from_file(File,Text), add_theory(Text).\n" +
		"reconsult(File) :- text_from_file(File,Text), set_theory(Text).\n" +
		"solve_file(File,Goal) :- text_from_file(File,Text),text_term(Text,Goal),call(Goal).\n" +
		"agent_file(X)  :- text_from_file(X,Y),agent(Y).\n";
	}
	
}