package de.uni_potsdam.hpi.bpt.promnicat.correctionModule.rules;

import java.util.Arrays;
import java.util.HashSet;

import de.uni_potsdam.hpi.bpt.ai.diagram.Shape;
import de.uni_potsdam.hpi.bpt.ai.util.BpmMetrics;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.correctionModule.wrapper.ShapeWrapper;

public class EpcDiagramRules extends DiagramRules {
	protected EpcDiagramRules() throws UnsupportedModelException {
		super();
	}
	
	private static HashSet<String> SUPPORTED_STENCILSET_EXTENSIONS = new HashSet<String>(Arrays.asList(new String[]{
		"http://signavio.com/stencilsets/extensions/epc.normal#",
		"http://signavio.com/stencilsets/extensions/epc.basicsubset#"
	}));
	
	@Override
	public boolean isNode(Shape shape){
		return super.isNode(shape) 
				|| shape.getStencilId().contentEquals("Letter")
				|| shape.getStencilId().contentEquals("Entity")
				|| shape.getStencilId().contentEquals("Mail");
	}

	@Override
	public boolean isControlFlowNode(ShapeWrapper wrapper){
		return BpmMetrics.isControlFlowNode(wrapper.getShape());
	}
	
	@Override
	public boolean isControlFlowEdge(ShapeWrapper wrapper){
		return BpmMetrics.isControlFlowArc(wrapper.getShape());
	}
	
	private boolean isAnnotationElement(ShapeWrapper wrapper){
		return !isControlFlowNode(wrapper) && !isControlFlowEdge(wrapper);
	}
	
	@Override
	public boolean connectionCanBeAppliedTo(ShapeWrapper from, ShapeWrapper to, String byStencilId){
		boolean canBeApplied = true;
		if (byStencilId.contentEquals("ControlFlow") && from != null && to != null){
			canBeApplied = isControlFlowNode(from) && isControlFlowNode(to) && !from.isStencil(to.getStencilId());
		}
		if (byStencilId.contentEquals("Relation") && from != null && to != null){
			canBeApplied = (isControlFlowNode(from) && isAnnotationElement(to))
							|| (isControlFlowNode(to) && isAnnotationElement(from));
		}
		return canBeApplied && super.connectionCanBeAppliedTo(from, to, byStencilId);
	}
	
	@Override
	public boolean needsIncomingControlFlow(ShapeWrapper shape){
		return false;
	}
	
	@Override
	public boolean needsOutgoingControlFlow(ShapeWrapper shape){
		return false;
	}
	
	@Override
	public boolean needsAtLeastOneControlFlowConnection(ShapeWrapper shape){
		if (shape != null &&
				(shape.getStencilId().contentEquals("TextNote")
				|| shape.getStencilId().contentEquals("null"))){ // the canvas of some epc charts
			return false;
		}
		return super.needsAtLeastOneControlFlowConnection(shape);
	}
	
	@Override
	public boolean canBeConnected(ShapeWrapper from, ShapeWrapper to, ShapeWrapper by){
		for (Connection connection : roleToRoleConnection){
			if (canBeConnected(from, to, by, connection)
					&& connectionCanBeAppliedTo(from, to, by.getStencilId())){
				return true;
			}
		}
		return false; 
	}
	
	@Override
	public boolean isEdge(Shape shape){
		return !isNode(shape);
		// edge instanceof shape - can not be applied because the epc specification has missing elements 
		// and therefore the creation of the shape by the ShapeFactory does not yield the correct class
	}
	
	@Override
	public boolean isUndirectedEdge(ShapeWrapper shape){
		return shape.isStencil("Relation");
	}

	@Override
	public String getSupportedStencilSet() {
		return "http://b3mn.org/stencilset/epc#";
	}
	
	@Override
	public HashSet<String> getSupportedStencilSetExtensions() {
		return SUPPORTED_STENCILSET_EXTENSIONS;
	}
}