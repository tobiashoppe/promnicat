package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.utilityUnits;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.InvalidModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.Statistic;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors.ElementDetector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors.MissingControlFlowConnectionDetector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors.MissingEdgeConnectionDetector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors.WrongEdgeTypeConnectionDetector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.DiagramWrapper;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Unit to generate statistics about the occurrence of errors in the processed diagrams
 * @author Christian Kieschnick
 *
 */
public class ErrorDetectorUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> >{
	/**
	 * logger used by this class
	 */
	private Logger logger = Logger.getLogger(ErrorCorrectorUnit.class.getName());
	
	/**
	 * statistics about occurred errors
	 */
	private Statistic errorStatistic = new Statistic();
	/**
	 * statistics about detected elements
	 */
	private Statistic elementStatistic = new Statistic();
	
	public ErrorDetectorUnit(){
		MissingEdgeConnectionDetector.errorModels = new HashSet<String>();
		WrongEdgeTypeConnectionDetector.errorModels = new HashSet<String>();
	}
	
	/**
	 * print statistics to in a readable way
	 * @return
	 */
	public String printStatistics(){
		return "ERROR STATISTIC:\n"
				+ errorStatistic.toString()+"\n"
				+ "ELEMENT STATISTIC:\n"
				+ elementStatistic.toString();
	}
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input)
			throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (input.getValue() == null){
			logger.warning("Got no model as input for processing");
			return input;
			
		}
		Diagram model = (Diagram)input.getValue();
		try {
			Statistic.GroupedValueResult errorResult = new Statistic.GroupedValueResult();
			DiagramWrapper wrapper = new DiagramWrapper(model);
			errorResult = new MissingEdgeConnectionDetector().process(wrapper, errorResult);
			errorResult = new WrongEdgeTypeConnectionDetector().process(wrapper, errorResult);
			errorResult = new MissingControlFlowConnectionDetector.Incoming().process(wrapper, errorResult);
			errorResult = new MissingControlFlowConnectionDetector.Outgoing().process(wrapper, errorResult);
			errorStatistic.addResult(model.getPath(), model.getStencilset().getNamespace(), errorResult);
			
			Statistic.GroupedValueResult elementResult = new Statistic.GroupedValueResult();
			elementResult = new ElementDetector().process(wrapper, elementResult);
			elementStatistic.addResult(model.getPath(), model.getStencilset().getNamespace(), elementResult);
		} catch (InvalidModelException e){
			logger.log(Level.WARNING, "Model  "+model.getResourceId() + " has an invalid state\n\t"+e.getMessage());
		} catch (UnsupportedModelException e){
			logger.info("Model  "+model.getResourceId() + " could not be corrected due to unsupported stencil set\n\t"+e.getMessage());
		}
		return input;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public Class<?> getInputType() {
		return Diagram.class;
	}

	@Override
	public Class<?> getOutputType() {
		return Diagram.class;
	}
}
