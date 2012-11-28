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
package de.uni_potsdam.hpi.bpt.promnicat.persistenceAPI.db4o.pojos;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IRepresentation;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.AbstractPojo;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.impl.Representation;

/**
 * This class can be used to store a label of one node and the type of the node together with the {@link Representation}.
 * 
 * @author Andrina Mascher
 *
 */
public class LabelStorage extends AbstractPojo {
	
	/**
	 * the label within the node
	 */
	String label = null;
	
	/**
	 * the type of the node, e.g. DataNode
	 */
	String className = null;
	
	/**
	 * the process model id that this node belongs to
	 */
	String representationId = null;
	
	/**
	 * the representation object, but don't store representation connection in
	 * database, but store dbId instead for performance reasons
	 */
	transient IRepresentation representation = null;
	
	public LabelStorage() {
	}
	
	public LabelStorage(String label, String className) {
		this.label = label;
		this.className = className;
	}
	
	public String toString() {
		return "LabelStorage [label: " + label + ", className: " + className
				+ " representationId: " + representationId
				+ "]";
	}
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the representationId
	 */
	public String getRepresentationId() {
		return representationId;
	}

	/**
	 * @param representationId the representationId to set
	 */
	public void setRepresentationId(String representationId) {
		this.representationId = representationId;
	}

	/**
	 * @return the representation
	 */
	public IRepresentation getRepresentation() {
		return representation;
	}

	/**
	 * @param representation the representation to set
	 */
	public void setRepresentation(IRepresentation representation) {
		this.representation = representation;
	}
}
