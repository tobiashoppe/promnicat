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
package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.classification;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnActivity;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jbpt.pm.bpmn.BpmnEvent;
import org.jbpt.pm.bpmn.BpmnEventTypes.BPMN_EVENT_TYPES;
import org.jbpt.pm.bpmn.BpmnMessageFlow;
import org.jbpt.pm.bpmn.EventBasedXorGateway;
import org.jbpt.pm.bpmn.Subprocess;

/**
 * This class checks a given {@link Bpmn} process model for conformance according to
 * the BPMN Conformance Levels defined in the BPMN specification. In difference to the 
 * definition of the specification this conformance level checker only find the 
 * lowest level (descriptive modeling < analytic modeling < common executable modeling)
 * and all upper levels are not marked as conform.
 * 
 * @author Tobias Hoppe
 */
public class BpmnConformanceLevelChecker {
	
	private Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model = null;
	private Boolean isDescriptiveConform = null;
	private Boolean isAnalyticConform = null;
	private Boolean isExecutableConform = null;
	
	public BpmnConformanceLevelChecker(Bpmn<BpmnControlFlow<FlowNode>, FlowNode> model) {
		if(null == model) {
			throw new IllegalArgumentException("The provided model must be a valid BPMN Model and not null!");
		}
		this.model = model;
	}

	/**
	 * @return the model used by this analyzer instance
	 */
	public Bpmn<BpmnControlFlow<FlowNode>, FlowNode> getModel() {
		return model;
	}
	
	/**
	 * @return <code>true</code> if the model only uses model elements conform to the
	 * descriptive modeling standard as defined in the BPMN specification version 2.0,
	 * <code>false</code> otherwise.
	 */
	public boolean isDescriptiveModelingConform() {
		//check for already calculated results
		if (this.isDescriptiveConform != null) {
			return this.isDescriptiveConform;
		}
		//check for already calculated results
		if((this.isAnalyticConform != null && this.isAnalyticConform)
				|| (this.isExecutableConform != null && this.isExecutableConform)) {
			this.isDescriptiveConform = false;
			return this.isAnalyticConform;
		}
		
		//or as well as event based xor gateways are not allowed
		if (!this.model.filter(OrGateway.class).isEmpty() ||
				(!this.model.filter(EventBasedXorGateway.class).isEmpty())) {
			this.isDescriptiveConform = false;
			return this.isDescriptiveConform;
		}
		
		if (!filterByEvents() || !filterByTasks()) {
			this.isDescriptiveConform = false;
			return this.isDescriptiveConform;
		}

		//default or conditional edges as well as attached events are not allowed
		for (ControlFlow<FlowNode> edge : this.model.getControlFlow()) {
			if(edge instanceof BpmnControlFlow<?>) {
				if (((BpmnControlFlow<FlowNode>)edge).isDefault() ||
						//see empty string condition as edge without condition
						(((BpmnControlFlow<FlowNode>) edge).getCondition() != null && !((BpmnControlFlow<FlowNode>) edge).getCondition().equals("")) ||
						((BpmnControlFlow<FlowNode>) edge).hasAttachedEvent()) {
					this.isDescriptiveConform = false;
					return this.isDescriptiveConform;
				}
			}
		}
		
		this.setDescriptiveConform();
		return this.isDescriptiveConform;
	}

	/**
	 * @return <code>true</code> if the model only uses model elements conform to the
	 * analytic modeling standard as defined in the BPMN specification version 2.0,
	 * <br/><code>false</code> otherwise.
	 */
	public boolean isAnalyticModelingConform() {
		//check for already calculated results
		if (this.isAnalyticConform != null) {
			return this.isAnalyticConform;
		}
		
		//check for already calculated results
		if(this.isDescriptiveModelingConform() || (this.isExecutableConform != null && this.isExecutableConform)) {
			this.isAnalyticConform = false;
			return this.isAnalyticConform;
		}

		@SuppressWarnings("unchecked")
		Collection<Subprocess> subProcesses = (Collection<Subprocess>) this.model.filter(Subprocess.class);
		for(Subprocess subProcess : subProcesses) {
			if ((!subProcess.isCollapsed()) &&
					(subProcess.isStandardLoop() || subProcess.isParallelMultiple() || subProcess.isSequentialMultiple())) {
				this.setCommonExecutableConform();
				return this.isAnalyticConform;
			}
		}
		
		this.setAnalyticConform();
		return this.isAnalyticConform;
	}
	
	/**
	 * @return <code>true</code> if the model only uses model elements conform to the
	 * common executable modeling standard as defined in the BPMN specification version 2.0,
	 * <br/><code>false</code> otherwise.
	 */
	public boolean isCommonExecutableModelingConform() {
		//check for already calculated results
		if (this.isExecutableConform != null) {
			return this.isExecutableConform;
		}
		//check for already calculated results
		if(this.isDescriptiveModelingConform() || this.isAnalyticModelingConform()) {
			this.isAnalyticConform = false;
			return this.isAnalyticConform;
		}
		
		this.isExecutableConform = true;
		return this.isExecutableConform;
	}

	/**
	 * @return <code>false</code> if the model contains any intermediate events as well as
	 * start or end events not from the following types:
	 * blank(none), message, timer(only start  allowed), and terminate (only end allowed).
	 * <br/><code>true</code> otherwise.
	 */
	private boolean filterByEvents() {
		//intermediate events are not allowed
		Collection<Event> startEvents = new ArrayList<Event>();
		Collection<Event> endEvents = new ArrayList<Event>();
		for( FlowNode entry : this.model.getEntries()) {
			if (entry instanceof Event) {
				startEvents.add((Event) entry);
			}
		}
		for( FlowNode exit : this.model.getExits()) {
			if (exit instanceof Event) {
				endEvents.add((Event) exit);
			}
		}
		//if an event is neither entry nor exit it is intermediate
		Collection<Event> startOrEndEvents = new ArrayList<Event>(startEvents);
		startOrEndEvents.addAll(endEvents);
		if (!startOrEndEvents.containsAll(this.model.getEvents())) {
			return false;
		}
		//only blank(none) events, message start/end events as well as timer start events and terminate end events are allowed
		for (Event event : startOrEndEvents) {
			if (event instanceof BpmnEvent) {
				if (((BpmnEvent) event).getEventType() != BPMN_EVENT_TYPES.BLANK &&
						((BpmnEvent) event).getEventType() != BPMN_EVENT_TYPES.MESSAGE) {
					if ((((BpmnEvent) event).getEventType() == BPMN_EVENT_TYPES.TIMER && startEvents.contains(event)) ||
							(((BpmnEvent) event).getEventType() == BPMN_EVENT_TYPES.TERMINATE && endEvents.contains(event))) {
						continue;
					}
					return false;					 
				}
			}
		}
		return true;
	}

	/**
	 * @return <code>false</code> if the model contains any loop, multiple instance, send, or receive tasks.
	 * <br/><code>true</code> otherwise.
	 */
	private boolean filterByTasks() {
		//loop and multiple instance tasks are not allowed
		for(Activity activity : this.model.getActivities()) {
			if(activity instanceof BpmnActivity) {
				if (((BpmnActivity) activity).isSequentialMultiple() ||
						((BpmnActivity) activity).isParallelMultiple() ||
						((BpmnActivity) activity).isStandardLoop()) {
					return false;
				}
			}
			
		}
		
		//send and receive tasks are not allowed
		for(BpmnMessageFlow msgFlowEdge : this.model.getMessageflows()) {
			if(msgFlowEdge.getSource() instanceof Activity ||
					msgFlowEdge.getTarget() instanceof Activity) {
				return false;
			}
		}
		return true;
	}

	/**
	 * set current {@link ProcessModel} as analytic conform
	 */
	private void setAnalyticConform() {
		this.isAnalyticConform = true;
		this.isDescriptiveConform = false;
		this.isExecutableConform = false;
	}
	
	/**
	 * set current {@link ProcessModel} as descriptive conform
	 */
	private void setDescriptiveConform() {
		this.isAnalyticConform = false;
		this.isDescriptiveConform = true;
		this.isExecutableConform = false;
	}
	
	/**
	 * set current {@link ProcessModel} as common executable conform
	 */
	private void setCommonExecutableConform() {
		this.isAnalyticConform = false;
		this.isDescriptiveConform = false;
		this.isExecutableConform = true;
	}
}
