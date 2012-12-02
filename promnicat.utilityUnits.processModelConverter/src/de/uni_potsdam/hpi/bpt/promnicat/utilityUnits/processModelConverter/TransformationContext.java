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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelConverter;

import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.bpmn.BpmnEvent;

/**
 * @author Tobias Hoppe
 *
 */
public class TransformationContext {
	
	/**
	 * {@link Place} indicating that the attached event is not activated
	 */
	private Place pOk;
	
	/**
	 * {@link Place} indicating that the attached event is activated
	 */
	private Place pNotOk;
	
	/**
	 * Mapped {@link Transition} of the attached event
	 */
	private Transition exception;
	
	/**
	 * Attached event to handle with this {@link TransformationContext}.
	 */
	private BpmnEvent attachedEvent;
	
	/**
	 * Container for {@link Place}s and {@link Transition} for attached {@link BpmnEvent} to convert
	 * into {@link PetriNet}.
	 * @param attachedEvent to convert
	 * @param placeOk {@link Place} for state without exception
	 * @param placeNotOk {@link Place} for state with exception
	 * @param exception {@link Transition} representing the converted attached {@link BpmnEvent}
	 */
	public TransformationContext(BpmnEvent attachedEvent, Place placeOk, Place placeNotOk, Transition exception) {
		this.pOk = placeOk;
		this.pNotOk = placeNotOk;
		this.exception = exception;
		this.attachedEvent = attachedEvent;
	}

	/**
	 * @return the {@link Place} indicating that the attached event is not activated
	 */
	public Place getPlaceOk() {
		return pOk;
	}

	/**
	 * @param pOk the {@link Place} indicating that the attached event is not activated to set
	 */
	public void setPlaceOk(Place pOk) {
		this.pOk = pOk;
	}

	/**
	 * @return the {@link Place} indicating that the attached event is activated
	 */
	public Place getPlaceNotOk() {
		return pNotOk;
	}

	/**
	 * @param pNotOk the {@link Place} indicating that the attached event is activated to set
	 */
	public void setPlaceNotOk(Place pNotOk) {
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

	/**
	 * @return the attachedEvent
	 */
	public BpmnEvent getAttachedEvent() {
		return attachedEvent;
	}

	/**
	 * @param attachedEvent the attachedEvent to set
	 */
	public void setAttachedEvent(BpmnEvent attachedEvent) {
		this.attachedEvent = attachedEvent;
	}
	
	
}
