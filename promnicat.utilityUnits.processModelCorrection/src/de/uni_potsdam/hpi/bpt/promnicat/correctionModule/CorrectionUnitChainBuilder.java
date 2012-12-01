package de.uni_potsdam.hpi.bpt.promnicat.correctionModule;

import java.io.IOException;

import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.utilityUnits.ErrorCorrectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.utilityUnits.ErrorDetectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.utilityUnits.UnparsableModelFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.impl.UnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * extended version of the UnitChainBuilder for more flexibility without changing the original 
 * @author Christian Kieschnick
 *
 */
public class CorrectionUnitChainBuilder extends UnitChainBuilder {
	
	/**
	 * {@inheritDoc}
	 */
	public CorrectionUnitChainBuilder(String pathToConfig, Class<?> unitDataType) throws IOException {
		super(pathToConfig, unitDataType);
	}

	/**
	 * register a new Unit into the chain while checking that the input types and output types correspond 
	 * @param unit
	 * @throws IllegalTypeException
	 */
	public void register(IUnit<IUnitData<Object>, IUnitData<Object> > unit) throws IllegalTypeException{
		if (this.unitChain.getLastUnit().getOutputType() == unit.getInputType()) {
			this.unitChain.register(unit);
		} else {
			throw new IllegalTypeException(unit.getInputType(), this.unitChain.getLastUnit().getOutputType(), INCOMPATIBLE_OUTPUT_INPUT_TYPES_FOR_UNITS);
		}
	}
	
	/**
	 * injects an error correction unit into the bpmai to jbpt unit chain
	 */
	@Override
	public void createBpmaiJsonToJbptUnit(boolean strictness) throws IllegalTypeException {
		register(new BpmaiJsonToDiagramUnit());
		register(new ErrorCorrectorUnit());
		register(new DiagramToJbptUnit(strictness));
	}
	
	/**
	 * utility method for detecting flawed models by using the detection algorithms and 
	 * a customized converter to jbpt
	 * @param strictness of the jbpt converter
	 * @param detector a detection unit which should be injected
	 * @throws IllegalTypeException
	 */
	public void createBpmaiJsonToJbptWithStatistics(boolean strictness, ErrorDetectorUnit detector) throws IllegalTypeException {
		register(new BpmaiJsonToDiagramUnit());
		register(detector);
		register(new UnparsableModelFilterUnit(strictness));

	}
	
	/**
	 * utility method for detecting flawed models by using the detection algorithms 
	 * before and after correction and finally collecting information about 
	 * successful transformation by employing the customized jbpt converter
	 * @param strictness of the jbpt convert
	 * @param before a detection unit which should be injected before correction
	 * @param after a detection unit which should be injected after correction
	 * @throws IllegalTypeException
	 */
	public void createBpmaiCorrectorModelCollectionWithStatistics(boolean strictness, ErrorDetectorUnit before, ErrorDetectorUnit after) throws IllegalTypeException {
		register(new BpmaiJsonToDiagramUnit());
		register(before);
		register(new ErrorCorrectorUnit());
		register(after);
		register(new UnparsableModelFilterUnit(strictness));

	}
	
	/**
	 * utility unit to filter incorrect models by using a customized jbpt converter
	 * @param strictness
	 * @throws IllegalTypeException
	 */
	public void createBpmaiErrorsModelCollection(boolean strictness) throws IllegalTypeException {
		register(new BpmaiJsonToDiagramUnit());
		register(new ErrorCorrectorUnit());
		register(new UnparsableModelFilterUnit(strictness));
	}
}