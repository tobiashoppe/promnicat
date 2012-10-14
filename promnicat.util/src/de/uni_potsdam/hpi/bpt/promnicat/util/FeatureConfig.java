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
package de.uni_potsdam.hpi.bpt.promnicat.util;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Configuration class to select process metrics and other features to be calculated and 
 * assign a weight to them.
 * 
 * @author Cindy Fähnrich
 *
 */
public class FeatureConfig {

	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(FeatureConfig.class.getName());
	
	/** contains the selected metrics as attributes for clustering */
	private ArrayList<ProcessFeatureConstants.METRICS> metrics = new ArrayList<ProcessFeatureConstants.METRICS>();
	
	/** contains the selected labels as attributes for clustering */
	private ArrayList<ProcessFeatureConstants.PROCESS_LABELS> labels = new ArrayList<ProcessFeatureConstants.PROCESS_LABELS>();
	
	/** 
	 * adds a string attribute/label as cluster attribute
	 * @param label the string attribute/label 
	 * @param weight the weight of this attribute
	 */
	public void addLabel(ProcessFeatureConstants.PROCESS_LABELS label){
		labels.add(label);
	}
	
	/**
	 * returns the string attributes/labels stelected
	 * @return the string attributes/labels selected
	 */
	public ArrayList<ProcessFeatureConstants.PROCESS_LABELS> getSelectedLabels(){
		return labels;
	}
	
	/** 
	 * adds a process metric as cluster attribute
	 * @param met the process metric
	 * @param weight the weight of this attribute
	 */
	public void addMetric(ProcessFeatureConstants.METRICS met){
		metrics.add(met);
	}
	
	/**
	 * returns the process metrics selected
	 * @return the process metrics selected
	 */
	public ArrayList<ProcessFeatureConstants.METRICS> getSelectedMetrics(){
		return metrics;
	}
}
