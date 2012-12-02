package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.uni_potsdam.hpi.bpt.ai.util.BpmMetrics;
import de.uni_potsdam.hpi.bpt.promnicat.util.modelConstants.Bpmn1_1Constants;
import de.uni_potsdam.hpi.bpt.promnicat.util.modelConstants.Bpmn2_0Constants;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.ShapeWrapper;

/**
 * Defines common rules for Bpmn 2.0 and Bpmn 1.1 process models
 * @author Christian Kieschnick
 */
public abstract class BpmnDiagramRules extends DiagramRules {
	/**
	 * the Constants used by PromniCAT
	 */
	private Bpmn1_1Constants constants;
	
	protected BpmnDiagramRules(Bpmn1_1Constants constants) throws UnsupportedModelException {
		super();
		this.constants = constants; 
	}

	/**
	 * get the constants used by PromniCAT for identifying special proberties or shapes
	 * @return
	 */
	public Bpmn1_1Constants getConstants(){
		return constants;
	}
	
	@Override
	public boolean isControlFlowNode(ShapeWrapper shape){
		return BpmMetrics.isControlFlowNode(shape.getShape());
	}
	
	@Override
	public boolean isControlFlowEdge(ShapeWrapper shape){
		return BpmMetrics.isControlFlowArc(shape.getShape());
	}
	
	/**
	 * determine if the given shape can be start of a control flow
	 * @param shape the shape to inspect
	 * @return true if the shape can start a control flow
	 */
	public boolean isStartEvent(ShapeWrapper shape){
		Collection<String> roles = getRoles(shape);
		return roles.contains("Startevents_all")
					|| roles.contains("sequence_start");
	}
	
	/**
	 * determine if the given shape can be end of a control flow
	 * @param shape the shape to inspect
	 * @return true if the shape can end a control flow
	 */
	public boolean isEndEvent(ShapeWrapper shape){
		Collection<String> roles = getRoles(shape);
		return roles.contains("Endevents_all")
					|| roles.contains("sequence_end");
	}
	
	@Override
	public boolean needsIncomingControlFlow(ShapeWrapper shape) {
		return needsAtLeastOneControlFlowConnection(shape) 
				&& !isStartEvent(shape);
	}

	@Override
	public boolean needsOutgoingControlFlow(ShapeWrapper shape) {
		return needsAtLeastOneControlFlowConnection(shape)
				&& !isEndEvent(shape);
	}
	
	/**
	 * determine attached shapes
	 * @param shape the shape to inspect
	 * @return true if the shape can occur attached to another element
	 */
	public boolean isAttachedControlFlowNode(ShapeWrapper shape) {
		return shape.isStencil("IntermediateTimerEvent")
				|| shape.isStencil("IntermediateMessageEventCatching")
				|| shape.isStencil("IntermediateMultipleEventCatching")
				|| shape.isStencil("IntermediateSignalEventCatching")
				|| shape.isStencil("IntermediateConditionalEvent")
				|| shape.isStencil("IntermediateCompensationEventCatching")
				|| shape.isStencil("IntermediateErrorEvent")
				|| shape.isStencil("IntermediateEscalationEvent")
				|| shape.isStencil("IntermediateParallelMultipleEventCatching");
	}
		
	@Override
	public boolean needsAtLeastOneControlFlowConnection(ShapeWrapper shape){
		if (shape.getStencilId() == null 
				|| shape.isStencil("Lane")
				|| shape.isStencil("Diagram")
				|| shape.isStencil("null")
				|| shape.isStencil("BPMNDiagram")
				|| shape.isStencil("Pool")
				|| shape.isStencil("CollapsedPool") //this would be a semantic error since the role is not used
				|| shape.isStencil("EventSubprocess")
				|| shape.isStencil("CollapsedEventSubprocess")
				|| shape.isStencil("Group")
				|| shape.isStencil("TextAnnotation")
				|| shape.isStencil("Task")
				|| shape.isStencil("Subprocess")){
			return false;
		}
		return super.needsAtLeastOneControlFlowConnection(shape);
	}
	
	/**
	 * Rules which should be applied to Bpmn 2.0 processes
	 * @author Christian Kieschnick
	 */
	public static class Bpmn2_0 extends BpmnDiagramRules {
		private static HashSet<String> SUPPORTED_STENCILSET_EXTENSIONS = new HashSet<String>(Arrays.asList(new String[]{
				"http://oryx-editor.org/stencilsets/extensions/bpmn2.0basicsubset#"
		}));

		public Bpmn2_0() throws UnsupportedModelException {
			super(new Bpmn2_0Constants());
		}

		@Override
		public String getSupportedStencilSet() {
			return "http://b3mn.org/stencilset/bpmn2.0#";
		}

		@Override
		public HashSet<String> getSupportedStencilSetExtensions() {
			return SUPPORTED_STENCILSET_EXTENSIONS;
		}
		
		/**
		 * check if between from and to should be a message flow
		 * @param from the source of the connection
		 * @param to the target of the connection
		 * @return true if a message flow should connect from and to
		 */
		private boolean needsMessageFlow(ShapeWrapper from, ShapeWrapper to){
			Set<ShapeWrapper> fromAncestors = from.getDirectAncestors();
			fromAncestors.add(from);
			Set<ShapeWrapper> toAncestors = to.getDirectAncestors();
			toAncestors.add(to);
			// look if only the diagram is the parent
			if (fromAncestors.size() == 2 && toAncestors.size() == 2)
				return false;
			fromAncestors.retainAll(toAncestors);
			return fromAncestors.size() == 1;
		}
		
		@Override
		public boolean connectionCanBeAppliedTo(ShapeWrapper from, ShapeWrapper to, String byStencilId){
			boolean canBeApplied = true;
			if (byStencilId.contentEquals("MessageFlow") && from != null && to != null){
				canBeApplied = needsMessageFlow(from, to);
			} else if (byStencilId.contentEquals("SequenceFlow") && from != null && to != null){
				canBeApplied = !needsMessageFlow(from, to);
			}
			return canBeApplied && super.connectionCanBeAppliedTo(from, to, byStencilId);
		}
	}
	
	/**
	 * Rules which should be applied for Bpmn 1.1
	 * @author Christian Kieschnick
	 */
	public static class Bpmn1_1 extends BpmnDiagramRules {
		private static HashSet<String> SUPPORTED_STENCILSET_EXTENSIONS = new HashSet<String>(Arrays.asList(new String[]{
				"http://oryx-editor.org/stencilsets/extensions/bpmn1.1basicsubset#"
		}));
		public Bpmn1_1() throws UnsupportedModelException {
			super(new Bpmn1_1Constants());
		}

		@Override
		public String getSupportedStencilSet() {
			return "http://b3mn.org/stencilset/bpmn1.1#";
		}

		@Override
		public HashSet<String> getSupportedStencilSetExtensions() {
			return SUPPORTED_STENCILSET_EXTENSIONS;
		}

	}
}


