package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

import de.uni_potsdam.hpi.bpt.ai.diagram.Diagram;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.Statistic;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors.MissingControlFlowConnectionDetector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.detectors.WrongEdgeTypeConnectionDetector;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.util.TestModelBuilder;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.wrapper.DiagramWrapper;

public class DetectorTest {

	@Test
	public void simpleWrongConnectionDetectionTest() throws Exception {
		Diagram model = TestModelBuilder.getModelFromFile("process_connection_through_message_flow");
		WrongEdgeTypeConnectionDetector detector = new WrongEdgeTypeConnectionDetector();
		Statistic.GroupedValueResult result = new Statistic.GroupedValueResult();
		detector.process(new DiagramWrapper(model), result);
		HashMap<String, Integer> resourceErrorCount = result.get(WrongEdgeTypeConnectionDetector.class.getSimpleName());
		Assert.assertSame(1, resourceErrorCount.size());
		Assert.assertSame(2, resourceErrorCount.get("MessageFlow"));
	}
	
	@Test
	public void extendedWrongConnectionDetectionTest() throws Exception {
		Diagram model = TestModelBuilder.getModelFromFile("process_connection_through_message_flow_extended");
		WrongEdgeTypeConnectionDetector detector = new WrongEdgeTypeConnectionDetector();
		Statistic.GroupedValueResult result = new Statistic.GroupedValueResult();
		detector.process(new DiagramWrapper(model), result);
		HashMap<String, Integer> resourceErrorCount = result.get(WrongEdgeTypeConnectionDetector.class.getSimpleName());
		Assert.assertSame(2, resourceErrorCount.size());
		Assert.assertSame(3, resourceErrorCount.get("MessageFlow"));
		Assert.assertSame(1, resourceErrorCount.get("SequenceFlow"));
	}
	
	@Test
	public void test() throws Exception{
		Diagram model = TestModelBuilder.getModelFromFile("1315717225_rev3");
		WrongEdgeTypeConnectionDetector detector = new WrongEdgeTypeConnectionDetector();
		Statistic.GroupedValueResult result = new Statistic.GroupedValueResult();
		detector.process(new DiagramWrapper(model), result);
		assert result.isEmpty();
	}
	
	@Test
	public void test2() throws Exception{
		Diagram model = TestModelBuilder.getModelFromFile("2003663082_rev1");
		WrongEdgeTypeConnectionDetector detector = new WrongEdgeTypeConnectionDetector();
		Statistic.GroupedValueResult result = new Statistic.GroupedValueResult();
		detector.process(new DiagramWrapper(model), result);
		assert result.isEmpty();
	}
	
	@Test
	public void paperModelWithWrongTypeDetectorTest() throws Exception {
		Diagram model = TestModelBuilder.getModelFromFile("paper_model");
		WrongEdgeTypeConnectionDetector detector = new WrongEdgeTypeConnectionDetector();
		Statistic.GroupedValueResult result = new Statistic.GroupedValueResult();
		detector.process(new DiagramWrapper(model), result);
		HashMap<String, Integer> resourceErrorCount = result.get(WrongEdgeTypeConnectionDetector.class.getSimpleName());
		Assert.assertSame(1, resourceErrorCount.size());
		Assert.assertSame(1, resourceErrorCount.get("MessageFlow"));
	}
	
	
	@Test
	public void paperModelWithMissingControlFlowDetectorTest() throws Exception {
		Diagram model = TestModelBuilder.getModelFromFile("paper_model");
		DiagramWrapper diagram = new DiagramWrapper(model);
		Statistic.GroupedValueResult result = new Statistic.GroupedValueResult();
		new MissingControlFlowConnectionDetector.Incoming().process(diagram, result);
		new MissingControlFlowConnectionDetector.Outgoing().process(diagram, result);
		
		HashMap<String, Integer>  incoming = result.get(MissingControlFlowConnectionDetector.Incoming.class.getSimpleName());
		HashMap<String, Integer>  outgoing = result.get(MissingControlFlowConnectionDetector.Outgoing.class.getSimpleName());

		Assert.assertEquals(3, incoming.size());
		Assert.assertEquals(3, outgoing.size());
		Assert.assertSame(1, outgoing.get("Task"));
		Assert.assertSame(1, outgoing.get("SequenceFlow"));
		Assert.assertSame(1, outgoing.get("IntermediateMessageEventThrowing"));
		Assert.assertSame(1, incoming.get("Task"));
		Assert.assertSame(1, incoming.get("SequenceFlow"));
		Assert.assertSame(1, incoming.get("IntermediateMessageEventCatching"));
	}
}
