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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.jbpt.hypergraph.abs.Vertex;
import org.jbpt.petri.PetriNet;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.IPersistenceApi;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.config.DbFilterConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.builder.impl.UnitChainBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.filter.DatabaseFilterUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.util.ProcessMetricConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.FeatureConfig;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;


/**
 * Interface class for the {@link UnitChainBuilder}.
 * @author Tobias Hoppe, Cindy Fähnrich
 * 
 */
public interface IUnitChainBuilder {

	/**
	 * Add a {@link DbFilterConfig} to be used by the {@link DatabaseFilterUnit} of the internal {@link IUnitChain}.
	 * 
	 * @param config to be added
	 */
	public void addDbFilterConfig(DbFilterConfig config);

	/**
	 * Adds all {@link IUnit}s of the given {@link IUnitChain} to the {@link IUnitChain} of this {@link IUnitChainBuilder}.
	 * 
	 * @param chain the {@link IUnitChain} added to the {@link IUnitChain} of this {@link IUnitChainBuilder}.
	 */
	public void addUnitChain(IUnitChain<IUnitData<Object>, IUnitData<Object>> chain);
	
	/**
	 * <b>Programmatic API</b>, use one of the provided methods to add an
	 * {@link IUnit} to the {@link IUnitChainBuilder}'s internal {@link IUnitChain}
	 * instead!<br/>
	 * Creates a new {@link IUnit} of the given type and with the given parameters.
	 * The instance is added to the {@link UnitChainBuilder}'s internal {@link IUnitChain}.
	 * @param clazz {@link IUnit} class to instantiate
	 * @param parameterTypes the class types of the parameters of the class to instantiate
	 * @param parameters the parameter values to pass to the constructor of the class to instantiate
	 */
	public void addUnit(Class<? extends IUnit<IUnitData<Object>, IUnitData<Object>>> clazz, Class<?>[] parameterTypes, List<?> parameters); 
	
	/**
	 * Add all {@link IUnit}s to the internal {@link IUnitChain}, that are used to transform
	 * the BPM Academic Initiative JSON to a jBPT {@link ProcessModel}.
	 * @param strictness whether to return only the correctly parsed {@link ProcessModel}s
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createBpmaiJsonToJbptUnit(boolean strictness) throws IllegalTypeException;
	
	/**
	 * Add all {@link IUnit}s to the internal {@link IUnitChain}, that are used to transform
	 * the BPM Academic Initiative JSON to a jBPT {@link ProcessModel}.
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createBpmaiJsonToJbptUnit() throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to
	 * check a given {@link ProcessModel} for BPMN Conformance Level conformance. 
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createBpmnConformanceLevelCheckerUnit() throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to filter out
	 * jBPT {@link ProcessModel}s that are connected.
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createConnectednessFilterUnit() throws IllegalTypeException;

	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to check whether
	 * the {@link ProcessModel} contains an element of the given type.
	 * 
	 * @param classType the element type that must be contained in the {@link ProcessModel}
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelFilterUnit(Class<?> classType) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to check whether
	 * the {@link ProcessModel} contains an element of each of the given 'included types' and none of the 'excluded types'.
	 * 
	 * @param includedTypes the element types that must be contained in the {@link ProcessModel}
	 * @param excludedTypes the element types that are not allowed in the {@link ProcessModel}
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelFilterUnit(Collection<Class<?>> includedTypes, Collection<Class<?>> excludedTypes) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to check whether
	 * the {@link ProcessModel} contains the given element.
	 * 
	 * @param element an element instance that must be contained in the {@link ProcessModel}
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelFilterUnit(Vertex element) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to extract all
	 * labels from a jBPT {@link ProcessModel}.
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelLabelExtractorUnit() throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to calculate a
	 * large set of process model metrics for a jBPT {@link ProcessModel} and all of its
	 * available sub processes.
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelMetricsCalulatorUnit() throws IllegalTypeException;

	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to calculate a
	 * large set of process model metrics for a jBPT {@link ProcessModel}.
	 * @param handleSubProcesses flag that indicates whether to include 
	 * available sub process in metric calculation or not 
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelMetricsCalulatorUnit(boolean handleSubProcesses) throws IllegalTypeException;

	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to transform a
	 * {@link ProcessModel} to the corresponding {@link PetriNet} without using any database
	 * lookups for already existing transformation results.
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelToPetriNetUnit() throws IllegalTypeException;
	
	/**
	 * 	Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to transform a
	 * {@link ProcessModel} to the corresponding {@link PetriNet} using  the given database
	 * to look up already existing transformation results.
	 * @param persistenceAPI the database to use
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelToPetriNetUnit(IPersistenceApi persistenceAPI) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to transform the formerly
	 * calculated process model metrics into a feature vector for a jBPT {@link ProcessModel}.
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createModelToFeatureVectorUnit(FeatureConfig conf) throws IllegalTypeException;
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to extract all
	 * labels from a {@link Collection} of given {@link ProcessModel} elements.
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createElementLabelExtractorUnit() throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to extract all
	 * labels matching the given {@link Pattern}.
	 * 
	 * @param patternToFilter is the search criterion
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createLabelFilterUnit(Pattern patternToFilter) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to extract all
	 * labels containing the given label to filter. The matching is <b>case insensitive</>.
	 * 
	 * @param labelToFilter is the search criterion
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createLabelFilterUnit(String labelToFilter) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to extract all
	 * meta data containing the given key and value. If all keys or values should be retrieved,
	 * <code>null</code> can be provided.
	 * @param keyToFilter meta data key to filter
	 * @param valueToFilter meta data value to filter
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createMetaDataFilterUnit(String keyToFilter, String valueToFilter) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to extract all
	 * meta data containing the given key and value. If all keys or values should be retrieved,
	 * <code>null</code> can be provided.
	 * @param keyToFilter meta data key to filter
	 * @param valueToFilter meta data value to filter
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createMetaDataFilterUnit(Pattern keyToFilter, Pattern valueToFilter) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to evaluate,
	 * whether a given {@link ProcessModel} can be structured or even is structured, using bpstruct. 
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createModelStructuringUnit() throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to filter out
	 * the given model element type and all of it's sub-types from a jBPT {@link ProcessModel}.
	 * 
	 * @param classToFilter the type of class to extract all elements from the {@link ProcessModel}
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createElementExtractorUnit(Class<?> classToFilter) throws IllegalTypeException;

	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to filter out
	 * the given model element types and all of it's sub-types from a jBPT {@link ProcessModel}.
	 * 
	 * @param classesToFilter the type of classes to extract all elements from the {@link ProcessModel}
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createElementExtractorUnit(Collection<Class<?>> classesToFilter) throws IllegalTypeException;
	
	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to analyze the formerly
	 * created {@link PetriNet} regarding soundness, free choice, S/T-Net, workflow net, ...
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createPetriNetAnalyzerUnit() throws IllegalTypeException;

	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that holds the result of the 
	 * {@link IUnitChain} execution. The result is created by simply collecting
	 * the results of all processed elements.
	 */
	public void createSimpleCollectorUnit();

	/**
	 * Add a {@link IUnit} to the internal {@link IUnitChain}, that is used to calculate a
	 * large set of process model metrics for a jBPT {@link ProcessModel}.
	 * @param handleSubProcesses flag that indicates whether to include 
	 * available sub process in metric calculation or not
	 * @param metricsToCalculate a collection of metrics that shall be evaluated
	 * for each process model
	 * @throws IllegalTypeException if the unit's input and output value classes are not compatible.
	 */
	public void createProcessModelMetricsCalulatorUnit(Collection<ProcessMetricConstants.METRICS> metricsToCalculate, boolean handleSubProcesses) throws IllegalTypeException;
	
	/**
	 * @return the {@link IUnitChain} created by this {@link IUnitChainBuilder}.
	 */
	public IUnitChain<IUnitData<Object>, IUnitData<Object> > getChain();
}