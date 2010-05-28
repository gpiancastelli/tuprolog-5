package alice.tuprolog.ui;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;
import alice.tuprolog.Var;
import alice.tuprolog.event.OutputEvent;
import alice.tuprolog.event.OutputListener;
import alice.tuprolog.event.SpyEvent;
import alice.tuprolog.event.SpyListener;
import alice.tuprolog.event.WarningEvent;
import alice.tuprolog.event.WarningListener;

public class CUIConsole extends Automaton implements OutputListener, SpyListener, WarningListener {

    BufferedReader  stdin;
    Prolog          engine;
    SolveInfo       info;

    static final String incipit =
        "tuProlog " + Prolog.getVersion() + " - DEIS, Università di Bologna a Cesena\n"+
        new java.util.Date();
       
    public CUIConsole(String[] args){

        if (args.length>1){
            System.err.println("args: { theory file }");
            System.exit(-1);
        }
        
        engine = new Prolog();
        stdin = new BufferedReader(new InputStreamReader(System.in));
        engine.addWarningListener(this);
        engine.addOutputListener(this);
        engine.addSpyListener(this);
        if (args.length>0) {
            try {
                engine.setTheory(new Theory(new FileInputStream(args[0])));
            } catch (InvalidTheoryException ex){
                System.err.println("invalid theory - line: "+ex.line);
                System.exit(-1);
            } catch (Exception ex){
                System.err.println("invalid theory.");
                System.exit(-1);
            }
        }
    }

    public void boot(){
        System.out.println(incipit);
        become("goalRequest");
    }

    public void goalRequest(){
        String goal="";
        while (goal.equals("")){
            System.out.print("\n?- ");
            try {
				goal=stdin.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        solveGoal(goal);
    }

    void solveGoal(String goal){
        try {
        	info = engine.solve(goal);
        	if (engine.isHalted())
        		System.exit(0);
        	if (!info.isSuccess()) {
        		System.out.println("no.");
        		become("goalRequest");
        	} else
        		if (!engine.hasOpenAlternatives()) {
        			String binds = info.toString();
        			if (binds.equals("")) {
        				System.out.println("yes.");
        			} else {
        				System.out.println(solveInfoToString(info) + "\nyes.");
        			}
        			become("goalRequest");
        		} else {
        			String bindings = solveInfoToString(info); 
        			System.out.print((bindings == "" ? "true" : bindings) + " ? ");
        			become("getChoice");
        		}
        } catch (MalformedGoalException ex){
            System.out.println("syntax error in goal:\n"+goal);
            become("goalRequest");
        }
    }
    
    private String solveInfoToString(SolveInfo result) {
    	String s = "";
    	try {
			for (Iterator i = result.getBindingVars().iterator(); i.hasNext();) {
				Var v = (Var) i.next();
				if (v != null && !v.isAnonymous() && v.isBound() && (!(v.getTerm() instanceof Var) || (!((Var) (v.getTerm())).getName().startsWith("_")))) {
					s += v.getName() + " / " + v.getTerm();
					if (i.hasNext())
						s += "\n";
				}
			}
		} catch (NoSolutionException e) {}
    	return s;
    }

    public void getChoice(){
        String choice="";
        try {
            while (true){
                choice = stdin.readLine();
                if (!choice.equals(";") && !choice.equals(""))
                    System.out.println("\nAction ( ';' for more choices, otherwise <return> ) ");
                else
                    break;
            }
        } catch (IOException ex){
        }
        if (!choice.equals(";")) {
		    System.out.println("yes.");
		    engine.solveEnd();
		    become("goalRequest");
		} else {
		    try {
		    	System.out.println();
		        info = engine.solveNext();
		        if (!info.isSuccess()){
		            System.out.println("no.");
		            become("goalRequest");
		        } else {
		        	String bindings = solveInfoToString(info); 
        			System.out.print((bindings == "" ? "true" : bindings) + " ? ");
		            become("getChoice");
		        }
		    } catch (Exception ex){
		        System.out.println("no.");
		        become("goalRequest");
		    }
		}
    }

    public void onOutput(OutputEvent e) {
        System.out.print(e.getMsg());
    }
    public void onSpy(SpyEvent e) {
        System.out.println(e.getMsg());
    }
    public void onWarning(WarningEvent e) {
        System.out.println(e.getMsg());
    }

    public static void main(String[] args){
        new Thread(new CUIConsole(args)).start();
    }
}
