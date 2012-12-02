package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.UnsupportedModelException;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.BpmnDiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.DiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.rules.EpcDiagramRules;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.util.TestModelBuilder;

public class DiagramRulesTest {

	@Test
	public void testDiagramRuleBpmn1_1Association() throws UnsupportedModelException, UnsupportedEncodingException, FileNotFoundException, IOException, JSONException {
		Diagram diagram = TestModelBuilder.createDiagramWith("http://b3mn.org/stencilset/bpmn1.1#");
		diagram.getSsextensions().add("http://oryx-editor.org/stencilsets/extensions/bpmn1.1basicsubset#");
		DiagramRules rules = DiagramRules.of(diagram);
		Assert.assertTrue(rules instanceof BpmnDiagramRules.Bpmn1_1);
	}
	
	@Test
	public void testDiagramRuleEpcAssociation() throws UnsupportedModelException, UnsupportedEncodingException, FileNotFoundException, IOException, JSONException {
		DiagramRules rules = DiagramRules.of(TestModelBuilder.createDiagramWith("http://b3mn.org/stencilset/epc#"));
		Assert.assertTrue(rules instanceof EpcDiagramRules);
	}
	
	@Test
	public void testDiagramRuleBpmn2_0Association() throws UnsupportedModelException{
		DiagramRules rules = DiagramRules.of(TestModelBuilder.createDiagramWith("http://b3mn.org/stencilset/bpmn2.0#"));
		Assert.assertTrue(rules instanceof BpmnDiagramRules.Bpmn2_0);

//		PA.getMethodSignatures(instanceOrClass)
	}

}
