package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.utilityUnits;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.parser.ModelParser;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.impl.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.BpmnDiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.DiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.EpcDiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * Unit to detect models which cannot be parsed to jBPT
 * It returns the identifier of the models for further processing
 * @author Christian Kieschnick
 */
public class UnparsableModelFilterUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	public static int importedBpmn = 0;
	public static int importedEpc = 0;
	public static int unsupported = 0;
	public static int all = 0;
	public static int rejectedEpc = 0;
	public static int rejectedBpmn = 0;
	
	/**
	 * increase all counter
	 */
	public static synchronized void increaseAll(){
		all++;
	}
	
	/**
	 * increase bpmn counter
	 * @param rejected to increase rejected counter
	 */
	public static synchronized void increaseBpmn(boolean rejected){
		if (rejected){
			rejectedBpmn++;
		}
		else{
			importedBpmn++;
		}
	}
	
	/**
	 * increase epc counters
	 * @param rejected to increase rejected counter
	 */
	public static synchronized void increaseEpc(boolean rejected){
		if (rejected){
			rejectedEpc++;
		}
		else{
			importedEpc++;
		}
	}
	
	/**
	 * increase unsupported counter
	 */
	public static synchronized void increaseUnsupported(){
		unsupported++;
	}
	
	/**
	 * the strictness level of the parser which should be applied
	 */
	private boolean strictness;
	
	public UnparsableModelFilterUnit(boolean strictness) {
		this.strictness = strictness;
	}
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input)
			throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (!(input.getValue() instanceof Diagram)){
			throw new IllegalTypeException(Diagram.class, input.getValue().getClass(), "Got wrong input type in" + this.getName());
		}
		Diagram diagram = (Diagram)input.getValue();
		ProcessModel processModel = new ModelParser(this.strictness).transformProcess(diagram);
		boolean rejected = processModel == null;
		increaseAll();
		try {
			DiagramRules rules = DiagramRules.of(diagram);
			if (rules instanceof BpmnDiagramRules){
				increaseBpmn(rejected);
			}
			if (rules instanceof EpcDiagramRules){
				increaseEpc(rejected);
			}
		} catch (UnsupportedModelException e) {
			increaseUnsupported();
		}
		if (rejected)
		{
			input.setValue(((Diagram)input.getValue()).getPath());
		} else {
			input.setValue(null);
		}
		return input;
	}

	@Override
	public String getName() {
		return "UnparsableModelFilterUnit";
	}

	@Override
	public Class<?> getInputType() {
		return Diagram.class;
	}

	@Override
	public Class<?> getOutputType() {
		return String.class;
	}

}
