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
import org.jbpt.pm.bpmn.Bpmn;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jbpt.pm.bpmn.BpmnEvent;
import org.jbpt.pm.bpmn.Subprocess;
import org.jbpt.throwable.TransformationException;

/**
 * This class converts a {@link Bpmn} model to the corresponding {@link PetriNet}.
 * TODO handle specifics of certain event types(e.g. Link event)
 * TODO handle message flow and data flow(in subclasses?!)
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
	 * - Model does not contain any {@link OrGateway}s or Ad-hoc-{@link Subprocess}es
	 * or event-based-{@link Subprocess}es
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
		ProcessModel transformedModel = prepareProcessModel(model);
		if(transformedModel == null) {
			return null;
		}
		//create places and transitions according to the flow nodes of the model
		convertFlowNodes(transformedModel.getFlowNodes());
		//add edges according to the control flow of the model
		convertControlFlowEdges(transformedModel.getControlFlow());
		return this.petriNet;
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
				//TODO handle ad hoc subprocess converting
				throw new TransformationException("Ad hoc supbrocesses could not be handled.");
			} else if (((Subprocess) activity).isEventDriven()) {
				//TODO check how to handle
				throw new TransformationException("Event driven subprocess could not be handled.");
			} else {				
				convertSubprocess((Subprocess)activity);
			}
		} else {
			//handle activities that are not a subprocess
			super.convertActivity(activity);
		}
	}
	
	/**
	 * Handles boundary events before creation of {@link Flow}s.
	 * @see AbstractModelToPetriNetConverter#convertControlFlowEdges(Collection)
	 */
	@Override
	protected void convertControlFlowEdges(Collection<ControlFlow<FlowNode>> edges) {
		for(ControlFlow<FlowNode> edge : edges) {
			if ((edge instanceof BpmnControlFlow<?>) && ((BpmnControlFlow<FlowNode>)edge).hasAttachedEvent()) {
				convertAttachedEvent((BpmnControlFlow<FlowNode>) edge);
			}
		}
		super.convertControlFlowEdges(edges);
	}

	/**
	 * converts the attached {@link BpmnEvent} of the given {@link BpmnControlFlow}.
	 * @param edge containing the attached {@link Event}.
	 */
	protected void convertAttachedEvent(BpmnControlFlow<FlowNode> edge) {
		if (edge.getSource() instanceof Subprocess) {
			convertAttachedEventForSubprocess(edge);
		} else {
			BpmnEvent attachedEvent = edge.getAttachedEvent();
			Transition attachedEventTransition = new Transition(attachedEvent.getLabel());
			for (ControlFlow<FlowNode> e : edge.getSource().getModel().getOutgoingControlFlow(edge.getSource())) {
				if (e != edge) {
					Place p = new Place();
					this.petriNet.addFreshFlow(this.nodeMapping.get(e.getSource()), p);
					this.nodeMapping.put(e.getSource(), p);
					this.petriNet.addFlow(p, attachedEventTransition);
					if (!edge.getAttachedEvent().isInterrupting()) {
						this.petriNet.addFlow(attachedEventTransition, p);
					}
				}
			}
			edge.setSource(attachedEvent);
			this.nodeMapping.put(attachedEvent, attachedEventTransition);
		}
	}

	/**
	 * converts the attached {@link BpmnEvent} of the given {@link BpmnControlFlow}
	 * starting at a {@link Subprocess}.
	 * @param edge containing the attached {@link Event}.
	 */
	protected void convertAttachedEventForSubprocess(BpmnControlFlow<FlowNode> edge) {
		// TODO Auto-generated method stub		
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
		//convert subprocess to Petrinet and add it to the resulting net
		PetriNet pn = new BpmnToPetriNetConverter().convertToPetriNet(process);
		if(pn.getNodes().isEmpty()) {
			super.convertActivity(subprocess);
		}
		this.petriNet.addNodes(pn.getNodes());
		for (Flow f : pn.getFlow()) {
			this.petriNet.addFreshFlow(f.getSource(), f.getTarget());
		}
		//insert place to connecting to all start nodes of subprocess net
		if (pn.getSourceNodes().size() > 1) {
			Place p = new Place("subprocesses_start" + getNextId());
			for(Node n : pn.getSourceNodes()) {
				this.petriNet.addFreshFlow(p, n);
			}
			this.nodeMapping.put(subprocess, p);
		} else if (!pn.getSourceNodes().isEmpty()){
			this.nodeMapping.put(subprocess, pn.getSourceNodes().iterator().next());
		}
		//add dummy activity to original process model to handle end of subprocess
		ProcessModel model = subprocess.getModel();
		Collection<ControlFlow<FlowNode>> outgoingEdges = model.getOutgoingControlFlow(subprocess);
		if (outgoingEdges.size() > 0) {
			ControlFlow<FlowNode> outgoingEdge = outgoingEdges.iterator().next();
			Activity dummy = new Activity();
			model.addControlFlow(dummy, outgoingEdge.getTarget());
			model.removeControlFlow(outgoingEdge);
			//handle integration of end of subprocess into Petrinet
			if (pn.getSinkNodes().size() > 1) {
				//insert place to connect all ends
				Place p = new Place("subprocesses_end" + getNextId());
				for(Node n : pn.getSinkNodes()) {
					this.petriNet.addFreshFlow(n, p);
				}
				this.nodeMapping.put(dummy, p);
			} else if (!pn.getSinkNodes().isEmpty()) {
				this.nodeMapping.put(dummy, pn.getSinkNodes().iterator().next());
			}
		}
		
	}

}
