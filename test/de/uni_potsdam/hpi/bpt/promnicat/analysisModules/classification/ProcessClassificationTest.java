package de.uni_potsdam.hpi.bpt.promnicat.analysisModules.classification;

import java.util.Collection;
import java.util.Iterator;

import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.rpst.RPSTNode;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
import org.jbpt.pm.OrGateway;
import org.jbpt.pm.ProcessModel;
import org.jbpt.pm.bpmn.BpmnControlFlow;
import org.jbpt.pm.bpmn.Subprocess;
import org.junit.Ignore;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.unitData.IUnitDataClassification;

@Ignore
public class ProcessClassificationTest {
	
	public static void openMappings(Collection<IUnitDataClassification<Object>> resultSet) {
		long attachedEventSp = 0;
		long adHocSp = 0;
		long eventDrivenSp = 0;
		long orgateway = 0;
		long notMappableOrGateway = 0;
		long errors = 0;
		
		for(IUnitDataClassification<Object> resultItem : resultSet){
			ProcessModel model = (ProcessModel) resultItem.getProcessModel();
			for (IVertex sp : model.filter(Subprocess.class)) {
				adHocSp += ((Subprocess) sp).isAdhoc() ? 1 : 0;
				eventDrivenSp += ((Subprocess) sp).isEventDriven() ? 1 : 0;
				Collection<ControlFlow<FlowNode>> edges = model.getEdgesWithSource((FlowNode) sp);
				Iterator<ControlFlow<FlowNode>> it = edges.iterator();
				while (it.hasNext()) {
					AbstractDirectedEdge<FlowNode> cf = it.next();
					if (cf instanceof BpmnControlFlow && ((BpmnControlFlow<FlowNode>) cf).hasAttachedEvent()) {	
						attachedEventSp++;
					}
				}
					
			}
			try {
				RPST<ControlFlow<FlowNode>, FlowNode> rpst = new RPST<ControlFlow<FlowNode>, FlowNode>(model);
				orLoop: for (IVertex sp : model.filter(OrGateway.class)) {
					for (RPSTNode<ControlFlow<FlowNode>, FlowNode> rigidNode : rpst.getRPSTNodes(TCType.RIGID)) {
						if (rigidNode.getFragment().contains((OrGateway)sp)) {
							notMappableOrGateway++;
							break orLoop;
						}
					}				
				}
			} catch (Exception e) {
				errors++;
			}
			if (model.filter(OrGateway.class).size() > 0) orgateway++;
		}
		System.out.println("Attached Events for Subprocesses: " + attachedEventSp);
		System.out.println("Ad Hoc Subprocesses: " + adHocSp);
		System.out.println("Event Driven Subprocesses: " + eventDrivenSp);
		System.out.println("Total number of Processes with OR-Gateways: " + orgateway);
		System.out.println("Number of Processes with not-mappable OR-Gateway: " + notMappableOrGateway);
		System.out.println("Errors: " + errors);
	}
}
