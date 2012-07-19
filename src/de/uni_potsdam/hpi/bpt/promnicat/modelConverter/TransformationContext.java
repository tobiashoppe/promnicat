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
package de.uni_potsdam.hpi.bpt.promnicat.modelConverter;

import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;

/**
 * @author Tobias Hoppe
 *
 */
public class TransformationContext {
	
	/**
	 * {@link Place} indicating that the attached event is not activated
	 */
	Place pOk;
	
	/**
	 * {@link Place} indicating that the attached event is activated
	 */
	Place pNotOk;
	
	/**
	 * Mapped {@link Transition} of the attached event
	 */
	Transition exception;
	
	public TransformationContext(Place pOk, Place pNotOk, Transition exception) {
		this.pOk = pOk;
		this.pNotOk = pNotOk;
		this.exception = exception;
	}

	/**
	 * @return the {@link Place} indicating that the attached event is not activated
	 */
	public Place getpOk() {
		return pOk;
	}

	/**
	 * @param pOk the {@link Place} indicating that the attached event is not activated to set
	 */
	public void setpOk(Place pOk) {
		this.pOk = pOk;
	}

	/**
	 * @return the {@link Place} indicating that the attached event is activated
	 */
	public Place getpNotOk() {
		return pNotOk;
	}

	/**
	 * @param pNotOk the {@link Place} indicating that the attached event is activated to set
	 */
	public void setpNotOk(Place pNotOk) {
		this.pNotOk = pNotOk;
	}

	/**
	 * @return the mapped {@link Transition} of the attached event
	 */
	public Transition getException() {
		return exception;
	}

	/**
	 * @param exception the mapped {@link Transition} of the attached event to set
	 */
	public void setException(Transition exception) {
		this.exception = exception;
	}
	
	
}
