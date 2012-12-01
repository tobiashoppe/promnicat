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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.util.IllegalTypeException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.AbstractUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.IUnit;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitData;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataProcessMetrics;

/**
 * This class calculates a set of metrics for a {@link ProcessModel}.
 * 
 * The expected input type is {@link IUnitDataProcessMetrics}<{@link ProcessModel}>.
 * The output type is the same as the input type.
 * 
 * @author Tobias Hoppe
 *
 */
public class ProcessModelMetricsCalculatorUnit extends AbstractUnit implements IUnit<IUnitData<Object>, IUnitData<Object> > {

	/**
	 * flag indicates whether to include available sub process in metric calculation or not.
	 */
	private boolean includeSubProcesses = true;

	/**
	 * logger used by this class
	 */
	private Logger logger = Logger.getLogger(ProcessModelLabelExtractorUnit.class.getName());

	/**
	 * internal list of metrics to calculate during execution
	 */
	private Collection<ProcessMetricConstants.METRICS> metricsToCalculate = null;
	
	/**
	 * Creates a new unit which calculates all available metrics in the
	 * {@link #execute(IUnitData)} method and includes all available sub process
	 * for metric calculation.
	 */
	public ProcessModelMetricsCalculatorUnit() {
		this.metricsToCalculate = new ArrayList<ProcessMetricConstants.METRICS>();
	}
	
	/**
	 * Creates a new unit which calculates all available metrics in the
	 * {@link #execute(IUnitData)} method
	 */
	public ProcessModelMetricsCalculatorUnit(boolean includeSubProcesses) {
		this.metricsToCalculate = new ArrayList<ProcessMetricConstants.METRICS>();
		this.includeSubProcesses = includeSubProcesses;
	}

	/**
	 * Creates a new unit which calculates only the given metric in the
	 * {@link #execute(IUnitData)} method.
	 * @param metricToCalculate the metric to calculate
	 */
	public ProcessModelMetricsCalculatorUnit(ProcessMetricConstants.METRICS metricToCalculate, boolean includeSubProcesses) {
		this.metricsToCalculate = new ArrayList<ProcessMetricConstants.METRICS>();
		this.metricsToCalculate.add(metricToCalculate);
		this.includeSubProcesses = includeSubProcesses;
	}

	/**
	 * Creates a new unit, which calculates all given metrics in the
	 * {@link #execute(IUnitData)} method. If all available metrics should be
	 * calculate use {@link ProcessModelMetricsCalculatorUnit} constructor without parameters.
	 * @param metricsToCalculate a list of metrics to calculate
	 */
	public ProcessModelMetricsCalculatorUnit(Collection<ProcessMetricConstants.METRICS> metricsToCalculate, boolean includeSubProcesses) {
		this.metricsToCalculate = metricsToCalculate;
		this.includeSubProcesses = includeSubProcesses;
	}
	
	@Override
	public IUnitData<Object> execute(IUnitData<Object> input) throws IllegalTypeException {
		if (input == null) {
			throw new IllegalArgumentException("Got an invalid null pointer input!");
		}
		if (input.getValue() == null){
			logger.warning("Got no model as input for metric calculation");
			return input;
		}
		if (!(input instanceof IUnitDataProcessMetrics<?>)){
			throw new IllegalTypeException(IUnitDataProcessMetrics.class, input.getClass(), "Got wrong input type in " + this.getName());
		}
		ProcessMetricsCalculator metricsCalculator = new ProcessMetricsCalculator();
		if (this.metricsToCalculate.isEmpty()) {
			metricsCalculator.calculateAllProcessMetrics((IUnitDataProcessMetrics<Object>) input, this.includeSubProcesses);
		} else {
			metricsCalculator.calculateProcessMetrics(this.metricsToCalculate, (IUnitDataProcessMetrics<Object>) input, this.includeSubProcesses);
		}
		return input;
	}

	@Override
	public Class<?> getInputType() {
		return ProcessModel.class;
	}

	@Override
	public String getName() {
		return "ProcessModelMetricsCalculatorUnit";
	}

	@Override
	public Class<?> getOutputType() {
		return ProcessModel.class;
	}
	
	/**
	 * resets all internal variables.
	 */
	public void reset() {
		this.metricsToCalculate.clear();
	}

}
