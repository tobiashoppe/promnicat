/**
 * PromniCAT - Collection and Analysis of Business Process Models
 * Copyright (C) 2012 Cindy Fähnrich, Tobias Hoppe, Andrina Mascher
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_potsdam.hpi.bpt.promnicat.util;

/**
 * An {@link Exception} that can be thrown if the type of a class is not the expected one.
 * @author Tobias Hoppe
 *
 */
public class IllegalTypeException extends Exception {

	/**
	 * generated id.
	 */
	private static final long serialVersionUID = 6252429258867332554L;

	/**
	 * expected class type
	 */
	private Class<?> expectedType;

	/**
	 * received class type
	 */
	private Class<?> receivedType;
	
	/**
	 * Create a new {@link IllegalTypeException} with the given expected type and the received type.
	 * An error message can be provided.
	 * @param expectedType the expected class type
	 * @param receivedType the received class type
	 * @param msg the message that should be provided with this {@link Exception};
	 */
	public IllegalTypeException(Class<?> expectedType, Class<?> receivedType, String msg) {
		super(msg);
		this.setExpectedType(expectedType);
		this.setReceivedType(receivedType);
	}

	/**
	 * @return the expectedType
	 */
	public Class<?> getExpectedType() {
		return expectedType;
	}

	/**
	 * @param expectedType the expectedType to set
	 */
	public void setExpectedType(Class<?> expectedType) {
		this.expectedType = expectedType;
	}

	/**
	 * @return the receivedType
	 */
	public Class<?> getReceivedType() {
		return receivedType;
	}

	/**
	 * @param receivedType the receivedType to set
	 */
	public void setReceivedType(Class<?> receivedType) {
		this.receivedType = receivedType;
	}

	
}
