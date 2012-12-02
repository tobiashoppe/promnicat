package de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.test.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.test.BpmaiJsonToDiagramUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.test.DiagramToJbptUnitTest;
import de.uni_potsdam.hpi.bpt.promnicat.utilityUnits.transformer.test.ProcessModelToPetriNetUnitTest;

@RunWith(Suite.class)
@SuiteClasses({
	BpmaiJsonToDiagramUnitTest.class,
	DiagramToJbptUnitTest.class,
	ProcessModelToPetriNetUnitTest.class
})
public class AllTests {

}
