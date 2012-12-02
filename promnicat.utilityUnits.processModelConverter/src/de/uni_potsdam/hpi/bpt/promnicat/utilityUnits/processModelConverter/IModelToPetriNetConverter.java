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
import org.jbpt.pm.ProcessModel;
import org.jbpt.throwable.TransformationException;

/**
 * Interface for all converter transforming any kind of business process model
 * into a jBPT {@link PetriNet}.
 * 
 * @author Tobias Hoppe
 *
 */
public interface IModelToPetriNetConverter {
	
	/**
	 * Transforms the given {@link ProcessModel} into a {@link PetriNet}.
	 * @param model to transform
	 * @return the created {@link PetriNet}
	 * @throws TransformationException if transformation failed.
	 */
	public PetriNet convertToPetriNet(ProcessModel model) throws TransformationException;

	/**
	 * @return the {@link PetriNet} being transformed from the last {@link ProcessModel}
	 * converted with {@link #convertToPetriNet(ProcessModel)}. Or <code>null</code> if
	 * no {@link ProcessModel} has been converted yet.
	 */
	public PetriNet getLastPetriNet();
}
