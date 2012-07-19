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
package de.uni_potsdam.hpi.bpt.promnicat.modelConverter;

import java.util.ArrayList;
import java.util.Collection;

import org.jbpt.petri.Flow;
import org.jbpt.petri.Node;
import org.jbpt.petri.PetriNet;
import org.jbpt.petri.Place;
import org.jbpt.petri.Transition;
import org.jbpt.pm.Activity;
import org.jbpt.pm.AndGateway;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.DataNode;
import org.jbpt.pm.Event;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.Gateway;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.AdHocOrdering;
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jbpt.pm.bpmn.BpmnEvent;
import org.jbpt.pm.bpmn.BpmnMessageFlow;
import org.jbpt.pm.bpmn.Subprocess;
import org.jbpt.throwable.TransformationException;

/**
 * This class converts a {@link Bpmn} model to the corresponding {@link PetriNet}.
 * TODO handle data flow(in subclass?!)
 * 
 * @author Tobias Hoppe
 *
 */
public class BpmnToPetriNetConverter extends AbstractModelToPetriNetConverter {

	/**
	 * Transforms the given {@link ProcessModel} into a {@link PetriNet}.
	 * {@link DataNode}s are not converted.
	 * <b><br/>Assumptions:</b><br/>
	 * TODO check
	 * - Model does not contain any {@link OrGateway}s or event-based-{@link Subprocess}es
	 * 
	 * @param model to transform
	 * @return the created {@link PetriNet}
	 * @throws TransformationException if assumptions are violated.
	 */
	@Override
	public PetriNet convertToPetriNet(ProcessModel model) throws TransformationException {
		if(!(model instanceof Bpmn<?, ?>)) {
			throw new IllegalArgumentException(THE_GIVEN_PROCESS_MODEL_CAN_NOT_BE_HANDELED_BY_THIS_CONVERTER);
		}
		@SuppressWarnings("unchecked")
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> transformedModel = (Bpmn<BpmnControlFlow<FlowNode>, FlowNode>) prepareProcessModel(model);
		if(transformedModel == null) {
			return null;
		}
		//create places and transitions according to the flow nodes of the model
		convertFlowNodes(transformedModel.getFlowNodes());
		//add edges according to the control flow of the model
		convertControlFlowEdges(transformedModel.getControlFlow());
		convertMessageFlowEdges(transformedModel);
		return this.petriNet;
	}
	
	/**
	 * Transform the {@link BpmnMessageFlow}s of the given {@link Bpmn}.
	 * @param transformedModel model to handle
	 */
	protected void convertMessageFlowEdges(Bpmn<BpmnControlFlow<FlowNode>, FlowNode> transformedModel) {
		for(BpmnMessageFlow messageFlow : ((Bpmn<?,?>) transformedModel).getMessageFlowEdges()) {
			Node source = this.nodeMapping.get(messageFlow.getSource());
			Node target = this.nodeMapping.get(messageFlow.getTarget());
			if ((source instanceof Place && target instanceof Transition)
					|| (source instanceof Transition && target instanceof Place)) {
				this.petriNet.addFreshFlow(source, target);
			}
			else if ((source instanceof Place && target instanceof Place)) {
				this.connectTwoPlaces((Place) source, (Place) target, "transitionForMsgFlow" + messageFlow.getId());
			}
			else if ((source instanceof Transition && target instanceof Transition)) {
				this.connectTwoTransitions((Transition) source, (Transition) target, "placeForMsgFlow" + messageFlow.getId());
			}
		}
	}

	/**
	 * Transform {@link Activity}s or {@link Event}s with multiple outgoing edges
	 * by inserting additional {@link Gateway}s.
	 * @param model to pre-process
	 * @param node to analyze
	 * @param outgoingControlFlows outgoing edges of current node
	 * @throws TransformationException if an {@link OrGateway} shall be created
	 */
	@Override
	protected void preProcessMultiOutgoingEdges(ProcessModel model, FlowNode node,
			Collection<ControlFlow<FlowNode>> outgoingControlFlows) throws TransformationException {
		String name = "post_" + node.getName();
		Gateway g = new AndGateway(name);
		Collection<Boolean> conditional = new ArrayList<Boolean>();
		Collection<Boolean> attachedEventEdges = new ArrayList<Boolean>();
		for(ControlFlow<FlowNode> edge : outgoingControlFlows) {
			if(!((BpmnControlFlow<FlowNode>)edge).hasCondition()) {
				conditional.add(false);
			}
			if(!((BpmnControlFlow<FlowNode>)edge).hasAttachedEvent()) {
				attachedEventEdges.add(false);
			}
		}
		//if there is only one edge without attached event return
		if(outgoingControlFlows.size() - 1 <= attachedEventEdges.size()) {
			return;
		}
		if(conditional.size() >= outgoingControlFlows.size() - 1) {
			g = new OrGateway(name);
		}
		//add gateway only for edges without attached events
		for(ControlFlow<FlowNode> edge : outgoingControlFlows) {
			if (!((BpmnControlFlow<FlowNode>)edge).hasAttachedEvent()) {
				edge.setSource(g);
			}
		}
		model.addControlFlow(node, g);
	}
	
	/**
	 * Apart from classical {@link Activity}s, {@link Subprocess}es are handled.
	 * @see AbstractModelToPetriNetConverter#convertActivity(Activity)
	 */
	@Override
	protected void convertActivity(Activity activity) throws TransformationException {
		//handle different subprocesses
		if (activity instanceof Subprocess) {
			if (((Subprocess) activity).isCollapsed()) {
				super.convertActivity(activity);
			} else if (((Subprocess) activity).isAdhoc()) {
				convertAdHocSubprocess((Subprocess) activity);
			} else if (((Subprocess) activity).isEventDriven()) {
				convertEventDrivenSubprocess((Subprocess) activity);
			} else {				
				convertSubprocess((Subprocess)activity);
			}
		} else {
			//handle activities that are not a subprocess
			super.convertActivity(activity);
		}
	}
	
	/**
	 * @param subprocess event-driven subprocess to convert
	 * @throws TransformationException
	 */
	protected void convertEventDrivenSubprocess(Subprocess subprocess) throws TransformationException {
		// TODO Auto-generated method stub
		throw new TransformationException("Event driven subprocess could not be handled.");
	}

	/**
	 * Convert an adhoc subprocess into it's corresponding {@link PetriNet} part.
	 * Adhoc subprocesses with parallel execution order are transformed into a
	 * {@link Transition} putting a marking into each place followed by the 
	 * mapping of the corresponding node. If it is sequential execution order,
	 * all nodes share the same initial {@link Place}. The end of the adhoc subprocess
	 * is mapped to a {@link Transition} which took all markings.
	 * @param subprocess adhoc subprocess to convert
	 * @throws TransformationException if adhoc subprocess' internal model can not be converted
	 */
	protected void convertAdHocSubprocess(Subprocess subprocess) throws TransformationException {
		ProcessModel model = subprocess.getModel();
		Collection<ControlFlow<FlowNode>> edges = model.getOutgoingControlFlow(subprocess);
		ControlFlow<FlowNode> outgoingControlFlowEdge = null;
		Collection<Transition> attachedEvents = new ArrayList<Transition>();
		//identify attached events
		for(ControlFlow<FlowNode> edge : edges) {
			if(edge instanceof BpmnControlFlow) {
				if (((BpmnControlFlow<?>) edge).hasAttachedEvent()) {
					Transition t = new Transition(((BpmnControlFlow<?>) edge).getAttachedEvent().getName());
					attachedEvents.add(t);
					this.nodeMapping.put(((BpmnControlFlow<?>) edge).getAttachedEvent(), t);
				} else {
					outgoingControlFlowEdge = edge;
				}
			} else {
				outgoingControlFlowEdge = edge;
			}
		}
		//create transition for control flow end and start place
		Transition end = new Transition("adhocEnd");
		Place start = new Place("adhocStart");
		this.nodeMapping.put(subprocess, start);
		//transform inner model of adhoc subprocess and add result to current petri net
		PetriNet pn = new BpmnToPetriNetConverter().convertToPetriNet(subprocess.getSubProcess());
		this.petriNet.addNodes(pn.getNodes());
		for(Flow edge : pn.getEdges()) {
			this.petriNet.addFreshFlow(edge.getSource(), edge.getTarget());
		}
		if(subprocess.getAdhocOrder().equals(AdHocOrdering.Parallel)){
			handleAdhocParallelExecution(end, start, pn, attachedEvents);
		} else {
			handleAdhocSequentialExecution(end, start, pn, attachedEvents);
		}
		//hanlde mapping of outgoing control flow
		if(outgoingControlFlowEdge != null){
			Activity dummy = new Activity("placeholder");
			outgoingControlFlowEdge.setSource(dummy);
			this.nodeMapping.put(dummy, end);
		}
	}

	/**
	 * Connect each independent flow of adhoc {@link Subprocess} to the final {@link PetriNet}
	 * regarding parallel adhoc {@link Subprocess} execution order.
	 * @param end of adhoc subprocess
	 * @param start of ad hoc subprocess
	 * @param pn {@link PetriNet} of adhoc {@link Subprocess}' inner model.
	 * @param attachedEvents {@link Transition}s mapped from attached events
	 */
	private void handleAdhocParallelExecution(Transition end, Place start, PetriNet pn, Collection<Transition> attachedEvents) {
		Transition tSplit = new Transition("adHocSplit");
		this.petriNet.addFlow(start, tSplit);
		for(Node node : pn.getSourceNodes()) {
			//add edge from place p to each start of adhoc subprocess' independent flows
			// and from end of each flow to place p
			Place p = new Place();
			this.petriNet.addFlow(tSplit, p);
			if (node instanceof Transition) {
				this.petriNet.addFlow(p, (Transition) node);
			} else {
				connectTwoPlaces(p, (Place) node, "");
			}
			for(Node n : pn.getSinkNodes()) {
				if (PetriNet.DGA.hasPath(pn, node, n)) {
					if (node instanceof Transition) {
						this.petriNet.addFlow((Transition) n, p);					
					} else {
						connectTwoPlaces((Place) n, p, "");
					}
				}
			}				
			this.petriNet.addFlow(p, end);
			//add flows to attached events
			for(Transition t : attachedEvents) {
				this.petriNet.addFlow(p, t);
			}
		}
	}

	/**
	 * Connect each independent flow of adhoc {@link Subprocess} to the final {@link PetriNet}
	 * regarding sequential adhoc {@link Subprocess} execution order.
	 * @param end of adhoc subprocess
	 * @param start of ad hoc subprocess
	 * @param pn {@link PetriNet} of adhoc {@link Subprocess}' inner model.
	 * @param attachedEvents {@link Transition}s mapped from attached events
	 */
	private void handleAdhocSequentialExecution(Transition end, Place start, PetriNet pn, Collection<Transition> attachedEvents) {
		//add edge from start of adhoc subprocess to each start of adhoc 
		// subprocess' independent flows and from start to end of adhoc subprocess
		for(Node node : pn.getSourceNodes()) {
			if (node instanceof Transition) {
				this.petriNet.addFlow(start, (Transition) node);
			} else {
				connectTwoPlaces(start, (Place) node, "");					
			}
			this.petriNet.addFlow(start, end);
		}
		//add edge from each end of adhoc subprocess' independent flows 
		// to adhoc subprocess' start
		for(Node node : pn.getSinkNodes()) {
			if (node instanceof Transition) {
				this.petriNet.addFlow((Transition) node, start);
			} else {
				connectTwoPlaces((Place) node, start, "");					
			}
		}
		//add flows to attached events
		for(Transition t : attachedEvents) {
			this.petriNet.addFlow(start, t);
		}
	}

	/**
	 * Handles attached events before creation of {@link Flow}s.
	 * @see AbstractModelToPetriNetConverter#convertControlFlowEdges(Collection)
	 */
	@Override
	protected void convertControlFlowEdges(Collection<ControlFlow<FlowNode>> edges) {
		Collection<ControlFlow<FlowNode>> edgesToConvert = new ArrayList<ControlFlow<FlowNode>>();
		for(ControlFlow<FlowNode> edge : edges) {
			if ((edge instanceof BpmnControlFlow<?>) && ((BpmnControlFlow<FlowNode>)edge).hasAttachedEvent()) {
				convertAttachedEvent((BpmnControlFlow<FlowNode>) edge);
			} else {
				edgesToConvert.add(edge);
			}
		}
		super.convertControlFlowEdges(edgesToConvert);
	}

	/**
	 * converts the attached {@link BpmnEvent} of the given {@link BpmnControlFlow}.
	 * @param edge containing the attached {@link Event}.
	 */
	protected void convertAttachedEvent(BpmnControlFlow<FlowNode> edge) {
		if (edge.getSource() instanceof Subprocess) {
			//attached event is already mapped to a transition and
			// input of attached event is already connected.
			// Only connect target of edge with transition of attached event.
			Node source = this.nodeMapping.get(edge.getAttachedEvent());
			Node target = this.nodeMapping.get(edge.getTarget());
			if ((source instanceof Place && target instanceof Transition)
					|| (source instanceof Transition && target instanceof Place)) {
				this.petriNet.addFreshFlow(source, target);
			}
			else if ((source instanceof Place && target instanceof Place)) {
				this.connectTwoPlaces((Place) source, (Place) target, "helperTransitionForEdge" + edge.getId());
			}
			else if ((source instanceof Transition && target instanceof Transition)) {
				this.connectTwoTransitions((Transition) source, (Transition) target, "helperPlaceForEdge" + edge.getId());
			}
		} else {
			// transform attached event to transition and
			// connect input/output accordingly
			BpmnEvent attachedEvent = edge.getAttachedEvent();
			Transition attachedEventTransition = new Transition(attachedEvent.getLabel());
			Node source = this.nodeMapping.get(edge.getSource());
			if (source instanceof Place) {
				this.petriNet.addFreshFlow(source, attachedEventTransition);
			} else {
				this.connectTwoTransitions((Transition) source, attachedEventTransition, "helperPlaceForEdge" + edge.getId());
				Place p = this.petriNet.getPreset(attachedEventTransition).iterator().next();
				this.nodeMapping.put(edge.getSource(), p);
				if(!attachedEvent.isInterrupting()) {
					this.petriNet.addFlow(attachedEventTransition, p);
				}
			}
			Node target = this.nodeMapping.get(edge.getTarget());
			if (target instanceof Place) {
				this.petriNet.addFreshFlow(attachedEventTransition, target);
			} else {
				this.connectTwoTransitions(attachedEventTransition ,(Transition) target , "helperPlaceForEdge" + edge.getId());
			}
		}
	}

	/**
	 * Converts a {@link Subprocess} into a {@link PetriNet} fragment by converting an
	 * included subprocess to a {@link PetriNet} and adding this net to the result.
	 * Afterwards, the source nodes of the subprocess' {@link PetriNet} are connected with
	 * a {@link Place} which is mapped as defined by the incoming edges of the {@link Subprocess}
	 * node. This is done in the same manner with the sink {@link Node}s of the {@link PetriNet}
	 * representing the {@link Subprocess}. Therefore, a dummy {@link Activity} is added to the
	 * given {@link ProcessModel} to ensure a correct mapping of the {@link Flow} from the last
	 * {@link Place} of the subprocess' {@link PetriNet} to the following {@link FlowNode} of
	 * the given {@link ProcessModel}.
	 * @param subprocess the {@link Subprocess} to convert
	 * @throws TransformationException if the {@link Subprocess} could not be converted
	 */
	protected void convertSubprocess(Subprocess subprocess) throws TransformationException {
		Bpmn<BpmnControlFlow<FlowNode>, FlowNode> process = subprocess.getSubProcess();
		//convert subprocess to Petri net
		PetriNet pn = new BpmnToPetriNetConverter().convertToPetriNet(process);
		if(pn.getNodes().isEmpty()) {
			//if subprocess is empty handle it like an activity
			super.convertActivity(subprocess);
			return;
		}
		//identify attached events and initialize place for state 'ok' and 'not ok' for each attached event
		ControlFlow<FlowNode> outgoingControlFlowEdge = null;
		Collection<TransformationContext> attachedEventContexts = new ArrayList<TransformationContext>();
		//identify attached events
		for(ControlFlow<FlowNode> edge : subprocess.getModel().getOutgoingControlFlow(subprocess)) {
			if(edge instanceof BpmnControlFlow) {
				if (((BpmnControlFlow<?>) edge).hasAttachedEvent()) {
					String name = ((BpmnControlFlow<?>) edge).getAttachedEvent().getName();
					TransformationContext context = new TransformationContext(((BpmnControlFlow<?>) edge).getAttachedEvent(),
							new Place("placeOkFor" + name), new Place("placeNotOkFor" + name), new Transition(name));
					attachedEventContexts.add(context);
					this.nodeMapping.put(((BpmnControlFlow<?>) edge).getAttachedEvent(), context.getException());
				} else {
					outgoingControlFlowEdge = edge;
				}
			} else {
				outgoingControlFlowEdge = edge;
			}
		}
		Collection<Transition> originalTransitions = pn.getTransitions();
		//new transitions for start and end of subprocess
		Transition subprocessStart = new Transition("subprocessStart" + getNextId());
		Transition subprocessEnd = new Transition("subprocessEnd" + getNextId());

		connectStartAndEndOfSubprocess(subprocess, pn, outgoingControlFlowEdge, subprocessStart, subprocessEnd);		
		//handle attached events
		handleSubprocessAttachedEvent(pn, attachedEventContexts, originalTransitions, subprocessStart, subprocessEnd);
		//add converted subprocess net to final Petri net
		this.petriNet.addNodes(pn.getNodes());
		for (Flow f : pn.getFlow()) {
			this.petriNet.addFreshFlow(f.getSource(), f.getTarget());
		}
	}

	/**
	 * Connects the start and end nodes of the {@link Subprocess} to the given start- and end-{@link Transition}s
	 * @param subprocess {@link Subprocess} to convert
	 * @param pn {@link PetriNet} converted from given {@link Subprocess}
	 * @param outgoingControlFlowEdge outgoing {@link ControlFlow} edge of the {@link Subprocess}
	 * @param subprocessStart start {@link Transition} of {@link Subprocess}
	 * @param subprocessEnd end {@link Transition} of {@link Subprocess}
	 */
	private void connectStartAndEndOfSubprocess(Subprocess subprocess, PetriNet pn,
			ControlFlow<FlowNode> outgoingControlFlowEdge, Transition subprocessStart, Transition subprocessEnd) {
		//insert place connecting all start nodes of subprocess net
		if (pn.getSourceNodes().size() > 1) {
			Place p = new Place("subprocessesStartPlace" + getNextId());
			for(Node n : pn.getSourceNodes()) {
				pn.addFreshFlow(p, n);
			}
			pn.addFlow(subprocessStart, p);
		} else if (!pn.getSourceNodes().isEmpty()){
			pn.addFreshFlow(subprocessStart, pn.getSourceNodes().iterator().next());
		}
		this.nodeMapping.put(subprocess, subprocessStart);
		//add dummy activity to original process model to handle end of subprocess
		if (outgoingControlFlowEdge != null) {
			Activity dummy = new Activity();
			outgoingControlFlowEdge.setSource(dummy);
			//handle integration of end of subprocess into Petri net
			if (pn.getSinkNodes().size() > 1) {
				//insert place to connect all ends
				Place p = new Place("subprocessesEndPlace" + getNextId());
				for(Node n : pn.getSinkNodes()) {
					pn.addFreshFlow(n, p);
				}
				pn.addFlow(p, subprocessEnd);
			} else if (!pn.getSinkNodes().isEmpty()) {
				pn.addFlow((Place) pn.getSinkNodes().iterator().next(), subprocessEnd);
			}
			this.nodeMapping.put(dummy, subprocessEnd);
		}
	}

	/**
	 * Adds skipping {@link Transition}s and {@link Flow}s according to the transformation rules.
	 * @param pn {@link PetriNet} representing the {@link Subprocess} to convert
	 * @param attachedEventContexts the contexts of all attached events
	 * @param originalTransitions the transitions of the converted inner {@link Subprocess}
	 * @param subprocessStart start {@link Transition} of {@link Subprocess}
	 * @param subprocessEnd end {@link Transition} of {@link Subprocess}
	 */
	private void handleSubprocessAttachedEvent(PetriNet pn, Collection<TransformationContext> attachedEventContexts,
			Collection<Transition> originalTransitions, Transition subprocessStart, Transition subprocessEnd) {
		Place lastSubprocessPlace = (Place) pn.getFirstDirectPredecessor(subprocessEnd);
		for(TransformationContext context : attachedEventContexts) {
			//connect places for state 'ok' and 'not ok' accordingly
			pn.addFlow(subprocessStart, context.getPlaceOk());
			pn.addFlow(context.getPlaceOk(), subprocessEnd);
			String exceptionName = context.getException().getName();
			Transition exception = new Transition("ExFor" + exceptionName);
			pn.addFlow(context.getPlaceOk(), exception);
			if(!context.getAttachedEvent().isInterrupting()) {
				pn.addFlow(exception, context.getPlaceOk());
				connectTwoTransitions(exception, context.getException(), "helperForNonInterruptingFor" + exceptionName);
			} else {
				for(Transition t : originalTransitions) {
					//add skip transition for each transition of original mapped Petri net
					Transition tSkip = new Transition("skip" + t.getName());
					if(pn.getEdgesWithTarget(t).isEmpty()) {
						pn.addFlow(new Place(), t);
					}
					pn.addFlow((Place) pn.getEdgesWithTarget(t).iterator().next().getSource(), tSkip);
					if(pn.getEdgesWithSource(t).isEmpty()) {
						pn.addFlow(t, new Place());
					}
					pn.addFlow(tSkip, (Place)pn.getEdgesWithSource(t).iterator().next().getTarget());
					pn.addFlow(context.getPlaceOk(), t);
					pn.addFlow(t, context.getPlaceOk());
					pn.addFlow(context.getPlaceNotOk(), tSkip);
					pn.addFlow(tSkip, context.getPlaceNotOk());
				}
				pn.addFlow(exception, context.getPlaceNotOk());
				pn.addFlow(context.getPlaceNotOk(), context.getException());
				pn.addFlow(lastSubprocessPlace, context.getException());
			}
		}
	}

}
