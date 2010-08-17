package alice.tuprolog.lib;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import alice.tuprolog.Functor;
import alice.tuprolog.Int;
import alice.tuprolog.Library;
import alice.tuprolog.Number;
import alice.tuprolog.Predicate;
import alice.tuprolog.Term;

/*
 * Implementation note: when constructing a new BigDecimal, the preferred way
 * is to use BigDecimal(String) rather than BigDecimal(double), because the
 * results obtained by the latter can be "somewhat unpredictable" (as briefly
 * explained by the Java API documentation). No problem should arise when a
 * BigDecimal is created from an integer or a long integer number.
 */
public class StrictMathLibrary extends Library {
	
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
		else {
			BigDecimal val0b = new BigDecimal(val0n.toString());
			BigDecimal val1b = new BigDecimal(val1n.toString());
			return val0b.compareTo(val1b) == 0;
		}
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
		return expressionGreaterThan((Number) val0, (Number) val1);
	}
	
	@Predicate("=</2")
	public boolean expressionLessOrEqualThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return !expressionGreaterThan((Number) val0, (Number) val1);
	}

	private boolean expressionGreaterThan(Number num0, Number num1) {
		if (num0.isInteger() && num1.isInteger())
			return num0.intValue() > num1.intValue();
		else {
			BigDecimal big0 = new BigDecimal(num0.toString());
			BigDecimal big1 = new BigDecimal(num1.toString());
			return big0.compareTo(big1) == 1;
		}
	}
	
	@Predicate("</2")
	public boolean expressionLessThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return expressionLessThan((Number) val0, (Number) val1);
	}
	
	@Predicate(">=/2")
	public boolean expressionGreaterOrEqualThan(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 == null || val1 == null || !(val0 instanceof Number) || !(val1 instanceof Number))
			return false;
		return !expressionLessThan((Number) val0, (Number) val1);
	}

	private boolean expressionLessThan(Number num0, Number num1) {
		if (num0.isInteger() && num1.isInteger())
			return num0.intValue() < num1.intValue();
		else {
			BigDecimal big0 = new BigDecimal(num0.toString());
			BigDecimal big1 = new BigDecimal(num1.toString());
			return big0.compareTo(big1) == -1;
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
		if (val0 != null && val0 instanceof Number) {
			Number val0n = (Number) val0;
			if (val0n instanceof Int)
				return new Int(val0n.intValue() * -1);
			else if (val0n instanceof alice.tuprolog.Long)
				return new alice.tuprolog.Long(val0n.longValue() * -1);
			else if (val0n instanceof alice.tuprolog.Double) {
				BigDecimal val0b = new BigDecimal(val0n.doubleValue());
				val0b = val0b.negate();
				return new alice.tuprolog.Double(val0b.doubleValue());
			} else if (val0n instanceof alice.tuprolog.Float) {
				BigDecimal val0b = new BigDecimal(val0n.toString());
				val0b = val0b.negate();
				return new alice.tuprolog.Float(val0b.floatValue());
			} else
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
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			Number val0n = (Number) val0;
			Number val1n = (Number) val1;
			if (val0n.isInteger() && val1n.isInteger())
				return getIntegerNumber(val0n.longValue() + val1n.longValue());
			else {
				BigDecimal val0b = new BigDecimal(val0n.toString());
				BigDecimal val1b = new BigDecimal(val1n.toString());
				BigDecimal result = val0b.add(val1b);
				return new alice.tuprolog.Double(result.doubleValue());
			}
		} else
			return null;
	}
	
	@Functor("-/2")
	public Term expressionMinus(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			Number val0n = (Number) val0;
			Number val1n = (Number) val1;
			if (val0n.isInteger() && val1n.isInteger())
				return getIntegerNumber(val0n.longValue() - val1n.longValue());
			else {
				BigDecimal val0b = new BigDecimal(val0n.toString());
				BigDecimal val1b = new BigDecimal(val1n.toString());
				BigDecimal result = val0b.subtract(val1b);
				return new alice.tuprolog.Double(result.doubleValue());
			}
		} else
			return null;
	}
	
	@Functor("*/2")
	public Term expressionMultiply(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			Number val0n = (Number) val0;
			Number val1n = (Number) val1;
			if (val0n.isInteger() && val1n.isInteger())
				return getIntegerNumber(val0n.longValue() * val1n.longValue());
			else {
				BigDecimal val0b = new BigDecimal(val0n.toString());
				BigDecimal val1b = new BigDecimal(val1n.toString());
				BigDecimal result = val0b.multiply(val1b);
				return new alice.tuprolog.Double(result.doubleValue());
			}
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
			if (val0n.isInteger() && val1n.isInteger())
				return getIntegerNumber(val0n.longValue() / val1n.longValue());
			else {
				BigDecimal val0b = new BigDecimal(val0n.toString());
				BigDecimal val1b = new BigDecimal(val1n.toString());
				BigDecimal result = val0b.divide(val1b);
				return new alice.tuprolog.Double(result.doubleValue());
			}
		} else
			return null;
	}
	
	@Functor("///2")
	public Term expressionIntegerDiv(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			Number val0n = (Number) val0;
			Number val1n = (Number) val1;
			return getIntegerNumber(val0n.longValue() / val1n.longValue());
		} else
			return null;
	}
	
	@Functor("**/2")
	public Term expressionPow(Term arg0, Term arg1) {
		Term val0 = evalExpression(arg0);
		Term val1 = evalExpression(arg1);
		if (val0 != null && val1 != null && val0 instanceof Number && val1 instanceof Number) {
			Number val0n = (Number) val0;
			Number val1n = (Number) val1;
			double result = StrictMath.pow(val0n.doubleValue(), val1n.doubleValue());
			return new alice.tuprolog.Double(result);
		} else
			return null;
	}
	
	@Functor("sin/1")
	public Term sin(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double result = Math.sin(((Number) val0).doubleValue());
			return new alice.tuprolog.Double(result);
		}
		return null;
	}
	
	@Functor("cos/1")
	public Term cos(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double result = Math.cos(((Number) val0).doubleValue());
			return new alice.tuprolog.Double(result);
		}
		return null;
	}
	
	@Functor("exp/1")
	public Term exp(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double result = Math.exp(((Number) val0).doubleValue());
			return new alice.tuprolog.Double(result);
		}
		return null;
	}
	
	@Functor("atan/1")
	public Term atan(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double result = Math.atan(((Number) val0).doubleValue());
			return new alice.tuprolog.Double(result);
		}
		return null;
	}
	
	@Functor("log/1")
	public Term log(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double result = Math.log(((Number) val0).doubleValue());
			return new alice.tuprolog.Double(result);
		}
		return null;
	}
	
	@Functor("sqrt/1")
	public Term sqrt(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			double result = Math.sqrt(((Number) val0).doubleValue());
			return new alice.tuprolog.Double(result);
		}
		return null;
	}
	
	@Functor("abs/1")
	public Term abs(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long)
			return new alice.tuprolog.Int(Math.abs(((Number) val0).intValue()));
		if (val0 instanceof alice.tuprolog.Double || val0 instanceof alice.tuprolog.Float) {
			BigDecimal big0 = new BigDecimal(val0.toString());
			BigDecimal result = big0.abs();
			return new alice.tuprolog.Double(result.doubleValue());
		}
		return null;
	}
	
	@Functor("sign/1")
	public Term sign(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Int || val0 instanceof alice.tuprolog.Long)
			return new alice.tuprolog.Double(((Number) val0).intValue() > 0 ? 1.0: -1.0);
		if (val0 instanceof alice.tuprolog.Double || val0 instanceof alice.tuprolog.Float) {
			BigDecimal big0 = new BigDecimal(val0.toString());
			return new alice.tuprolog.Double((double) big0.signum());
		}
		return null;
	}
	
	@Functor("float_integer_part/1")
	public Term floatIntegerPart(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			BigDecimal big0 = new BigDecimal(val0.toString());
			long result = big0.toBigInteger().longValue();
			return new alice.tuprolog.Double(result);
		}
		return null;
	}
	
	@Functor("float_fractional_part/1")
	public Term floatFractionalPart(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			BigDecimal fl = new BigDecimal(val0.toString());
			BigDecimal result = fl.subtract(new BigDecimal(fl.toBigInteger())).abs();
			return new alice.tuprolog.Double(result.doubleValue());
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
		if (val0 instanceof Number) {
			BigDecimal big0 = new BigDecimal(val0.toString());
			// FIXME Not working as expected with -0.4 (and others?)
			BigDecimal result = big0.round(new MathContext(1, RoundingMode.FLOOR));
			return new Int(result.intValue());
		}
		return null;
	}
	
	@Functor("round/1")
	public Term round(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			BigDecimal big0 = new BigDecimal(val0.toString());
			// FIXME Not working as expected with -0.6 (also 0.6, I think)
			BigDecimal result = big0.round(new MathContext(1, RoundingMode.HALF_UP));
			return new alice.tuprolog.Long(result.longValue());
		}
		return null;
	}
	
	@Functor("truncate/1")
	public Term truncate(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			BigDecimal big0 = new BigDecimal(val0.toString());
			return new Int(big0.toBigInteger().intValue());
		}
		return null;
	}
	
	@Functor("ceiling/1")
	public Term ceiling(Term val) {
		Term val0 = evalExpression(val);
		if (val0 instanceof Number) {
			BigDecimal big0 = new BigDecimal(val0.toString());
			BigDecimal result = big0.round(new MathContext(1, RoundingMode.CEILING));
			return new Int(result.intValue());
		}
		return null;
	}
	
	@Functor("div/2")
	public Term div(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		if (val0 instanceof Number && val1 instanceof Number) {
			BigDecimal big0 = new BigDecimal(((Number) val0).intValue());
			BigDecimal big1 = new BigDecimal(((Number) val1).intValue());
			BigDecimal result = big0.divideToIntegralValue(big1, MathContext.DECIMAL128);
			return new Int(result.intValue());
		}
		return null;
	}
	
	@Functor("mod/2")
	public Term mod(Term v0, Term v1) {
		Term val0 = evalExpression(v0);
		Term val1 = evalExpression(v1);
		// FIXME Why avoiding BigDecimal?
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
		if (val0 instanceof Number && val1 instanceof Number) {
			double val0d = ((Number) val0).doubleValue();
			double val1d = ((Number) val1).doubleValue();
			return new alice.tuprolog.Double(Math.IEEEremainder(val0d, val1d));
		}
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
