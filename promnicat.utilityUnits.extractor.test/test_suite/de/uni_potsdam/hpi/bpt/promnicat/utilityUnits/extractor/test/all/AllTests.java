package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.BpmnConformanceLevelCheckerTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.BpmnConformanceLevelCheckerUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ElementExtractorUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ElementLabelExtractorUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.PetriNetAnalyzerUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ProcessMetricsCalculatorTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ProcessModelLabelExtractorUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ProcessModelMetricsCalculatorUnitTest;

@RunWith(Suite.class)
@SuiteClasses({
	BpmnConformanceLevelCheckerTest.class,
	BpmnConformanceLevelCheckerUnitTest.class,
	ElementExtractorUnitTest.class,
	ElementLabelExtractorUnitTest.class,
	PetriNetAnalyzerUnitTest.class,
	ProcessMetricsCalculatorTest.class,
	ProcessModelLabelExtractorUnitTest.class,
	ProcessModelMetricsCalculatorUnitTest.class
})
public class AllTests {

}
