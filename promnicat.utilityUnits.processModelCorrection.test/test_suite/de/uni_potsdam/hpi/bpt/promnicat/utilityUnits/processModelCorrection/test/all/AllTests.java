package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.CorrectorTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.DetectorTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.DiagramRulesTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.processModelCorrection.test.DiagramWrapperTest;

@RunWith(Suite.class)
@SuiteClasses({
	CorrectorTest.class,
	DetectorTest.class,
	DiagramRulesTest.class,
	DiagramWrapperTest.class
})
public class AllTests {

}
