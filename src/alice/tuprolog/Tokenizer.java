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
package alice.tuprolog;

import java.io.*;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * BNF for tuProlog
 *
 * part 1: Lexer
 *      digit ::= 0 .. 9
 *      lc_letter ::= a .. z
 *      uc_letter ::= A .. Z | _
 *      symbol ::= \ | $ | & | ^ | @ | # | . | , | : | ; | = | < | > | + | - | * | / | ~

 *      letter ::= digit | lc_letter | uc_letter
 *      integer ::= { digit }+
 *      float ::= { digit }+ . { digit }+ [ E|e [ +|- ] { digit }+ ]
 *                                                                           // TODO Update BNF for quotes?
 *      atom ::= lc_letter { letter }* | !
 *      variable ::= uc_letter { letter }*
 *
 * from the super class, the super.nextToken() returns and updates the following relevant fields:
 * - if the next token is a collection of wordChars,
 * the type returned is TT_WORD and the value is put into the field sval.
 * - if the next token is an ordinary char,
 * the type returned is the same as the unicode int value of the ordinary character
 * - other characters should be handled as ordinary characters.
 */
class Tokenizer extends StreamTokenizer implements Serializable {

    static final int TYPEMASK = 0x00FF;
    static final int ATTRMASK = 0xFF00;
    static final int LPAR = 0x0001;
    static final int RPAR = 0x0002;
    static final int LBRA = 0x0003;
    static final int RBRA = 0x0004;
    static final int BAR = 0x0005;
    static final int INTEGER = 0x0006;
    static final int FLOAT = 0x0007;
    static final int ATOM = 0x0008;
    static final int VARIABLE = 0x0009;
    static final int SQ_SEQUENCE = 0x000A;
    static final int DQ_SEQUENCE = 0x000B;
    static final int END = 0x000D;
    static final int LBRA2 = 0x000E;
    static final int RBRA2 = 0x000F;
    static final int FUNCTOR = 0x0100;
    static final int OPERATOR = 0x0200;
    static final int EOF = 0x1000;

    static final char[] GRAPHIC_CHARS = {'\\', '$', '&', '?', '^', '@', '#', '.', ',', ':', ';', '=', '<', '>', '+', '-', '*', '/', '~'};
    static {
        Arrays.sort(Tokenizer.GRAPHIC_CHARS);  // must be done to ensure correct behavior of Arrays.binarySearch
    }

    //used to enable pushback from the parser. Not in any way connected with pushBack2 and super.pushBack().
    private LinkedList tokenList = new LinkedList();

    //used in the double lookahead check that . following ints is a fraction marker or end marker (pushback() only works on one level)
    private PushBack pushBack2 = null;

    public Tokenizer(String text) {
        this(new StringReader(text));
    }
    /**
     * creating a tokenizer for the source stream
     */
    public Tokenizer(Reader text) {
        super(text);

        // Prepare the tokenizer for Prolog-style tokenizing rules
        resetSyntax();

        // letters
        wordChars('a', 'z');
        wordChars('A', 'Z');
        wordChars('_', '_');
        wordChars('0', '9'); // need to parse numbers as special words

        ordinaryChar('!');

        // symbols
        ordinaryChar('\\');
        ordinaryChar('$');
        ordinaryChar('&');
        ordinaryChar('^');
        ordinaryChar('@');
        ordinaryChar('#');
        ordinaryChar(',');
        ordinaryChar('.');
        ordinaryChar(':');
        ordinaryChar(';');
        ordinaryChar('=');
        ordinaryChar('<');
        ordinaryChar('>');
        ordinaryChar('+');
        ordinaryChar('-');
        ordinaryChar('*');
        ordinaryChar('/');
        ordinaryChar('~');

        // quotes
        ordinaryChar('\''); // must be parsed individually to handles \\ in quotes and character code constants
        ordinaryChar('\"'); // same as above?

        // comments
        ordinaryChar('%');
        // it is not possible to enable StreamTokenizer#slashStarComments and % as a StreamTokenizer#commentChar
        // and it is also not possible to use StreamTokenizer#whitespaceChars for ' '
    }

    /**
     * reads next available token
     */
    Token readToken() throws InvalidTermException, IOException {
    	return !tokenList.isEmpty() ? (Token) tokenList.removeFirst() : readNextToken();
    }

    /**
     * puts back token to be read again
     */
    void unreadToken(Token token) {
        tokenList.addFirst(token);
    }

    Token readNextToken() throws IOException, InvalidTermException {
        int typea;
        String svala;
        if (pushBack2 != null) {
            typea = pushBack2.typea;
            svala = pushBack2.svala;
            pushBack2 = null;
        } else {
            typea = super.nextToken();
            svala = sval;
        }

        // skips whitespace
        // could be simplified if lookahead for blank space in functors wasn't necessary
        // and if '.' in numbers could be written with blank space
        while (Tokenizer.isWhite(typea)) {
            typea = super.nextToken();
            svala = sval;
        }

        // skips single line comments
        // could be simplified if % was not a legal character in quotes
        if (typea == '%'){
            do {
                typea = super.nextToken();
            } while (typea != '\r' && typea != '\n' && typea != TT_EOF);
            pushBack();  // pushes back \r or \n. These are whitespace, so when readNextToken() finds them, they are marked as whitespace
            return readNextToken();
        }

        // skips /* comments */
        if (typea == '/'){
            int typeb = super.nextToken();
            if (typeb == '*'){
                do {
                    typea = typeb;
                    typeb = super.nextToken();
                } while (typea != '*' || typeb != '/');
                return readNextToken();
            } else {
                pushBack();
            }
        }

        // syntactic charachters
        if (typea == TT_EOF) return new Token("", Tokenizer.EOF);
        if (typea == '(') return new Token("(", Tokenizer.LPAR);
        if (typea == ')') return new Token(")", Tokenizer.RPAR);
        if (typea == '{') return new Token("{", Tokenizer.LBRA2);
        if (typea == '}') return new Token("}", Tokenizer.RBRA2);
        if (typea == '[') return new Token("[", Tokenizer.LBRA);
        if (typea == ']') return new Token("]", Tokenizer.RBRA);
        if (typea == '|') return new Token("|", Tokenizer.BAR);

        if (typea == '!') return new Token("!", Tokenizer.ATOM);
        if (typea == ',') return new Token(",", Tokenizer.OPERATOR);

        if (typea == '.')  { // check that '.' as end token is followed by a layout character, see ISO Standard 6.4.8 endnote
        	int typeb = super.nextToken();
        	if (Tokenizer.isWhite(typeb) || typeb == '%' || typeb == StreamTokenizer.TT_EOF)
        		return new Token(".", Tokenizer.END);
        	else
        		pushBack();
        }

        boolean isNumber = false;

        // variable, atom or number
        if (typea == TT_WORD) {
            char firstChar = svala.charAt(0);
            // variable
            if (Character.isUpperCase(firstChar) || '_' == firstChar)
                return new Token(svala, Tokenizer.VARIABLE);

            else if (firstChar >= '0' && firstChar <= '9') // all words starting with 0 or 9 must be a number
                isNumber = true;                           // set type to number and handle later

            else {                                         // otherwise, it must be an atom (or wrong)
                int typeb = super.nextToken();             // lookahead 1 to identify what type of atom
                pushBack();                                // this does not skip whitespaces, only readNext does so.
                if (typeb == '(')
                    return new Token(svala, Tokenizer.ATOM | Tokenizer.FUNCTOR);
                if (Tokenizer.isWhite(typeb))
                    return new Token(svala, Tokenizer.ATOM | Tokenizer.OPERATOR);
                return new Token(svala, Tokenizer.ATOM);
            }
        }

        // quotes
        if (typea == '\'' || typea == '\"' || typea == '`' ) {
            int qType = typea;
            StringBuffer quote = new StringBuffer();
            while (true) { // run through entire quote and added body to quote buffer
                typea = super.nextToken();
                svala = sval;
                // continuation escape sequence
                if (typea == '\\'){
                    int typeb = super.nextToken();
                    if (typeb == '\n') // continuation escape sequence marker \\n
                        continue;
                    if (typeb == '\r'){
                        int typec = super.nextToken();
                        if (typec == '\n')
                            continue; // continuation escape sequence marker \\r\n
                        pushBack();
                        continue; // continuation escape sequence marker \\r
                    }
                    pushBack(); // pushback typeb
                }
                // double '' or "" or ``
                if (typea == qType){
                    int typeb = super.nextToken();
                    if (typeb == qType) { // escaped '' or "" or ``
                        quote.append((char) qType);
                        continue;
                    } else {
                        pushBack();
                        break; // otherwise, break on single quote
                    }
                }
                if (typea == '\n' || typea == '\r')
                    throw new InvalidTermException("line break in quote not allowed (unless they are escaped \\ first)");

                if (svala != null)
                    quote.append(svala);
                else
                    quote.append((char) typea);
            }

            String quoteBody = quote.toString();

            qType = qType == '\'' ? SQ_SEQUENCE : qType == '\"' ? DQ_SEQUENCE : SQ_SEQUENCE;
            if (qType == SQ_SEQUENCE) {
                if (Parser.isAtom(quoteBody))
                    qType = ATOM;
                int typeb = super.nextToken(); // lookahead 1 to identify what type of quote
                pushBack();                    // nextToken() does not skip whitespaces, only readNext does so.
                if (typeb == '(')
                    return new Token(quoteBody, qType | FUNCTOR);
            }
            return new Token(quoteBody, qType);
        }

        // symbols
        if (Arrays.binarySearch(Tokenizer.GRAPHIC_CHARS, (char) typea) >= 0) {

            // the symbols are parsed individually by the super.nextToken(), so accumulate symbollist
            StringBuffer symbols = new StringBuffer();
            int typeb = typea;
            // String svalb = null;
            while (Arrays.binarySearch(Tokenizer.GRAPHIC_CHARS, (char) typeb) >= 0) {
                symbols.append((char) typeb);
                typeb = super.nextToken();
                // svalb = sval;
            }
            pushBack();
            
            // special symbols: unary + and unary -
//            try {
//                if (symbols.length() == 1 && typeb == TT_WORD && java.lang.Long.parseLong(svalb) > 0) {
//                    if (typea == '+')                         //todo, issue of handling + and -. I don't think this is ISO..
//                        return readNextToken();               //skips + and returns the next number
//                    if (typea == '-') {
//                        Token t = readNextToken();            //read the next number
//                        t.seq = "-" + t.seq;                   //add minus to value
//                        return t;                             //return token
//                    }
//                }                                             //ps. the reason why the number isn't returned right away, but through nextToken(), is because the number might be for instance a float
//            } catch (NumberFormatException e) {
//            }
            return new Token(symbols.toString(), Tokenizer.OPERATOR);
        }

        // numbers: 1. integer, 2. float
        if (isNumber) {
            try { // the various parseInt checks will throw exceptions when parts of numbers are written illegally

                // 1.a. complex integers
                if (svala.startsWith("0")) {
                    if (svala.indexOf('b') == 1)
                        return new Token("" + java.lang.Long.parseLong(svala.substring(2), 2), Tokenizer.INTEGER); // try binary
                    if (svala.indexOf('o') == 1)
                        return new Token("" + java.lang.Long.parseLong(svala.substring(2), 8), Tokenizer.INTEGER); // try octal
                    if (svala.indexOf('x') == 1)
                        return new Token("" + java.lang.Long.parseLong(svala.substring(2), 16), Tokenizer.INTEGER); // try hex
                }

                // lookahead 1
                int typeb = super.nextToken();
                String svalb = sval;

                // 1.b ordinary integers
                if (typeb != '.' && typeb != '\'') { // i.e. not float or character constant
                    pushBack(); // lookahead 0
                    return new Token("" + java.lang.Long.parseLong(svala), Tokenizer.INTEGER);
                }

                // 1.c character code constant
                if (typeb == '\'' && "0".equals(svala)) {
                    int typec = super.nextToken(); // lookahead 2
                    String svalc = sval;
                    int intVal;
                    if ((intVal = isCharacterCodeConstantToken(typec, svalc)) != -1)
                        return new Token("" + intVal, Tokenizer.INTEGER);

                    // this is an invalid character code constant int
                    throw new InvalidTermException("Character code constant starting with 0'<X> at line: " + super.lineno() + " cannot be recognized.");
                }

                // 2.a check that the value of the word prior to period is a valid long
                java.lang.Long.parseLong(svala); // throws an exception if not

                // 2.b first int is followed by a period
                if (typeb != '.')
                    throw new InvalidTermException("A number starting with 0-9 cannot be rcognized as an int and does not have a fraction '.' at line: " + super.lineno() );

                // lookahead 2
                int typec = super.nextToken();
                String svalc = sval;

                // 2.c check that the next token after '.' is a possible fraction
                if (typec != TT_WORD) { // if its not, the period is an End period
                    pushBack(); // pushback 1 the token after period
                    pushBack2 = new PushBack(typeb, svalb); // pushback 2 the period token
                    return new Token(svala, INTEGER); // return what must be an int
                }

                // 2.d checking for exponent
                int exponent = svalc.indexOf("E");
                if (exponent == -1)
                    exponent = svalc.indexOf("e");

                if (exponent >= 1) {                                  // the float must have a valid exponent
                    if (exponent == svalc.length() - 1) {             // the exponent must be signed exponent
                        int typeb2 = super.nextToken();
                        if (typeb2 == '+' || typeb2 == '-') {
                            int typec2 = super.nextToken();
                            String svalc2 = sval;
                            if (typec2 == TT_WORD){
                                // verify the remaining parts of the float and return
                                java.lang.Long.parseLong(svalc.substring(0, exponent));
                                java.lang.Integer.parseInt(svalc2);
                                return new Token(svala + "." + svalc + (char) typeb2 + svalc2, Tokenizer.FLOAT);
                            }
                        }
                    }
                }
                // 2.e verify lastly that ordinary floats and unsigned exponent floats are Java legal and return them
                java.lang.Double.parseDouble(svala + "." + svalc);
                return new Token(svala + "." + svalc, Tokenizer.FLOAT);

            } catch (NumberFormatException e) {
                // TODO return more info on what was wrong with the number given
                throw new InvalidTermException("A term starting with 0-9 cannot be parsed as a number at line: "+ lineno());
            }
        }
        throw new InvalidTermException("Unknown Unicode character: " + typea + "  (" + svala + ")");
    }

    /**
     *
     *
     * @param typec
     * @param svalc
     * @return the intValue of the next character token, -1 if invalid
     * todo needs a lookahead if typec is \
     */
    private static int isCharacterCodeConstantToken(int typec, String svalc) {
        if (svalc != null) {
            if (svalc.length() == 1)
                return (int) svalc.charAt(0);
            if (svalc.length() > 1) {
// TODO the following charachters is not implemented:
//                * 1 meta escape sequence (* 6.4.2.1 *) todo
//                * 1 control escape sequence (* 6.4.2.1 *)
//                * 1 octal escape sequence (* 6.4.2.1 *)
//                * 1 hexadecimal escape sequence (* 6.4.2.1 *)
                return -1;
            }
        }
        if (typec == ' ' ||                       // space char (* 6.5.4 *)
            Arrays.binarySearch(GRAPHIC_CHARS, (char)typec) >= 0)  // graphic char (* 6.5.1 *)
//        	TODO solo char (* 6.5.3 *)
            return typec;

        return -1;
    }

    private static boolean isWhite(int type) {
        return type == ' ' || type == '\r' || type == '\n' || type == '\t' || type == '\f';
    }

    /**
     * used to implement lookahead for two tokens, super.pushBack() only handles one pushBack..
     */
    private static class PushBack {
        int typea;
        String svala;

        public PushBack(int i, String s) {
            typea = i;
            svala = s;
        }
    }
}