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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.unitData;

import java.util.logging.Logger;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.FeatureVector;
import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.ProcessInstance;
import de.uni_potsdam.hpi.bpt.promnicat.persistenceApi.Representation;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnitChain;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.UnitData;

/**
 * {@link IUnitChain} result type implementation containing the {@link Representation} id of the used 
 * jBPT {@link ProcessModel} and the result value of the last {@link IUnit} in the {@link IUnitChain}.
 * Furthermore, the corresponding feature vector of this process model is stored.
 * 
 * @author Cindy Fähnrich
 *
 */
public class UnitDataFeatureVector<V extends Object> extends UnitData<V> implements
		IUnitDataFeatureVector<V> {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(UnitDataFeatureVector.class.getName());
	
	private FeatureVector featureVector;
	
	/**
	 * Creates an empty result with <code>null</code> elements.
	 */
	public UnitDataFeatureVector() {
		super();
		featureVector = new FeatureVector();
	}
	
	/**
	 * Creates a result type with the given value as result and <code>null</code>
	 * as the database id of the used process model.
	 *
	 * @param value the result of the last {@link IUnit}
	 */
	public UnitDataFeatureVector(V value) {
		super(value);
	}

	/**
	 * A result type with the given values.
	 * @param value the result of the {@link IUnit}
	 * @param dbId the database id of the {@link Representation} used for result value calculation
	 */
	public UnitDataFeatureVector(V value, String dbId) {
		super(value, dbId);
	}
	
	@Override
	public FeatureVector getFeatureVector() {
		return featureVector;
	}
	
	/**
	 * Returns the {@link FeatureVector} in form of a {@ProcessInstance}
	 * for clustering
	 * @return
	 */
	public ProcessInstance getInstance(){
		ProcessInstance inst = new ProcessInstance(featureVector.size(), featureVector.getNumericFeatures(), featureVector.getStringFeatures());
		inst.process = (ProcessModel) this.getValue();
		return inst;
	}

	/**
	 * Sets the feature vector of this unit data
	 * @param features
	 * 			the feature vector to set
	 */
	@Override
	public void setFeatureVector(FeatureVector features) {
		featureVector = features;		
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder(super.toString());
		builder.append("\nfeature vector: ");
		builder.append(featureVector.toString());
		builder.append("\n");		
		return builder.toString();
	}

	/**
	 * Returns the features in form of an array of doubles
	 * @return the features
	 */
	public double[] getNomFeatures() {
		return featureVector.getNumericFeatures();
	}
	
	/**
	 * Returns the features in form of an array of doubles
	 * @return the features
	 */
	public String[] getStrFeatures() {
		return featureVector.getStringFeatures();
	}

}
