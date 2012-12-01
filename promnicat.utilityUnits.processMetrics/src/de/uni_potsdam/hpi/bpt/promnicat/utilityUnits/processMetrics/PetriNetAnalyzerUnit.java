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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics;

import java.util.logging.Logger;

import org.jbpt.petri.Flow;
import org.jbpt.petri.NetSystem;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.behavior.LolaSoundnessChecker;
import org.jbpt.petri.structure.PetriNetPathUtils;
import org.jbpt.petri.structure.PetriNetStructuralClassChecks;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;

/**
 * This class analysis a {@link PetriNet} according to the following criteria:
 * <li> soundness (using the {@link LolaSoundnessChecker})</li>
 * <li> is cyclic </li>
 * <li> is free choice </li>
 * <li> is extended free choice </li>
 * <li> is S-Net </li>
 * <li> is T-Net </li>
 * <li> is workflow net </li>
 * 
 * <br/><br/>
 * The expected input type is {@link IUnitDataClassification}<{@link PetriNet}>.
 * The output type is the same as the input type.
 * 
 * @author Tobias Hoppe
 */
public class PetriNetAnalyzerUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object>> {

	/**
	 * logger used by this class
	 */
	private Logger logger = Logger.getLogger(PetriNetAnalyzerUnit.class.getName());
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (input instanceof IUnitDataClassification<?>){
			PetriNet petriNet = null;
			if (input.getValue() == null || !(input.getValue() instanceof PetriNet)){
				if (((IUnitDataClassification<Object>) input).getPetriNet() == null){
					logger.warning("Got no petri net as input for soundness check");
					return input;					
				} else {
					petriNet = ((IUnitDataClassification<Object>) input).getPetriNet();
				}
			} else {
				petriNet = (PetriNet) input.getValue();
			}
			try {
				NetSystem netSystem = new NetSystem();
				netSystem.addNodes(petriNet.getNodes());
				for(Flow edge : petriNet.getEdges()) {
					netSystem.addFreshFlow(edge.getSource(), edge.getTarget());
				}
				netSystem.loadNaturalMarking();
				((IUnitDataClassification<?>) input).setSoundnessResults(LolaSoundnessChecker.analyzeSoundness(netSystem));
			} catch (Exception e) {
				logger.warning("Soundness check failed with message: " + e.getMessage());
			}
			((IUnitDataClassification<?>) input).setCyclic(PetriNetPathUtils.isCyclic(petriNet));
			((IUnitDataClassification<?>) input).setFreeChoice(PetriNetStructuralClassChecks.isFreeChoice(petriNet));
			((IUnitDataClassification<?>) input).setExtendedFreeChoice(PetriNetStructuralClassChecks.isExtendedFreeChoice(petriNet));
			((IUnitDataClassification<?>) input).setSNet(PetriNetStructuralClassChecks.isSNet(petriNet));
			((IUnitDataClassification<?>) input).setTnet(PetriNetStructuralClassChecks.isTNet(petriNet));
			((IUnitDataClassification<?>) input).setWorkflowNet(PetriNetStructuralClassChecks.isWorkflowNet(petriNet));	
			return input;		
		}
		logger.warning("Petri Net analysis has been skiped, due to wrong IUnit Data type!");
		return input;
	}

	@Override
	public String getName() {
		return "PetriNetAnalyzerUnit";
	}

	@Override
	public Class<?> getInputType() {
		return PetriNet.class;
	}

	@Override
	public Class<?> getOutputType() {
		return PetriNet.class;
	}

}
