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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.correctors;

import java.util.ArrayList;
import java.util.List;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.CorrectionConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.DiagramWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.ShapeWrapper;


/**
 * Module for correcting structural incorrect BPMN process models or EPC models
 * @author Christian Kieschnick
 *
 */
public class DiagramCorrector  {
	
	/**
	 * the corrector which should be applied to incorrect models
	 */
	private List<AbstractCorrector> correctors = new ArrayList<AbstractCorrector>();

	public DiagramCorrector(){
		correctors.add(new BundledEdgeCorrector());
		correctors.add(new DirectedEdgeAttacher());
		correctors.add(new EdgeTypeCorrector());
	}
	
	/**
	 * apply the correction to the given model
	 * @param diagram the model to correct
	 */
	public void applyCorrection(DiagramWrapper diagram){
		// we do several rounds since some errors can be fixed after applying 
		// a correction mechanism before
		// for example at first a reference edge of a bundle has to be connected to a source and a target
		// before floating bundle edges can be attached
		for (int i = 0; i < CorrectionConstants.MaximalNumberOfCorrectionIterations; i++){
			boolean couldFixErrors = applyCorrection(diagram, correctors);
			if (!couldFixErrors){
				break;
			}
		}
	}
	
	/**
	 * apply correction with the given list of correctors
	 * @param diagram the model to correct
	 * @param correctors the correction mechanisms to apply
	 * @return true if at least one correction was applied
	 */
	public boolean applyCorrection(DiagramWrapper diagram, List<AbstractCorrector> correctors){
		List<AbstractCorrector> applicableCorrectors = new ArrayList<AbstractCorrector>();
		boolean correctionApplied = false;
		for (ShapeWrapper shape : diagram.getShapeWrappers()){
			// collect all possible correction mechanisms
			for (AbstractCorrector corrector : correctors){
				if (corrector.fulfillsPrecondition(shape)){
					applicableCorrectors.add(corrector);
				}
			}
			// apply the possible correction mechanisms until all fail or the first succeeds
			for (AbstractCorrector corrector : applicableCorrectors){
				if (corrector.fulfillsPrecondition(shape)){
					correctionApplied |= corrector.applyCorrection(shape);
				}
			}
			applicableCorrectors.clear();
		}
		return correctionApplied;
	}
}
