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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.analysisModules.clustering.FeatureVector;
import de.uni_potsdam.hpi.bpt.promnicat.util.FeatureConfig;
import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.util.ProcessFeatureConstants;
import de.uni_potsdam.hpi.bpt.promnicat.util.ProcessMetricConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.unitData.IUnitDataFeatureVector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.unitData.IUnitDataProcessMetrics;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.unitData.UnitDataFeatureVector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;

/**
 * This class transforms the given metrics of a {@link ProcessModel} in a
 * corresponding feature vector.
 * 
 * The expected input type is {@link IUnitDataProcessMetrics}<
 * {@link ProcessModel}>. The output type is the same as the input type.
 * 
 * @author Cindy Fähnrich
 * 
 */
public class ModelToFeatureVectorUnit implements
		IUnit<IUnitData<Object>, IUnitData<Object>> {

	private Logger logger = Logger.getLogger(ModelToFeatureVectorUnit.class
			.getName());

	FeatureConfig config;

	public ModelToFeatureVectorUnit(FeatureConfig conf) {
		super();
		config = conf;
	}

	@Override
	public IUnitData<Object> execute(IUnitData<Object> input)
			throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException(
					"Got an invalid null pointer input!");
		}
		if (input.getValue() == null) {
			logger.warning("Got no model as input for feature vector creation");
			return input;
		}
		if (!(input instanceof IUnitDataProcessMetrics<?>)) {
			throw new IllegalTypeException(IUnitDataProcessMetrics.class,
					input.getClass(), "Got wrong input type in"
							+ this.getName());
		}
		// transform metrics into feature vectors
		FeatureVector features = transformIntoFeatureVector((IUnitDataProcessMetrics<Object>) input);
		IUnitDataFeatureVector<Object> result = new UnitDataFeatureVector<Object>(
				((IUnitDataProcessMetrics<Object>) input).getValue(),
				input.getDbId());
		result.setFeatureVector(features);
		return result;
	}

	@Override
	public String getName() {
		return "ModelToFeatureVectorUnit";
	}

	@Override
	public Class<?> getInputType() {
		return ProcessModel.class;
	}

	@Override
	public Class<?> getOutputType() {
		return ProcessModel.class;
	}

	/**
	 * Selects from the input (the given metrics) the corresponding metrics as
	 * stated in the configuration and transforms them into a
	 * {@link FeatureVector}
	 * 
	 * @param input
	 *            with the process metrics from the former unit
	 * @return the result {@link FeatureVector}
	 */
	public FeatureVector transformIntoFeatureVector(
			IUnitDataProcessMetrics<?> input) {
		FeatureVector features = new FeatureVector();
		// create here
		ArrayList<ProcessFeatureConstants.METRICS> metrics = config
				.getSelectedMetrics();
		ArrayList<ProcessFeatureConstants.PROCESS_LABELS> labels = config
				.getSelectedLabels();
		for (ProcessMetricConstants.METRICS metric : metrics) {
			features.addNumericFeature(metric.getAttribute(input));
		}
		for (ProcessFeatureConstants.PROCESS_LABELS label : labels) {
			features.addStringFeature(label.getAttribute(input));
		}
		return features;
	}

}
