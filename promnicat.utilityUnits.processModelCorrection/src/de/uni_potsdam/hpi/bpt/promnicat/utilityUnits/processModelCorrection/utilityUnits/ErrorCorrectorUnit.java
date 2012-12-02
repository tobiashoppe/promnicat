package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.utilityUnits;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.InvalidModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.correctors.DiagramCorrector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.DiagramWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Unit to correct Bpmai models in a unit chain
 * @author Christian Kieschnick
 */
public class ErrorCorrectorUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	/**
	 * logger used by this class
	 */
	private Logger logger = Logger.getLogger(ErrorCorrectorUnit.class.getName());
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (input.getValue() == null){
			logger.warning("Got no model as input for processing");
			return input;
		}
		Diagram model = (Diagram)input.getValue();
		try {
			DiagramCorrector modelCorrector = new DiagramCorrector();
			modelCorrector.applyCorrection(new DiagramWrapper(model));
			input.setValue(model);
		} catch (InvalidModelException e){
			logger.log(Level.WARNING, "Model  "+model.getResourceId() + " has an invalid state\n\t"+e.getMessage());
		} catch (UnsupportedModelException e){
			logger.info("Model  "+model.getResourceId() + " could not be corrected due to unsupported stencil set\n\t"+e.getMessage());
		}
		return input;
	}

	@Override
	public Class<?> getInputType() {
		return Diagram.class;
	}

	@Override
	public String getName() {
		return "ModelCorrectorUnit";
	}

	@Override
	public Class<?> getOutputType() {
		return Diagram.class;
	}
}
