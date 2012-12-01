package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test.BpmnConformanceLevelCheckerTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test.BpmnConformanceLevelCheckerUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test.PetriNetAnalyzerUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test.ProcessMetricsCalculatorTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processMetrics.test.ProcessModelMetricsCalculatorUnitTest;

@RunWith(Suite.class)
@SuiteClasses({
	BpmnConformanceLevelCheckerTest.class,
	BpmnConformanceLevelCheckerUnitTest.class,
	PetriNetAnalyzerUnitTest.class,
	ProcessMetricsCalculatorTest.class,
	ProcessModelMetricsCalculatorUnitTest.class
})
public class AllTests {

}
