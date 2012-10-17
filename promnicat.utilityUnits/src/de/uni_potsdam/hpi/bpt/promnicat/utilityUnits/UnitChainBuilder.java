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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.jbpt.hypergraph.abs.Vertex;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.ConfigurationParser;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.FeatureConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.util.ProcessMetricConstants.METRICS;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.ICollectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.collector.SimpleCollectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.BpmnConformanceLevelCheckerUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ElementExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ElementLabelExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ModelStructuringUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.PetriNetAnalyzerUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ProcessModelLabelExtractorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ProcessModelMetricsCalculatorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.ConnectednessFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.DatabaseFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.LabelFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.MetaDataFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.ProcessModelFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.BpmaiJsonToDiagramUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.DiagramToJbptUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.ModelToFeatureVectorUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.ModelToPetriNetUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitData;

/**
 * Builder class for constructing the appropriate {@link UnitChain}.
 * @author Tobias Hoppe, Cindy Fähnrich
 *
 */
public class UnitChainBuilder implements IUnitChainBuilder {

	protected static final String INCOMPATIBLE_OUTPUT_INPUT_TYPES_FOR_UNITS = "Incompatible output/input types for units.";

	private Logger logger = Logger.getLogger(UnitChainBuilder.class.getName());
	
	/**
	 * internal chain
	 */
	protected IUnitChain<IUnitData<Object>, IUnitData<Object> > unitChain = null;
	
	/**
	 * Creates a new {@link UnitChainBuilder} with a {@link DatabaseFilterUnit} as first unit
	 * using the given {@link IPersistenceApi}. The maximum number of threads used for {@link UnitChain}
	 * execution is set to the default value.
	 * @param persistenceApi the persistence API used by the {@link DatabaseFilterUnit}.
	 * @param unitDataType the type of {@link IUnitData} to be used in the chain. If <code>null</code> is provided, {@link IUnitData} is used.
	 */
	public UnitChainBuilder(IPersistenceApi persistenceApi, Class<?> unitDataType) {
		this(persistenceApi, -1, unitDataType);
	}
	
	/**
	 * Creates a new {@link UnitChainBuilder} with a {@link DatabaseFilterUnit} as first unit
	 * using the given {@link IPersistenceApi}.
	 * @param persistenceApi the persistence API used by the {@link DatabaseFilterUnit}.
	 * @param maxNumberOfThreads the maximum number of threads used for {@link UnitChain} execution.
	 * @param unitDataType the type of {@link IUnitData} to be used in the chain. If <code>null</code> is provided, {@link IUnitData} is used.
	 */
	public UnitChainBuilder(IPersistenceApi persistenceApi, int maxNumberOfThreads, Class<?> unitDataType) {
		if (unitDataType == null) {
			unitDataType = UnitData.class;
		}
		this.unitChain = new UnitChain(maxNumberOfThreads, unitDataType);
		this.unitChain.register(new DatabaseFilterUnit(persistenceApi));		
		((DatabaseFilterUnit) this.unitChain.getFirstUnit()).setDatabaseConfig(new DbFilterConfig());
	}
	
	/**
	 * Creates a new {@link UnitChainBuilder} with a {@link DatabaseFilterUnit} as first unit
	 * using the given configuration file.
	 * @param pathToConfig the path to the configuration file being used. If empty, the default file 'configuration.properties' is used.
	 * @param database the database type to use
	 * @param unitDataType the type of {@link IUnitData} to be used in the chain. If <code>null</code> is provided, {@link IUnitData} is used.
	 * @throws IOException if the given configuration file could not be found.
	 */
	public UnitChainBuilder(String pathToConfig, Constants.DATABASE_TYPES database, Class<?> unitDataType) throws IOException {
		this(new ConfigurationParser(pathToConfig), database, unitDataType);
	}
	
	/**
	 * Only a delegating constructor.
	 */
	private UnitChainBuilder(ConfigurationParser configParser, Constants.DATABASE_TYPES database, Class<?> unitDataType){
		this(configParser.getDbInstance(database), configParser.getThreadCount(), unitDataType);
	}
	
	@Override
	public void addDbFilterConfig(DbFilterConfig config) {
		((DatabaseFilterUnit) this.unitChain.getFirstUnit()).setDatabaseConfig(config);
	}

	@Override
	public void addUnitChain(IUnitChain<IUnitData<Object>, IUnitData<Object> > chain) {
		if (chain.getFirstUnit() instanceof DatabaseFilterUnit) {
			throw new IllegalArgumentException("The given chain does not include a database filter unit!");
		}
		this.unitChain.register(chain.getUnits());
	}

	@Override
	public void createBpmaiJsonToJbptUnit() throws IllegalTypeException {
		this.createBpmaiJsonToJbptUnit(false);
	}

	@Override
	public void createBpmaiJsonToJbptUnit(boolean strictness)
			throws IllegalTypeException {
		BpmaiJsonToDiagramUnit jsonToDiagram = new BpmaiJsonToDiagramUnit();
		if (this.unitChain.getLastUnit().getOutputType() == jsonToDiagram.getInputType()) {
			this.unitChain.register(jsonToDiagram);
			this.unitChain.register(new DiagramToJbptUnit(strictness));			
		} else {
			throw new IllegalTypeException(jsonToDiagram.getInputType(), this.unitChain.getLastUnit().getOutputType(), INCOMPATIBLE_OUTPUT_INPUT_TYPES_FOR_UNITS);
		}
		
	}

	@Override
	public void createBpmnConformanceLevelCheckerUnit() throws IllegalTypeException {
		BpmnConformanceLevelCheckerUnit confLevelChecker = new BpmnConformanceLevelCheckerUnit();
		checkForCompatibility(confLevelChecker);
	}

	@Override
	public void createConnectednessFilterUnit() throws IllegalTypeException {
		ConnectednessFilterUnit conFilter = new ConnectednessFilterUnit();
		checkForCompatibility(conFilter);
	}

	@Override
	public void createElementExtractorUnit(Class<?> classToFilter) throws IllegalTypeException {
		ElementExtractorUnit extractorUnit = new ElementExtractorUnit(classToFilter);
		checkForCompatibility(extractorUnit);
	}

	@Override
	public void createElementExtractorUnit(Collection<Class<?>> classesToFilter) throws IllegalTypeException {
		ElementExtractorUnit extractorUnit = new ElementExtractorUnit(classesToFilter);
		checkForCompatibility(extractorUnit);
	}

	@Override
	public void createElementLabelExtractorUnit() throws IllegalTypeException {
		ElementLabelExtractorUnit labelExtractor = new ElementLabelExtractorUnit();
		checkForCompatibility(labelExtractor);
	}

	@Override
	public void createLabelFilterUnit(Pattern patternToFilter) throws IllegalTypeException {
		LabelFilterUnit labelFilter = new LabelFilterUnit(patternToFilter);
		checkForCompatibility(labelFilter);
	}

	@Override
	public void createLabelFilterUnit(String labelToFilter) throws IllegalTypeException {
		LabelFilterUnit labelFilter = new LabelFilterUnit(labelToFilter);
		checkForCompatibility(labelFilter);
	}
	
	@Override
	public void createMetaDataFilterUnit(Pattern keyToFilter, Pattern valueToFilter) throws IllegalTypeException {
		MetaDataFilterUnit metaDataFilter = new MetaDataFilterUnit(keyToFilter, valueToFilter);
		checkForCompatibility(metaDataFilter);
	}

	@Override
	public void createMetaDataFilterUnit(String keyToFilter, String valueToFilter) throws IllegalTypeException {
		MetaDataFilterUnit metaDataFilter = new MetaDataFilterUnit(keyToFilter, valueToFilter);
		checkForCompatibility(metaDataFilter);
	}

	@Override
	public void createModelStructuringUnit() throws IllegalTypeException {
		ModelStructuringUnit modelStructuringUnit = new ModelStructuringUnit();
		checkForCompatibility(modelStructuringUnit);		
	}

	@Override
	public void createPetriNetAnalyzerUnit() throws IllegalTypeException {
		PetriNetAnalyzerUnit pnAnalyzerUnit = new PetriNetAnalyzerUnit();
		checkForCompatibility(pnAnalyzerUnit);
	}

	@Override
	public void createProcessModelFilterUnit(Class<?> classType) throws IllegalTypeException {
		ProcessModelFilterUnit elemFilterUnit = new ProcessModelFilterUnit(classType);
		checkForCompatibility(elemFilterUnit);
	}

	@Override
	public void createProcessModelFilterUnit(Collection<Class<?>> includedTypes,
			Collection<Class<?>> excludedTypes) throws IllegalTypeException {
		if (includedTypes == null) {
			includedTypes = new HashSet<Class<?>>();
		}
		if (excludedTypes == null) {
			excludedTypes = new HashSet<Class<?>>();
		}
		ProcessModelFilterUnit elemFilterUnit = new ProcessModelFilterUnit(includedTypes, excludedTypes);
		checkForCompatibility(elemFilterUnit);
	}

	@Override
	public void createProcessModelFilterUnit(Vertex element) throws IllegalTypeException {
		ProcessModelFilterUnit elemFilterUnit = new ProcessModelFilterUnit(element);
		checkForCompatibility(elemFilterUnit);
	}

	@Override
	public void createProcessModelLabelExtractorUnit() throws IllegalTypeException {
		ProcessModelLabelExtractorUnit labelExtUnit = new ProcessModelLabelExtractorUnit();
		checkForCompatibility(labelExtUnit);
	}

	@Override
	public void createProcessModelMetricsCalulatorUnit() throws IllegalTypeException {
		ProcessModelMetricsCalculatorUnit metricsUnit = new ProcessModelMetricsCalculatorUnit();
		checkForCompatibility(metricsUnit);
	}
	
	@Override
	public void createProcessModelMetricsCalulatorUnit(boolean handleSubProcesses) throws IllegalTypeException {
		ProcessModelMetricsCalculatorUnit metricsUnit = new ProcessModelMetricsCalculatorUnit(handleSubProcesses);
		checkForCompatibility(metricsUnit);
	}
	
	@Override
	public void createProcessModelMetricsCalulatorUnit(Collection<METRICS> metricsToCalculate, boolean handleSubProcesses) throws IllegalTypeException {		ProcessModelMetricsCalculatorUnit metricsUnit = new ProcessModelMetricsCalculatorUnit(metricsToCalculate, handleSubProcesses);
		checkForCompatibility(metricsUnit);	
	}

	@Override
	public void createProcessModelToPetriNetUnit() throws IllegalTypeException {
		ModelToPetriNetUnit pmToPnUnit = new ModelToPetriNetUnit();
		checkForCompatibility(pmToPnUnit);		
	}

	@Override
	public void createProcessModelToPetriNetUnit(IPersistenceApi persistenceAPI) throws IllegalTypeException {
		ModelToPetriNetUnit pmToPnUnit = new ModelToPetriNetUnit(persistenceAPI);
		checkForCompatibility(pmToPnUnit);
	}

	@Override
	public void createModelToFeatureVectorUnit(FeatureConfig conf) throws IllegalTypeException {
		ModelToFeatureVectorUnit featureUnit = new ModelToFeatureVectorUnit(conf);
		checkForCompatibility(featureUnit);
	}

	@Override
	public void createSimpleCollectorUnit() {
		this.unitChain.register(new SimpleCollectorUnit());
	}

	@Override
	public IUnitChain<IUnitData<Object>, IUnitData<Object> > getChain() {
		if (!(this.unitChain.getLastUnit() instanceof ICollectorUnit)) {
			this.unitChain.register(new SimpleCollectorUnit());
			logger.info("Added simple collector unit, because a collector unit is always needed as last element.");
		}
		return this.unitChain;
	}

	/**
	 * Check whether the given {@link ProcessModelFilterUnit} can be used at this point in the {@link IUnitChain}.
	 * @param unit the {@link ProcessModelFilterUnit} to check
	 * @throws IllegalTypeException if the given {@link ProcessModelFilterUnit} is not allowed at this point in the {@link IUnitChain}.
	 */
	private void checkForCompatibility(IUnit<IUnitData<Object>, IUnitData<Object>> unit)
			throws IllegalTypeException {
		if (this.unitChain.getLastUnit().getOutputType() == unit.getInputType()) {
			this.unitChain.register(unit);			
		} else {
			throw new IllegalTypeException(unit.getInputType(), this.unitChain.getLastUnit().getOutputType(), INCOMPATIBLE_OUTPUT_INPUT_TYPES_FOR_UNITS);
		}
	}

}