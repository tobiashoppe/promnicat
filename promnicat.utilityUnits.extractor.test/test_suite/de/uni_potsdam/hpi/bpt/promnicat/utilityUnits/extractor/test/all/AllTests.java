package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ElementExtractorUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ElementLabelExtractorUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.extractor.test.ProcessModelLabelExtractorUnitTest;

@RunWith(Suite.class)
@SuiteClasses({
	ElementExtractorUnitTest.class,
	ElementLabelExtractorUnitTest.class,
	ProcessModelLabelExtractorUnitTest.class
})
public class AllTests {

}
