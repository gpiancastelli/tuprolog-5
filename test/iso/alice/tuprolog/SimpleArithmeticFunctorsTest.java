package alice.tuprolog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

public class SimpleArithmeticFunctorsTest {

    Prolog engine;

    @Before
    public void setUp() {
        engine = new Prolog();
    }
    
    @Test public void test0() throws PrologException {
        SolveInfo solution = engine.solve("X is '+'(7, 35).");
        assertEquals(Term.createTerm("42"), solution.getTerm("X"));
    }

    @Test public void test1() throws PrologException {
        SolveInfo solution = engine.solve("X is '+'(0, 3+11).");
        assertEquals(Term.createTerm("14"), solution.getTerm("X"));
    }

    @Test public void test2() throws PrologException {
        SolveInfo solution = engine.solve("X is '+'(0, 3.2+11).");
        assertEquals(Term.createTerm("14.2"), solution.getTerm("X"));
    }

    @Test public void test3() throws PrologException {
        SolveInfo solution = engine.solve("X is '+'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test4() throws PrologException {
        SolveInfo solution = engine.solve("X is '+'(foo, 77).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test public void test5() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(7).");
        assertEquals(Term.createTerm("-7"), solution.getTerm("X"));
    }

    @Test public void test6() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(3-11).");
        assertEquals(Term.createTerm("8"), solution.getTerm("X"));
    }

    @Test public void test7() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(3.2-11).");
        assertEquals(Term.createTerm("7.8"), solution.getTerm("X"));
    }

    @Test public void test8() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test9() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test public void test10() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(7, 35).");
        assertEquals(Term.createTerm("-28"), solution.getTerm("X"));
    }

    @Test public void test11() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(20, 3+11).");
        assertEquals(Term.createTerm("6"), solution.getTerm("X"));
    }

    @Test public void test12() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(0, 3.2+11).");
        assertEquals(Term.createTerm("-14.2"), solution.getTerm("X"));
    }

    @Test public void test13() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test14() throws PrologException {
        SolveInfo solution = engine.solve("X is '-'(foo, 77).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test public void test15() throws PrologException {
        SolveInfo solution = engine.solve("X is '*'(7, 35).");
        assertEquals(Term.createTerm("245"), solution.getTerm("X"));
    }

    @Test public void test16() throws PrologException {
        SolveInfo solution = engine.solve("X is '*'(0, 3+11).");
        assertEquals(Term.createTerm("0"), solution.getTerm("X"));
    }

    @Test(expected=AssertionError.class)
    public void test17() throws PrologException {
        SolveInfo solution = engine.solve("X is '*'(1.5, 3.2+11).");
        assertEquals(Term.createTerm("21.3"), solution.getTerm("X"));
    }

    @Test public void test18() throws PrologException {
        SolveInfo solution = engine.solve("X is '*'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test19() throws PrologException {
        SolveInfo solution = engine.solve("X is '*'(foo, 77).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test public void test20() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(7, 35).");
        assertEquals(Term.createTerm("0"), solution.getTerm("X"));
    }

    @Test public void test21() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(7.0, 35).");
        assertEquals(Term.createTerm("0.2"), solution.getTerm("X"));
    }

    @Test public void test22() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(140, 3+11).");
        assertEquals(Term.createTerm("10"), solution.getTerm("X"));
    }

    @Test(expected=AssertionError.class)
    public void test23() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(20.164, 3.2+11).");
        assertEquals(Term.createTerm("1.42"), solution.getTerm("X"));
    }

    @Test public void test24() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(7, -3).");
        assertNotNull(solution.getTerm("X"));
    }

    @Test public void test25() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(-7, 3).");
        assertNotNull(solution.getTerm("X"));
    }

    @Test public void test26() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test27() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(foo, 77).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test(expected=AssertionError.class)
    public void test28() throws PrologException {
        SolveInfo solution = engine.solve("X is '/'(3, 0).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(zero_divisor)
    }

    @Test public void test29() throws PrologException {
        SolveInfo solution = engine.solve("X is mod(7, 3).");
        assertEquals(Term.createTerm("1"), solution.getTerm("X"));
    }

    @Test public void test30() throws PrologException {
        SolveInfo solution = engine.solve("X is mod(0, 3+11).");
        assertEquals(Term.createTerm("0"), solution.getTerm("X"));
    }

    @Test public void test31() throws PrologException {
        SolveInfo solution = engine.solve("X is mod(7, -2).");
        assertEquals(Term.createTerm("-1"), solution.getTerm("X"));
    }

    @Test public void test32() throws PrologException {
        SolveInfo solution = engine.solve("X is mod(77, N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test33() throws PrologException {
        SolveInfo solution = engine.solve("X is mod(foo, 77).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test(expected=AssertionError.class)
    public void test34() throws PrologException {
        SolveInfo solution = engine.solve("X is mod(7.5, 2).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(integer, 7.5)
    }

    @Test(expected=AssertionError.class)
    public void test35() throws PrologException {
        SolveInfo solution = engine.solve("X is mod(7, 0).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(zero_division)
    }

    @Test public void test36() throws PrologException {
        SolveInfo solution = engine.solve("X is floor(7.4).");
        assertEquals(Term.createTerm("7"), solution.getTerm("X"));
    }

    @Test public void test37() throws PrologException {
        SolveInfo solution = engine.solve("X is floor(-0.4).");
        assertEquals(Term.createTerm("-1"), solution.getTerm("X"));
    }

    @Test public void test38() throws PrologException {
        SolveInfo solution = engine.solve("X is round(7.5).");
        assertEquals(Term.createTerm("8"), solution.getTerm("X"));
    }

    @Test public void test39() throws PrologException {
        SolveInfo solution = engine.solve("X is round(7.6).");
        assertEquals(Term.createTerm("8"), solution.getTerm("X"));
    }

    @Test public void test40() throws PrologException {
        SolveInfo solution = engine.solve("X is round(-0.6).");
        assertEquals(Term.createTerm("-1"), solution.getTerm("X"));
    }

    @Test public void test41() throws PrologException {
        SolveInfo solution = engine.solve("X is round(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test42() throws PrologException {
        SolveInfo solution = engine.solve("X is ceiling(-0.5).");
        assertEquals(Term.createTerm("0"), solution.getTerm("X"));
    }

    @Test public void test43() throws PrologException {
        SolveInfo solution = engine.solve("X is truncate(-0.5).");
        assertEquals(Term.createTerm("0"), solution.getTerm("X"));
    }

    @Test public void test44() throws PrologException {
        SolveInfo solution = engine.solve("X is truncate(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test public void test45() throws PrologException {
        SolveInfo solution = engine.solve("X is float(7).");
        assertEquals(Term.createTerm("7.0"), solution.getTerm("X"));
    }

    @Test public void test46() throws PrologException {
        SolveInfo solution = engine.solve("X is float(7.3).");
        assertEquals(Term.createTerm("7.3"), solution.getTerm("X"));
    }

    @Test public void test47() throws PrologException {
        SolveInfo solution = engine.solve("X is float(5 / 3).");
        assertEquals(Term.createTerm("1.0"), solution.getTerm("X"));
    }

    @Test public void test48() throws PrologException {
        SolveInfo solution = engine.solve("X is float(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test49() throws PrologException {
        SolveInfo solution = engine.solve("X is float(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test public void test50() throws PrologException {
        SolveInfo solution = engine.solve("X is abs(7).");
        assertEquals(Term.createTerm("7"), solution.getTerm("X"));
    }

    @Test public void test51() throws PrologException {
        SolveInfo solution = engine.solve("X is abs(3-11).");
        assertEquals(Term.createTerm("8"), solution.getTerm("X"));
    }

    @Test public void test52() throws PrologException {
        SolveInfo solution = engine.solve("X is abs(3.2-11.0).");
        assertEquals(Term.createTerm("7.8"), solution.getTerm("X"));
    }

    @Test public void test53() throws PrologException {
        SolveInfo solution = engine.solve("X is abs(N).");
        assertFalse(solution.isSuccess());
        // TODO Should throw instantiation_error
    }

    @Test public void test54() throws PrologException {
        SolveInfo solution = engine.solve("X is abs(foo).");
        assertFalse(solution.isSuccess());
        // TODO Should throw type_error(number, foo)
    }

    @Test(expected=AssertionError.class)
    public void test55() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(max_integer, MI), X is '+'(MI, 1).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(int_overflow)
    }

    @Test(expected=AssertionError.class)
    public void test56() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(max_integer, MI), X is '-'('+'(MI, 1), 1).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(int_overflow)
    }

    @Test(expected=AssertionError.class)
    public void test57() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(max_integer, MI), X is '*'(MI, 2).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(int_overflow)
    }

    @Test(expected=AssertionError.class)
    public void test58() throws PrologException {
        SolveInfo solution = engine.solve("current_prolog_flag(max_integer, MI), R is float(MI) * 2, X is floor(R).");
        assertFalse(solution.isSuccess());
        // TODO Should throw evaluation_error(int_overflow)
    }

}