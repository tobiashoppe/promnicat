package de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests.CapitalizeStringTest;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests.LeskTest;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests.StopWordsTests;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests.StringOperationsTest;
import de.uni_potsdam.hpi.bpt.promnicat.bpmTranslator.tests.WordNetTest;

@RunWith(Suite.class)
@SuiteClasses({ CapitalizeStringTest.class, LeskTest.class,
		StopWordsTests.class, StringOperationsTest.class, WordNetTest.class })
public class AllTests {

}
