package alice.tuprolog.lib;

import alice.tuprolog.Functor;
import alice.tuprolog.Int;
import alice.tuprolog.Library;
import alice.tuprolog.Number;
import alice.tuprolog.Predicate;
import alice.tuprolog.Term;

public class MathLibrary extends Library {

	@Predicate("=:=/2")
	public boolean expressionEquality(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		Number val0n = (Number) val0;
		Number val1n = (Number) val1;
		if (val0n.isInteger() && val1n.isInteger())
			return val0n.intValue() == val1n.intValue();
		else 
			return val0n.doubleValue() == val1n.doubleValue();
	}
	
	@Predicate("=\\=/2")
	public boolean expressionInequality(Term arg0, Term arg1) {
		return !expressionEquality(arg0, arg1);
	}
	
	@Predicate(">/2")
	public boolean expressionGreaterThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return expressionGreaterThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}
	
	@Predicate("=</2")
	public boolean expressionLessOrEqualThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return !expressionGreaterThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}

	private boolean expressionGreaterThan(alice.tuprolog.Number num0, alice.tuprolog.Number num1) {
		if (num0.isInteger() && num1.isInteger()) {
			return num0.intValue() > num1.intValue();
		} else {
			return num0.doubleValue() > num1.doubleValue();
		}
	}
	
	@Predicate("</2")
	public boolean expressionLessThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return expressionLessThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}
	
	@Predicate(">=/2")
	public boolean expressionGreaterOrEqualThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return !expressionLessThan((alice.tuprolog.Number) val0, (alice.tuprolog.Number) val1);
	}

	private boolean expressionLessThan(alice.tuprolog.Number num0, alice.tuprolog.Number num1) {
		if (num0.isInteger() && num1.isInteger()) {
			return num0.intValue() < num1.intValue();
		} else {
			return num0.doubleValue() < num1.doubleValue();
		}
	}
	/*
	@Functor("+/1")
	public Term expressionPlus(Term arg0) {
		Term val0 = evalExpression(arg0);
		if (val0!=null && val0 instanceof Number)
			return val0;
		else
			return null;
	}
	*/
	@Functor("-/1")
	public Term expressionMinus(Term arg1) {
		Term val0 = evalExpression(arg1);
		if (val0!=null && val0 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			if (val0n instanceof Int)
				return new Int(val0n.intValue() * -1);
			else if (val0n instanceof alice.tuprolog.Double)
				return new alice.tuprolog.Double(val0n.doubleValue() * -1);
			else if (val0n instanceof alice.tuprolog.Long)
				return new alice.tuprolog.Long(val0n.longValue() * -1);
			else if (val0n instanceof alice.tuprolog.Float)
				return new alice.tuprolog.Float(val0n.floatValue() * -1);
			else
				return null;
		} else
			return null;
	}
	
	alice.tuprolog.Number getIntegerNumber(long num) {
		if (num > Integer.MIN_VALUE && num < Integer.MAX_VALUE)
			return new Int((int) num);
		else
			return new alice.tuprolog.Long(num);
	}
	
	@Functor("+/2")
	public Term expressionPlus(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0!=null && val1!=null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			if (val0n.isInteger() && (val1n.isInteger()))
				return getIntegerNumber(val0n.longValue() + val1n.longValue());
			else
				return new alice.tuprolog.Double(val0n.doubleValue() + val1n.doubleValue());
		} else
			return null;
	}
	
	@Functor("-/2")
	public Term expressionMinus(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0!=null && val1!=null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			if (val0n.isInteger() && (val1n.isInteger()))
				return getIntegerNumber(val0n.longValue() - val1n.longValue());
			else
				return new alice.tuprolog.Double(val0n.doubleValue() - val1n.doubleValue());
		} else
			return null;
	}
	
	@Functor("*/2")
	public Term expressionMultiply(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0!=null && val1!=null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			if (val0n.isInteger() && (val1n.isInteger()))
				return getIntegerNumber(val0n.longValue() * val1n.longValue());
			else
				return new alice.tuprolog.Double(val0n.doubleValue() * val1n.doubleValue());
		} else
			return null;
	}
	
	@Functor("//2")
	public Term expressionDiv(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			Number val0n = (Number) val0;
			Number val1n = (Number) val1;
			alice.tuprolog.Double result = new alice.tuprolog.Double(val0n.doubleValue()/val1n.doubleValue());
			if (val0n.isInteger() && val1n.isInteger())
				return getIntegerNumber(result.longValue());
			else
				return result;
		} else
			return null;
	}
	
	@Functor("///2")
	public Term expressionIntegerDiv(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && (val1 instanceof Number)) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return getIntegerNumber(val0n.longValue() / val1n.longValue());
		} else
			return null;
	}
	
	@Functor("**/2")
	public Term expressionPow(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			alice.tuprolog.Number val0n = (alice.tuprolog.Number) val0;
			alice.tuprolog.Number val1n = (alice.tuprolog.Number) val1;
			return new alice.tuprolog.Double(Math.pow(val0n.doubleValue(), val1n.doubleValue()));
		} else
			return null;
	}
	
	@Functor("sin/1")
	public Term sin(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.sin(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("cos/1")
	public Term cos(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.cos(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("exp/1")
	public Term exp(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.exp(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("atan/1")
	public Term atan(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.atan(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("log/1")
	public Term log(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.log(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("sqrt/1")
	public Term sqrt(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(Math.sqrt(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("abs/1")
	public Term abs(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long)
			return new alice.tuprolog.Int(Math.abs(((Number)val0).intValue()));
		if (val0 instanceof alice.tuprolog.Double || val0 instanceof alice.tuprolog.Float)
			return new alice.tuprolog.Double(Math.abs(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("sign/1")
	public Term sign(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long)
			return new alice.tuprolog.Double(((Number)val0).intValue()>0 ? 1.0: -1.0);
		if (val0 instanceof alice.tuprolog.Double || val0 instanceof alice.tuprolog.Float)
			return new alice.tuprolog.Double(((Number)val0).doubleValue()>0 ? 1.0: -1.0);
		return null;
	}
	
	@Functor("float_integer_part/1")
	public Term floatIntegerPart(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double((long)Math.rint(((Number)val0).doubleValue()));
		return null;
	}
	
	@Functor("float_fractional_part/1")
	public Term floatFractionalPart(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double fl = ((Number)val0).doubleValue();
			return new alice.tuprolog.Double(Math.abs(fl-Math.rint(fl)));
		}
		return null;
	}
	
	@Functor("float/1")
	public Term toFloat(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Double(((Number) val0).doubleValue());
		return null;
	}
	
	@Functor("floor/1")
	public Term floor(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new Int((int) Math.floor(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("round/1")
	public Term round(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new alice.tuprolog.Long(Math.round(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("truncate/1")
	public Term truncate(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new Int((int) Math.rint(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("ceiling/1")
	public Term ceiling(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number)
			return new Int((int) Math.ceil(((Number) val0).doubleValue()));
		return null;
	}
	
	@Functor("div/2")
	public Term div(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		if (val0 instanceof Number && val1 instanceof Number)
			return new alice.tuprolog.Int(((Number) val0).intValue() / ((Number) val1).intValue());
		return null;
	}
	
	@Functor("mod/2")
	public Term mod(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		if (val0 instanceof Number && val1 instanceof Number) {
			int x = ((Number) val0).intValue();
	        int y = ((Number) val1).intValue();
	        int f = new java.lang.Double(Math.floor((double) x / (double) y)).intValue();
	        return new Int(x - (f * y));
		}
		return null;
	}
	
	@Functor("rem/2")
	public Term rem(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		if (val0 instanceof Number && val1 instanceof Number)
			return new alice.tuprolog.Double(Math.IEEEremainder(((Number) val0).doubleValue(), ((Number) val1).doubleValue()));
		return null;
	}
	
	@Override
	public String getTheory() {
		return
		":- op(700, xfx, '=:='). \n" +
		":- op(700, xfx, '=\\='). \n" +
		":- op(700, xfx, '>'). \n" +
		":- op(700, xfx, '<'). \n" +
		":- op(700, xfx, '=<'). \n" +
		":- op(700, xfx, '>='). \n" +
		":- op(500, yfx, '+'). \n" +
		":- op(500, yfx, '-'). \n" +
		":- op(500, yfx, '/\\'). \n" +
		":- op(500, yfx, '\\/'). \n" +
		":- op(400, yfx, '*'). \n" +
		":- op(400, yfx, '/'). \n" +
		":- op(400, yfx, '//'). \n" +
		":- op(400, yfx, 'rem'). \n" +
		":- op(400, yfx, 'mod'). \n" +
		":- op(300, yfx, 'div'). \n" +
		":- op(200,  fx, 'sin'). \n" +
		":- op(200,  fx, 'cos'). \n" +
		":- op(200,  fx, 'sqrt'). \n" +
		":- op(200,  fx, 'atan'). \n" +
		":- op(200,  fx, 'exp'). \n" +
		":- op(200,  fx, 'log'). \n" +
		":- op(200, xfx, '**'). \n" +
		":- op(200,  fy, '-'). \n";
	}
	
}
