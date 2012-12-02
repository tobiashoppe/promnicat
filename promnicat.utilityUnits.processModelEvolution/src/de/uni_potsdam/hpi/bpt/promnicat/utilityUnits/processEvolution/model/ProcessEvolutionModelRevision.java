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
package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processEvolution.model;

import java.util.HashMap;
import java.util.Set;

import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.util.ProcessMetricConstants;


/**
 * The revision of a {@link ProcessEvolutionModel}. It contains the values of the metrics
 * it was analyzed by and the {@link ProcessModel} it is represented by.
 * 
 * @author Tobias Metzke
 *
 */
/**
 * @author Tobi
 *
 */
public class ProcessEvolutionModelRevision {

	
	/**
	 * exception message if two revisions can not be merged.
	 */
	private static final String REVISIONS_NOT_EQUAL_EXCEPTION_MESSAGE = "Revisions do not have the same revision number and are therefore not equal, can not be merged.\n";


	/**
	 * the metrics the revision was analyzed by and their values.
	 */
	private HashMap<String, Double> metrics = new HashMap<String, Double>();
	
	/**
	 * the number of this revision
	 */
	private int revisionNumber;
	
	/**
	 * the representation of this revision 
	 */
	private ProcessModel processModel;
	
	/**
	 * default constructor
	 * @param revisionNumber the number of this revision
	 */
	public ProcessEvolutionModelRevision(int revisionNumber) {
		setRevisionNumber(revisionNumber);
	}

	/**
	 * merge constructor to combine two revisions into a new one containing all
	 * attributes of both 
	 * @param revision1 the first revision to merge with the second one
	 * @param revision2 the second revision to merge with the first one
	 */
	public ProcessEvolutionModelRevision(ProcessEvolutionModelRevision revision1,
			ProcessEvolutionModelRevision revision2) {
		// constructor calls have to go first in a constructor
		this(revision1.getRevisionNumber());
		// merge the two revisions if they have the same number
		if (revision1.getRevisionNumber() != revision2.getRevisionNumber())
			throw new RuntimeException(REVISIONS_NOT_EQUAL_EXCEPTION_MESSAGE);
		else {
			// go through the first revision and add the maximum value of a metric
			// if it occurs in both to avoid that 0-values override real estimated values
			for (String metricKey : revision1.getMetricKeys()) {
				if (revision2.getMetricKeys().contains(metricKey))
					add(metricKey, Math.max(revision1.get(metricKey), revision2.get(metricKey)));
				else
					add(metricKey, revision1.get(metricKey));
			}
			// add metric values that occur in the second revision only
			for (String metricKey : revision2.getMetricKeys())
				if (!getMetricKeys().contains(metricKey))
					add(metricKey, revision2.get(metricKey));
		}
	}

	/**
	 * add a {@link METRICS} and its values to the revision
	 * @param metric the {@link METRICS} to add
	 * @param metricValue the value of the metric
	 */
	public void add(ProcessMetricConstants.METRICS metric, double metricValue) {
		metrics.put(metric.name(), metricValue);
	}
	
	/**
	 * add a metric that is not of type {@link METRICS} and its value
	 * @param metricKey the key to identify the metric by
	 * @param metricValue the value of the metric
	 */
	public void add(String metricKey, double metricValue) {
		metrics.put(metricKey, metricValue);
	}
	
	/**
	 * @return the metrics' identifiers
	 */
	public Set<String> getMetricKeys() {
		return metrics.keySet();
	}

	/**
	 * @return the number of this revision
	 */
	public int getRevisionNumber() {
		return revisionNumber;
	}

	/**
	 * change the number of this revision
	 * @param revisionNumber the number of this revision
	 */
	public void setRevisionNumber(int revisionNumber) {
		this.revisionNumber = revisionNumber;
	}

	/**
	 * @param metric the {@link METRICS} to get the value of
	 * @return the value of the given metric in this revision
	 */
	public Double get(ProcessMetricConstants.METRICS metric) {
		return metrics.get(metric.name());
	}
	
	/**
	 * @param metricKey the metric to get the value of
	 * @return the value of the given metric in this revision
	 */
	public Double get(String metricKey) {
		return metrics.get(metricKey);
	}

	/**
	 * add the representation of this model
	 * @param model the {@link ProcessModel} to set
	 */
	public void setProcessModel(ProcessModel model) {
		this.processModel = model;
	}
	
	/**
	 * @return the representation of this revision
	 */
	public ProcessModel getProcessModel() {
		return processModel;
	}
	
	/**
	 * @return the metrics and their values in this revision
	 */
	public HashMap<String, Double> getMetrics() {
		return metrics;
	}

}
