/*
 * tuProlog - Copyright (C) 2001-2007 aliCE team at deis.unibo.it
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

/**
 * This exceptions means that a not valid tuProlog theory has been specified.
 *
 * @see Theory
 */
public class InvalidTheoryException extends PrologException {
	
	private static final long serialVersionUID = 8697898609842609809L;
	
	public int line = -1;
	public int pos = -1;
	
	public InvalidTheoryException() {}
	
	public InvalidTheoryException(int line, int pos) {
		this.line = line;
		this.pos = pos;
	}

	public InvalidTheoryException(String message) {
		super(message);
	}
	
}