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

import java.util.Collection;
import java.util.Iterator;

import org.jbpt.pm.Activity;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.ProcessModel;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.ProcessMetricConstants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.unitData.IUnitDataProcessMetrics;

/**
 *  Class containing all necessary constants regarding the configuration of the 
 * {@link ModelToFeatureVectorUnit}. Inherits from {@link ProcessMetricConstants} 
 * to also include the process metrics
 * 
 * @author Cindy Fähnrich
 *
 */
public class ProcessFeatureConstants extends ProcessMetricConstants {

	public static final String PROCESS_NAME = "ProcessName";
	public static final String PROCESS_DESCRIPTION = "ProcessDescription";
	public static final String FIRST_FLOWNODE_LABEL = "FirstFlowNodeLabel";
	public static final String ALL_ACTIVITY_LABELS = "AllActivityLabels";
	
	public enum PROCESS_LABELS{
		PROCESS_NAME(ProcessFeatureConstants.PROCESS_NAME){
			/** returns the name of the process model */
			public String getAttribute(IUnitDataProcessMetrics<?> input){
				String name = ((ProcessModel)input.getValue()).getName();
				if ((name == null) || (name.equals(""))){
					return "no title";
				}
				return name;
			}
		},
		PROCESS_DESCRIPTION(ProcessFeatureConstants.PROCESS_DESCRIPTION){
			/** returns the name of the process model */
			public String getAttribute(IUnitDataProcessMetrics<?> input){
				String description = ((ProcessModel)input.getValue()).getDescription();
				if ((description == null) || (description.equals(""))){
					return "no description";
				}
				return description;
			}
		},
		FIRST_FLOWNODE_LABEL(ProcessFeatureConstants.FIRST_FLOWNODE_LABEL){
			/** returns the label of the first flownode of the process model */
			public String getAttribute(IUnitDataProcessMetrics<?> input){
				String result = ((ProcessModel)input.getValue()).getName();
				result = "";
				Collection<FlowNode> coll = ((ProcessModel)input.getValue()).getFlowNodes();
				Iterator<FlowNode> it = coll.iterator();
				FlowNode n;
				if (coll.size() >0){
					if ((n = it.next()) != null){
						result = n.getName();
						if (result == ""){
							return "no title";
						}
						return result;
					} else {
						return "no name";
					}
				} 
				return "no name";
			}
		},
		ALL_ACTIVITY_LABELS(ProcessFeatureConstants.ALL_ACTIVITY_LABELS){
			/** returns the label of the all activities of the process model */
			public String getAttribute(IUnitDataProcessMetrics<?> input){
				String activityLabels = ((ProcessModel)input.getValue()).getName();
				activityLabels = "";
				Collection<FlowNode> coll = ((ProcessModel)input.getValue()).getFlowNodes();
				Iterator<FlowNode> it = coll.iterator();
				FlowNode n;
				while (it.hasNext()){
					n = it.next();
					if (n instanceof Activity){
						if ((!n.getName().equals("")) || (n.getName() != null))
						activityLabels += " " + n.getName();
					}
				}
				if (activityLabels.equals("")){
					return "no activty labels";
				}
				
				return activityLabels;
			};
		};
		
		private String description;
	     
		PROCESS_LABELS(String description) {
			this.description = description;
	    }

		public String toString() {
			return description;
		}
		public abstract String getAttribute(IUnitDataProcessMetrics<?> input);
	};
}
